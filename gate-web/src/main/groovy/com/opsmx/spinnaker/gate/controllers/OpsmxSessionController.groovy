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


import groovy.util.logging.Slf4j
import io.swagger.annotations.ApiOperation
import org.springframework.beans.factory.annotation.Value
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpSession
import java.sql.Timestamp

@RequestMapping("/session")
@RestController
@Slf4j
class OpsmxSessionController {

  @Value('${server.session.timeout-in-seconds:3600}')
  private int sessionTimeout

  @ApiOperation(value = "get session timeout")
  @GetMapping(value = "/getSessionTimeout")
  Integer getSessionTimeout() {
    log.info("sessionTimeout : {}" , sessionTimeout)
    return sessionTimeout
  }

  @ApiOperation(value = "extend session by given duration")
  @GetMapping(value = "/extendSession")
  void extendSession(@RequestParam("duration") int duration, HttpServletRequest request) {
    HttpSession session = request.getSession()

    int currentInterval = session.getMaxInactiveInterval()
    int extendedInterval = currentInterval + duration

    session.setMaxInactiveInterval(extendedInterval)

    Timestamp time = new Timestamp(session.getLastAccessedTime())
    log.info("Last access time: {}" , time.toString())
  }

}
