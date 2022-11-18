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

package com.opsmx.spinnaker.gate.security.ldap;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

public class APIKeyAuthenticationManager implements AuthenticationManager {
  private String credentialRequestValue;

  public APIKeyAuthenticationManager(String credentialRequestValue) {
    this.credentialRequestValue = credentialRequestValue;
  }

  @Override
  public Authentication authenticate(Authentication authentication)
    throws AuthenticationException {
    String credentials = (String) authentication.getCredentials();
    if (!credentialRequestValue.equals(credentials)) {
      throw new BadCredentialsException("The API key was not found or not the expected value.");
    }
    authentication.setAuthenticated(true);
    return authentication;
  }
}
