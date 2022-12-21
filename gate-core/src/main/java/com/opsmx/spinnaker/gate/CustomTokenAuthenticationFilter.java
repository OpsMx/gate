package com.opsmx.spinnaker.gate;

import com.netflix.spinnaker.kork.common.Header;
import com.netflix.spinnaker.security.AuthenticatedRequest;
import com.squareup.okhttp.HttpUrl;
import java.io.IOException;
import java.util.Optional;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.RequestMatcher;

@Slf4j
public class CustomTokenAuthenticationFilter extends AbstractAuthenticationProcessingFilter {

  public CustomTokenAuthenticationFilter(AuthenticationManager authenticationManager) {
    super("/**");
    this.setAuthenticationManager(authenticationManager);
  }

  public CustomTokenAuthenticationFilter(
      AuthenticationManager authenticationManager,
      RequestMatcher requiresAuthenticationRequestMatcher) {
    super(requiresAuthenticationRequestMatcher);
    this.setAuthenticationManager(authenticationManager);
  }

  private static String getFullRequestURL(HttpServletRequest request) {
    return request.getRequestURL().append("?").append(request.getQueryString()).toString();
  }

  private static int getCanaryIdFromQueryParam(String requestUrl) {
    final HttpUrl url = HttpUrl.parse(requestUrl);
    int canaryId = -1;
    if (url != null) {
      canaryId = NumberUtils.toInt(url.queryParameter("canaryId"), -1);
      if (canaryId == -1) {
        canaryId = NumberUtils.toInt(url.queryParameter("id"), -1);
      }
    }
    return canaryId;
  }

  @Override
  public Authentication attemptAuthentication(
      HttpServletRequest request, HttpServletResponse response) {

    Optional<String> apiTokenOptional = Optional.ofNullable(request.getHeader("x-opsmx-token"));

    CustomApiTokenAuthentication token =
        apiTokenOptional
            .map(CustomApiTokenAuthentication::new)
            .orElse(new CustomApiTokenAuthentication());

    String requestUrl = getFullRequestURL(request);
    int canaryIdFromQueryParam = getCanaryIdFromQueryParam(requestUrl);

    if (canaryIdFromQueryParam != -1) {
      token.setCanaryId(canaryIdFromQueryParam);
    }
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
    AuthenticatedRequest.set(Header.USER.getHeader(), (String) authResult.getPrincipal());
    chain.doFilter(request, response);
  }
}
