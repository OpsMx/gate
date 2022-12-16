/*
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

package com.opsmx.spinnaker.gate;

import com.netflix.spinnaker.gate.config.RequestMatcherProvider;
import java.util.Collections;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.AnonymousAuthenticationFilter;
import org.springframework.security.web.util.matcher.RequestHeaderRequestMatcher;

/*
   - Configuration: Adds a new Spring configuration
   - Enable web security: Overrides the default web security configuration
*/
@Configuration
@EnableWebSecurity
@Order(1)
public class CustomTokenSecurityConfig extends WebSecurityConfigurerAdapter {

  @Autowired private CustomApiTokenAuthenticationProvider customApiTokenAuthenticationProvider;

  @Autowired RequestMatcherProvider requestMatcherProvider;

  @Override
  public void configure(WebSecurity web) throws Exception {
    web.ignoring()
        .antMatchers("/error")
        .antMatchers("/resources/**")
        .antMatchers("/images/**")
        .antMatchers("/js/**")
        .antMatchers("/fonts/**")
        .antMatchers("/css/**")
        .antMatchers("/**/favicon.ico")
        .antMatchers(HttpMethod.OPTIONS, "/**")
        .antMatchers("/auth/loggedOut")
        .antMatchers("/auth/user")
        .antMatchers("/health")
        .antMatchers("/prometheus")
        .antMatchers("/info")
        .antMatchers("/metrics");
  }

  @Override
  protected void configure(HttpSecurity httpSecurity) throws Exception {
    httpSecurity
        //      .httpBasic().disable()
        .sessionManagement()
        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        .and()
        .cors()
        .and()
        .requestCache()
        .disable()
        //      .csrf().disable()
        //      .csrf().csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse()).and()

        .antMatcher("/platformservice/v7/argo/cdIntegrations")
        .antMatcher("/platformservice/v1/applications")

        /*.requestMatchers()
        .antMatchers("/platformservice/v7/argo/cdIntegrations")
        .and()*/
        //      .requestMatcher(requestMatcherProvider.requestMatcher())
        //      .anonymous().and()
        //      .authorizeRequests()
        //      .antMatchers("/error").permitAll()
        //      .and()
        .authorizeRequests()
        //      .antMatchers("/favicon.ico").permitAll()
        //      .antMatchers("/resources/**").permitAll()
        //      .antMatchers("/images/**").permitAll()
        //      .antMatchers("/js/**").permitAll()
        //      .antMatchers("/fonts/**").permitAll()
        //      .antMatchers("/css/**").permitAll()
        //      .antMatchers("/**/favicon.ico").permitAll()
        //      .antMatchers(HttpMethod.OPTIONS, "/**").permitAll()
        //      .antMatchers("/auth/loggedOut").permitAll()
        //      .antMatchers("/auth/user").permitAll()
        //      .antMatchers("/health").permitAll()
        //      .antMatchers("/prometheus").permitAll()
        //      .antMatchers("/info").permitAll()
        //      .antMatchers("/metrics").permitAll()
        .anyRequest()
        //      .antMatchers("/**")

        .authenticated()
        .and()
        .requestMatcher(new RequestHeaderRequestMatcher("x-opsmx-token"))
        .addFilterBefore(
            new CustomTokenAuthenticationFilter(authenticationManager()),
            AnonymousAuthenticationFilter.class);
  }

  //  @Bean
  public AuthenticationManager authenticationManager() {
    return new ProviderManager(Collections.singletonList(customApiTokenAuthenticationProvider));
  }
}
