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

package com.opsmx.spinnaker.gate.security.saml;

import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.SpringSecurityMessageSource;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.providers.ExpiringUsernameAuthenticationToken;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.web.filter.GenericFilterBean;

public class SamlAuthTokenUpdateFilter extends GenericFilterBean {

  private AuthenticationEntryPoint authenticationEntryPoint;

  private RequestCache requestCache = new HttpSessionRequestCache();

  private final MessageSourceAccessor messages = SpringSecurityMessageSource.getAccessor();

  //  public SamlAuthTokenUpdateFilter(AuthenticationEntryPoint authenticationEntryPoint) {
  //    this(authenticationEntryPoint, new HttpSessionRequestCache());
  //  }
  //
  //  public SamlAuthTokenUpdateFilter(
  //      AuthenticationEntryPoint authenticationEntryPoint, RequestCache requestCache) {
  //    Assert.notNull(authenticationEntryPoint, "authenticationEntryPoint cannot be null");
  //    Assert.notNull(requestCache, "requestCache cannot be null");
  //    this.authenticationEntryPoint = authenticationEntryPoint;
  //    this.requestCache = requestCache;
  //  }
  //
  //  @Override
  //  public void afterPropertiesSet() {
  //    Assert.notNull(authenticationEntryPoint, "authenticationEntryPoint must be specified");
  //  }

  public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
      throws IOException, ServletException {
    logger.debug("SamlAuthTokenUpdateFilter doFilter started");
    HttpServletRequest request = (HttpServletRequest) req;
    HttpServletResponse response = (HttpServletResponse) res;
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

    logger.debug("Previously Authenticated is : " + authentication);
    if (authentication instanceof ExpiringUsernameAuthenticationToken
        && !authentication.isAuthenticated()) {
      if (logger.isDebugEnabled()) {
        logger.debug(
            "Previously Authenticated token Expired; redirecting to authentication entry point. \n Previously Authenticated is : "
                + authentication);
      }

      SecurityContextHolder.clearContext();
      HttpSession session = request.getSession();
      if (session != null) {
        session.invalidate();
      }
      response.sendRedirect("/gate/auth/logout");
      //      response.setContentType("text/html");
      //      PrintWriter out = response.getWriter();
      //
      //      out.println("<html>");
      //      out.println("<head>");
      //      out.println("<title>Hola</title>");
      //      out.println("</head>");
      //      out.println("<body bgcolor=\"white\">");
      //      out.println("</body>");
      //      out.println("</html>");

      return;
      //      throw new AccessDeniedException("Previously Authenticated token Expired.");

      //      sendStartAuthentication(
      //          request,
      //          response,
      //          chain,
      //          new InsufficientAuthenticationException("Previously Authenticated token
      // Expired."));
    }

    logger.debug("SamlAuthTokenUpdateFilter doFilter ended");

    chain.doFilter(request, response);
  }

  //  protected void sendStartAuthentication(
  //      HttpServletRequest request,
  //      HttpServletResponse response,
  //      FilterChain chain,
  //      AuthenticationException reason)
  //      throws ServletException, IOException {
  //    SecurityContextHolder.getContext().setAuthentication(null);
  //    requestCache.saveRequest(request, response);
  //    logger.debug("Calling Authentication entry point.");
  //    authenticationEntryPoint.commence(request, response, reason);
  //  }
}
