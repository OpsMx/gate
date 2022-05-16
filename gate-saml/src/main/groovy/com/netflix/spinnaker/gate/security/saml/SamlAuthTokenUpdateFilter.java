package com.netflix.spinnaker.gate.security.saml;

import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.SpringSecurityMessageSource;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.util.Assert;
import org.springframework.web.filter.GenericFilterBean;

public class SamlAuthTokenUpdateFilter extends GenericFilterBean {

  // ~ Instance fields
  // ================================================================================================

  private AuthenticationEntryPoint authenticationEntryPoint;

  private RequestCache requestCache = new HttpSessionRequestCache();

  private final MessageSourceAccessor messages = SpringSecurityMessageSource.getAccessor();

  public SamlAuthTokenUpdateFilter(AuthenticationEntryPoint authenticationEntryPoint) {
    this(authenticationEntryPoint, new HttpSessionRequestCache());
  }

  public SamlAuthTokenUpdateFilter(
      AuthenticationEntryPoint authenticationEntryPoint, RequestCache requestCache) {
    Assert.notNull(authenticationEntryPoint, "authenticationEntryPoint cannot be null");
    Assert.notNull(requestCache, "requestCache cannot be null");
    this.authenticationEntryPoint = authenticationEntryPoint;
    this.requestCache = requestCache;
  }

  // ~ Methods
  // ========================================================================================================

  @Override
  public void afterPropertiesSet() {
    Assert.notNull(authenticationEntryPoint, "authenticationEntryPoint must be specified");
  }

  public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
      throws IOException, ServletException {
    HttpServletRequest request = (HttpServletRequest) req;
    HttpServletResponse response = (HttpServletResponse) res;
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

    if (!authentication.isAuthenticated()) {
      if (logger.isDebugEnabled()) {
        logger.debug(
            "Previously Authenticated token Expired; redirecting to authentication entry point. \n Previously Authenticated is : "
                + authentication);
      }

      sendStartAuthentication(
          request,
          response,
          chain,
          new InsufficientAuthenticationException("Previously Authenticated token Expired."));
    }

    chain.doFilter(request, response);
  }

  protected void sendStartAuthentication(
      HttpServletRequest request,
      HttpServletResponse response,
      FilterChain chain,
      AuthenticationException reason)
      throws ServletException, IOException {
    // SEC-112: Clear the SecurityContextHolder's Authentication, as the
    // existing Authentication is no longer considered valid
    SecurityContextHolder.getContext().setAuthentication(null);
    requestCache.saveRequest(request, response);
    logger.debug("Calling Authentication entry point.");
    authenticationEntryPoint.commence(request, response, reason);
  }
}
