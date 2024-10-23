/*
 * Copyright 2021 OpsMx, Inc.
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

package com.opsmx.spinnaker.gate.controllers


import com.netflix.spinnaker.gate.exceptions.OesRequestException
import groovy.util.logging.Slf4j
import io.swagger.v3.oas.annotations.Operation
import org.springframework.beans.factory.annotation.Value
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpSession

@RequestMapping("/session")
@RestController
@Slf4j
class OpsmxSessionController {

  @Value('${server.session.timeout-in-seconds:3600}')
  int sessionTimeout

  @Operation(summary = "get session timeout")
  @GetMapping(value = "/getSessionTimeout")
  Integer getSessionTimeout() {
    log.info("sessionTimeout : {}" , sessionTimeout)
    return sessionTimeout
  }

  @Operation(summary = "extend session by given duration")
  @PutMapping(value = "/extendSession")
  void extendSession(@RequestParam("duration") int duration, HttpServletRequest request) {
    log.info("*****************Start of the extend session of OpsmxSessionController*****");
    log.info("*****************Extend session duration :{}",duration);
    if(duration <= 0) {
      throw new OesRequestException("Invalid duration passed. Session cant be extended.")
    }
    HttpSession session = request.getSession()
    log.info("*****************is session New :{}",session.isNew());
    log.info("*****************existing session age :{}",session.getMaxInactiveInterval());
    log.info("*****************Extend session duration :{}",duration);
    session.setMaxInactiveInterval(duration)
    log.debug("Session timeout is overridden by {} seconds" , duration)
  }

}
