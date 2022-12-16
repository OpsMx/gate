/*
 * Copyright 2022 Netflix, Inc.
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

package com.opsmx.spinnaker.gate;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.Transient;
import org.springframework.security.core.authority.AuthorityUtils;

/*
   A marker for Authentications that should never be stored across requests, for example a bearer token authentication
*/
@Transient
public class CustomApiTokenAuthentication extends AbstractAuthenticationToken {

  private String apiToken;

  /*
   GrantedAuthority as being a "permission" or a "right". Those "permissions" are (normally) expressed as
   strings (with the getAuthority() method). Those strings let you identify the permissions and let your
   voters decide if they grant access to something.
  */
  public CustomApiTokenAuthentication(String apiToken, boolean authenticated) {
    super(AuthorityUtils.NO_AUTHORITIES);
    this.apiToken = apiToken;
    setAuthenticated(authenticated);
  }

  public CustomApiTokenAuthentication(String apiToken) {
    super(AuthorityUtils.NO_AUTHORITIES);
    this.apiToken = apiToken;
    setAuthenticated(false);
  }

  public CustomApiTokenAuthentication() {
    super(AuthorityUtils.NO_AUTHORITIES);
    setAuthenticated(false);
  }

  @Override
  public Object getCredentials() {
    return apiToken;
  }

  /*

   The identity of the principal being authenticated. In the case of an authentication
   request with username and password, this would be the username.
  */
  @Override
  public Object getPrincipal() {
    return "admin";
  }
}
