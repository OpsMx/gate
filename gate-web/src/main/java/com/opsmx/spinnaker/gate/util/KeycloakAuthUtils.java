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

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ws.rs.core.MediaType;
import lombok.extern.slf4j.Slf4j;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataOutput;
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
@Slf4j
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
    ComponentRepresentation componentRepresentation =
        populateComponentRepresentation(config, realm.getId());
    realmResource.components().add(componentRepresentation);
    log.info("Successfully added ldap: {} ", componentRepresentation);
  }

  private ComponentRepresentation populateComponentRepresentation(
      MultivaluedHashMap<String, String> config, String id) {
    ComponentRepresentation componentRepresentation = new ComponentRepresentation();
    componentRepresentation.setName(ldapName);
    componentRepresentation.setConfig(config);
    componentRepresentation.setParentId(id);
    componentRepresentation.setProviderType(USER_STORAGE_PROVIDER_TYPE);
    componentRepresentation.setProviderId("ldap");
    return componentRepresentation;
  }

  public void updateLdapComponent(MultivaluedHashMap<String, String> config) {
    ComponentRepresentation componentRepresentation = getLdapComponent();
    componentRepresentation.setConfig(config);
    RealmResource realmResource = getRealm();
    realmResource
        .components()
        .component(componentRepresentation.getId())
        .update(componentRepresentation);
    log.info("Successfully updated ldap: {} ", componentRepresentation);
  }

  public void deleteLdapComponent() {
    ComponentRepresentation componentRepresentation = getLdapComponent();
    RealmResource realmResource = getRealm();
    realmResource.components().component(componentRepresentation.getId()).remove();
    log.info("Successfully deleted ldap: {} ", componentRepresentation);
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
        .orElse(null);
  }

  public void addSamlIdentityProvider(MultipartFile data) {
    Map<String, String> config = getConfig(data);
    IdentityProviderRepresentation identityProvider =
        populateIdentityProviderRepresentation(config);
    getRealm().identityProviders().create(identityProvider);
    log.info("Successfully added saml: {} ", identityProvider);
  }

  private Map<String, String> getConfig(MultipartFile data) {
    MultipartFormDataOutput form = new MultipartFormDataOutput();
    form.addFormData("providerId", "saml", MediaType.TEXT_PLAIN_TYPE);
    String body;
    try {
      body = new String(data.getBytes(), Charset.forName("utf-8"));
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    form.addFormData("file", body, MediaType.APPLICATION_XML_TYPE, "saml-idp-metadata.xml");
    Map<String, String> config = getRealm().identityProviders().importFrom(form);
    config.remove("addExtensionsElementWithKeyInfo");
    config.remove("enabledFromMetadata");
    config.remove("signingCertificate");
    config.put("allowCreate", "true");
    config.put("allowedClockSkew", "");
    config.put("attributeConsumingServiceIndex", "");
    config.put("attributeConsumingServiceName", "");
    config.put("backchannelSupported", "false");
    config.put("entityId", "");
    config.put("forceAuthn", "false");
    config.put("guiOrder", "");
    config.put("principalType", "Subject NameID");
    config.put("signSpMetadata", "false");
    config.put("singleLogoutServiceUrl", "");
    config.put("wantAssertionsEncrypted", "false");
    config.put("wantAssertionsSigned", "false");
    return config;
  }

  public IdentityProviderRepresentation disableSamlIdentityProvider() {
    IdentityProviderRepresentation idpRep = getSamlIdentityProvider();
    idpRep.setEnabled(false);
    getRealm().identityProviders().get(samlName).update(idpRep);
    log.info("Successfully disabled saml: {} ", idpRep);
    return idpRep;
  }

  private IdentityProviderRepresentation populateIdentityProviderRepresentation(
      Map<String, String> config) {
    IdentityProviderRepresentation identityProviderRepresentation =
        new IdentityProviderRepresentation();
    identityProviderRepresentation.setAlias(samlName);
    identityProviderRepresentation.setProviderId(samlName);
    identityProviderRepresentation.setDisplayName(samlName);
    identityProviderRepresentation.setConfig(config);
    return identityProviderRepresentation;
  }

  public IdentityProviderRepresentation getSamlIdentityProvider() {
    RealmResource realmResource = getRealm();
    return realmResource.identityProviders().get(samlName).toRepresentation();
  }

  public void deleteSamlIdentityProvider() {
    getRealm().identityProviders().get(samlName).remove();
    log.info("Successfully deleted saml.");
  }
}
