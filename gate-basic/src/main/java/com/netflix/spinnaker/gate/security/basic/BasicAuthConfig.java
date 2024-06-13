/*
 * Copyright 2018 Google, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License")
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.netflix.spinnaker.gate.security.basic;

import com.netflix.spinnaker.gate.config.AuthConfig;
import com.netflix.spinnaker.gate.security.SpinnakerAuthConfig;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.DelegatingPasswordEncoder;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.session.web.http.DefaultCookieSerializer;

@ConditionalOnExpression("${security.basicform.enabled:false}")
@Configuration
@SpinnakerAuthConfig
@EnableWebSecurity
@Slf4j
public class BasicAuthConfig {

  @Autowired private final AuthConfig authConfig;
  @Autowired private final BasicAuthProvider authProvider;

  @Autowired DefaultCookieSerializer defaultCookieSerializer;

  @Value("${security.user.roles:}")
  String roles;

  @Value("${security.user.name:}")
  String name;

  @Value("${security.user.password:}")
  String password;

  @Autowired
  public BasicAuthConfig(AuthConfig authConfig, BasicAuthProvider authProvider) {
    this.authConfig = authConfig;
    this.authProvider = authProvider;
  }

  @Bean
  public AuthenticationManager authManager(HttpSecurity http) throws Exception {
    AuthenticationManagerBuilder authenticationManagerBuilder =
        http.getSharedObject(AuthenticationManagerBuilder.class);
    if (name == null || name.isEmpty() || password == null || password.isEmpty()) {
      throw new AuthenticationServiceException(
          "User credentials are not configured properly. Please check username and password are properly configured");
    }

    if (roles == null || roles.isEmpty()) {
      log.warn(
          "No roles are configured for the user. This would leads to authorizations issues if RBAC is enabled");
    } else {
      authProvider.setRoles(
          Stream.of(roles.split(",")).map(String::trim).collect(Collectors.toList()));
    }
    log.info(
        "User --> "
            + this.name
            + "Password Bcrypt--> "
            + (new BCryptPasswordEncoder()).encode(this.password));
    log.info(
        "User --> "
            + this.name
            + "Password NoopPassword--> "
            + (NoOpPasswordEncoder.getInstance()).encode(this.password));
    authProvider.setName(this.name);
    //    authProvider.setPassword((new BCryptPasswordEncoder()).encode(this.password));
    authProvider.setPassword(this.password);
    authenticationManagerBuilder.authenticationProvider(authProvider);
    authenticationManagerBuilder.eraseCredentials(false);
    return authenticationManagerBuilder.build();
  }

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    defaultCookieSerializer.setSameSite(null);
    http.cors().and().csrf().disable();
    http.formLogin()
        .and()
        .authenticationManager(authManager(http))
        .httpBasic()
        .authenticationEntryPoint(new LoginUrlAuthenticationEntryPoint("/login"));
    authConfig.configure(http);
    return http.build();
  }

  @Bean
  public WebSecurityCustomizer webSecurityCustomizer() {
    return (web) -> {
      try {
        authConfig.configure(web);
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
    };
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    var passwordEncoder =
        (DelegatingPasswordEncoder) PasswordEncoderFactories.createDelegatingPasswordEncoder();
    passwordEncoder.setDefaultPasswordEncoderForMatches(NoOpPasswordEncoder.getInstance());
    return passwordEncoder;
  }
}
