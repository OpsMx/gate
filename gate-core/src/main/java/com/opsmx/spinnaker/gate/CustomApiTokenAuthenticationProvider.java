package com.opsmx.spinnaker.gate;

import com.netflix.spinnaker.gate.model.TokenModel;
import com.netflix.spinnaker.gate.services.TokenValidationService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Header;
import io.jsonwebtoken.Jwt;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.util.Base64;
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
      if (!(array.length == 2)) {
        throw new InsufficientAuthenticationException("Invalid API token");
      }
      String[] values = array[1].split("[.]");
      if (!(values.length == 3)) {
        throw new InsufficientAuthenticationException("Invalid API token");
      }
      String token = array[1].trim();
      int signatureIndex = token.lastIndexOf('.');
      if (signatureIndex <= 0) {
        throw new InsufficientAuthenticationException("Invalid API token");
      }
      String nonSignedToken = token.substring(0, signatureIndex + 1);
      Jwt<Header, Claims> jwt = Jwts.parser().parseClaimsJwt(nonSignedToken);
      Claims body = jwt.getBody();
      String subject = body.getSubject();
      long iat = body.getIssuedAt().getTime();
      String sub = subject.split(":")[0];
      String key =
          String.format(
              "%s/%s/GOl@ngCOntrollerH@ndshake-@OPSMX-JavABACKeND|udfPLQZS",
              String.valueOf(iat / 1000), sub);
      String encodekey = Base64.getEncoder().encodeToString(key.getBytes());

      String jwtToken =
          Jwts.builder()
              .claim("iss", "controller")
              .setSubject(subject)
              .setIssuedAt(body.getIssuedAt())
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
