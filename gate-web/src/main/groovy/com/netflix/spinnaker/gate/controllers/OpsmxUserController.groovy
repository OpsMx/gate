/*
 * Copyright 2023 Netflix, Inc.
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

import com.netflix.spinnaker.gate.security.SpinnakerUser
import com.netflix.spinnaker.gate.services.PermissionService
import com.netflix.spinnaker.gate.services.UserInfoService
import com.netflix.spinnaker.gate.services.internal.OpsmxOesService
import com.netflix.spinnaker.security.User
import com.opsmx.spinnaker.gate.model.UserInfoDetailsModel
import groovy.util.logging.Slf4j
import io.swagger.annotations.ApiOperation
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RestController
import springfox.documentation.annotations.ApiIgnore

@RequestMapping("/user")
@RestController
@Slf4j
@ConditionalOnExpression('${services.opsmx.enabled:false}')
class OpsmxUserController {

  @Autowired
  PermissionService permissionService

  @Autowired
  UserInfoService userInfoService

  @Autowired
  OpsmxOesService opsmxOesService

  @ApiOperation(value = "Get user Details")
  @RequestMapping(value = "/userInfo", method = RequestMethod.GET)
  Object userInfo(@ApiIgnore @SpinnakerUser User user) {
    if (!user) {
      throw new Exception("UnAuthorized User")
    }
    def fiatRoles = permissionService.getRoles(user.username)?.collect{ it.name }
    if (fiatRoles) {
      user.roles = fiatRoles
    }
    def response = opsmxOesService.getOesResponse5(
      "accountsConfig", "v3", "spinnaker", "cloudProviderAccount", false, false)
    def userInfo = userInfoService.getAllInfoOfUser(user, response)
    log.info("UserInfo: {}", userInfo)
    return userInfo
  }
}
