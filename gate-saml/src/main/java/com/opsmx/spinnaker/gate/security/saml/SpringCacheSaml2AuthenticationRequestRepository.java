/*
 * Copyright 2024 Netflix, Inc.
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
 *
 */

package com.opsmx.spinnaker.gate.security.saml;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.concurrent.ConcurrentMapCache;
import org.springframework.security.saml2.core.Saml2ParameterNames;
import org.springframework.security.saml2.provider.service.authentication.AbstractSaml2AuthenticationRequest;
import org.springframework.security.saml2.provider.service.web.Saml2AuthenticationRequestRepository;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
@Slf4j
public class SpringCacheSaml2AuthenticationRequestRepository
    implements Saml2AuthenticationRequestRepository<AbstractSaml2AuthenticationRequest> {
  private final Cache cache = new ConcurrentMapCache("authentication-requests");

  @Override
  public AbstractSaml2AuthenticationRequest loadAuthenticationRequest(HttpServletRequest request) {
    log.debug("********* loadAuthenticationRequest ********************");
    String relayState = request.getParameter(Saml2ParameterNames.RELAY_STATE);
    log.debug("********* relayState : {}", relayState);
    return this.cache.get(relayState, AbstractSaml2AuthenticationRequest.class);
  }

  @Override
  public void saveAuthenticationRequest(
      AbstractSaml2AuthenticationRequest authenticationRequest,
      HttpServletRequest request,
      HttpServletResponse response) {
    log.debug("********* saveAuthenticationRequest ********************");
    String relayState = authenticationRequest.getRelayState();
    log.debug("********* relayState : {}", relayState);
    // String relayState = request.getParameter(Saml2ParameterNames.RELAY_STATE);
    this.cache.put(relayState, authenticationRequest);
  }

  @Override
  public AbstractSaml2AuthenticationRequest removeAuthenticationRequest(
      HttpServletRequest request, HttpServletResponse response) {
    log.debug("********* removeAuthenticationRequest ********************");
    String relayState = request.getParameter(Saml2ParameterNames.RELAY_STATE);
    log.debug("********* relayState : {}", relayState);
    AbstractSaml2AuthenticationRequest authenticationRequest =
        this.cache.get(relayState, AbstractSaml2AuthenticationRequest.class);
    if (authenticationRequest == null) {
      return null;
    }
    this.cache.evict(relayState);
    return authenticationRequest;
  }
}
