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

import com.google.gson.Gson;
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
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RestController
@RequestMapping(value = "/auth/config")
@ConditionalOnExpression("${feature.auth-provider.flag:false}")
public class AuthConfigurerController {

  @Autowired private KeycloakAuthProvider keycloakAuthProvider;
  @Autowired private KeycloakAutowireService keycloakAuthService;

  private Gson gson = new Gson();

  @GetMapping(value = "/supported/providers", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<KeycloakAuthProviderModel> getSupportedAuthProviders() {

    log.debug("Auth providers : {}", keycloakAuthProvider.getProviders());

    return ResponseEntity.ok(new KeycloakAuthProviderModel(keycloakAuthProvider.getProviders()));
  }

  @RequestMapping(value = "/authProvider/{authProvider}/create", method = RequestMethod.POST)
  public ResponseEntity<AuthProviderModel> createAuthProvider(
      @RequestPart(value = "file", required = false) MultipartFile file,
      @RequestPart(value = "authProviderModel") String authProviderStr,
      @PathVariable(name = "authProvider") String authProvider) {
    log.trace("createAuthProvider started");
    AuthProviderModel authProviderModel = gson.fromJson(authProviderStr, AuthProviderModel.class);
    if (file != null) {
      authProviderModel.setFile(file);
    }
    AuthProviderModel created = keycloakAuthService.create(authProvider, authProviderModel);
    ResponseEntity<AuthProviderModel> response = new ResponseEntity<>(created, HttpStatus.CREATED);
    log.trace("createAuthProvider ended");
    return response;
  }

  @RequestMapping(value = "/authProvider/{authProvider}/update", method = RequestMethod.PUT)
  public ResponseEntity<AuthProviderModel> updateAuthProvider(
      @RequestPart(value = "file", required = false) MultipartFile file,
      @RequestPart(value = "authProviderModel") String authProviderStr,
      @PathVariable(name = "authProvider") String authProvider) {
    log.trace("updateAuthProvider started");
    AuthProviderModel authProviderModel = gson.fromJson(authProviderStr, AuthProviderModel.class);
    if (file != null) {
      authProviderModel.setFile(file);
    }
    AuthProviderModel updated = keycloakAuthService.update(authProvider, authProviderModel);
    ResponseEntity<AuthProviderModel> response = new ResponseEntity<>(updated, HttpStatus.ACCEPTED);
    log.trace("updateAuthProvider ended");
    return response;
  }

  @GetMapping(
      value = "/authProvider/{authProvider}/get",
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<AuthProviderModel> getAuthProvider(
      @PathVariable(name = "authProvider") String authProvider) {
    log.trace("getAuthProvider started");
    AuthProviderModel provider = keycloakAuthService.get(authProvider);
    ResponseEntity<AuthProviderModel> response = new ResponseEntity<>(provider, HttpStatus.OK);
    log.trace("getAuthProvider ended");
    return response;
  }

  @DeleteMapping(value = "/authProvider/{authProvider}/delete")
  public ResponseEntity deleteAuthProvider(
      @PathVariable(name = "authProvider") String authProvider) {
    log.trace("deleteAuthProvider started");
    keycloakAuthService.delete(authProvider);
    log.trace("deleteAuthProvider ended");
    return ResponseEntity.ok().build();
  }

  @PutMapping(
      value = "/authProvider/{authProvider}/disable",
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<AuthProviderModel> disableAuthProvider(
      @PathVariable(name = "authProvider") String authProvider) {
    log.trace("disableAuthProvider started");
    AuthProviderModel provider = keycloakAuthService.disable(authProvider);
    ResponseEntity<AuthProviderModel> response =
        new ResponseEntity<>(provider, HttpStatus.ACCEPTED);
    log.trace("disableAuthProvider ended");
    return response;
  }
}
