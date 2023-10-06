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

package com.netflix.spinnaker.gate.model

import groovy.transform.Canonical

@Canonical
class UserInfoDetailsModel {

  private String userMailId
  private String userName
  private String firstName


  void setUserMailId(String userMailId) {
    this.userMailId = userMailId
  }

  void setUserName(String userName) {
    this.userName = userName
  }

  String getUserMailId() {
    return userMailId
  }

  String getUserName() {
    return userName
  }

  String getFirstName() {
    return firstName
  }

  String getLastName() {
    return lastName
  }

  Collection<String> getUserRoles() {
    return userRoles
  }

  Collection<String> getCloudAccounts() {
    return cloudAccounts
  }

  void setFirstName(String firstName) {
    this.firstName = firstName
  }

  void setLastName(String lastName) {
    this.lastName = lastName
  }

  void setUserRoles(Collection<String> userRoles) {
    this.userRoles = userRoles
  }

  void setCloudAccounts(Collection<String> cloudAccounts) {
    this.cloudAccounts = cloudAccounts
  }
  private String lastName;

  protected Collection<String> userRoles = new ArrayList<>()
  protected Collection<String> cloudAccounts = new ArrayList<>()
}
