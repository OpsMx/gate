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
        .antMatchers("/metrics")
        .antMatchers("/auth/login")
        .antMatchers("/auth/logout")
        //        .antMatchers(HttpMethod.POST,"/login")
        .antMatchers("/session")
        .antMatchers("/redirectauto")
        .antMatchers(HttpMethod.POST, "/autopilot/registerCanary")
        .antMatchers(HttpMethod.GET, "/autopilot/api/v2/autopilot/canaries/{id}")
        .antMatchers(HttpMethod.GET, "/autopilot/api/v1/autopilot/canaries/{id}")
        .antMatchers(HttpMethod.POST, "/autopilot/api/v1/registerCanary")
        .antMatchers(HttpMethod.POST, "/autopilot/api/v2/registerCanary")
        .antMatchers(HttpMethod.POST, "/autopilot/api/v3/registerCanary")
        .antMatchers(HttpMethod.POST, "/autopilot/api/v5/registerCanary")
        .antMatchers(HttpMethod.GET, "/autopilot/canaries/{id}")
        .antMatchers(HttpMethod.GET, "/autopilot/v5/canaries/{id}")
        .antMatchers(HttpMethod.GET, "/autopilot/api/v5/external/template")
        .antMatchers(HttpMethod.POST, "/autopilot/api/v5/external/template")
        .antMatchers(HttpMethod.POST, "/visibilityservice/v1/approvalGates/{id}/trigger")
        .antMatchers(HttpMethod.POST, "/visibilityservice/v2/approvalGates/{id}/trigger")
        .antMatchers(HttpMethod.POST, "/visibilityservice/v4/approvalGates/{id}/trigger")
        .antMatchers(HttpMethod.POST, "/visibilityservice/v5/approvalGates/{id}/trigger")
        .antMatchers(HttpMethod.GET, "/visibilityservice/v2/approvalGateInstances/{id}/status")
        .antMatchers(HttpMethod.GET, "/visibilityservice/v1/approvalGateInstances/{id}/status")
        .antMatchers(
            HttpMethod.PUT, "/visibilityservice/v1/approvalGateInstances/{id}/spinnakerReview")
        .antMatchers(HttpMethod.POST, "/oes/echo")
        .antMatchers(HttpMethod.POST, "/oes/echo/")
        .antMatchers(HttpMethod.POST, "/auditservice/v1/echo/events/data")
        .antMatchers(HttpMethod.POST, "/auditservice/v1/echo/events/data/")
        .antMatchers(HttpMethod.POST, "/v1/data/**")
        .antMatchers(HttpMethod.POST, "/v1/staticPolicy/eval")
        .antMatchers(HttpMethod.POST, "/v1/staticPolicy/eval/")
        .antMatchers(HttpMethod.GET, "/autopilot/mgmt/**")
        .antMatchers(HttpMethod.POST, "/datasource/cache/save")
        .antMatchers(HttpMethod.DELETE, "/datasource/cache/evict")
        .antMatchers("/plugins/deck/**")
        .antMatchers(HttpMethod.POST, "/webhooks/**")
        .antMatchers(HttpMethod.POST, "/notifications/callbacks/**")
        .antMatchers(HttpMethod.POST, "/managed/notifications/callbacks/**")
        .antMatchers(HttpMethod.GET, "/oes/accountsConfig/v2/agents/apple/automation")
        .antMatchers(HttpMethod.POST, "/oes/accountsConfig/v1/agents/apple/automation")
        .antMatchers(
            HttpMethod.GET, "/oes/accountsConfig/v1/agents/{agentName}/manifest/apple/automation")
        .antMatchers(
            HttpMethod.GET,
            "/oes/accountsConfig/v2/spinnaker/cloudProviderAccount/apple/automation")
        .antMatchers(
            HttpMethod.GET,
            "/oes/accountsConfig/v2/spinnaker/cloudProviderAccount/{agentName}/{accountName}/apple/automation")
        .antMatchers(
            HttpMethod.POST,
            "/oes/accountsConfig/v2/spinnaker/cloudProviderAccount/apple/automation")
        .antMatchers(HttpMethod.GET, "/oes/accountsConfig/v3/spinnaker/apple/automation")
        .antMatchers(HttpMethod.GET, "/dashboardservice/v4/getAllDatasources/apple/automation")
        .antMatchers(
            HttpMethod.GET,
            "/dashboardservice/v5/agents/{agentName}/accounts/{accountName}/accountType/{accountType}/apple/automation")
        .antMatchers(HttpMethod.POST, "/dashboardservice/v4/datasource/apple/automation")
        .antMatchers(
            HttpMethod.GET,
            "/platformservice/v6/applications/{applicationname}/pipeline/{pipelineName}/reference/{ref}/gates/{gatesName}");
  }

  @Override
  protected void configure(HttpSecurity httpSecurity) throws Exception {
    httpSecurity
        //      .cors()
        //      .and()
        .requestMatcher(requestMatcherProvider.requestMatcher())
        .authorizeRequests()
        .antMatchers(HttpMethod.POST, "/login")
        .permitAll()
        .antMatchers("/error")
        .permitAll()
        .antMatchers("/favicon.ico")
        .permitAll()
        .antMatchers("/resources/**")
        .permitAll()
        .antMatchers("/images/**")
        .permitAll()
        .antMatchers("/js/**")
        .permitAll()
        .antMatchers("/fonts/**")
        .permitAll()
        .antMatchers("/css/**")
        .permitAll()
        .antMatchers("/**/favicon.ico")
        .permitAll()
        .antMatchers(HttpMethod.OPTIONS, "/**")
        .permitAll()
        .antMatchers("/auth/loggedOut")
        .permitAll()
        .antMatchers("/auth/user")
        .permitAll()
        .antMatchers(HttpMethod.POST, "/autopilot/registerCanary")
        .permitAll()
        .antMatchers(HttpMethod.GET, "/autopilot/api/v2/autopilot/canaries/{id}")
        .permitAll()
        .antMatchers(HttpMethod.GET, "/autopilot/api/v1/autopilot/canaries/{id}")
        .permitAll()
        .antMatchers(HttpMethod.POST, "/autopilot/api/v1/registerCanary")
        .permitAll()
        .antMatchers(HttpMethod.POST, "/autopilot/api/v2/registerCanary")
        .permitAll()
        .antMatchers(HttpMethod.POST, "/autopilot/api/v3/registerCanary")
        .permitAll()
        .antMatchers(HttpMethod.POST, "/autopilot/api/v5/registerCanary")
        .permitAll()
        .antMatchers(HttpMethod.GET, "/autopilot/canaries/{id}")
        .permitAll()
        .antMatchers(HttpMethod.GET, "/autopilot/v5/canaries/{id}")
        .permitAll()
        .antMatchers(HttpMethod.GET, "/autopilot/api/v5/external/template")
        .permitAll()
        .antMatchers(HttpMethod.POST, "/autopilot/api/v5/external/template")
        .permitAll()
        .antMatchers(HttpMethod.POST, "/visibilityservice/v1/approvalGates/{id}/trigger")
        .permitAll()
        .antMatchers(HttpMethod.POST, "/visibilityservice/v2/approvalGates/{id}/trigger")
        .permitAll()
        .antMatchers(HttpMethod.POST, "/visibilityservice/v4/approvalGates/{id}/trigger")
        .permitAll()
        .antMatchers(HttpMethod.POST, "/visibilityservice/v5/approvalGates/{id}/trigger")
        .permitAll()
        .antMatchers(HttpMethod.GET, "/visibilityservice/v2/approvalGateInstances/{id}/status")
        .permitAll()
        .antMatchers(HttpMethod.GET, "/visibilityservice/v1/approvalGateInstances/{id}/status")
        .permitAll()
        .antMatchers(
            HttpMethod.PUT, "/visibilityservice/v1/approvalGateInstances/{id}/spinnakerReview")
        .permitAll()
        .antMatchers(HttpMethod.POST, "/oes/echo")
        .permitAll()
        .antMatchers(HttpMethod.POST, "/oes/echo/")
        .permitAll()
        .antMatchers(HttpMethod.POST, "/auditservice/v1/echo/events/data")
        .permitAll()
        .antMatchers(HttpMethod.POST, "/auditservice/v1/echo/events/data/")
        .permitAll()
        .antMatchers(HttpMethod.POST, "/v1/data/**")
        .permitAll()
        .antMatchers(HttpMethod.POST, "/v1/staticPolicy/eval")
        .permitAll()
        .antMatchers(HttpMethod.POST, "/v1/staticPolicy/eval/")
        .permitAll()
        .antMatchers(HttpMethod.GET, "/autopilot/mgmt/**")
        .permitAll()
        .antMatchers(HttpMethod.POST, "/datasource/cache/save")
        .permitAll()
        .antMatchers(HttpMethod.DELETE, "/datasource/cache/evict")
        .permitAll()
        .antMatchers("/plugins/deck/**")
        .permitAll()
        .antMatchers(HttpMethod.POST, "/webhooks/**")
        .permitAll()
        .antMatchers(HttpMethod.POST, "/notifications/callbacks/**")
        .permitAll()
        .antMatchers(HttpMethod.POST, "/managed/notifications/callbacks/**")
        .permitAll()
        .antMatchers(HttpMethod.GET, "/oes/accountsConfig/v2/agents/apple/automation")
        .permitAll()
        .antMatchers(HttpMethod.POST, "/oes/accountsConfig/v1/agents/apple/automation")
        .permitAll()
        .antMatchers(
            HttpMethod.GET, "/oes/accountsConfig/v1/agents/{agentName}/manifest/apple/automation")
        .permitAll()
        .antMatchers(
            HttpMethod.GET,
            "/oes/accountsConfig/v2/spinnaker/cloudProviderAccount/apple/automation")
        .permitAll()
        .antMatchers(
            HttpMethod.GET,
            "/oes/accountsConfig/v2/spinnaker/cloudProviderAccount/{agentName}/{accountName}/apple/automation")
        .permitAll()
        .antMatchers(
            HttpMethod.POST,
            "/oes/accountsConfig/v2/spinnaker/cloudProviderAccount/apple/automation")
        .permitAll()
        .antMatchers(HttpMethod.GET, "/oes/accountsConfig/v3/spinnaker/apple/automation")
        .permitAll()
        .antMatchers(HttpMethod.GET, "/dashboardservice/v4/getAllDatasources/apple/automation")
        .permitAll()
        .antMatchers(
            HttpMethod.GET,
            "/dashboardservice/v5/agents/{agentName}/accounts/{accountName}/accountType/{accountType}/apple/automation")
        .permitAll()
        .antMatchers(HttpMethod.POST, "/dashboardservice/v4/datasource/apple/automation")
        .permitAll()
        .antMatchers(
            HttpMethod.GET,
            "/platformservice/v6/applications/{applicationname}/pipeline/{pipelineName}/reference/{ref}/gates/{gatesName}")
        .permitAll()
        .antMatchers("/health")
        .permitAll()
        .antMatchers("/prometheus")
        .permitAll()
        .antMatchers("/info")
        .permitAll()
        .antMatchers("/metrics")
        .permitAll()
        .and()
        .antMatcher("/platformservice/v7/argo/cdIntegrations")

        /*.antMatchers("/platformservice/v7/argo/cdIntegrations", "/autopilot/api/v2/permissions/getApplications",
        "/autopilot/api/v1/applications/{source1}/tags", "/autopilot/api/v2/applications/getApplicationHealth",
        "/autopilot/api/v1/defaultLogTemplate", "/autopilot/cas/getCanaryOutputNew", "/autopilot/api/v1/correlation/log/{source1}/{source2}",
        "/autopilot/api/v1/correlation/metric", "/autopilot/logs/feedbackHistory", "/autopilot/api/v5/global/template",
        "/autopilot/api/v4/monitoringproviders/log", "/autopilot/api/v1/scoring-algorithms", "/platformservice/v6/users/{source1}/datasources",
        "/autopilot/api/v4/monitoringproviders/custom", "/autopilot/api/v4/monitoringproviders/apm", "/dashboardservice/v2/users/{source1}/applications/latest-canary",
        "/autopilot/api/v1/getClusterTags", "/autopilot/logs/getDataSourceResponseKeys", "/autopilot/logs/updateFeedbackLogTemplate",
        "/platformservice/v1/users/{username}/verifications/applications", "/autopilot/api/v1/correlation/metric/{riskAnalysisId}/{serviceId}").authenticated()*/

        // Start of report page APIs, Need to remove them once token based authentication is in
        // place.
        //      .antMatcher("/autopilot/api/v2/permissions/getApplications")
        //      .antMatcher("/autopilot/api/v1/applications/{source1}/tags")
        //      .antMatcher("/autopilot/api/v2/applications/getApplicationHealth")
        //      .antMatcher("/autopilot/api/v1/defaultLogTemplate")
        //      .antMatcher("/autopilot/cas/getCanaryOutputNew")
        //      .antMatcher("/autopilot/api/v1/correlation/log/{source1}/{source2}")
        //      .antMatcher("/autopilot/api/v1/correlation/metric")
        //      .antMatcher("/autopilot/logs/feedbackHistory")
        //      .antMatcher("/autopilot/api/v5/global/template")
        //      .antMatcher("/autopilot/api/v4/monitoringproviders/log")
        //      .antMatcher("/autopilot/api/v1/scoring-algorithms")
        //      .antMatcher("/platformservice/v6/users/{source1}/datasources")
        //      .antMatcher("/autopilot/api/v4/monitoringproviders/custom")
        //      .antMatcher("/autopilot/api/v4/monitoringproviders/apm")
        //      .antMatcher("/dashboardservice/v2/users/{source1}/applications/latest-canary")
        //      .antMatcher("/autopilot/api/v1/getClusterTags")
        //      .antMatcher("/autopilot/logs/getDataSourceResponseKeys")
        //      .antMatcher("/autopilot/logs/updateFeedbackLogTemplate")
        //      .antMatcher("/platformservice/v1/users/{username}/verifications/applications")
        //      .antMatcher("/autopilot/api/v1/correlation/metric/{riskAnalysisId}/{serviceId}")
        // End of report page APIs
        .authorizeRequests()
        ////
        .anyRequest()
        .authenticated()
        .and()
        .sessionManagement()
        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        .and()
        .requestCache()
        .disable()
        //        .requestMatcher(new RequestHeaderRequestMatcher("x-opsmx-token"))
        .addFilterBefore(
            new CustomTokenAuthenticationFilter(authenticationManager()),
            AnonymousAuthenticationFilter.class);
  }

  @Override
  public AuthenticationManager authenticationManager() {
    return new ProviderManager(Collections.singletonList(customApiTokenAuthenticationProvider));
  }
}
