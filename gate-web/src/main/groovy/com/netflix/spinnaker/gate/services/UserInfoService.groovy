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

import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.google.gson.JsonObject
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

  @Autowired
  OpsmxOesService opsmxOesService

  Gson gson = new Gson()

  UserInfoDetailsModel getAllInfoOfUser(User user) throws Exception {
    UserInfoDetailsModel userInfoDetails = new UserInfoDetailsModel()
    try {
      def response = opsmxOesService.getOesResponse5(
        "accountsConfig", "v3", "spinnaker", "cloudProviderAccount", false, false)

      /*ResponseEntity<List<CloudProviderAccountModel>> entityResponse = (ResponseEntity<List<CloudProviderAccountModel>>) response

      if (entityResponse.statusCode.'2xxSuccessful' && entityResponse.body != null) {
      List<String> extractedCloudAccounts =
        entityResponse.getBody().stream()
          .map { account -> "accountType: ${account.accountType}, name: ${account.name}" }
          .collect(Collectors.toList())

      userInfoDetails.setCloudAccounts(extractedCloudAccounts)
      }*/

      //List<CloudProviderAccountModel> accountModels = []

      // Iterate through the response and create CloudProviderAccountModel instances
      /*log.info("CloudProviderAccounts: {}", response)
      def cloudProviderAccountList = response.collect { item ->
        new CloudProviderAccountModel(
          name: item.name,
          accountType: item.accountType
        )
      }
      log.info("cloudProviderAccountList: {}", cloudProviderAccountList)
      def cloudAccounts= cloudProviderAccountList.collect { it.toString() }
      log.info("cloudAccounts: {}", cloudAccounts)*/
      log.info("CloudProviderAccounts: {}", response)
      def inputStr = gson.toJson(response)
      def extractedCloudAccounts = gson.fromJson(inputStr, JsonArray.class)

      def cloudAccounts = extractedCloudAccounts.collect{
        def accountType = it.getAsJsonPrimitive("accountType").getAsString()
        def name = it.getAsJsonPrimitive("name").getAsString()
        "Account Type: $accountType, Name: $name"}

    userInfoDetails.setCloudAccounts(cloudAccounts)
    }catch (Exception e) {
      e.printStackTrace()
    }
    userInfoDetails.setUserName(user.username)
    userInfoDetails.setFirstName(user.firstName)
    userInfoDetails.setLastName(user.lastName)
    userInfoDetails.setUserMailId(user.email)
    userInfoDetails.setUserRoles(user.roles)

  return userInfoDetails
}
}
