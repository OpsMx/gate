/*
 * Copyright 2016 Netflix, Inc.
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

package com.netflix.spinnaker.gate.config

import com.netflix.spinnaker.fiat.shared.FiatClientConfigurationProperties
import com.netflix.spinnaker.fiat.shared.FiatPermissionEvaluator
import com.netflix.spinnaker.fiat.shared.FiatStatus
import com.netflix.spinnaker.gate.filters.FiatSessionFilter
import com.netflix.spinnaker.gate.services.PermissionService
import com.netflix.spinnaker.gate.services.ServiceAccountFilterConfigProps
import com.netflix.spinnaker.security.User
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.InitializingBean
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.security.SecurityProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.builders.WebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.core.Authentication
import org.springframework.security.web.authentication.AnonymousAuthenticationFilter
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler
import org.springframework.security.web.authentication.logout.SimpleUrlLogoutSuccessHandler
import org.springframework.stereotype.Component

import jakarta.servlet.Filter
import jakarta.servlet.ServletException
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse

@Slf4j
@Configuration
@EnableConfigurationProperties([ServiceConfiguration, ServiceAccountFilterConfigProps])
class AuthConfig {

  @Autowired
  PermissionRevokingLogoutSuccessHandler permissionRevokingLogoutSuccessHandler

  @Autowired
  SecurityProperties securityProperties

  @Autowired
  FiatClientConfigurationProperties configProps

  @Autowired
  FiatStatus fiatStatus

  @Autowired
  FiatPermissionEvaluator permissionEvaluator

  @Autowired
  RequestMatcherProvider requestMatcherProvider

  @Autowired
  private JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint

  @Autowired
  private JwtRequestFilter jwtRequestFilter;

  @Value('${security.debug:false}')
  boolean securityDebug

  @Value('${fiat.session-filter.enabled:true}')
  boolean fiatSessionFilterEnabled


  @Value('${ldap.enabled:false}')
  boolean ldapEnabled

  @Value('${security.webhooks.default-auth-enabled:false}')
  boolean webhookDefaultAuthEnabled

  @Value('${allowUnauthenticatedAccess.agentAPI:false}')
  boolean isAgentAPIUnauthenticatedAccessEnabled

  @Value('${allowUnauthenticatedAccess.webhooks:false}')
  boolean isSpinnakerWebhooksUnauthenticatedAccessEnabled

  @Value('${security.contentSecurityPolicy:object-src \'none\'; script-src \'unsafe-eval\' \'unsafe-inline\' https: http:;}')
  String contentSecurityPolicy

  void configure(HttpSecurity http) throws Exception {
    // @formatter:off
    if(isAgentAPIUnauthenticatedAccessEnabled && isSpinnakerWebhooksUnauthenticatedAccessEnabled){
      http.authorizeHttpRequests((authz) ->
        authz
         .requestMatchers("/error", "/favicon.ico", "/resources/**", "/images/**", "/js/**", "/fonts/**", "/css/**", "/**/favicon.ico", "/auth/user", "/health", "/aop-prometheus").permitAll()
        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
        .requestMatchers(PermissionRevokingLogoutSuccessHandler.LOGGED_OUT_URL).permitAll()
        .requestMatchers(HttpMethod.POST,'/autopilot/registerCanary').permitAll()
        .requestMatchers(HttpMethod.GET,'/autopilot/api/v2/autopilot/canaries/{id}').permitAll()
        .requestMatchers(HttpMethod.GET,'/autopilot/api/v1/autopilot/canaries/{id}').permitAll()
        .requestMatchers(HttpMethod.POST,'/autopilot/api/v1/registerCanary').permitAll()
        .requestMatchers(HttpMethod.POST,'/autopilot/api/v2/registerCanary').permitAll()
        .requestMatchers(HttpMethod.POST,'/autopilot/api/v3/registerCanary').permitAll()
        .requestMatchers(HttpMethod.GET,'/autopilot/canaries/{id}').permitAll()
        .requestMatchers(HttpMethod.POST,'/visibilityservice/v1/approvalGates/{id}/trigger').permitAll()
        .requestMatchers(HttpMethod.POST,'/visibilityservice/v2/approvalGates/{id}/trigger').permitAll()
        .requestMatchers(HttpMethod.POST,'/visibilityservice/v4/approvalGates/{id}/trigger').permitAll()
        .requestMatchers(HttpMethod.POST,'/visibilityservice/v5/approvalGates/{id}/trigger').permitAll()
        .requestMatchers(HttpMethod.GET,'/visibilityservice/v2/approvalGateInstances/{id}/status').permitAll()
        .requestMatchers(HttpMethod.GET,'/visibilityservice/v1/approvalGateInstances/{id}/status').permitAll()
        .requestMatchers(HttpMethod.PUT,'/visibilityservice/v1/approvalGateInstances/{id}/spinnakerReview').permitAll()
        .requestMatchers(HttpMethod.POST,'/oes/echo').permitAll()
        .requestMatchers(HttpMethod.POST,'/oes/echo/').permitAll()
        .requestMatchers(HttpMethod.POST,'/auditservice/v1/echo/events/data').permitAll()
        .requestMatchers(HttpMethod.POST,'/auditservice/v1/echo/events/data/').permitAll()
        .requestMatchers(HttpMethod.POST,'/v1/data/**').permitAll()
        .requestMatchers(HttpMethod.POST,'/v1/staticPolicy/eval').permitAll()
        .requestMatchers(HttpMethod.POST,'/v1/staticPolicy/eval/').permitAll()
        .requestMatchers(HttpMethod.GET,'/autopilot/mgmt/**').permitAll()
        .requestMatchers(HttpMethod.POST,'/datasource/cache/save').permitAll()
        .requestMatchers(HttpMethod.DELETE,'/datasource/cache/evict').permitAll()
        .requestMatchers('/plugins/deck/**').permitAll()
        .requestMatchers(HttpMethod.POST, '/webhooks/**').permitAll()
        .requestMatchers(HttpMethod.POST, '/notifications/callbacks/**').permitAll()
        .requestMatchers(HttpMethod.POST, '/managed/notifications/callbacks/**').permitAll()
        .requestMatchers(HttpMethod.GET, '/oes/accountsConfig/v2/agents/apple/automation').permitAll()
        .requestMatchers(HttpMethod.POST, '/oes/accountsConfig/v1/agents/apple/automation').permitAll()
        .requestMatchers(HttpMethod.GET, '/oes/accountsConfig/v1/agents/{agentName}/manifest/apple/automation').permitAll()
        .requestMatchers(HttpMethod.GET, '/oes/accountsConfig/v2/spinnaker/cloudProviderAccount/apple/automation').permitAll()
        .requestMatchers(HttpMethod.GET, '/oes/accountsConfig/v2/spinnaker/cloudProviderAccount/{agentName}/{accountName}/apple/automation').permitAll()
        .requestMatchers(HttpMethod.POST, '/oes/accountsConfig/v2/spinnaker/cloudProviderAccount/apple/automation').permitAll()
        .requestMatchers(HttpMethod.GET, '/oes/accountsConfig/v3/spinnaker/apple/automation').permitAll()
        .requestMatchers(HttpMethod.GET, '/dashboardservice/v4/getAllDatasources/apple/automation').permitAll()
        .requestMatchers(HttpMethod.GET, '/dashboardservice/v5/agents/{agentName}/accounts/{accountName}/accountType/{accountType}/apple/automation').permitAll()
        .requestMatchers(HttpMethod.POST, '/dashboardservice/v4/datasource/apple/automation').permitAll()
        .requestMatchers(HttpMethod.GET, '/platformservice/v6/applications/{applicationname}/pipeline/{pipelineName}/reference/{ref}/gates/{gatesName}').permitAll()
        .requestMatchers(HttpMethod.GET, '/platformservice/v1/applications/{applicationName}/pipelines/{pipelineName}').permitAll()
        .requestMatchers(HttpMethod.POST, '/dashboardservice/v4/pipelines/{pipelineId}/gates').permitAll()
        .requestMatchers(HttpMethod.PUT, '/platformservice/v6/usergroups/permissions/users/{username}/resources/{resourceId}').permitAll()
        .requestMatchers(HttpMethod.PUT, '/visibilityservice/v4/approvalGates/{id}/connector').permitAll()
        .requestMatchers(HttpMethod.PUT, '/dashboardservice/v4/pipelines/{pipelineId}/gates/{gateId}').permitAll()
        .requestMatchers(HttpMethod.POST, '/ssdservice/v1/spinnakerevents').permitAll()
        .requestMatchers(HttpMethod.POST, '/ssdservice/v1/spinnakerevents/').permitAll()
        .requestMatchers('/prometheus').permitAll()
        .requestMatchers('/info').permitAll()
        .requestMatchers('/metrics').permitAll()
        .requestMatchers('/**').authenticated())
    }else if(isAgentAPIUnauthenticatedAccessEnabled){
      http
      http.authorizeHttpRequests((authz) ->
        authz
        .requestMatchers("/error").permitAll()
        .requestMatchers('/favicon.ico').permitAll()
        .requestMatchers("/resources/**").permitAll()
        .requestMatchers("/images/**").permitAll()
        .requestMatchers("/js/**").permitAll()
        .requestMatchers("/fonts/**").permitAll()
        .requestMatchers("/css/**").permitAll()
        .requestMatchers('/**/favicon.ico').permitAll()
        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
        .requestMatchers(PermissionRevokingLogoutSuccessHandler.LOGGED_OUT_URL).permitAll()
        .requestMatchers('/auth/user').permitAll()
        .requestMatchers(HttpMethod.POST,'/autopilot/registerCanary').permitAll()
        .requestMatchers(HttpMethod.GET,'/autopilot/api/v2/autopilot/canaries/{id}').permitAll()
        .requestMatchers(HttpMethod.GET,'/autopilot/api/v1/autopilot/canaries/{id}').permitAll()
        .requestMatchers(HttpMethod.POST,'/autopilot/api/v1/registerCanary').permitAll()
        .requestMatchers(HttpMethod.POST,'/autopilot/api/v2/registerCanary').permitAll()
        .requestMatchers(HttpMethod.POST,'/autopilot/api/v3/registerCanary').permitAll()
        .requestMatchers(HttpMethod.GET,'/autopilot/canaries/{id}').permitAll()
        .requestMatchers(HttpMethod.POST,'/visibilityservice/v1/approvalGates/{id}/trigger').permitAll()
        .requestMatchers(HttpMethod.POST,'/visibilityservice/v2/approvalGates/{id}/trigger').permitAll()
        .requestMatchers(HttpMethod.POST,'/visibilityservice/v4/approvalGates/{id}/trigger').permitAll()
        .requestMatchers(HttpMethod.POST,'/visibilityservice/v5/approvalGates/{id}/trigger').permitAll()
        .requestMatchers(HttpMethod.GET,'/visibilityservice/v2/approvalGateInstances/{id}/status').permitAll()
        .requestMatchers(HttpMethod.GET,'/visibilityservice/v1/approvalGateInstances/{id}/status').permitAll()
        .requestMatchers(HttpMethod.PUT,'/visibilityservice/v1/approvalGateInstances/{id}/spinnakerReview').permitAll()
        .requestMatchers(HttpMethod.POST,'/oes/echo').permitAll()
        .requestMatchers(HttpMethod.POST,'/oes/echo/').permitAll()
        .requestMatchers(HttpMethod.POST,'/auditservice/v1/echo/events/data').permitAll()
        .requestMatchers(HttpMethod.POST,'/auditservice/v1/echo/events/data/').permitAll()
        .requestMatchers(HttpMethod.POST,'/v1/data/**').permitAll()
        .requestMatchers(HttpMethod.POST,'/v1/staticPolicy/eval').permitAll()
        .requestMatchers(HttpMethod.POST,'/v1/staticPolicy/eval/').permitAll()
        .requestMatchers(HttpMethod.GET,'/autopilot/mgmt/**').permitAll()
        .requestMatchers(HttpMethod.POST,'/datasource/cache/save').permitAll()
        .requestMatchers(HttpMethod.DELETE,'/datasource/cache/evict').permitAll()
        .requestMatchers('/plugins/deck/**').permitAll()
        .requestMatchers(HttpMethod.POST, '/notifications/callbacks/**').permitAll()
        .requestMatchers(HttpMethod.POST, '/managed/notifications/callbacks/**').permitAll()
        .requestMatchers(HttpMethod.GET, '/oes/accountsConfig/v2/agents/apple/automation').permitAll()
        .requestMatchers(HttpMethod.POST, '/oes/accountsConfig/v1/agents/apple/automation').permitAll()
        .requestMatchers(HttpMethod.GET, '/oes/accountsConfig/v1/agents/{agentName}/manifest/apple/automation').permitAll()
        .requestMatchers(HttpMethod.GET, '/oes/accountsConfig/v2/spinnaker/cloudProviderAccount/apple/automation').permitAll()
        .requestMatchers(HttpMethod.GET, '/oes/accountsConfig/v2/spinnaker/cloudProviderAccount/{agentName}/{accountName}/apple/automation').permitAll()
        .requestMatchers(HttpMethod.POST, '/oes/accountsConfig/v2/spinnaker/cloudProviderAccount/apple/automation').permitAll()
        .requestMatchers(HttpMethod.GET, '/oes/accountsConfig/v3/spinnaker/apple/automation').permitAll()
        .requestMatchers(HttpMethod.GET, '/dashboardservice/v4/getAllDatasources/apple/automation').permitAll()
        .requestMatchers(HttpMethod.GET, '/dashboardservice/v5/agents/{agentName}/accounts/{accountName}/accountType/{accountType}/apple/automation').permitAll()
        .requestMatchers(HttpMethod.POST, '/dashboardservice/v4/datasource/apple/automation').permitAll()
        .requestMatchers(HttpMethod.GET, '/platformservice/v6/applications/{applicationname}/pipeline/{pipelineName}/reference/{ref}/gates/{gatesName}').permitAll()
        .requestMatchers(HttpMethod.GET, '/platformservice/v1/applications/{applicationName}/pipelines/{pipelineName}').permitAll()
        .requestMatchers(HttpMethod.POST, '/dashboardservice/v4/pipelines/{pipelineId}/gates').permitAll()
        .requestMatchers(HttpMethod.PUT, '/platformservice/v6/usergroups/permissions/users/{username}/resources/{resourceId}').permitAll()
        .requestMatchers(HttpMethod.PUT, '/visibilityservice/v4/approvalGates/{id}/connector').permitAll()
        .requestMatchers(HttpMethod.PUT, '/dashboardservice/v4/pipelines/{pipelineId}/gates/{gateId}').permitAll()
        .requestMatchers(HttpMethod.POST, '/ssdservice/v1/spinnakerevents').permitAll()
        .requestMatchers(HttpMethod.POST, '/ssdservice/v1/spinnakerevents/').permitAll()
        .requestMatchers('/health').permitAll()
        .requestMatchers('/prometheus').permitAll()
        .requestMatchers('/info').permitAll()
        .requestMatchers('/metrics').permitAll()
        .requestMatchers('/**').authenticated())
    }else if(isSpinnakerWebhooksUnauthenticatedAccessEnabled){
      http.authorizeHttpRequests((authz) ->
        authz
        .requestMatchers("/error").permitAll()
        .requestMatchers('/favicon.ico').permitAll()
        .requestMatchers("/resources/**").permitAll()
        .requestMatchers("/images/**").permitAll()
        .requestMatchers("/js/**").permitAll()
        .requestMatchers("/fonts/**").permitAll()
        .requestMatchers("/css/**").permitAll()
        .requestMatchers('/**/favicon.ico').permitAll()
        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
        .requestMatchers(PermissionRevokingLogoutSuccessHandler.LOGGED_OUT_URL).permitAll()
        .requestMatchers('/auth/user').permitAll()
        .requestMatchers(HttpMethod.POST,'/autopilot/registerCanary').permitAll()
        .requestMatchers(HttpMethod.GET,'/autopilot/api/v2/autopilot/canaries/{id}').permitAll()
        .requestMatchers(HttpMethod.GET,'/autopilot/api/v1/autopilot/canaries/{id}').permitAll()
        .requestMatchers(HttpMethod.POST,'/autopilot/api/v1/registerCanary').permitAll()
        .requestMatchers(HttpMethod.POST,'/autopilot/api/v2/registerCanary').permitAll()
        .requestMatchers(HttpMethod.POST,'/autopilot/api/v3/registerCanary').permitAll()
        .requestMatchers(HttpMethod.GET,'/autopilot/canaries/{id}').permitAll()
        .requestMatchers(HttpMethod.POST,'/visibilityservice/v1/approvalGates/{id}/trigger').permitAll()
        .requestMatchers(HttpMethod.POST,'/visibilityservice/v2/approvalGates/{id}/trigger').permitAll()
        .requestMatchers(HttpMethod.POST,'/visibilityservice/v4/approvalGates/{id}/trigger').permitAll()
        .requestMatchers(HttpMethod.POST,'/visibilityservice/v5/approvalGates/{id}/trigger').permitAll()
        .requestMatchers(HttpMethod.GET,'/visibilityservice/v2/approvalGateInstances/{id}/status').permitAll()
        .requestMatchers(HttpMethod.GET,'/visibilityservice/v1/approvalGateInstances/{id}/status').permitAll()
        .requestMatchers(HttpMethod.PUT,'/visibilityservice/v1/approvalGateInstances/{id}/spinnakerReview').permitAll()
        .requestMatchers(HttpMethod.GET, '/platformservice/v6/applications/{applicationname}/pipeline/{pipelineName}/reference/{ref}/gates/{gatesName}').permitAll()
        .requestMatchers(HttpMethod.POST,'/oes/echo').permitAll()
        .requestMatchers(HttpMethod.POST,'/oes/echo/').permitAll()
        .requestMatchers(HttpMethod.POST,'/auditservice/v1/echo/events/data').permitAll()
        .requestMatchers(HttpMethod.POST,'/auditservice/v1/echo/events/data/').permitAll()
        .requestMatchers(HttpMethod.POST,'/v1/data/**').permitAll()
        .requestMatchers(HttpMethod.POST,'/v1/staticPolicy/eval').permitAll()
        .requestMatchers(HttpMethod.POST,'/v1/staticPolicy/eval/').permitAll()
        .requestMatchers(HttpMethod.GET,'/autopilot/mgmt/**').permitAll()
        .requestMatchers(HttpMethod.POST,'/datasource/cache/save').permitAll()
        .requestMatchers(HttpMethod.DELETE,'/datasource/cache/evict').permitAll()
        .requestMatchers('/plugins/deck/**').permitAll()
        .requestMatchers(HttpMethod.POST, '/webhooks/**').permitAll()
        .requestMatchers(HttpMethod.POST, '/notifications/callbacks/**').permitAll()
        .requestMatchers(HttpMethod.POST, '/managed/notifications/callbacks/**').permitAll()
        .requestMatchers(HttpMethod.GET, '/platformservice/v1/applications/{applicationName}/pipelines/{pipelineName}').permitAll()
        .requestMatchers(HttpMethod.POST, '/dashboardservice/v4/pipelines/{pipelineId}/gates').permitAll()
        .requestMatchers(HttpMethod.PUT, '/platformservice/v6/usergroups/permissions/users/{username}/resources/{resourceId}').permitAll()
        .requestMatchers(HttpMethod.PUT, '/visibilityservice/v4/approvalGates/{id}/connector').permitAll()
        .requestMatchers(HttpMethod.PUT, '/dashboardservice/v4/pipelines/{pipelineId}/gates/{gateId}').permitAll()
        .requestMatchers(HttpMethod.POST, '/ssdservice/v1/spinnakerevents').permitAll()
        .requestMatchers(HttpMethod.POST, '/ssdservice/v1/spinnakerevents/').permitAll()
        .requestMatchers('/health').permitAll()
        .requestMatchers('/prometheus').permitAll()
        .requestMatchers('/info').permitAll()
        .requestMatchers('/metrics').permitAll()
        .requestMatchers('/**').authenticated())
    }else{
      http.authorizeHttpRequests((authz) ->
        authz
        .requestMatchers("/error").permitAll()
        .requestMatchers('/favicon.ico').permitAll()
        .requestMatchers("/resources/**").permitAll()
        .requestMatchers("/images/**").permitAll()
        .requestMatchers("/js/**").permitAll()
        .requestMatchers("/fonts/**").permitAll()
        .requestMatchers("/css/**").permitAll()
        .requestMatchers('/**/favicon.ico').permitAll()
        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
        .requestMatchers(PermissionRevokingLogoutSuccessHandler.LOGGED_OUT_URL).permitAll()
        .requestMatchers('/auth/user').permitAll()
        .requestMatchers(HttpMethod.POST,'/autopilot/registerCanary').permitAll()
        .requestMatchers(HttpMethod.GET,'/autopilot/api/v2/autopilot/canaries/{id}').permitAll()
        .requestMatchers(HttpMethod.GET,'/autopilot/api/v1/autopilot/canaries/{id}').permitAll()
        .requestMatchers(HttpMethod.POST,'/autopilot/api/v1/registerCanary').permitAll()
        .requestMatchers(HttpMethod.POST,'/autopilot/api/v2/registerCanary').permitAll()
        .requestMatchers(HttpMethod.POST,'/autopilot/api/v3/registerCanary').permitAll()
        .requestMatchers(HttpMethod.GET,'/autopilot/canaries/{id}').permitAll()
        .requestMatchers(HttpMethod.POST,'/visibilityservice/v1/approvalGates/{id}/trigger').permitAll()
        .requestMatchers(HttpMethod.POST,'/visibilityservice/v2/approvalGates/{id}/trigger').permitAll()
        .requestMatchers(HttpMethod.POST,'/visibilityservice/v4/approvalGates/{id}/trigger').permitAll()
        .requestMatchers(HttpMethod.POST,'/visibilityservice/v5/approvalGates/{id}/trigger').permitAll()
        .requestMatchers(HttpMethod.GET,'/visibilityservice/v2/approvalGateInstances/{id}/status').permitAll()
        .requestMatchers(HttpMethod.GET,'/visibilityservice/v1/approvalGateInstances/{id}/status').permitAll()
        .requestMatchers(HttpMethod.PUT,'/visibilityservice/v1/approvalGateInstances/{id}/spinnakerReview').permitAll()
        .requestMatchers(HttpMethod.GET, '/platformservice/v6/applications/{applicationname}/pipeline/{pipelineName}/reference/{ref}/gates/{gatesName}').permitAll()
        .requestMatchers(HttpMethod.POST,'/oes/echo').permitAll()
        .requestMatchers(HttpMethod.POST,'/oes/echo/').permitAll()
        .requestMatchers(HttpMethod.POST,'/auditservice/v1/echo/events/data').permitAll()
        .requestMatchers(HttpMethod.POST,'/auditservice/v1/echo/events/data/').permitAll()
        .requestMatchers(HttpMethod.POST,'/v1/data/**').permitAll()
        .requestMatchers(HttpMethod.POST,'/v1/staticPolicy/eval').permitAll()
        .requestMatchers(HttpMethod.POST,'/v1/staticPolicy/eval/').permitAll()
        .requestMatchers(HttpMethod.GET,'/autopilot/mgmt/**').permitAll()
        .requestMatchers(HttpMethod.POST,'/datasource/cache/save').permitAll()
        .requestMatchers(HttpMethod.DELETE,'/datasource/cache/evict').permitAll()
        .requestMatchers('/plugins/deck/**').permitAll()
        .requestMatchers(HttpMethod.POST, '/notifications/callbacks/**').permitAll()
        .requestMatchers(HttpMethod.POST, '/managed/notifications/callbacks/**').permitAll()
        .requestMatchers(HttpMethod.GET, '/platformservice/v1/applications/{applicationName}/pipelines/{pipelineName}').permitAll()
        .requestMatchers(HttpMethod.POST, '/dashboardservice/v4/pipelines/{pipelineId}/gates').permitAll()
        .requestMatchers(HttpMethod.PUT, '/platformservice/v6/usergroups/permissions/users/{username}/resources/{resourceId}').permitAll()
        .requestMatchers(HttpMethod.PUT, '/visibilityservice/v4/approvalGates/{id}/connector').permitAll()
        .requestMatchers(HttpMethod.PUT, '/dashboardservice/v4/pipelines/{pipelineId}/gates/{gateId}').permitAll()
        .requestMatchers(HttpMethod.POST, '/ssdservice/v1/spinnakerevents').permitAll()
        .requestMatchers(HttpMethod.POST, '/ssdservice/v1/spinnakerevents/').permitAll()
        .requestMatchers('/health').permitAll()
        .requestMatchers('/prometheus').permitAll()
        .requestMatchers('/info').permitAll()
        .requestMatchers('/metrics').permitAll()
        .requestMatchers('/**').authenticated())
    }

    if (fiatSessionFilterEnabled) {
      Filter fiatSessionFilter = new FiatSessionFilter(
        fiatSessionFilterEnabled,
        fiatStatus,
        permissionEvaluator)

      http.addFilterBefore(fiatSessionFilter, AnonymousAuthenticationFilter.class)
    }


    if (ldapEnabled) {
      http.formLogin().loginPage("/login").permitAll()
    }

    if (webhookDefaultAuthEnabled) {
      http.authorizeHttpRequests(
        (requests) ->
          requests.requestMatchers(HttpMethod.POST, "/webhooks/**").authenticated());
    }

    http.headers().contentSecurityPolicy(contentSecurityPolicy)

    http.logout()
        .logoutUrl("/auth/logout")
        .logoutSuccessHandler(permissionRevokingLogoutSuccessHandler)
        .permitAll()
        .and()
      .csrf()
        .disable()
    // @formatter:on
  }

   void jwtconfigure(HttpSecurity http) throws Exception {
     if (isAgentAPIUnauthenticatedAccessEnabled && isSpinnakerWebhooksUnauthenticatedAccessEnabled){
       http
         .csrf()
         .disable()
         .cors()
         .disable()
         .exceptionHandling()
         .authenticationEntryPoint(jwtAuthenticationEntryPoint).and().sessionManagement()
         .sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
         .authorizeHttpRequests((authz) ->
           authz
         .requestMatchers("/auth/login").permitAll()
         .requestMatchers(HttpMethod.POST,'/autopilot/registerCanary').permitAll()
         .requestMatchers(HttpMethod.POST,'/autopilot/api/v1/registerCanary').permitAll()
         .requestMatchers(HttpMethod.POST,'/autopilot/api/v2/registerCanary').permitAll()
         .requestMatchers(HttpMethod.POST,'/autopilot/api/v3/registerCanary').permitAll()
         .requestMatchers(HttpMethod.GET,'/autopilot/canaries/{id}').permitAll()
         .requestMatchers(HttpMethod.GET,'/autopilot/api/v2/autopilot/canaries/{id}').permitAll()
         .requestMatchers(HttpMethod.GET,'/autopilot/api/v1/autopilot/canaries/{id}').permitAll()
         .requestMatchers(HttpMethod.POST,'/visibilityservice/v1/approvalGates/{id}/trigger').permitAll()
         .requestMatchers(HttpMethod.POST,'/visibilityservice/v2/approvalGates/{id}/trigger').permitAll()
         .requestMatchers(HttpMethod.POST,'/visibilityservice/v4/approvalGates/{id}/trigger').permitAll()
         .requestMatchers(HttpMethod.POST,'/visibilityservice/v5/approvalGates/{id}/trigger').permitAll()
         .requestMatchers(HttpMethod.GET,'/visibilityservice/v2/approvalGateInstances/{id}/status').permitAll()
         .requestMatchers(HttpMethod.GET,'/visibilityservice/v1/approvalGateInstances/{id}/status').permitAll()
         .requestMatchers(HttpMethod.PUT,'/visibilityservice/v1/approvalGateInstances/{id}/spinnakerReview').permitAll()
         .requestMatchers(HttpMethod.POST,'/oes/echo').permitAll()
         .requestMatchers(HttpMethod.POST,'/oes/echo/').permitAll()
         .requestMatchers(HttpMethod.POST,'/auditservice/v1/echo/events/data').permitAll()
         .requestMatchers(HttpMethod.POST,'/auditservice/v1/echo/events/data/').permitAll()
         .requestMatchers(HttpMethod.POST,'/v1/data/**').permitAll()
         .requestMatchers(HttpMethod.POST,'/v1/staticPolicy/eval').permitAll()
         .requestMatchers(HttpMethod.POST,'/v1/staticPolicy/eval/').permitAll()
         .requestMatchers(HttpMethod.GET,'/autopilot/mgmt/**').permitAll()
         .requestMatchers(HttpMethod.POST,'/datasource/cache/save').permitAll()
         .requestMatchers(HttpMethod.DELETE,'/datasource/cache/evict').permitAll()
         .requestMatchers('/**/favicon.ico').permitAll()
         .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
         .requestMatchers(PermissionRevokingLogoutSuccessHandler.LOGGED_OUT_URL).permitAll()
         .requestMatchers('/plugins/deck/**').permitAll()
         .requestMatchers(HttpMethod.POST, '/webhooks/**').permitAll()
         .requestMatchers(HttpMethod.POST, '/notifications/callbacks/**').permitAll()
         .requestMatchers(HttpMethod.GET, '/oes/accountsConfig/v2/agents/apple/automation').permitAll()
         .requestMatchers(HttpMethod.POST, '/oes/accountsConfig/v1/agents/apple/automation').permitAll()
         .requestMatchers(HttpMethod.GET, '/oes/accountsConfig/v1/agents/{agentName}/manifest/apple/automation').permitAll()
         .requestMatchers(HttpMethod.GET, '/oes/accountsConfig/v2/spinnaker/cloudProviderAccount/apple/automation').permitAll()
         .requestMatchers(HttpMethod.GET, '/oes/accountsConfig/v2/spinnaker/cloudProviderAccount/{agentName}/{accountName}/apple/automation').permitAll()
         .requestMatchers(HttpMethod.POST, '/oes/accountsConfig/v2/spinnaker/cloudProviderAccount/apple/automation').permitAll()
         .requestMatchers(HttpMethod.GET, '/oes/accountsConfig/v3/spinnaker/apple/automation').permitAll()
         .requestMatchers(HttpMethod.GET, '/dashboardservice/v4/getAllDatasources/apple/automation').permitAll()
         .requestMatchers(HttpMethod.GET, '/dashboardservice/v5/agents/{agentName}/accounts/{accountName}/accountType/{accountType}/apple/automation').permitAll()
         .requestMatchers(HttpMethod.POST, '/dashboardservice/v4/datasource/apple/automation').permitAll()
         .requestMatchers(HttpMethod.GET, '/platformservice/v6/applications/{applicationname}/pipeline/{pipelineName}/reference/{ref}/gates/{gatesName}').permitAll()
         .requestMatchers(HttpMethod.GET, '/platformservice/v1/applications/{applicationName}/pipelines/{pipelineName}').permitAll()
         .requestMatchers(HttpMethod.POST, '/dashboardservice/v4/pipelines/{pipelineId}/gates').permitAll()
         .requestMatchers(HttpMethod.PUT, '/platformservice/v6/usergroups/permissions/users/{username}/resources/{resourceId}').permitAll()
         .requestMatchers(HttpMethod.PUT, '/visibilityservice/v4/approvalGates/{id}/connector').permitAll()
         .requestMatchers(HttpMethod.PUT, '/dashboardservice/v4/pipelines/{pipelineId}/gates/{gateId}').permitAll()
         .requestMatchers(HttpMethod.POST, '/ssdservice/v1/spinnakerevents').permitAll()
         .requestMatchers(HttpMethod.POST, '/ssdservice/v1/spinnakerevents/').permitAll()
         .requestMatchers('/health').permitAll()
         .requestMatchers('/prometheus').permitAll()
         .requestMatchers('/info').permitAll()
         .requestMatchers('/metrics').permitAll()
         .anyRequest().authenticated())
       http.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);
     }else if(isAgentAPIUnauthenticatedAccessEnabled){
       http
         .csrf()
         .disable()
         .cors()
         .disable()
         .exceptionHandling()
         .authenticationEntryPoint(jwtAuthenticationEntryPoint).and().sessionManagement()
         .sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
         .authorizeHttpRequests((authz) ->
         authz
         .requestMatchers("/auth/login").permitAll()
         .requestMatchers(HttpMethod.POST,'/autopilot/registerCanary').permitAll()
         .requestMatchers(HttpMethod.POST,'/autopilot/api/v1/registerCanary').permitAll()
         .requestMatchers(HttpMethod.POST,'/autopilot/api/v2/registerCanary').permitAll()
         .requestMatchers(HttpMethod.POST,'/autopilot/api/v3/registerCanary').permitAll()
         .requestMatchers(HttpMethod.GET,'/autopilot/canaries/{id}').permitAll()
         .requestMatchers(HttpMethod.GET,'/autopilot/api/v2/autopilot/canaries/{id}').permitAll()
         .requestMatchers(HttpMethod.GET,'/autopilot/api/v1/autopilot/canaries/{id}').permitAll()
         .requestMatchers(HttpMethod.POST,'/visibilityservice/v1/approvalGates/{id}/trigger').permitAll()
         .requestMatchers(HttpMethod.POST,'/visibilityservice/v2/approvalGates/{id}/trigger').permitAll()
         .requestMatchers(HttpMethod.POST,'/visibilityservice/v4/approvalGates/{id}/trigger').permitAll()
         .requestMatchers(HttpMethod.POST,'/visibilityservice/v5/approvalGates/{id}/trigger').permitAll()
         .requestMatchers(HttpMethod.GET,'/visibilityservice/v2/approvalGateInstances/{id}/status').permitAll()
         .requestMatchers(HttpMethod.GET,'/visibilityservice/v1/approvalGateInstances/{id}/status').permitAll()
         .requestMatchers(HttpMethod.PUT,'/visibilityservice/v1/approvalGateInstances/{id}/spinnakerReview').permitAll()
         .requestMatchers(HttpMethod.POST,'/oes/echo').permitAll()
         .requestMatchers(HttpMethod.POST,'/oes/echo/').permitAll()
         .requestMatchers(HttpMethod.POST,'/auditservice/v1/echo/events/data').permitAll()
         .requestMatchers(HttpMethod.POST,'/auditservice/v1/echo/events/data/').permitAll()
         .requestMatchers(HttpMethod.POST,'/v1/data/**').permitAll()
         .requestMatchers(HttpMethod.POST,'/v1/staticPolicy/eval').permitAll()
         .requestMatchers(HttpMethod.POST,'/v1/staticPolicy/eval/').permitAll()
         .requestMatchers(HttpMethod.GET,'/autopilot/mgmt/**').permitAll()
         .requestMatchers(HttpMethod.POST,'/datasource/cache/save').permitAll()
         .requestMatchers(HttpMethod.DELETE,'/datasource/cache/evict').permitAll()
         .requestMatchers('/**/favicon.ico').permitAll()
         .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
         .requestMatchers(PermissionRevokingLogoutSuccessHandler.LOGGED_OUT_URL).permitAll()
         .requestMatchers('/plugins/deck/**').permitAll()
         .requestMatchers(HttpMethod.POST, '/notifications/callbacks/**').permitAll()
         .requestMatchers(HttpMethod.GET, '/oes/accountsConfig/v2/agents/apple/automation').permitAll()
         .requestMatchers(HttpMethod.POST, '/oes/accountsConfig/v1/agents/apple/automation').permitAll()
         .requestMatchers(HttpMethod.GET, '/oes/accountsConfig/v1/agents/{agentName}/manifest/apple/automation').permitAll()
         .requestMatchers(HttpMethod.GET, '/oes/accountsConfig/v2/spinnaker/cloudProviderAccount/apple/automation').permitAll()
         .requestMatchers(HttpMethod.GET, '/oes/accountsConfig/v2/spinnaker/cloudProviderAccount/{agentName}/{accountName}/apple/automation').permitAll()
         .requestMatchers(HttpMethod.POST, '/oes/accountsConfig/v2/spinnaker/cloudProviderAccount/apple/automation').permitAll()
         .requestMatchers(HttpMethod.GET, '/oes/accountsConfig/v3/spinnaker/apple/automation').permitAll()
         .requestMatchers(HttpMethod.GET, '/dashboardservice/v4/getAllDatasources/apple/automation').permitAll()
         .requestMatchers(HttpMethod.GET, '/dashboardservice/v5/agents/{agentName}/accounts/{accountName}/accountType/{accountType}/apple/automation').permitAll()
         .requestMatchers(HttpMethod.POST, '/dashboardservice/v4/datasource/apple/automation').permitAll()
         .requestMatchers(HttpMethod.GET, '/platformservice/v6/applications/{applicationname}/pipeline/{pipelineName}/reference/{ref}/gates/{gatesName}').permitAll()
         .requestMatchers(HttpMethod.GET, '/platformservice/v1/applications/{applicationName}/pipelines/{pipelineName}').permitAll()
         .requestMatchers(HttpMethod.POST, '/dashboardservice/v4/pipelines/{pipelineId}/gates').permitAll()
         .requestMatchers(HttpMethod.PUT, '/platformservice/v6/usergroups/permissions/users/{username}/resources/{resourceId}').permitAll()
         .requestMatchers(HttpMethod.PUT, '/visibilityservice/v4/approvalGates/{id}/connector').permitAll()
         .requestMatchers(HttpMethod.PUT, '/dashboardservice/v4/pipelines/{pipelineId}/gates/{gateId}').permitAll()
         .requestMatchers(HttpMethod.POST, '/ssdservice/v1/spinnakerevents').permitAll()
         .requestMatchers(HttpMethod.POST, '/ssdservice/v1/spinnakerevents/').permitAll()
         .requestMatchers('/health').permitAll()
         .requestMatchers('/prometheus').permitAll()
         .requestMatchers('/info').permitAll()
         .requestMatchers('/metrics').permitAll()
         .anyRequest().authenticated())
       http.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);
     }else if(isSpinnakerWebhooksUnauthenticatedAccessEnabled){
       http
         .csrf()
         .disable()
         .cors()
         .disable()
         .exceptionHandling()
         .authenticationEntryPoint(jwtAuthenticationEntryPoint).and().sessionManagement()
         .sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
         .authorizeHttpRequests((authz) ->
           authz
         .requestMatchers("/auth/login").permitAll()
         .requestMatchers(HttpMethod.POST,'/autopilot/registerCanary').permitAll()
         .requestMatchers(HttpMethod.POST,'/autopilot/api/v1/registerCanary').permitAll()
         .requestMatchers(HttpMethod.POST,'/autopilot/api/v2/registerCanary').permitAll()
         .requestMatchers(HttpMethod.POST,'/autopilot/api/v3/registerCanary').permitAll()
         .requestMatchers(HttpMethod.GET,'/autopilot/canaries/{id}').permitAll()
         .requestMatchers(HttpMethod.GET,'/autopilot/api/v2/autopilot/canaries/{id}').permitAll()
         .requestMatchers(HttpMethod.GET,'/autopilot/api/v1/autopilot/canaries/{id}').permitAll()
         .requestMatchers(HttpMethod.POST,'/visibilityservice/v1/approvalGates/{id}/trigger').permitAll()
         .requestMatchers(HttpMethod.POST,'/visibilityservice/v2/approvalGates/{id}/trigger').permitAll()
         .requestMatchers(HttpMethod.POST,'/visibilityservice/v4/approvalGates/{id}/trigger').permitAll()
         .requestMatchers(HttpMethod.POST,'/visibilityservice/v5/approvalGates/{id}/trigger').permitAll()
         .requestMatchers(HttpMethod.GET,'/visibilityservice/v2/approvalGateInstances/{id}/status').permitAll()
         .requestMatchers(HttpMethod.GET,'/visibilityservice/v1/approvalGateInstances/{id}/status').permitAll()
         .requestMatchers(HttpMethod.PUT,'/visibilityservice/v1/approvalGateInstances/{id}/spinnakerReview').permitAll()
         .requestMatchers(HttpMethod.GET, '/platformservice/v6/applications/{applicationname}/pipeline/{pipelineName}/reference/{ref}/gates/{gatesName}').permitAll()
         .requestMatchers(HttpMethod.GET, '/platformservice/v1/applications/{applicationName}/pipelines/{pipelineName}').permitAll()
         .requestMatchers(HttpMethod.POST, '/dashboardservice/v4/pipelines/{pipelineId}/gates').permitAll()
         .requestMatchers(HttpMethod.PUT, '/platformservice/v6/usergroups/permissions/users/{username}/resources/{resourceId}').permitAll()
         .requestMatchers(HttpMethod.PUT, '/visibilityservice/v4/approvalGates/{id}/connector').permitAll()
         .requestMatchers(HttpMethod.PUT, '/dashboardservice/v4/pipelines/{pipelineId}/gates/{gateId}').permitAll()
         .requestMatchers(HttpMethod.POST,'/oes/echo').permitAll()
         .requestMatchers(HttpMethod.POST,'/oes/echo/').permitAll()
         .requestMatchers(HttpMethod.POST,'/auditservice/v1/echo/events/data').permitAll()
         .requestMatchers(HttpMethod.POST,'/auditservice/v1/echo/events/data/').permitAll()
         .requestMatchers(HttpMethod.POST,'/v1/data/**').permitAll()
         .requestMatchers(HttpMethod.POST,'/v1/staticPolicy/eval').permitAll()
         .requestMatchers(HttpMethod.POST,'/v1/staticPolicy/eval/').permitAll()
         .requestMatchers(HttpMethod.GET,'/autopilot/mgmt/**').permitAll()
         .requestMatchers(HttpMethod.POST,'/datasource/cache/save').permitAll()
         .requestMatchers(HttpMethod.DELETE,'/datasource/cache/evict').permitAll()
         .requestMatchers(HttpMethod.POST, '/ssdservice/v1/spinnakerevents').permitAll()
         .requestMatchers(HttpMethod.POST, '/ssdservice/v1/spinnakerevents/').permitAll()
         .requestMatchers('/**/favicon.ico').permitAll()
         .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
         .requestMatchers(PermissionRevokingLogoutSuccessHandler.LOGGED_OUT_URL).permitAll()
         .requestMatchers('/plugins/deck/**').permitAll()
         .requestMatchers(HttpMethod.POST, '/webhooks/**').permitAll()
         .requestMatchers(HttpMethod.POST, '/notifications/callbacks/**').permitAll()
         .requestMatchers('/health').permitAll()
         .requestMatchers('/prometheus').permitAll()
         .requestMatchers('/info').permitAll()
         .requestMatchers('/metrics').permitAll()
         .anyRequest().authenticated())
       http.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);
     }else{
       http
         .csrf()
         .disable()
         .cors()
         .disable()
         .exceptionHandling()
         .authenticationEntryPoint(jwtAuthenticationEntryPoint).and().sessionManagement()
         .sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
         .authorizeHttpRequests((authz) ->
           authz
         .requestMatchers("/auth/login").permitAll()
         .requestMatchers(HttpMethod.POST,'/autopilot/registerCanary').permitAll()
         .requestMatchers(HttpMethod.POST,'/autopilot/api/v1/registerCanary').permitAll()
         .requestMatchers(HttpMethod.POST,'/autopilot/api/v2/registerCanary').permitAll()
         .requestMatchers(HttpMethod.POST,'/autopilot/api/v3/registerCanary').permitAll()
         .requestMatchers(HttpMethod.GET,'/autopilot/canaries/{id}').permitAll()
         .requestMatchers(HttpMethod.GET,'/autopilot/api/v2/autopilot/canaries/{id}').permitAll()
         .requestMatchers(HttpMethod.GET,'/autopilot/api/v1/autopilot/canaries/{id}').permitAll()
         .requestMatchers(HttpMethod.POST,'/visibilityservice/v1/approvalGates/{id}/trigger').permitAll()
         .requestMatchers(HttpMethod.POST,'/visibilityservice/v2/approvalGates/{id}/trigger').permitAll()
         .requestMatchers(HttpMethod.POST,'/visibilityservice/v4/approvalGates/{id}/trigger').permitAll()
         .requestMatchers(HttpMethod.POST,'/visibilityservice/v5/approvalGates/{id}/trigger').permitAll()
         .requestMatchers(HttpMethod.GET,'/visibilityservice/v2/approvalGateInstances/{id}/status').permitAll()
         .requestMatchers(HttpMethod.GET,'/visibilityservice/v1/approvalGateInstances/{id}/status').permitAll()
         .requestMatchers(HttpMethod.PUT,'/visibilityservice/v1/approvalGateInstances/{id}/spinnakerReview').permitAll()
         .requestMatchers(HttpMethod.GET, '/platformservice/v6/applications/{applicationname}/pipeline/{pipelineName}/reference/{ref}/gates/{gatesName}').permitAll()
         .requestMatchers(HttpMethod.GET, '/platformservice/v1/applications/{applicationName}/pipelines/{pipelineName}').permitAll()
         .requestMatchers(HttpMethod.POST, '/dashboardservice/v4/pipelines/{pipelineId}/gates').permitAll()
         .requestMatchers(HttpMethod.PUT, '/platformservice/v6/usergroups/permissions/users/{username}/resources/{resourceId}').permitAll()
         .requestMatchers(HttpMethod.PUT, '/visibilityservice/v4/approvalGates/{id}/connector').permitAll()
         .requestMatchers(HttpMethod.PUT, '/dashboardservice/v4/pipelines/{pipelineId}/gates/{gateId}').permitAll()
         .requestMatchers(HttpMethod.POST,'/oes/echo').permitAll()
         .requestMatchers(HttpMethod.POST,'/oes/echo/').permitAll()
         .requestMatchers(HttpMethod.POST,'/auditservice/v1/echo/events/data').permitAll()
         .requestMatchers(HttpMethod.POST,'/auditservice/v1/echo/events/data/').permitAll()
         .requestMatchers(HttpMethod.POST,'/v1/data/**').permitAll()
         .requestMatchers(HttpMethod.POST,'/v1/staticPolicy/eval').permitAll()
         .requestMatchers(HttpMethod.POST,'/v1/staticPolicy/eval/').permitAll()
         .requestMatchers(HttpMethod.GET,'/autopilot/mgmt/**').permitAll()
         .requestMatchers(HttpMethod.POST,'/datasource/cache/save').permitAll()
         .requestMatchers(HttpMethod.DELETE,'/datasource/cache/evict').permitAll()
         .requestMatchers('/**/favicon.ico').permitAll()
         .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
         .requestMatchers(PermissionRevokingLogoutSuccessHandler.LOGGED_OUT_URL).permitAll()
         .requestMatchers('/plugins/deck/**').permitAll()
         .requestMatchers(HttpMethod.POST, '/notifications/callbacks/**').permitAll()
         .requestMatchers(HttpMethod.POST, '/ssdservice/v1/spinnakerevents').permitAll()
         .requestMatchers(HttpMethod.POST, '/ssdservice/v1/spinnakerevents/').permitAll()
         .requestMatchers('/health').permitAll()
         .requestMatchers('/prometheus').permitAll()
         .requestMatchers('/info').permitAll()
         .requestMatchers('/metrics').permitAll()
         .anyRequest().authenticated())
       http.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);
     }
  }

  void configure(WebSecurity web) throws Exception {
    web.debug(securityDebug)
  }

  @Component
  static class PermissionRevokingLogoutSuccessHandler implements LogoutSuccessHandler, InitializingBean {

    static final String LOGGED_OUT_URL = "/auth/loggedOut"

    @Autowired
    PermissionService permissionService

    SimpleUrlLogoutSuccessHandler delegate = new SimpleUrlLogoutSuccessHandler();

    @Override
    void afterPropertiesSet() throws Exception {
      delegate.setDefaultTargetUrl(LOGGED_OUT_URL)
    }

    @Override
    void onLogoutSuccess(HttpServletRequest request,
                         HttpServletResponse response,
                         Authentication authentication) throws IOException, ServletException {
      def username = (authentication?.getPrincipal() as User)?.username
      if (username) {
        permissionService.logout(username)
      }
      delegate.onLogoutSuccess(request, response, authentication)
    }
  }
}
