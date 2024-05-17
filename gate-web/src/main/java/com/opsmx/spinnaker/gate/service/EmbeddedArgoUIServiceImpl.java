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

package com.opsmx.spinnaker.gate.service;

import com.netflix.spinnaker.gate.services.OesAuthorizationService;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.util.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class EmbeddedArgoUIServiceImpl implements EmbeddedArgoUIService {

  @Autowired(required = false)
  OesAuthorizationService oesAuthorizationService;

  @Value("${jwt.secret}")
  private String secret;

  @Value("${jwt.token.durationInSeconds:36000}")
  private Integer durationInSeconds;

  @Value("${vela.bounceUrl}")
  private String bounceUrl;

  @Override
  public String getBounceEndpoint(String username, String argoId, String path) {
    List<String> groups =
        oesAuthorizationService.getUserGroupsByUsername(username, username).getBody();
    Date currentDate = new Date(System.currentTimeMillis());
    Map<String, Object> header = new LinkedHashMap<>();
    header.put("alg", SignatureAlgorithm.HS256.name());
    header.put("typ", "JWT");
    String token =
        Jwts.builder()
            .signWith(SignatureAlgorithm.HS256, secret)
            .setHeader(header)
            .claim("groups", groups)
            .claim("argoId", argoId)
            .claim("path", path)
            .setSubject(username)
            .setExpiration(new Date(System.currentTimeMillis() + 1000L * durationInSeconds))
            .setNotBefore(currentDate)
            .setIssuedAt(currentDate)
            .setIssuer("isd")
            .compact();
    return bounceUrl + "?token=" + token;
  }
}
