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

package com.opsmx.spinnaker.gate.controller;

import com.opsmx.spinnaker.gate.model.KeycloakAuthProvider;
import com.opsmx.spinnaker.gate.model.KeycloakAuthProviderModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping(value = "/auth/config")
public class AuthConfigurerController {

  @Autowired private KeycloakAuthProvider keycloakAuthProvider;

  @GetMapping(value = "/supported/providers", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<KeycloakAuthProviderModel> getSupportedAuthProviders() {

    log.info("ldap params : {}", keycloakAuthProvider.getLdap());
    log.info("saml params : {}", keycloakAuthProvider.getSaml());

    return ResponseEntity.ok(
        new KeycloakAuthProviderModel(
            keycloakAuthProvider.getLdap(), keycloakAuthProvider.getSaml()));
  }
}
