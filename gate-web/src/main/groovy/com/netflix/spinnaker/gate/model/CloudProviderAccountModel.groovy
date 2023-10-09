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
import lombok.Data

@Canonical
@Data
class CloudProviderAccountModel {
  private int id;
  private List<String> spinnaker
  private String name
  private String environment
  private String assumeRole
  private boolean primaryAccount
  private String accountType
  private boolean remotecluster = false
  private String[] namespaces
  private String agentName
  private String accountId
  private String awsAccountType
  private String[] regions

  void setName(String name) {
    this.name = name
  }

  void setAccountType(String accountType) {
    this.accountType = accountType
  }
  private String accessKey
  private String secretKey

  String getName() {
    return name
  }

  String getAccountType() {
    return accountType
  }
  private String bakeryAccessKey
  private String bakerySecretKey
  private String appKey
  private String clientId
  private String defaultKeyVault
  private String tenantId
  private String subscriptionId
  private String JSONPath
  private String[] read
  private String[] write
  private String[] execute
  private String agentStatus
  private Boolean agentConfigured = false
  private String awsAccount
  private int agentId
  private String projectId
  private String gcpFileName
  private String defaultResourceGroup
  private String packerResourceGroup
  private String objectId
  private String packerStorageAccount
  private Boolean useSshPublicKey
  private String[] omitNamespaces
  private Boolean dynamicAccount = false
  private Boolean validateFlag = true
  private List<String> permissions
  private UserGroupPermissionModel groupPermissions
  private Boolean isAgentPermissionEnabled = false

}
