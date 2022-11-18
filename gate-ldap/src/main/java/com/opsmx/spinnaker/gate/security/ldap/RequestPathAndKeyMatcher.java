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
 *
 */

package com.opsmx.spinnaker.gate.security.ldap;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.web.util.matcher.RequestMatcher;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Slf4j
public class RequestPathAndKeyMatcher implements RequestMatcher {
  private String principalRequestHeader;
  List<String> allowedUrlPaths;

  public RequestPathAndKeyMatcher(String principalRequestHeader, List<String> allowedUrlPaths) {
    this.principalRequestHeader = principalRequestHeader;
    this.allowedUrlPaths = allowedUrlPaths;
  }

  @Override
  public boolean matches(HttpServletRequest request) {
    String uri = request.getRequestURI();
    String header = request.getHeader(principalRequestHeader);
    log.info("********************The uri is: {}" , uri);
    log.info("********************The header is: {}" , header);
    for(String path: allowedUrlPaths){
      log.info("********************* the path is: {}" , path);
      if(uri.endsWith(path) && header != null){
        log.info("********************The header and path matched !!!");
        return true;
      }
    }
    return false;
  }
}
