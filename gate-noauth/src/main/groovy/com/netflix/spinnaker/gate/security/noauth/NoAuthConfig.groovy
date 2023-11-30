/*
 * Copyright 2017 Google, Inc.
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

package com.netflix.spinnaker.gate.security.noauth

import com.netflix.spinnaker.gate.config.AuthConfig
import com.netflix.spinnaker.gate.security.AllowedAccountsSupport
import com.netflix.spinnaker.gate.security.SpinnakerAuthConfig
import com.netflix.spinnaker.gate.services.PermissionService
import lombok.Data
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression
import org.springframework.boot.autoconfigure.security.SecurityProperties
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.builders.WebSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContext
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter
import org.springframework.session.web.http.DefaultCookieSerializer
import org.springframework.stereotype.Component

@ConditionalOnExpression('${noauth.enabled:false}')
@Configuration
@SpinnakerAuthConfig
@EnableWebSecurity
class NoAuthConfig extends WebSecurityConfigurerAdapter {

  @Autowired
  AuthConfig authConfig;

  @Autowired
  PermissionService permissionService;

  @Autowired
  NoAuthConfigProps noAuthConfigProps;

  @Autowired
  AllowedAccountsSupport allowedAccountsSupport;

  private final NoAuthProvider authProvider;

  @Autowired
  DefaultCookieSerializer defaultCookieSerializer;

  @Autowired
  public NoAuthConfig(AuthConfig authConfig, SecurityProperties securityProperties,
                      PermissionService permService, NoAuthConfigProps props, AllowedAccountsSupport acct) {
    this.authConfig = authConfig;
    this.permissionService = permService;
    this.noAuthConfigProps = props
    this.allowedAccountsSupport = acct
    this.authProvider = new NoAuthProvider(securityProperties, noAuthConfigProps, permissionService, allowedAccountsSupport);
  }

  @Override
  protected void configure(AuthenticationManagerBuilder auth) {
    auth.authenticationProvider(authProvider);
  }

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    defaultCookieSerializer.setSameSite(null);

    http.httpBasic().disable();
    http.formLogin().disable();
    SecurityContext securityContext = SecurityContextHolder.getContext();
    Authentication request = new UsernamePasswordAuthenticationToken(noAuthConfigProps.user, "password");
    Authentication result = authenticationManager().authenticate(request);
    securityContext.setAuthentication(result);
    http.addFilterBefore(new BasicAuthenticationFilter(authenticationManager()), UsernamePasswordAuthenticationFilter.class);
    authConfig.configure(http);
  }

  @Override
  public void configure(WebSecurity web) throws Exception {
    authConfig.configure(web);
  }

  @Component
  @Data
  @ConfigurationProperties("noauth")
  public static class NoAuthConfigProps {
    String user;

    String email;
  }
}
