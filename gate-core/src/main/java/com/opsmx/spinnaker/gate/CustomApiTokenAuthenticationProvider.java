package com.opsmx.spinnaker.gate;

import com.google.gson.Gson;
import com.netflix.spinnaker.gate.model.TokenModel;
import com.netflix.spinnaker.gate.services.TokenValidationService;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.math.BigDecimal;
import java.util.Base64;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
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
  private Gson gson = new Gson();

  @Override
  public Authentication authenticate(Authentication authentication) throws AuthenticationException {
    String apiToken = (String) authentication.getCredentials();

    if (ObjectUtils.isEmpty(apiToken)) {
      throw new InsufficientAuthenticationException("No API token present in the request header");
    } else if (apiToken.startsWith("jwt")) {
      Map<String, Object> header = new LinkedHashMap<>();
      header.put("alg", SignatureAlgorithm.HS256.name());
      header.put("typ", "JWT");
      String[] array = apiToken.split(";");
      String[] values = array[1].split("[.]");
      String token = array[1].trim();
      byte[] decodedBytes = Base64.getDecoder().decode(values[1]);
      String decodedString = new String(decodedBytes);
      Map<String, Object> map = gson.fromJson(decodedString, Map.class);
      String sub = map.get("sub").toString().split(":")[0];
      String iatValue = map.get("iat").toString();
      BigDecimal bd = new BigDecimal(iatValue);
      long iat = bd.longValue();
      Date date = new Date(iat * 1000);
      String key =
          String.format(
              "%s/%s/GOl@ngCOntrollerH@ndshake-@OPSMX-JavABACKeND|udfPLQZS",
              String.valueOf(iat), sub);
      String encodekey = Base64.getEncoder().encodeToString(key.getBytes());

      String jwtToken =
          Jwts.builder()
              .claim("iss", "controller")
              .setSubject(map.get("sub").toString())
              .setIssuedAt(date)
              .signWith(SignatureAlgorithm.HS256, encodekey)
              .setHeader(header)
              .compact();

      if (!jwtToken.equals(token)) {
        throw new BadCredentialsException("API token is invalid");
      } else {
        CustomApiTokenAuthentication customApiTokenAuthentication =
            new CustomApiTokenAuthentication(apiToken, true);
        customApiTokenAuthentication.setUsername(sub);
        return customApiTokenAuthentication;
      }

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
