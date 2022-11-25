/*
 * Copyright 2022 OpsMx, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
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

package com.opsmx.spinnaker.gate.security.saml;

import com.netflix.spinnaker.gate.config.AuthConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

@Configuration
@EnableWebSecurity
public class MultipleAuthConfig {

  @Bean
  public UserDetailsService userDetailsService() throws Exception {
    InMemoryUserDetailsManager manager = new InMemoryUserDetailsManager();
    manager.createUser(User.withUsername("user").password("password").roles("USER").build());
    manager.createUser(
        User.withUsername("admin").password("password").roles("USER", "ADMIN").build());
    return manager;
  }

  @Configuration
  @Order(1)
  public static class AdminSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired AuthConfig authConfig;

    protected void configure(HttpSecurity http) throws Exception {
      http.antMatcher("/**").userDetailsService(userDetailsService());
      authConfig.configure(http);
    }
  }

  //  @Configuration
  //  public static class LdapConfigurerAdapter extends LdapSsoConfig {
  //
  //  }
}
