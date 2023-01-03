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

package com.opsmx.spinnaker.gate.util;

import java.util.*;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import org.jetbrains.annotations.NotNull;
import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.common.util.MultivaluedHashMap;
import org.keycloak.representations.idm.ComponentRepresentation;
import org.keycloak.representations.idm.IdentityProviderRepresentation;
import org.keycloak.representations.idm.RealmRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
public class KeycloakAuthUtils {

  private Keycloak keycloak;

  @Value("${keycloak.realmName:opsmxRealm}")
  private String realmName;

  @Value("${keycloak.ldapName:ldap}")
  private String ldapName;

  @Value("${keycloak.samlName:saml}")
  private String samlName;

  @Value("${keycloak.clientId:opsmx-client}")
  private String clientId;

  @Value("${keycloak.serverUrl}")
  private String serverUrl;

  @Value("${keycloak.username:}")
  private String username;

  @Value("${keycloak.password:}")
  private String password;

  @Value("${keycloak.token:}")
  private String token;

  public static final String USER_STORAGE_PROVIDER_TYPE =
      "org.keycloak.storage.UserStorageProvider";
  public static final String LDAP_STORAGE_MAPPER_TYPE =
      "org.keycloak.storage.ldap.mappers.LDAPStorageMapper";

  @PostConstruct
  public void initKeycloak() {
    KeycloakBuilder builder =
        KeycloakBuilder.builder().serverUrl(serverUrl).realm(realmName).clientId(clientId);
    if (token != null && !token.isEmpty()) {
      builder = builder.grantType(OAuth2Constants.CLIENT_CREDENTIALS).clientSecret(token);
    } else {
      builder = builder.grantType("password").username(username).password(password);
    }

    keycloak = builder.build();
  }

  @PreDestroy
  public void closeKeycloak() {
    keycloak.close();
  }

  public RealmResource getRealm() {
    return keycloak.realm(realmName);
  }

  public void addLdapComponent(MultivaluedHashMap<String, String> config) {
    RealmResource realmResource = getRealm();
    RealmRepresentation realm = realmResource.toRepresentation();
    String id = realm.getId();
    ComponentRepresentation componentRepresentation = populateComponentRepresentation(config, id);
    realmResource.components().add(componentRepresentation);
  }

  @NotNull
  private ComponentRepresentation populateComponentRepresentation(
      MultivaluedHashMap<String, String> config, String id) {
    ComponentRepresentation componentRepresentation = new ComponentRepresentation();
    componentRepresentation.setName(ldapName);
    componentRepresentation.setConfig(config);
    componentRepresentation.setParentId(id);
    componentRepresentation.setProviderType(USER_STORAGE_PROVIDER_TYPE);
    return componentRepresentation;
  }

  public void updateLdapComponent(MultivaluedHashMap<String, String> config) {
    ComponentRepresentation componentRepresentation = getLdapComponent();
    componentRepresentation.setConfig(config);
    RealmResource realmResource = getRealm();
    realmResource.components().add(componentRepresentation);
  }

  public void deleteLdapComponent() {
    ComponentRepresentation componentRepresentation = getLdapComponent();
    RealmResource realmResource = getRealm();
    realmResource.components().component(componentRepresentation.getId()).remove();
  }

  public ComponentRepresentation disableLdapComponent() {
    ComponentRepresentation componentRepresentation = getLdapComponent();
    componentRepresentation.getConfig().put("enabled", Arrays.asList("false"));
    RealmResource realmResource = getRealm();
    realmResource
        .components()
        .component(componentRepresentation.getId())
        .update(componentRepresentation);
    return componentRepresentation;
  }

  public ComponentRepresentation getLdapComponent() {
    RealmResource realmResource = getRealm();
    RealmRepresentation realm = realmResource.toRepresentation();
    String id = realm.getId();
    List<ComponentRepresentation> componentsResource =
        realmResource.components().query(id, USER_STORAGE_PROVIDER_TYPE);
    return Optional.ofNullable(componentsResource).orElse(new ArrayList<>()).stream()
        .filter(cr -> cr.getName().equalsIgnoreCase(ldapName))
        .findFirst()
        .map(ComponentRepresentation::getId)
        .map(componentId -> realmResource.components().query(componentId, LDAP_STORAGE_MAPPER_TYPE))
        .orElse(new ArrayList<>())
        .stream()
        .findFirst()
        .orElse(null);
  }

  public void addSamlIdentityProvider(MultipartFile data) {
    Map<String, String> config = getRealm().identityProviders().importFrom(data);

    getRealm().identityProviders().create(populateIdentityProviderRepresentation(config));
  }

  public IdentityProviderRepresentation disableSamlIdentityProvider() {
    IdentityProviderRepresentation idpRep = getSamlIdentityProvider();
    idpRep.setEnabled(false);
    getRealm().identityProviders().get(samlName).update(idpRep);
    return idpRep;
  }

  private IdentityProviderRepresentation populateIdentityProviderRepresentation(
      Map<String, String> config) {
    IdentityProviderRepresentation identityProviderRepresentation =
        new IdentityProviderRepresentation();
    identityProviderRepresentation.setAlias(samlName);
    identityProviderRepresentation.setConfig(config);
    return identityProviderRepresentation;
  }

  public IdentityProviderRepresentation getSamlIdentityProvider() {
    RealmResource realmResource = getRealm();
    return realmResource.identityProviders().get(samlName).toRepresentation();
  }

  public void deleteSamlIdentityProvider() {
    getRealm().identityProviders().get(samlName).remove();
  }
}
