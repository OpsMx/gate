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
package com.netflix.spinnaker.gate.security.basic;

import com.netflix.spinnaker.gate.services.OesAuthorizationService;
import com.netflix.spinnaker.gate.services.PermissionService;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class BasicAuthProvider implements AuthenticationProvider {

  private final PermissionService permissionService;
  private final OesAuthorizationService oesAuthorizationService;

  @Value("${services.platform.enabled:false}")
  private boolean isPlatformEnabled;

  @Setter private List<String> roles;
  @Setter private String name;
  @Setter private String password;

  @Value("${services.fiat.enabled:false}")
  private Boolean isFiatEnabled;

  @Autowired
  public BasicAuthProvider(
      PermissionService permissionService, OesAuthorizationService oesAuthorizationService) {
    this.permissionService = permissionService;
    this.oesAuthorizationService = oesAuthorizationService;
  }

  @Override
  public Authentication authenticate(Authentication authentication) throws AuthenticationException {
    String name = authentication.getName();
    log.info("User entered --> "+name);
    String password =
        authentication.getCredentials() != null ? authentication.getCredentials().toString() : null;

    log.info("Password entered --> "+password);
    if (!this.name.equals(name) || !this.password.equals(password)) {
      throw new BadCredentialsException("Invalid username/password combination");
    }
    log.info("roles configured for user: {} are roles: {}", name, roles);

    List<GrantedAuthority> grantedAuthorities = new ArrayList<>();

    if (roles != null && !roles.isEmpty() && permissionService != null) {
      grantedAuthorities.addAll(
          roles.stream()
              .map(role -> new SimpleGrantedAuthority(role))
              .collect(Collectors.toList()));
      // Updating roles in fiat service
      permissionService.loginWithRoles(name, roles);
      log.info("Platform service enabled value :{}", isPlatformEnabled);
      // Updating roles in platform service
      if (isPlatformEnabled) {
        oesAuthorizationService.cacheUserGroups(roles, name);
      }
    } else {
      grantedAuthorities.add(new SimpleGrantedAuthority("USER"));
    }
    UserDetails principal = new User(name, password, grantedAuthorities);
    return new UsernamePasswordAuthenticationToken(principal, password, grantedAuthorities);
  }

  @Override
  public boolean supports(Class<?> authentication) {
    return authentication == UsernamePasswordAuthenticationToken.class;
  }
}
