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
import com.opsmx.spinnaker.gate.util.KeycloakAuthUtils;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.keycloak.common.util.MultivaluedHashMap;
import org.keycloak.representations.idm.ComponentRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class LdapKeycloakAuthService implements KeycloakAuthService {

  @Value("${keycloak.ldapName:ldap}")
  private String ldapName;

  @Autowired private KeycloakAuthUtils keycloakAuthUtils;

  /**
   * @param authProviderModel
   * @return
   */
  @Override
  public AuthProviderModel create(AuthProviderModel authProviderModel) {
    MultivaluedHashMap<String, String> config =
        hashMapToMultivaluedHashMap(authProviderModel.getConfig());
    keycloakAuthUtils.addLdapComponent(config);
    return authProviderModel;
  }

  private MultivaluedHashMap<String, String> hashMapToMultivaluedHashMap(
      Map<String, String> config) {
    MultivaluedHashMap<String, String> map = new MultivaluedHashMap<>();
    Optional.ofNullable(config).orElse(new HashMap<>()).entrySet().stream()
        .forEach(entry -> map.add(entry.getKey(), entry.getValue()));
    return map;
  }

  /**
   * @param authProviderModel
   * @return
   */
  @Override
  public AuthProviderModel update(AuthProviderModel authProviderModel) {
    MultivaluedHashMap<String, String> config =
        hashMapToMultivaluedHashMap(authProviderModel.getConfig());
    keycloakAuthUtils.updateLdapComponent(config);
    return authProviderModel;
  }

  @Override
  public String getAuthType() {
    return ldapName;
  }

  @Override
  public AuthProviderModel get() {
    ComponentRepresentation ldap = keycloakAuthUtils.getLdapComponent();
    if (ldap == null) {
      return keycloakAuthUtils.getDefaultModel(ldapName);
    }
    Map<String, String> config = multivaluedHashMapToHashMap(ldap.getConfig());
    AuthProviderModel model = new AuthProviderModel();
    model.setName(ldap.getName());
    model.setConfig(config);
    return model;
  }

  private Map<String, String> multivaluedHashMapToHashMap(
      MultivaluedHashMap<String, String> config) {
    Map<String, String> map = new HashMap<>();
    Optional.ofNullable(config).orElse(new MultivaluedHashMap<>()).entrySet().stream()
        .forEach(
            entry -> {
              String value =
                  Optional.ofNullable(entry.getValue()).orElse(new ArrayList<>()).stream()
                      .findFirst()
                      .orElse("");
              map.put(entry.getKey(), value);
            });
    return map;
  }

  @Override
  public void delete() {
    keycloakAuthUtils.deleteLdapComponent();
  }

  @Override
  public AuthProviderModel disable() {
    return null;
  }
}
