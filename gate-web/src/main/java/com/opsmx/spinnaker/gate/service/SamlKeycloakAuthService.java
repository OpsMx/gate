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
import java.util.HashMap;
import java.util.Map;
import javax.validation.ValidationException;
import org.keycloak.representations.idm.IdentityProviderMapperRepresentation;
import org.keycloak.representations.idm.IdentityProviderRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.stereotype.Service;

@Service
@ConditionalOnExpression("${feature.auth-provider.flag:false}")
public class SamlKeycloakAuthService implements KeycloakAuthService {

  private static final String SAML = "saml";

  @Autowired private KeycloakAuthUtils keycloakAuthUtils;

  /**
   * @param authProviderModel
   * @return
   */
  @Override
  public AuthProviderModel create(AuthProviderModel authProviderModel) {
    if (authProviderModel.getFile() != null) {
      keycloakAuthUtils.addSamlIdentityProvider(authProviderModel.getFile());
    } else {
      throw new ValidationException("Cant create a saml without a file!");
    }
    String enabled = authProviderModel.getConfig().get("enabled");
    if (!Boolean.parseBoolean(enabled)) {
      keycloakAuthUtils.disableSamlIdentityProvider();
    }
    keycloakAuthUtils.addIdpGroupMapper(authProviderModel);
    keycloakAuthUtils.addUserAttributeProtocolMapper(authProviderModel);
    return authProviderModel;
  }

  /**
   * @param authProviderModel
   * @return
   */
  @Override
  public AuthProviderModel update(AuthProviderModel authProviderModel) {
    if (authProviderModel.getFile() != null) {
      // Delete the old saml and update the new config as it is not overriding the old one.
      keycloakAuthUtils.deleteSamlIdentityProvider();
      keycloakAuthUtils.addSamlIdentityProvider(authProviderModel.getFile());
      keycloakAuthUtils.addIdpGroupMapper(authProviderModel);
    } else {
      keycloakAuthUtils.updateIdpGroupMapper(authProviderModel);
    }
    String enabled = authProviderModel.getConfig().get("enabled");
    if (!Boolean.parseBoolean(enabled)) {
      keycloakAuthUtils.disableSamlIdentityProvider();
    }
    keycloakAuthUtils.updateUserAttributeProtocolMapper(authProviderModel);
    return authProviderModel;
  }

  @Override
  public String getAuthType() {
    return SAML;
  }

  @Override
  public AuthProviderModel get() {
    IdentityProviderRepresentation samlIdentityProvider =
        keycloakAuthUtils.getSamlIdentityProvider();
    if (samlIdentityProvider == null) {
      return keycloakAuthUtils.getDefaultModel(SAML);
    }
    boolean isEnabled = samlIdentityProvider.isEnabled();
    Map<String, String> config = new HashMap<>();
    config.put("enabled", Boolean.toString(isEnabled));
    Map<String, String> samlConfig = samlIdentityProvider.getConfig();
    if (samlConfig.containsKey("singleSignOnServiceUrl")) {
      config.put("singleSignOnServiceUrl", samlConfig.get("singleSignOnServiceUrl"));
    }
    IdentityProviderMapperRepresentation identityProviderMapperRepresentation =
        keycloakAuthUtils.getIdpMapper(samlIdentityProvider);
    if (identityProviderMapperRepresentation != null) {
      config.put(
          "groupAttribute", identityProviderMapperRepresentation.getConfig().get("user.attribute"));
    }
    AuthProviderModel model = new AuthProviderModel();
    model.setName(samlIdentityProvider.getAlias());
    model.setConfig(config);
    return model;
  }

  @Override
  public void delete() {
    keycloakAuthUtils.deleteSamlIdentityProvider();
  }

  @Override
  public AuthProviderModel disable() {
    return null;
  }
}
