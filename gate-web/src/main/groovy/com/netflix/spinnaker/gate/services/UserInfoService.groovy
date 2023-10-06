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

package com.netflix.spinnaker.gate.services

import com.netflix.spinnaker.gate.model.ApprovalGateTriggerResponseModel
import com.netflix.spinnaker.gate.model.UserInfoDetailsModel
import com.netflix.spinnaker.security.User
import groovy.util.logging.Slf4j
import org.springframework.cloud.openfeign.EnableFeignClients
import org.springframework.stereotype.Component

@Slf4j
@Component
@EnableFeignClients
class UserInfoService {


  UserInfoDetailsModel getAllInfoOfUser(User user) throws InterruptedException {
    UserInfoDetailsModel userInfoDetails = new UserInfoDetailsModel()

    try {
      //ResponseEntity<List<CloudProviderAccountModel>> cloudProviderAccountModelList = saporClient.getCloudProviderAccounts(user.username)
      //List<String> extractedCloudAccounts = null

      /*if (cloudProviderAccountModelList.statusCode.is2xxSuccessful() && cloudProviderAccountModelList.body) {
        extractedCloudAccounts = cloudProviderAccountModelList.body.stream()
          .map { account -> "accountType: ${account.accountType}, name: ${account.name}" }
          .collect(Collectors.toList())

        userInfoDetails.cloudAccounts = extractedCloudAccounts
      }*/

      userInfoDetails.setUserName(user.username)
      userInfoDetails.setFirstName(user.firstName)
      userInfoDetails.setLastName(user.lastName)
      userInfoDetails.setUserMailId(user.email)
      userInfoDetails.setUserRoles(user.roles)

    } catch (InterruptedException e) {
      // Handle the InterruptedException as needed
      // You can re-throw it or handle it here
      throw e
    }

    return userInfoDetails
  }
}