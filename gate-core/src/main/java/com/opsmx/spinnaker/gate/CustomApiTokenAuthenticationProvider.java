package com.opsmx.spinnaker.gate;

import com.netflix.spinnaker.gate.model.TokenModel;
import com.netflix.spinnaker.gate.services.TokenValidationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

@Slf4j
@Component
public class CustomApiTokenAuthenticationProvider implements AuthenticationProvider {

  @Autowired private TokenValidationService tokenValidationService;

  @Override
  public Authentication authenticate(Authentication authentication) throws AuthenticationException {
    String apiToken = (String) authentication.getCredentials();

    if (ObjectUtils.isEmpty(apiToken)) {
      throw new InsufficientAuthenticationException("No API token is request");
    } else {
      String[] values = apiToken.split(";");
      TokenModel tokenModel =
          tokenValidationService
              .validateTokenByCanaryRunId(Integer.valueOf(values[2]), values[3])
              .getBody();
      if (tokenModel != null && tokenModel.isValidToken()) {
        String username = tokenModel.getUserName();
        CustomApiTokenAuthentication customApiTokenAuthentication =
            new CustomApiTokenAuthentication(apiToken, true);
        customApiTokenAuthentication.setUsername(username);
        return customApiTokenAuthentication;
      }
      throw new BadCredentialsException("API token is invalid");
    }
  }

  @Override
  public boolean supports(Class<?> authentication) {
    return CustomApiTokenAuthentication.class.isAssignableFrom(authentication);
  }
}
