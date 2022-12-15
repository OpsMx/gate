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
 *
 */

package com.netflix.spinnaker.gate.security.basic;

import com.netflix.spinnaker.gate.config.AuthConfig;
import com.netflix.spinnaker.gate.config.RequestMatcherProvider;
import com.netflix.spinnaker.gate.security.SpinnakerAuthConfig;
import com.netflix.spinnaker.gate.services.OesAuthorizationService;
import com.netflix.spinnaker.gate.services.PermissionService;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.session.web.http.DefaultCookieSerializer;

@Slf4j
@Order(1007)
@Configuration
@SpinnakerAuthConfig
@ConditionalOnExpression("${security.admin.login.enabled:false}")
public class AdminBasicAuthConfig extends WebSecurityConfigurerAdapter {

  private final AuthConfig authConfig;

  private final BasicAuthProvider authProvider;

  @Autowired
  private AuthConfig.PermissionRevokingLogoutSuccessHandler permissionRevokingLogoutSuccessHandler;

  @Autowired DefaultCookieSerializer defaultCookieSerializer;

  @Value("${security.admin.user.roles:}")
  String roles;

  @Value("${security.admin.user.name:}")
  String name;

  @Value("${security.admin.user.password:}")
  String password;

  @Value(
      "${security.contentSecurityPolicy:\'object-src \'none\'; script-src \'unsafe-eval\' \'unsafe-inline\' https: http:;\'}")
  String contentSecurityPolicy;

  @Autowired private RequestMatcherProvider requestMatcherProvider;

  @Autowired PermissionService permissionService;

  @Autowired
  public AdminBasicAuthConfig(
      AuthConfig authConfig,
      PermissionService permissionService,
      OesAuthorizationService oesAuthorizationService) {
    this.authConfig = authConfig;
    this.authProvider = new BasicAuthProvider(permissionService, oesAuthorizationService);
  }

  @Autowired
  public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
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

    authProvider.setName(this.name);
    authProvider.setPassword(this.password);

    auth.authenticationProvider(authProvider);
  }

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    defaultCookieSerializer.setSameSite(null);
    //    http.formLogin()
    //      .and()
    //      .httpBasic()
    //      .authenticationEntryPoint(new LoginUrlAuthenticationEntryPoint("/login"));
    //    http.antMatcher("/admin/**").authorizeRequests().anyRequest().authenticated();
    //    http.antMatcher("/admin/**")
    //        .authorizeRequests(authroize -> authroize.anyRequest().authenticated())
    //        .formLogin()
    //        .loginPage("/login")
    //        .permitAll();

    http.requestMatcher(requestMatcherProvider.requestMatcher())
        .authorizeRequests()
        .antMatchers("/admin/error")
        .permitAll()
        .antMatchers("/admin/favicon.ico")
        .permitAll()
        .antMatchers("/admin/resources/**")
        .permitAll()
        .antMatchers("/admin/images/**")
        .permitAll()
        .antMatchers("/admin/js/**")
        .permitAll()
        .antMatchers("/admin/fonts/**")
        .permitAll()
        .antMatchers("/admin/css/**")
        .permitAll()
        .antMatchers("/admin/**/favicon.ico")
        .permitAll()
        .antMatchers(HttpMethod.OPTIONS, "/**")
        .permitAll()
        .antMatchers("/admin/auth/loggedOut")
        .permitAll()
        .antMatchers("/admin/auth/user")
        .permitAll()
        .antMatchers(HttpMethod.POST, "/admin/autopilot/registerCanary")
        .permitAll()
        //
        // .antMatchers(HttpMethod.GET,'/autopilot/api/v2/autopilot/canaries/{id}').permitAll()
        //        //
        // .antMatchers(HttpMethod.GET,'/autopilot/api/v1/autopilot/canaries/{id}').permitAll()
        //        //
        // .antMatchers(HttpMethod.POST,'/autopilot/api/v1/registerCanary').permitAll()
        //        //
        // .antMatchers(HttpMethod.POST,'/autopilot/api/v2/registerCanary').permitAll()
        //        //
        // .antMatchers(HttpMethod.POST,'/autopilot/api/v3/registerCanary').permitAll()
        //        //
        // .antMatchers(HttpMethod.POST,'/autopilot/api/v5/registerCanary').permitAll()
        //        //      .antMatchers(HttpMethod.GET,'/autopilot/canaries/{id}').permitAll()
        //        //      .antMatchers(HttpMethod.GET,'/autopilot/v5/canaries/{id}').permitAll()
        //        //
        // .antMatchers(HttpMethod.GET,'/autopilot/api/v5/external/template').permitAll()
        //        //
        // .antMatchers(HttpMethod.POST,'/autopilot/api/v5/external/template').permitAll()
        //        //
        //        //
        // .antMatchers(HttpMethod.POST,'/visibilityservice/v1/approvalGates/{id}/trigger').permitAll()
        //        //
        //        //
        // .antMatchers(HttpMethod.POST,'/visibilityservice/v2/approvalGates/{id}/trigger').permitAll()
        //        //
        //        //
        // .antMatchers(HttpMethod.POST,'/visibilityservice/v4/approvalGates/{id}/trigger').permitAll()
        //        //
        //        //
        // .antMatchers(HttpMethod.POST,'/visibilityservice/v5/approvalGates/{id}/trigger').permitAll()
        //        //
        //        //
        // .antMatchers(HttpMethod.GET,'/visibilityservice/v2/approvalGateInstances/{id}/status').permitAll()
        //        //
        //        //
        // .antMatchers(HttpMethod.GET,'/visibilityservice/v1/approvalGateInstances/{id}/status').permitAll()
        //        //
        //        //
        // .antMatchers(HttpMethod.PUT,'/visibilityservice/v1/approvalGateInstances/{id}/spinnakerReview').permitAll()
        //        //      .antMatchers(HttpMethod.POST,'/oes/echo').permitAll()
        //        //      .antMatchers(HttpMethod.POST,'/oes/echo/').permitAll()
        //        //
        // .antMatchers(HttpMethod.POST,'/auditservice/v1/echo/events/data').permitAll()
        //        //
        // .antMatchers(HttpMethod.POST,'/auditservice/v1/echo/events/data/').permitAll()
        //        //      .antMatchers(HttpMethod.POST,'/v1/data/**').permitAll()
        //        //      .antMatchers(HttpMethod.POST,'/v1/staticPolicy/eval').permitAll()
        //        //      .antMatchers(HttpMethod.POST,'/v1/staticPolicy/eval/').permitAll()
        //        //      .antMatchers(HttpMethod.GET,'/autopilot/mgmt/**').permitAll()
        //        //      .antMatchers(HttpMethod.POST,'/datasource/cache/save').permitAll()
        //        //      .antMatchers(HttpMethod.DELETE,'/datasource/cache/evict').permitAll()
        .antMatchers("/admin/plugins/deck/**")
        .permitAll()
        .antMatchers(HttpMethod.POST, "/admin/webhooks/**")
        .permitAll()
        .antMatchers(HttpMethod.POST, "/admin/notifications/callbacks/**")
        .permitAll()
        .antMatchers(HttpMethod.POST, "/admin/managed/notifications/callbacks/**")
        .permitAll()
        .antMatchers("/admin/health")
        .permitAll()
        .antMatchers("/admin/prometheus")
        .permitAll()
        .antMatchers("/admin/info")
        .permitAll()
        .antMatchers("/admin/metrics")
        .permitAll()
        .antMatchers("/admin/**")
        .authenticated();

    //    if (fiatSessionFilterEnabled) {
    //      Filter fiatSessionFilter = new FiatSessionFilter(
    //        fiatSessionFilterEnabled,
    //        fiatStatus,
    //        permissionEvaluator)
    //
    //      http.addFilterBefore(fiatSessionFilter, AnonymousAuthenticationFilter.class)
    //    }

    http.formLogin().loginPage("/login").permitAll();

    //            if (webhookDefaultAuthEnabled) {
    //              http.authorizeRequests().antMatchers(HttpMethod.POST,
    //     '/webhooks/**').authenticated()
    //            }

    // http.headers().contentSecurityPolicy(contentSecurityPolicy);

    http.logout()
        .logoutUrl("/admin/auth/logout")
        .logoutSuccessHandler(permissionRevokingLogoutSuccessHandler)
        .permitAll();
  }

  @Override
  public void configure(WebSecurity web) throws Exception {
    authConfig.configure(web);
  }
}
