/*
 * Copyright 2023 OpsMx, Inc.
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

import com.opsmx.spinnaker.gate.model.AuthProviderModel;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/***
 * An adapter service that invokes appropriate KeycloakAuthService based on AuthType
 */
@Service
public class KeycloakAutowireService {
  private final Map<String, KeycloakAuthService> servicesByAuthType;

  @Autowired
  public KeycloakAutowireService(List<KeycloakAuthService> services) {
    servicesByAuthType =
        services.stream()
            .collect(Collectors.toMap(KeycloakAuthService::getAuthType, Function.identity()));
  }

  public void delete(String authProvider) {
    servicesByAuthType.get(authProvider).delete();
  }

  public AuthProviderModel get(String authProvider) {
    return servicesByAuthType.get(authProvider).get();
  }

  public AuthProviderModel update(String authProvider, AuthProviderModel authProviderModel) {
    return servicesByAuthType.get(authProvider).update(authProviderModel);
  }

  public AuthProviderModel create(String authProvider, AuthProviderModel authProviderModel) {
    return servicesByAuthType.get(authProvider).create(authProviderModel);
  }

  public AuthProviderModel toggle(String authProvider, String action) {
    return servicesByAuthType.get(authProvider).toggle(action);
  }
}
