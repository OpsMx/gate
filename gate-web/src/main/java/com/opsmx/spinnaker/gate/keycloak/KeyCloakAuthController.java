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

package com.opsmx.spinnaker.gate.keycloak;

import javax.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

@Slf4j
@RestController
public class KeyCloakAuthController {

  @Autowired private RestTemplate restTemplate;

  @PostMapping(
      value = "/keycloak/oauth2/{clientId}",
      consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
  public void loginWithKeycloak(
      Object obj,
      @PathVariable("clientId") String clientId,
      @RequestParam("state") String state,
      @RequestParam("session_state") String session_state,
      @RequestParam("code") String code,
      HttpServletRequest httpServletRequest) {

    log.info("client id : {}", clientId);
    log.info("state : {}", state);
    log.info("session_state : {}", session_state);
    log.info("code : {}", code);
  }
}
