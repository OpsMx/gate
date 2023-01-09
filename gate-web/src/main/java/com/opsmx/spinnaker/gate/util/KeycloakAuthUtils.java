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

import com.opsmx.spinnaker.gate.model.AuthProviderModel;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.*;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ws.rs.core.MediaType;
import lombok.extern.slf4j.Slf4j;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataOutput;
import org.jetbrains.annotations.NotNull;
import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.common.util.MultivaluedHashMap;
import org.keycloak.representations.idm.*;
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

  @Value("${security.oauth2.client.clientSecret:}")
  private String clientSecret;

  public static final String USER_STORAGE_PROVIDER_TYPE =
      "org.keycloak.storage.UserStorageProvider";

  private final String GROUPS = "groups";
  private final String GROUP_MEMBERSHIP_PROTOCOL_MAPPER = "oidc-group-membership-mapper";
  private final String LDAP_GROUP_PROTOCOL = "ldap-group-protocol";
  private final String OPENID_CONNECT = "openid-connect";
  private final String LDAP_GROUP_MAPPER = "ldap-group-mapper";
  private final String LDAP_STORAGE_MAPPER = "org.keycloak.storage.ldap.mappers.LDAPStorageMapper";
  private final String GROUP_LDAP_MAPPER_PROVIDER = "group-ldap-mapper";
  private final String GROUP_ATTRIBUTE = "groupAttribute";
  private final String IDP_USER_ATTRIBUTE_MAPPER = "saml-user-attribute-idp-mapper";
  private final String USER_ATTRIBUTE_PROTOCOL_MAPPER = "oidc-usermodel-attribute-mapper";
  private final String IDP_GROUP_PROTOCOL = "idp-group-protocol";

  @PostConstruct
  public void initKeycloak() {
    KeycloakBuilder builder =
        KeycloakBuilder.builder().serverUrl(serverUrl).realm(realmName).clientId(clientId);
    if (token != null && !token.isEmpty()) {
      builder = builder.grantType(OAuth2Constants.CLIENT_CREDENTIALS).clientSecret(token);
    } else {
      builder =
          builder
              .grantType(OAuth2Constants.PASSWORD)
              .username(username)
              .password(password)
              .clientSecret(clientSecret);
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

  public void addLdapComponent(ComponentRepresentation componentRepresentation) {
    RealmResource realmResource = getRealm();
    realmResource.components().add(componentRepresentation);
    log.info("Successfully added a ldap component: {} ", componentRepresentation.getConfig());
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
    log.info("ldap name : {}", ldapName);
    RealmResource realmResource = getRealm();
    log.info("{}", realmResource.toRepresentation());
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
    List<IdentityProviderRepresentation> idps = realmResource.identityProviders().findAll();
    if (idps == null || idps.isEmpty()) {
      return null;
    }
    return realmResource.identityProviders().get(samlName).toRepresentation();
  }

  public void deleteSamlIdentityProvider() {
    getRealm().identityProviders().get(samlName).remove();
    log.info("Successfully deleted saml.");
  }

  public AuthProviderModel getDefaultModel(String name) {
    AuthProviderModel model = new AuthProviderModel();
    model.setName(name);
    Map<String, String> config = new HashMap<>();
    config.put("enabled", "false");
    model.setConfig(config);
    return model;
  }

  public void addLdapGroupMapper(AuthProviderModel authProviderModel) {
    ComponentRepresentation ldapComponentRepresentation = getLdapComponent();
    MultivaluedHashMap<String, String> ldapCompConfig = populateLdapCompConfig(authProviderModel);
    log.debug("Populated ldapCompConfig : {}", ldapCompConfig);

    ComponentRepresentation componentRepresentation =
        populateLdapGroupMapperComponentRepresentation(ldapComponentRepresentation, ldapCompConfig);
    addLdapComponent(componentRepresentation);
    log.debug("Successfully added LDAP group mapper");
  }

  public void addIdpGroupMapper(AuthProviderModel authProviderModel) {
    RealmResource realmResource = getRealm();
    IdentityProviderRepresentation idpComponentRepresentation = getSamlIdentityProvider();
    Map<String, String> idpMapperConfig = populateIdpMapperConfig(authProviderModel);
    log.debug("Populated idpGroupMapperConfig : {}", idpMapperConfig);

    IdentityProviderMapperRepresentation identityProviderMapperRepresentation =
        populateIdentityProviderMapperRepresentation(
            authProviderModel, idpComponentRepresentation, idpMapperConfig);
    realmResource.identityProviders().get(samlName).addMapper(identityProviderMapperRepresentation);
    log.debug("Successfully added Idp group mapper");
  }

  public void addUserAttributeProtocolMapper(AuthProviderModel authProviderModel) {
    RealmResource realmResource = getRealm();
    Map<String, String> protocolMapperConfig =
        populateProtocolMapperConfig(authProviderModel.getConfig().get(GROUP_ATTRIBUTE));
    protocolMapperConfig.put("user.attribute", authProviderModel.getConfig().get(GROUP_ATTRIBUTE));
    protocolMapperConfig.put("jsonType.label", "");
    protocolMapperConfig.put("multivalued", Boolean.TRUE.toString());
    protocolMapperConfig.put("aggregate.attrs", Boolean.FALSE.toString());
    log.debug("Populated user attribute protocol mapper config : {}", protocolMapperConfig);

    ProtocolMapperRepresentation protocolMapperRepresentation =
        populateProtocolMapperRepresentation(
            protocolMapperConfig, USER_ATTRIBUTE_PROTOCOL_MAPPER, IDP_GROUP_PROTOCOL);

    createProtocolMapper(protocolMapperRepresentation, realmResource);
    log.debug("Successfully added Idp protocol mapper");
  }

  @NotNull
  private IdentityProviderMapperRepresentation populateIdentityProviderMapperRepresentation(
      AuthProviderModel authProviderModel,
      IdentityProviderRepresentation idpComponentRepresentation,
      Map<String, String> idpMapperConfig) {
    IdentityProviderMapperRepresentation identityProviderMapperRepresentation =
        new IdentityProviderMapperRepresentation();
    identityProviderMapperRepresentation.setIdentityProviderMapper(IDP_USER_ATTRIBUTE_MAPPER);
    identityProviderMapperRepresentation.setIdentityProviderAlias(
        idpComponentRepresentation.getAlias());
    identityProviderMapperRepresentation.setName(
        authProviderModel.getConfig().get(GROUP_ATTRIBUTE));
    identityProviderMapperRepresentation.setConfig(idpMapperConfig);
    return identityProviderMapperRepresentation;
  }

  @NotNull
  private Map<String, String> populateIdpMapperConfig(AuthProviderModel authProviderModel) {
    Map<String, String> idpMapperConfig = new HashMap<>();
    idpMapperConfig.put("attributes", "[{\"key\":\"\",\"value\":\"\"}]");
    idpMapperConfig.put("are.attribute.values.regex", Boolean.FALSE.toString());
    idpMapperConfig.put("role", "");
    idpMapperConfig.put("syncMode", "INHERIT");
    idpMapperConfig.put("attribute.name", authProviderModel.getConfig().get(GROUP_ATTRIBUTE));
    idpMapperConfig.put(
        "attribute.friendly.name", authProviderModel.getConfig().get(GROUP_ATTRIBUTE));
    idpMapperConfig.put("user.attribute", authProviderModel.getConfig().get(GROUP_ATTRIBUTE));
    idpMapperConfig.put("attribute.name.format", "ATTRIBUTE_FORMAT_BASIC");
    idpMapperConfig.put("claims", "");
    return idpMapperConfig;
  }

  public void addGroupMembershipProtocolMapper() {
    Map<String, String> protocolMapperConfig = populateProtocolMapperConfig(GROUPS);
    protocolMapperConfig.put("full.path", Boolean.TRUE.toString());
    log.debug("Populated Group membership protocol mapper config : {}", protocolMapperConfig);
    RealmResource realmResource = getRealm();
    ProtocolMapperRepresentation protocolMapperRepresentation =
        populateProtocolMapperRepresentation(
            protocolMapperConfig, GROUP_MEMBERSHIP_PROTOCOL_MAPPER, LDAP_GROUP_PROTOCOL);
    createProtocolMapper(protocolMapperRepresentation, realmResource);
    log.debug("Successfully added LDAP group protocol mapper");
  }

  private void createProtocolMapper(
      ProtocolMapperRepresentation protocolMapperRepresentation, RealmResource realmResource) {
    realmResource.clientScopes().findAll().stream()
        .filter(
            clientScopeRepresentation ->
                clientScopeRepresentation.getName().trim().equalsIgnoreCase("roles"))
        .findFirst()
        .ifPresent(
            clientScopeRep ->
                realmResource
                    .clientScopes()
                    .get(clientScopeRep.getId())
                    .getProtocolMappers()
                    .createMapper(protocolMapperRepresentation));
  }

  @NotNull
  private ProtocolMapperRepresentation populateProtocolMapperRepresentation(
      Map<String, String> protocolMapperConfig, String protocolMapper, String name) {
    ProtocolMapperRepresentation protocolMapperRepresentation = new ProtocolMapperRepresentation();
    protocolMapperRepresentation.setName(name);
    protocolMapperRepresentation.setProtocol(OPENID_CONNECT);
    protocolMapperRepresentation.setProtocolMapper(protocolMapper);
    protocolMapperRepresentation.setConfig(protocolMapperConfig);
    return protocolMapperRepresentation;
  }

  @NotNull
  private Map<String, String> populateProtocolMapperConfig(String claimName) {
    Map<String, String> protocolMapperConfig = new HashMap<>();
    protocolMapperConfig.put("claim.name", claimName);
    protocolMapperConfig.put("id.token.claim", Boolean.TRUE.toString());
    protocolMapperConfig.put("access.token.claim", Boolean.TRUE.toString());
    protocolMapperConfig.put("userinfo.token.claim", Boolean.TRUE.toString());
    return protocolMapperConfig;
  }

  @NotNull
  private ComponentRepresentation populateLdapGroupMapperComponentRepresentation(
      ComponentRepresentation ldapComponentRepresentation,
      MultivaluedHashMap<String, String> ldapCompConfig) {
    ComponentRepresentation componentRepresentation = new ComponentRepresentation();
    componentRepresentation.setName(LDAP_GROUP_MAPPER);
    componentRepresentation.setConfig(ldapCompConfig);
    componentRepresentation.setParentId(ldapComponentRepresentation.getId());
    componentRepresentation.setProviderType(LDAP_STORAGE_MAPPER);
    componentRepresentation.setProviderId(GROUP_LDAP_MAPPER_PROVIDER);
    return componentRepresentation;
  }

  @NotNull
  private MultivaluedHashMap<String, String> populateLdapCompConfig(
      AuthProviderModel authProviderModel) {
    Map<String, String> ldapAuthConfig = authProviderModel.getConfig();
    MultivaluedHashMap<String, String> ldapCompConfig = new MultivaluedHashMap<>();
    if (ldapAuthConfig.containsKey("groupDN")) {
      ldapCompConfig.add("groups.dn", ldapAuthConfig.get("groupDN"));
    }

    if (ldapAuthConfig.containsKey("groupSearchFilter")) {
      ldapCompConfig.add("membership.ldap.attribute", ldapAuthConfig.get("groupSearchFilter"));
    }

    if (ldapAuthConfig.containsKey("groupRoleNameAttribute")) {
      ldapCompConfig.add("group.name.ldap.attribute", ldapAuthConfig.get("groupRoleNameAttribute"));
    }

    if (ldapAuthConfig.containsKey("groupObjectClasses")) {
      ldapCompConfig.add("group.object.classes", ldapAuthConfig.get("groupObjectClasses"));
    }

    if (ldapAuthConfig.containsKey("membershipUserLdapAttribute")) {
      ldapCompConfig.add(
          "membership.user.ldap.attribute", ldapAuthConfig.get("membershipUserLdapAttribute"));
    }

    if (ldapAuthConfig.containsKey("membershipAttributeType")) {
      ldapCompConfig.add(
          "membership.attribute.type", ldapAuthConfig.get("membershipAttributeType"));
    }

    if (ldapAuthConfig.containsKey("mode")) {
      ldapCompConfig.add("mode", ldapAuthConfig.get("mode"));
    }

    if (ldapAuthConfig.containsKey("userGroupsRetrieveStrategy")) {
      ldapCompConfig.add(
          "user.roles.retrieve.strategy", ldapAuthConfig.get("userGroupsRetrieveStrategy"));
    }

    if (ldapAuthConfig.containsKey("memberOfLdapAttribute")) {
      ldapCompConfig.add("memberof.ldap.attribute", ldapAuthConfig.get("memberOfLdapAttribute"));
    }

    ldapCompConfig.add("preserve.group.inheritance", Boolean.TRUE.toString());
    ldapCompConfig.add("ignore.missing.groups", Boolean.FALSE.toString());
    ldapCompConfig.add("groups.ldap.filter", "");
    ldapCompConfig.add("mapped.group.attributes", "");
    ldapCompConfig.add("drop.non.existing.groups.during.sync", Boolean.FALSE.toString());
    ldapCompConfig.add("groups.path", "/");
    return ldapCompConfig;
  }
}
