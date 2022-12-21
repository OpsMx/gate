package com.opsmx.spinnaker.gate;

import com.netflix.spinnaker.gate.config.AuthConfig;
import java.util.Collections;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.AnonymousAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.OrRequestMatcher;
import org.springframework.security.web.util.matcher.RequestHeaderRequestMatcher;

/*
   - Configuration: Adds a new Spring configuration
   - Enable web security: Overrides the default web security configuration
*/
@Configuration
@EnableWebSecurity
@Order(1)
public class CustomTokenSecurityConfig extends WebSecurityConfigurerAdapter {

  @Autowired AuthConfig authConfig;
  @Autowired private CustomApiTokenAuthenticationProvider customApiTokenAuthenticationProvider;

  @Override
  protected void configure(HttpSecurity httpSecurity) throws Exception {
    authConfig.configure(httpSecurity);
    httpSecurity
        .sessionManagement()
        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        .and()
        .requestCache()
        .disable()
        .requestMatcher(new RequestHeaderRequestMatcher("x-opsmx-token"))
        .addFilterBefore(
            new CustomTokenAuthenticationFilter(
                authenticationManager(),
                new OrRequestMatcher(
                    new AntPathRequestMatcher(
                        "/autopilot/api/v2/permissions/getApplications", "GET", false),
                    new AntPathRequestMatcher(
                        "/autopilot/api/v1/applications/{source1}/tags", "GET", false),
                    new AntPathRequestMatcher(
                        "/autopilot/api/v2/applications/getApplicationHealth", "GET", false),
                    new AntPathRequestMatcher(
                        "/autopilot/canaries/getServiceInformation", "GET", false),
                    new AntPathRequestMatcher("/autopilot/api/v1/defaultLogTemplate", "GET", false),
                    new AntPathRequestMatcher("/autopilot/cas/getCanaryOutputNew", "GET", false),
                    new AntPathRequestMatcher(
                        "/autopilot/api/v1/correlation/log/{source1}/{source2}", "GET", false),
                    new AntPathRequestMatcher("/autopilot/api/v1/correlation/metric", "GET", false),
                    new AntPathRequestMatcher(
                        "/autopilot/api/v1/correlation/metric/{riskAnalysisId}/{serviceId}",
                        "GET",
                        false),
                    new AntPathRequestMatcher("/autopilot/logs/feedbackHistory", "GET", false),
                    new AntPathRequestMatcher("/autopilot/api/v5/global/template", "GET", false),
                    new AntPathRequestMatcher(
                        "/autopilot/api/v4/monitoringproviders/log", "GET", false),
                    new AntPathRequestMatcher("/autopilot/api/v1/scoring-algorithms", "GET", false),
                    new AntPathRequestMatcher(
                        "/platformservice/v6/users/{source1}/datasources", "GET", false),
                    new AntPathRequestMatcher(
                        "/autopilot/api/v4/monitoringproviders/custom", "GET", false),
                    new AntPathRequestMatcher(
                        "/autopilot/api/v4/monitoringproviders/apm", "GET", false),
                    new AntPathRequestMatcher(
                        "/dashboardservice/v2/users/{source1}/applications/latest-canary",
                        "GET",
                        false),
                    new AntPathRequestMatcher("autopilot/canaries/debugLogsData", "GET", false),
                    new AntPathRequestMatcher("/autopilot/api/v1/getClusterTags", "GET", false),
                    new AntPathRequestMatcher(
                        "/autopilot/logs/getDataSourceResponseKeys", "GET", false),
                    new AntPathRequestMatcher(
                        "/autopilot/logs/updateFeedbackLogTemplate", "GET", false),
                    new AntPathRequestMatcher("/autopilot/canaries/getServiceList", "GET", false),
                    new AntPathRequestMatcher("/autopilot/canaries/logsData", "GET", false),
                    new AntPathRequestMatcher("/autopilot/canaries/clustersByEvent", "GET", false),
                    new AntPathRequestMatcher(
                        "/autopilot/canaries/getApplicationsOrServices", "GET", false),
                    new AntPathRequestMatcher(
                        "/autopilot/v5/canaries/generatecookbook", "GET", false),
                    new AntPathRequestMatcher(
                        "/autopilot/canaries/cancelRunningCanary", "GET", false),
                    new AntPathRequestMatcher(
                        "/autopilot/logs/updateFeedbackLogTemplate", "POST", false),
                    new AntPathRequestMatcher(
                        "/autopilot/api/v5/downloadUpdatedLogtemplate", "GET", false),
                    new AntPathRequestMatcher(
                        "/platformservice/v1/users/{username}/verifications/applications",
                        "GET",
                        false))),
            AnonymousAuthenticationFilter.class);
  }

  @Override
  public AuthenticationManager authenticationManager() {
    return new ProviderManager(Collections.singletonList(customApiTokenAuthenticationProvider));
  }
}
