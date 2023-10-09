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

import com.netflix.spinnaker.gate.model.CloudProviderAccountModel
import com.netflix.spinnaker.gate.model.UserInfoDetailsModel
import com.netflix.spinnaker.gate.services.internal.OpsmxOesService
import com.netflix.spinnaker.security.User
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service

@Slf4j
@Service
class UserInfoService {

  private OpsmxOesService opsmxOesService
  @Autowired
  UserInfoService(OpsmxOesService opsmxOesService) {
    this.opsmxOesService = opsmxOesService
  }

  UserInfoDetailsModel getAllInfoOfUser(User user) throws InterruptedException {
    UserInfoDetailsModel userInfoDetails = new UserInfoDetailsModel()
    ResponseEntity<List<Object>> cloudProviderAccountModelList

    try {
      cloudProviderAccountModelList = opsmxOesService.getOesResponse5("accountsConfig", "v3", "spinnaker", "cloudProviderAccount") as ResponseEntity<List<Object>>
    } catch (Exception e) {
      // Handle the InterruptedException as needed
      // You can re-throw it or handle it here
      e.printStackTrace()
    }

    if (cloudProviderAccountModelList.statusCode.is2xxSuccessful() && cloudProviderAccountModelList.body != null) {
      List<CloudProviderAccountModel> convertedList = cloudProviderAccountModelList.body.collect { item ->
        // Assuming CloudProviderAccount is a class with accountType and name properties
        new CloudProviderAccountModel(
          accountType: item.accountType,
          name: item.name
        )
      }

      userInfoDetails.setCloudAccounts(convertedList as Collection<String>)
      }

      userInfoDetails.setUserName(user.username)
      userInfoDetails.setFirstName(user.firstName)
      userInfoDetails.setLastName(user.lastName)
      userInfoDetails.setUserMailId(user.email)
      userInfoDetails.setUserRoles(user.roles)

    return userInfoDetails
  }
}
