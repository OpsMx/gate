/*
 * Copyright 2024 Netflix, Inc.
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

import com.netflix.spinnaker.gate.services.internal.OpsmxNotificationsAlertservice
import groovy.util.logging.Slf4j
import io.swagger.v3.oas.annotations.Operation
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RestController
import org.slf4j.Logger
import org.slf4j.LoggerFactory





@RequestMapping("/notifications")
@RestController
@Slf4j
@ConditionalOnExpression('${services.notifications.enabled:false}')
class OpsmxNotificationsAlertController {

  @Autowired
  OpsmxNotificationsAlertservice opsmxNotificationsAlertservice

  @Operation(summary = "Endpoint for notification rest services")
  @RequestMapping(value = "/{type}", method = RequestMethod.POST)
  Object postNotificationServiceResponse1(@PathVariable("type") String type,
                                          @RequestBody(required = false) Object data) {
    println "Received a POST request for notification type: ${type} with data: ${data}"
    return opsmxNotificationsAlertservice.postNotificationServiceResponse1(type, data)
  }

  @Operation(summary = "Endpoint for notification rest services")
  @RequestMapping(value = "/{type}/{id}", method = RequestMethod.PUT)
  Object putNotificationServiceResponse1(@PathVariable("type") String type,
                                         @PathVariable("id") String id,
                                          @RequestBody(required = false) Object data) {
    println "Received a PUT request for notification type: ${type} with data: ${data}"
    return opsmxNotificationsAlertservice.putNotificationServiceResponse2(type,id, data)
  }

  @Operation(summary = "Endpoint for notification services")
  @RequestMapping(value = "/{type}", method = RequestMethod.GET)
  Object getNotificationServiceResponse1(@PathVariable("type") String type) {
    println "Received a GET request for notification type: ${type} "
    return  opsmxNotificationsAlertservice.getNotificationServiceResponse3(type)
  }

  @Operation(summary = "Endpoint for notification rest services")
  @RequestMapping(value = "/{type}/{id}", method = RequestMethod.DELETE)
  Object deleteNotificationServiceResponse1(@PathVariable("type") String type,
                                            @PathVariable("id") String id) {
    println "Received a DELETE request for notification type: ${type} with data: ${data}"
    return opsmxNotificationsAlertservice.deleteNotificationServiceResponse4(type,id)
  }


}
