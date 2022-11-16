package com.opsmx.spinnaker.gate.config; /*
                                          * Copyright 2022 Netflix, Inc.
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

import com.opsmx.spinnaker.gate.filters.APIKeyAuthFilter;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

@Configuration
@EnableWebSecurity
@Order(1)
public class APISecurityConfig extends WebSecurityConfigurerAdapter {

  //  @Value("${yourapp.http.auth-token-header-name}")
  private String principalRequestHeader = "OPSMX_TOKEN";

  //  @Value("${yourapp.http.auth-token}")
  private String principalRequestValue = "topsecret";

  @Override
  protected void configure(HttpSecurity httpSecurity) throws Exception {
    APIKeyAuthFilter filter = new APIKeyAuthFilter(principalRequestHeader);
    filter.setAuthenticationManager(
        new AuthenticationManager() {

          @Override
          public Authentication authenticate(Authentication authentication)
              throws AuthenticationException {
            String principal = (String) authentication.getPrincipal();
            if (!principalRequestValue.equals(principal)) {
              throw new BadCredentialsException(
                  "The API key was not found or not the expected value.");
            }
            authentication.setAuthenticated(true);
            return authentication;
          }
        });
    httpSecurity
        .antMatcher("/platformservice/v6/applications")
        .csrf()
        .disable()
        .sessionManagement()
        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        .and()
        .addFilter(filter)
        .authorizeRequests()
        .anyRequest()
        .authenticated();
  }
}
