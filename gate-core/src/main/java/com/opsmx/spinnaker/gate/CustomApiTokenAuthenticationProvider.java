package com.opsmx.spinnaker.gate;

import com.netflix.spinnaker.gate.model.TokenModel;
import com.netflix.spinnaker.gate.services.TokenValidationService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
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
      throw new InsufficientAuthenticationException("No API token present in the request header");
    } else {
      String[] values = apiToken.split(";");

      if (values.length != 4) {
        throw new InsufficientAuthenticationException(
            "API token is not in correct format. "
                + "The correct format is app;number;number;token");
      }

      Integer canaryId = NumberUtils.toInt(values[2], -1);
      String token = values[3];

      CustomApiTokenAuthentication tokenObject = (CustomApiTokenAuthentication) authentication;
      Integer canaryIdFromRequestParam = tokenObject.getCanaryId();

      if (canaryIdFromRequestParam != null) {
        canaryId = canaryIdFromRequestParam;
      }

      if (canaryId == -1 || StringUtils.isBlank(token)) {
        throw new BadCredentialsException(
            "Either canary id or token is blank or has invalid value.");
      }

      TokenModel tokenModel =
          tokenValidationService.validateTokenByCanaryRunId(canaryId, token).getBody();

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
