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

package com.opsmx.spinnaker.gate.filters

import groovy.util.logging.Slf4j

import javax.servlet.*
import javax.servlet.http.HttpFilter
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Slf4j
class TokenAuthFilter extends HttpFilter {

  List<String> allowedUrlPaths = Arrays.asList("gate/platformservice/v6/applications", "")
  String token = "topsecret"

  @Override
  void doFilter(HttpServletRequest request, HttpServletResponse response,
                FilterChain chain) throws IOException, ServletException {
    String uri = request.getRequestURI()
    log.info("********************The uri is: {}" , uri)
    for(String path: allowedUrlPaths){
      log.info("********************* the path is: {}" , path)
      if(uri.endsWith(path) && authenticate(request)){
        return
      }
    }
    chain.doFilter(request, response)
  }

  boolean authenticate(HttpServletRequest request){
    String token = request.getHeader("OPSMX_TOKEN");
    log.info("********************The token is: {}" , token)
    return (token != null & token.equals(this.token))
  }
}
