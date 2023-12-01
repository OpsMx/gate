/*
 * Copyright 2018 Google, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License")
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
package com.netflix.spinnaker.gate.security.noauth


import com.netflix.spinnaker.gate.security.AllowedAccountsSupport
import com.netflix.spinnaker.gate.services.PermissionService
import com.netflix.spinnaker.security.User
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.security.SecurityProperties
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.authentication.AuthenticationServiceException
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.AuthenticationException

class NoAuthProvider implements AuthenticationProvider {

  private final SecurityProperties securityProperties;

  private final Logger log = LoggerFactory.getLogger(getClass());

  @Autowired
  PermissionService permissionService;

  @Autowired
  NoAuthConfig.NoAuthConfigProps noAuthConfigProps;

  @Autowired
  AllowedAccountsSupport allowedAccountsSupport;

  public NoAuthProvider(SecurityProperties securityProperties, NoAuthConfig.NoAuthConfigProps noAuthConfigProps,
                        PermissionService permissionService,AllowedAccountsSupport allowedAccountsSupport) {
    this.securityProperties = securityProperties;
    this.noAuthConfigProps = noAuthConfigProps;
    this.permissionService = permissionService;
    this.allowedAccountsSupport = allowedAccountsSupport;

    if (securityProperties.getUser() == null) {
      throw new AuthenticationServiceException("User credentials are not configured");
    }
  }

  @Override
  public Authentication authenticate(Authentication authentication) throws AuthenticationException {
    String name = authentication.getName();
    String password =
        authentication.getCredentials() != null ? authentication.getCredentials().toString() : null;

    List<String> roles = new ArrayList<>();
    def fiatRoles = permissionService.getRoles(name)?.collect{ it.name }
    if (fiatRoles) {
      roles = fiatRoles
    }
   permissionService.loginWithRoles(name, roles);
    log.debug("roles " + roles)
    User user = new User();
    user.setUsername(name);
    user.setEmail(noAuthConfigProps.email);
    user.setRoles(roles);
    user.setAllowedAccounts(allowedAccountsSupport.filterAllowedAccounts(name, roles));
    return new UsernamePasswordAuthenticationToken(user, password, new ArrayList<>());
  }

  @Override
  public boolean supports(Class<?> authentication) {
    return authentication == UsernamePasswordAuthenticationToken.class;
  }
}
