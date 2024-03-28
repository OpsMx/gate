/*
 * Copyright 2020 Netflix, Inc.
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

package com.netflix.spinnaker.gate.controllers

import groovy.util.logging.Slf4j
import io.swagger.v3.oas.annotations.Operation
import org.apache.commons.lang3.exception.ExceptionUtils
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

import jakarta.servlet.http.HttpServletResponse

@Slf4j
@RestController
@RequestMapping("/auth")
class OpsmxAuthController {

  @Operation(summary = "Redirect to Deck")
  @RequestMapping(value = "/redirectauto", method = RequestMethod.GET)
  void redirectAuto(HttpServletRequest request, HttpServletResponse response, @RequestParam String to) {
    log.info("to url : {}", to)
    validAutoRedirect(to,request.getRequestURL().toString()) ?
      response.sendRedirect(to) :
      response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Requested redirect address not recognized.")
  }

  boolean validAutoRedirect(String to,String from) {
    URL toURL
    URL hostURL
    try {
      toURL = new URL(to)
      hostURL = new URL(from)
      if (!toURL.getHost().equals(hostURL.getHost())) {
        return false
      }
    } catch (MalformedURLException malEx) {
      log.warn "Malformed redirect URL: $to\n${ExceptionUtils.getStackTrace(malEx)}"
      return false
    }

//    log.info([
//      "validateDeckRedirect(${to})",
//      "toUrl(host: ${toURL.host}, port: ${toURL.port})",
//      "deckBaseUrl(host: ${deckBaseUrl.host}, port: ${deckBaseUrl.port})",
//      "redirectHostPattern(${redirectHostPattern?.pattern()})"
//    ].join(" - ")
//    )
    return true
  }
}
