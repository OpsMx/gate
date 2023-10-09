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

package com.opsmx.spinnaker.gate.service;

import com.netflix.spinnaker.gate.services.internal.OpsmxOesService;
import com.netflix.spinnaker.security.User;
import com.opsmx.spinnaker.gate.model.CloudProviderAccountModel;
import com.opsmx.spinnaker.gate.model.UserInfoDetailsModel;
import groovy.util.logging.Slf4j;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class UserInfoService {

  @Autowired OpsmxOesService opsmxOesService;

  public UserInfoDetailsModel getAllInfoOfUser(User user) throws InterruptedException {
    UserInfoDetailsModel userInfoDetails = new UserInfoDetailsModel();
    // ResponseEntity<List<CloudProviderAccountModel>> cloudProviderAccountModelList;
    try {
      Object response =
          opsmxOesService.getOesResponse5(
              "accountsConfig", "v3", "spinnaker", "cloudProviderAccount", false, false);

      ResponseEntity<List<CloudProviderAccountModel>> entityResponse =
          (ResponseEntity<List<CloudProviderAccountModel>>) response;

      if (entityResponse.getStatusCode().is2xxSuccessful() && entityResponse.getBody() != null) {
        List<String> extractedCloudAccounts =
            entityResponse.getBody().stream()
                .map(
                    account ->
                        "accountType: " + account.getAccountType() + ", name: " + account.getName())
                .collect(Collectors.toList());

        userInfoDetails.setCloudAccounts(extractedCloudAccounts);
      }
    } catch (Exception e) {
      e.printStackTrace();
    }

    // Set other user-related information
    userInfoDetails.setUserName(user.getUsername());
    userInfoDetails.setFirstName(user.getFirstName());
    userInfoDetails.setLastName(user.getLastName());
    userInfoDetails.setUserMailId(user.getEmail());
    userInfoDetails.setUserRoles(user.getRoles());

    return userInfoDetails;
  }
}
