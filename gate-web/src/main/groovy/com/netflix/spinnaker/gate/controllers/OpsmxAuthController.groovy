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
import io.swagger.annotations.ApiOperation
import org.apache.commons.lang3.exception.ExceptionUtils
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

import javax.servlet.http.HttpServletResponse
import javax.servlet.http.HttpServletRequest

@Slf4j
@RestController
@RequestMapping("/auth")
class OpsmxAuthController {

  @ApiOperation(value = "Redirect to Deck")
  @RequestMapping(value = "/redirectauto", method = RequestMethod.GET)
  void redirectAuto(HttpServletRequest request, HttpServletResponse response, @RequestParam String to) {
    log.info("to url : {}", to)
    String host = request.getHeader("authority")
    Enumeration headerNames = request.getHeaderNames()
    while (headerNames.hasMoreElements()) {
      System.out.println("Header  " + headerNames.nextElement())
      log.info("Header Value is :{}",request.getHeader(headerNames.nextElement()))
    }
    validAutoRedirect(to,host) ?
      response.sendRedirect(to) :
      response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Requested redirect address not recognized.")
  }

  boolean validAutoRedirect(String to,String host) {
    URL toURL
    try {
      toURL = new URL(to)
      log.info("HOST:{}",host)
      String domain = toURL.getHost()
      log.info("DOMAIN:{}",domain)
      if (!domain.equals(host)) {
        log.info("DOMAINS ARE DIFFERENT ")
      } else {
        log.info("DOMAINS ARE SAME")
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
