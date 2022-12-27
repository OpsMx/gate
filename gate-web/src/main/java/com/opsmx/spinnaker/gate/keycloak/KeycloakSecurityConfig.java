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

package com.opsmx.spinnaker.gate.keycloak;

// @Primary
// @Configuration
// @EnableWebSecurity
// @SpinnakerAuthConfig
public class KeycloakSecurityConfig /*extends WebSecurityConfigurerAdapter*/ {

  //  @Autowired private AuthConfig authConfig;
  //
  //  @Autowired private KeycloakOauthAuthenticationProvider authProvider;
  //
  //  @Bean
  //  protected SessionAuthenticationStrategy sessionAuthenticationStrategy() {
  //    return new RegisterSessionAuthenticationStrategy(new SessionRegistryImpl());
  //  }
  //
  //  //  @Bean
  //  //  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
  //  //    authConfig.configure(http);
  //  //    http.authorizeRequests().antMatchers("/**").permitAll();
  //  //    http.oauth2Login();
  //  //    return http.build();
  //  //  }
  //
  //  @Override
  //  protected void configure(HttpSecurity http) throws Exception {
  //    authConfig.configure(http);
  //    //    http.authorizeRequests().antMatchers("/**").authenticated();
  //    http.authorizeRequests().antMatchers("/keycloak/oauth2/**").permitAll();
  //
  //    http.oauth2Login().defaultSuccessUrl("/application");
  //  }
  //
  //  @Override
  //  protected void configure(AuthenticationManagerBuilder auth) throws Exception {
  //    super.configure(auth);
  //    auth.authenticationProvider(authProvider);
  //  }
  //
  //  @Slf4j
  //  @Configuration
  //  static class KeycloakOauthAuthenticationProvider implements AuthenticationProvider {
  //
  //    @Autowired private PermissionService permissionService;
  //
  //    @Autowired private OesAuthorizationService oesAuthorizationService;
  //
  //    @Override
  //    public Authentication authenticate(Authentication authentication)
  //        throws AuthenticationException {
  //      OAuth2LoginAuthenticationToken authorizationCodeAuthentication =
  //          (OAuth2LoginAuthenticationToken) authentication;
  //      OAuth2AccessToken accessToken = authorizationCodeAuthentication.getAccessToken();
  //
  //      log.info("accessToken : {}", accessToken);
  //
  //      List<String> roles = new ArrayList<>();
  //      roles.add("admin");
  //
  //      User user = new User();
  //      user.setEmail("pranav.bhaskaran@opsmx.io");
  //      user.setUsername("pranavuser");
  //      user.setRoles(roles);
  //
  //      List<GrantedAuthority> grantedAuthorities = new ArrayList<>();
  //
  //      if (!roles.isEmpty() && permissionService != null) {
  //        grantedAuthorities =
  //            roles.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList());
  //        // Updating roles in fiat service
  //        permissionService.loginWithRoles("pranavuser", roles);
  //        // Updating roles in platform service
  //        oesAuthorizationService.cacheUserGroups(roles, "pranavuser");
  //      }
  //
  //      return new UsernamePasswordAuthenticationToken(user, "admin", grantedAuthorities);
  //    }
  //
  //    @Override
  //    public boolean supports(Class<?> authentication) {
  //      return OAuth2LoginAuthenticationToken.class.isAssignableFrom(authentication);
  //    }
  //  }
}
