package com.opsmx.spinnaker.gate;

import com.google.gson.Gson;
import com.netflix.spinnaker.gate.model.TokenModel;
import com.netflix.spinnaker.gate.services.TokenValidationService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Header;
import io.jsonwebtoken.Jwt;
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
      if (!(array.length == 2)) {
        throw new InsufficientAuthenticationException("Invalid API token");
      }
      String[] values = array[1].split("[.]");
      if (!(values.length == 3)) {
        throw new InsufficientAuthenticationException("Invalid API token");
      }
      String token = array[1].trim();
      byte[] decodedBytes = Base64.getDecoder().decode(values[1]);
      String decodedString = new String(decodedBytes);
      //
      /*
       * Claims claims = Jwts.parser().parseClaimsJws(token).getBody(); String
       * subjectFromClaim = claims.getSubject(); Date iatfromClaim =
       * claims.getIssuedAt();
       *
       * System.out.println("subject from claims is:" + subjectFromClaim);
       * System.out.println("iat from claims is:" + iatfromClaim);
       */

      /*
       * Jwt s = Jwts.parser().parse(token); Claims c = (Claims) s.getBody();
       * System.out.println("subject1 from claims is:" + c.getSubject());
       * System.out.println("iat1 from claims is:" + c.getIssuedAt().toString());
       */
      int signatureIndex = token.lastIndexOf('.');
      if (signatureIndex <= 0) {
        throw new InsufficientAuthenticationException("Invalid API token");
      }
      String nonSignedToken = token.substring(0, signatureIndex + 1);
      System.out.println("non signd token: " + nonSignedToken);
      Jwt<Header, Claims> jwt = Jwts.parser().parseClaimsJwt(nonSignedToken);

      Claims body = jwt.getBody();

      String subject2 = body.getSubject();
      String iat2 = body.getIssuedAt().toString();
      System.out.println("subject2 from claims is:" + subject2);
      System.out.println("iat2 from claims is:" + iat2);
      //

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
