package com.opsmx.spinnaker.gate;

import com.netflix.spinnaker.kork.common.Header;
import com.netflix.spinnaker.security.AuthenticatedRequest;
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

  public CustomTokenAuthenticationFilter(AuthenticationManager authenticationManager) {
    super("/**");
    this.setAuthenticationManager(authenticationManager);
  }

  @Override
  public Authentication attemptAuthentication(
      HttpServletRequest request, HttpServletResponse response) {

    Optional<String> apiTokenOptional = Optional.ofNullable(request.getHeader("x-opsmx-token"));

    CustomApiTokenAuthentication token =
        apiTokenOptional
            .map(CustomApiTokenAuthentication::new)
            .orElse(new CustomApiTokenAuthentication());
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
