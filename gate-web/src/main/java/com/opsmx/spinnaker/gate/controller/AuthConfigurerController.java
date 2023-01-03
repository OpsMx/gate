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

import com.opsmx.spinnaker.gate.model.AuthProviderModel;
import com.opsmx.spinnaker.gate.model.KeycloakAuthProvider;
import com.opsmx.spinnaker.gate.model.KeycloakAuthProviderModel;
import com.opsmx.spinnaker.gate.service.KeycloakAutowireService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping(value = "/auth/config")
@ConditionalOnExpression("${feature.auth-provider.flag:false}")
public class AuthConfigurerController {

  @Autowired private KeycloakAuthProvider keycloakAuthProvider;
  @Autowired private KeycloakAutowireService keycloakAuthService;

  @GetMapping(value = "/supported/providers", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<KeycloakAuthProviderModel> getSupportedAuthProviders() {

    log.debug("Auth providers : {}", keycloakAuthProvider.getProviders());

    return ResponseEntity.ok(new KeycloakAuthProviderModel(keycloakAuthProvider.getProviders()));
  }

  @PostMapping(
      value = "/authProvider/{authProvider}/create",
      consumes = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<AuthProviderModel> createAuthProvider(
      @RequestBody AuthProviderModel authProviderModel,
      @PathVariable(name = "authProvider") String authProvider) {
    AuthProviderModel created = keycloakAuthService.create(authProvider, authProviderModel);
    ResponseEntity<AuthProviderModel> response = new ResponseEntity<>(created, HttpStatus.CREATED);
    return response;
  }

  @PutMapping(
      value = "/authProvider/{authProvider}/update",
      consumes = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<AuthProviderModel> updateAuthProvider(
      @RequestBody AuthProviderModel authProviderModel,
      @PathVariable(name = "authProvider") String authProvider) {
    AuthProviderModel updated = keycloakAuthService.update(authProvider, authProviderModel);
    ResponseEntity<AuthProviderModel> response = new ResponseEntity<>(updated, HttpStatus.ACCEPTED);
    return response;
  }

  @GetMapping(
      value = "/authProvider/{authProvider}/get",
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<AuthProviderModel> getAuthProvider(
      @PathVariable(name = "authProvider") String authProvider) {
    AuthProviderModel provider = keycloakAuthService.get(authProvider);
    ResponseEntity<AuthProviderModel> response =
        new ResponseEntity<>(provider, HttpStatus.ACCEPTED);
    return response;
  }

  @DeleteMapping(value = "/authProvider/{authProvider}/delete")
  public ResponseEntity deleteAuthProvider(
      @PathVariable(name = "authProvider") String authProvider) {
    keycloakAuthService.delete(authProvider);
    return ResponseEntity.ok().build();
  }

  @GetMapping(
      value = "/authProvider/{authProvider}/disable",
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<AuthProviderModel> disbleAuthProvider(
      @PathVariable(name = "authProvider") String authProvider) {
    AuthProviderModel provider = keycloakAuthService.disble(authProvider);
    ResponseEntity<AuthProviderModel> response =
        new ResponseEntity<>(provider, HttpStatus.ACCEPTED);
    return response;
  }
}
