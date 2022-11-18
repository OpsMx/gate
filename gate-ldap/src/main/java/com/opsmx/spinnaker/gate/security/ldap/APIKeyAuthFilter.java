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
 */

package com.opsmx.spinnaker.gate.security.ldap;

import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.security.authentication.AuthenticationDetailsSource;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.event.InteractiveAuthenticationSuccessEvent;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.WebAttributes;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.util.Assert;
import org.springframework.web.filter.GenericFilterBean;

@Slf4j
public class APIKeyAuthFilter extends GenericFilterBean implements ApplicationEventPublisherAware {

  private String principalRequestHeader;
  private String credentialRequestHeader;

  public APIKeyAuthFilter(String principalRequestHeader, String credentialRequestHeader) {
    this.principalRequestHeader = principalRequestHeader;
    this.credentialRequestHeader = credentialRequestHeader;
  }

  protected Object getPreAuthenticatedPrincipal(HttpServletRequest request) {
    return request.getHeader(principalRequestHeader);
  }

  protected Object getPreAuthenticatedCredentials(HttpServletRequest request) {
    return request.getHeader(credentialRequestHeader);
  }

  private ApplicationEventPublisher eventPublisher = null;
  private AuthenticationDetailsSource<HttpServletRequest, ?> authenticationDetailsSource =
      new WebAuthenticationDetailsSource();
  private AuthenticationManager authenticationManager = null;
  private boolean continueFilterChainOnUnsuccessfulAuthentication = true;
  private boolean checkForPrincipalChanges;
  private boolean invalidateSessionOnPrincipalChange = true;
  private AuthenticationSuccessHandler authenticationSuccessHandler = null;
  private AuthenticationFailureHandler authenticationFailureHandler = null;
  private RequestMatcher requiresAuthenticationRequestMatcher;

  /** Check whether all required properties have been set. */
  public void afterPropertiesSet() {
    try {
      super.afterPropertiesSet();
    } catch (ServletException e) {
      // convert to RuntimeException for passivity on afterPropertiesSet signature
      throw new RuntimeException(e);
    }
    Assert.notNull(authenticationManager, "An AuthenticationManager must be set");
  }

  /**
   * Try to authenticate a pre-authenticated user with Spring Security if the user has not yet been
   * authenticated.
   */
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
      throws IOException, ServletException {

    if (log.isDebugEnabled()) {
      log.debug(
          "Checking secure context token: "
              + SecurityContextHolder.getContext().getAuthentication());
    }

    if (requiresAuthenticationRequestMatcher.matches((HttpServletRequest) request)) {
      doAuthenticate((HttpServletRequest) request, (HttpServletResponse) response);
    }

    chain.doFilter(request, response);
  }

  /**
   * Determines if the current principal has changed. The default implementation tries
   *
   * <ul>
   *   <li>If the {@link #getPreAuthenticatedPrincipal(HttpServletRequest)} is a String, the {@link
   *       Authentication#getName()} is compared against the pre authenticated principal
   *   <li>Otherwise, the {@link #getPreAuthenticatedPrincipal(HttpServletRequest)} is compared
   *       against the {@link Authentication#getPrincipal()}
   * </ul>
   *
   * <p>Subclasses can override this method to determine when a principal has changed.
   *
   * @param request
   * @param currentAuthentication
   * @return true if the principal has changed, else false
   */
  protected boolean principalChanged(
      HttpServletRequest request, Authentication currentAuthentication) {

    Object principal = getPreAuthenticatedPrincipal(request);

    if ((principal instanceof String) && currentAuthentication.getName().equals(principal)) {
      return false;
    }

    if (principal != null && principal.equals(currentAuthentication.getPrincipal())) {
      return false;
    }

    if (log.isDebugEnabled()) {
      log.debug(
          "Pre-authenticated principal has changed to "
              + principal
              + " and will be reauthenticated");
    }
    return true;
  }

  /** Do the actual authentication for a pre-authenticated user. */
  private void doAuthenticate(HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {
    Authentication authResult;

    Object principal = getPreAuthenticatedPrincipal(request);
    Object credentials = getPreAuthenticatedCredentials(request);

    if (principal == null) {
      if (log.isDebugEnabled()) {
        log.debug("No pre-authenticated principal found in request");
      }

      return;
    }

    if (log.isDebugEnabled()) {
      log.debug("preAuthenticatedPrincipal = " + principal + ", trying to authenticate");
    }

    try {
      PreAuthenticatedAuthenticationToken authRequest =
          new PreAuthenticatedAuthenticationToken(principal, credentials);
      authRequest.setDetails(authenticationDetailsSource.buildDetails(request));
      authResult = authenticationManager.authenticate(authRequest);
      successfulAuthentication(request, response, authResult);
    } catch (AuthenticationException failed) {
      unsuccessfulAuthentication(request, response, failed);

      if (!continueFilterChainOnUnsuccessfulAuthentication) {
        throw failed;
      }
    }
  }

  /**
   * Puts the <code>Authentication</code> instance returned by the authentication manager into the
   * secure context.
   */
  protected void successfulAuthentication(
      HttpServletRequest request, HttpServletResponse response, Authentication authResult)
      throws IOException, ServletException {
    if (log.isDebugEnabled()) {
      log.debug("Authentication success: " + authResult);
    }
    SecurityContextHolder.getContext().setAuthentication(authResult);
    // Fire event
    if (this.eventPublisher != null) {
      eventPublisher.publishEvent(
          new InteractiveAuthenticationSuccessEvent(authResult, this.getClass()));
    }

    if (authenticationSuccessHandler != null) {
      authenticationSuccessHandler.onAuthenticationSuccess(request, response, authResult);
    }
  }

  /**
   * Ensures the authentication object in the secure context is set to null when authentication
   * fails.
   *
   * <p>Caches the failure exception as a request attribute
   */
  protected void unsuccessfulAuthentication(
      HttpServletRequest request, HttpServletResponse response, AuthenticationException failed)
      throws IOException, ServletException {
    SecurityContextHolder.clearContext();

    if (log.isDebugEnabled()) {
      log.debug("Cleared security context due to exception", failed);
    }
    request.setAttribute(WebAttributes.AUTHENTICATION_EXCEPTION, failed);

    if (authenticationFailureHandler != null) {
      authenticationFailureHandler.onAuthenticationFailure(request, response, failed);
    }
  }

  /** @param anApplicationEventPublisher The ApplicationEventPublisher to use */
  public void setApplicationEventPublisher(ApplicationEventPublisher anApplicationEventPublisher) {
    this.eventPublisher = anApplicationEventPublisher;
  }

  /** @param authenticationDetailsSource The AuthenticationDetailsSource to use */
  public void setAuthenticationDetailsSource(
      AuthenticationDetailsSource<HttpServletRequest, ?> authenticationDetailsSource) {
    Assert.notNull(authenticationDetailsSource, "AuthenticationDetailsSource required");
    this.authenticationDetailsSource = authenticationDetailsSource;
  }

  protected AuthenticationDetailsSource<HttpServletRequest, ?> getAuthenticationDetailsSource() {
    return authenticationDetailsSource;
  }

  /** @param authenticationManager The AuthenticationManager to use */
  public void setAuthenticationManager(AuthenticationManager authenticationManager) {
    this.authenticationManager = authenticationManager;
  }

  /**
   * If set to {@code true} (the default), any {@code AuthenticationException} raised by the {@code
   * AuthenticationManager} will be swallowed, and the request will be allowed to proceed,
   * potentially using alternative authentication mechanisms. If {@code false}, authentication
   * failure will result in an immediate exception.
   *
   * @param shouldContinue set to {@code true} to allow the request to proceed after a failed
   *     authentication.
   */
  public void setContinueFilterChainOnUnsuccessfulAuthentication(boolean shouldContinue) {
    continueFilterChainOnUnsuccessfulAuthentication = shouldContinue;
  }

  /**
   * If set, the pre-authenticated principal will be checked on each request and compared against
   * the name of the current <tt>Authentication</tt> object. A check to determine if {@link
   * Authentication#getPrincipal()} is equal to the principal will also be performed. If a change is
   * detected, the user will be reauthenticated.
   *
   * @param checkForPrincipalChanges
   */
  public void setCheckForPrincipalChanges(boolean checkForPrincipalChanges) {
    this.checkForPrincipalChanges = checkForPrincipalChanges;
  }

  /**
   * If <tt>checkForPrincipalChanges</tt> is set, and a change of principal is detected, determines
   * whether any existing session should be invalidated before proceeding to authenticate the new
   * principal.
   *
   * @param invalidateSessionOnPrincipalChange <tt>false</tt> to retain the existing session.
   *     Defaults to <tt>true</tt>.
   */
  public void setInvalidateSessionOnPrincipalChange(boolean invalidateSessionOnPrincipalChange) {
    this.invalidateSessionOnPrincipalChange = invalidateSessionOnPrincipalChange;
  }

  /** Sets the strategy used to handle a successful authentication. */
  public void setAuthenticationSuccessHandler(
      AuthenticationSuccessHandler authenticationSuccessHandler) {
    this.authenticationSuccessHandler = authenticationSuccessHandler;
  }

  /** Sets the strategy used to handle a failed authentication. */
  public void setAuthenticationFailureHandler(
      AuthenticationFailureHandler authenticationFailureHandler) {
    this.authenticationFailureHandler = authenticationFailureHandler;
  }

  /** Sets the request matcher to check whether to proceed the request further. */
  public void setRequiresAuthenticationRequestMatcher(
      RequestMatcher requiresAuthenticationRequestMatcher) {
    Assert.notNull(requiresAuthenticationRequestMatcher, "requestMatcher cannot be null");
    this.requiresAuthenticationRequestMatcher = requiresAuthenticationRequestMatcher;
  }
}
