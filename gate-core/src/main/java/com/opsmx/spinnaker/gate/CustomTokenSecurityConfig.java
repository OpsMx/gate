package com.opsmx.spinnaker.gate;

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
        .sessionManagement()
        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        .and()
        .cors()
        .and()
        .requestCache()
        .disable()
        .antMatcher("/platformservice/v7/argo/cdIntegrations")
        .antMatcher("/platformservice/v1/applications")
        .antMatcher("/autopilot/api/v5/global/template")
        .authorizeRequests()
        .anyRequest()
        .authenticated()
        .and()
        .requestMatcher(new RequestHeaderRequestMatcher("x-opsmx-token"))
        .addFilterBefore(
            new CustomTokenAuthenticationFilter(authenticationManager()),
            AnonymousAuthenticationFilter.class);
  }

  @Override
  public AuthenticationManager authenticationManager() {
    return new ProviderManager(Collections.singletonList(customApiTokenAuthenticationProvider));
  }
}
