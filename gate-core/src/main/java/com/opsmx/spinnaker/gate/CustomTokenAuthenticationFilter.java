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

import java.io.IOException;
import java.util.Optional;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;

@Slf4j
public class CustomTokenAuthenticationFilter extends AbstractAuthenticationProcessingFilter {
  //  private final static UrlPathHelper urlPathHelper = new UrlPathHelper();

  public CustomTokenAuthenticationFilter(AuthenticationManager authenticationManager) {
    super("/**");
    this.setAuthenticationManager(authenticationManager);
  }

  /* @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException,
      ServletException {
      HttpServletRequest request = (HttpServletRequest) req;
      HttpServletResponse response = (HttpServletResponse) res;

      if (!this.requiresAuthentication(request, response)) {
        chain.doFilter(request, response);
      } else {
        log.info("*************** Request is to process authentication");
      }
        boolean success = true;

        Authentication authResult = null;
        try {
          Optional<String> apiTokenOptional = Optional.ofNullable(request.getHeader("x-opsmx-token"));
          if(apiTokenOptional.isPresent()){
            log.info("*************** calling authentication function");
            authResult = this.attemptAuthentication(request, response);
          }else{
            log.info("*************** continuing the filter");
  //          AbstractAuthenticationToken auth = new AbstractAuthenticationToken();
  //          CustomApiTokenAuthentication token = new CustomApiTokenAuthentication(null);
  //          getAuthenticationManager().authenticate(token);
            SecurityContextHolder.getContext().setAuthentication(null);
            chain.doFilter(request, response);
  //          return;
          }
        } catch (InternalAuthenticationServiceException var8) {
          this.logger.error("An internal error occurred while trying to authenticate the user.", var8);
          success = false;
        } catch (AuthenticationException var9) {
          success = false;
        }


        if (success && null != authResult) {
          this.successfulAuthentication(request, response, chain, authResult);
        }

        // Please ensure that chain.doFilter(request, response) is invoked upon successful authentication. You want
        // processing of the request to advance to the next filter, because very last one filter
        // FilterSecurityInterceptor#doFilter is responsible to actually invoke method in your controller that is
        // handling requested API resource.
  //      chain.doFilter(request, response);
      }*/

  @Override
  public Authentication attemptAuthentication(
      HttpServletRequest request, HttpServletResponse response) {

    Optional<String> apiTokenOptional = Optional.ofNullable(request.getHeader("x-opsmx-token"));
    log.info("******************# Request Header is {} ", apiTokenOptional);

    CustomApiTokenAuthentication token =
        apiTokenOptional
            .map(CustomApiTokenAuthentication::new)
            .orElse(new CustomApiTokenAuthentication());
    log.info("****************** Token Object {} ", token);
    return getAuthenticationManager().authenticate(token);
  }

  @Override
  protected void successfulAuthentication(
      HttpServletRequest request,
      HttpServletResponse response,
      FilterChain chain,
      Authentication authResult)
      throws IOException, ServletException {

    SecurityContextHolder.getContext().setAuthentication(authResult);
    log.info(
        "********** authentication {}",
        SecurityContextHolder.getContext().getAuthentication().getName());
    log.info(
        "********** details {}", SecurityContextHolder.getContext().getAuthentication().toString());
    chain.doFilter(request, response);
  }

  //  @Override
  //  protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse
  // response,
  //                                            AuthenticationException failed) throws IOException,
  // ServletException {
  //    log.info("************* failed authentication while attempting to access "
  //      + urlPathHelper.getPathWithinApplication((HttpServletRequest) request));
  //
  //    //Add more descriptive message
  //    response.sendError(HttpServletResponse.SC_UNAUTHORIZED,
  //      "Authentication Failed");
  //  }
}
