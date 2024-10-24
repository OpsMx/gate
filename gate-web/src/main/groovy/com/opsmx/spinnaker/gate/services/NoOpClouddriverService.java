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

package com.opsmx.spinnaker.gate.services;

import com.netflix.spinnaker.gate.services.internal.ClouddriverService;
import com.netflix.spinnaker.kork.plugins.SpinnakerPluginDescriptor;
import java.util.List;
import java.util.Map;
import retrofit.client.Response;

public class NoOpClouddriverService implements ClouddriverService {
  @Override
  public List<Account> getAccounts() {
    return List.of();
  }

  @Override
  public List<AccountDetails> getAccountDetails() {
    return List.of();
  }

  @Override
  public AccountDetails getAccount(String account) {
    return null;
  }

  @Override
  public List<AccountDefinition> getAccountDefinitionsByType(
      String type, Integer limit, String startingAccountName) {
    return List.of();
  }

  @Override
  public AccountDefinition createAccountDefinition(AccountDefinition accountDefinition) {
    return null;
  }

  @Override
  public AccountDefinition updateAccountDefinition(AccountDefinition accountDefinition) {
    return null;
  }

  @Override
  public Response deleteAccountDefinition(String account) {
    return null;
  }

  @Override
  public Map getTaskDetails(String taskDetailsId) {
    return Map.of();
  }

  @Override
  public List getApplications(boolean expand) {
    return List.of();
  }

  @Override
  public List getAllApplicationsUnrestricted(boolean expand) {
    return List.of();
  }

  @Override
  public Map getApplication(String name) {
    return Map.of();
  }

  @Override
  public Map getClusters(String name) {
    return Map.of();
  }

  @Override
  public List getClustersForAccount(String name, String account) {
    return List.of();
  }

  @Override
  public List getCluster(String name, String account, String cluster) {
    return List.of();
  }

  @Override
  public List getScalingActivities(
      String application,
      String account,
      String cluster,
      String provider,
      String serverGroupName,
      String region) {
    return List.of();
  }

  @Override
  public Map getClusterByType(String name, String account, String cluster, String type) {
    return Map.of();
  }

  @Override
  public List getServerGroup(
      String name, String account, String cluster, String type, String serverGroupName) {
    return List.of();
  }

  @Override
  public Map getTargetServerGroup(
      String application,
      String account,
      String cluster,
      String type,
      String scope,
      String target,
      Boolean onlyEnabled,
      Boolean validateOldest) {
    return Map.of();
  }

  @Override
  public List<Map<String, Object>> getApplicationRawResources(String appName) {
    return List.of();
  }

  @Override
  public List getServerGroups(String name, String expand, String cloudProvider, String clusters) {
    return List.of();
  }

  @Override
  public List getServerGroups(List applications, List ids, String cloudProvider) {
    return List.of();
  }

  @Override
  public Map getJobDetails(
      String name, String account, String region, String jobName, String emptyStringForRetrofit) {
    return Map.of();
  }

  @Override
  public Map getServerGroupDetails(
      String appName,
      String account,
      String region,
      String serverGroupName,
      String includeDetails) {
    return Map.of();
  }

  @Override
  public List getClusterLoadBalancers(String appName, String account, String cluster, String type) {
    return List.of();
  }

  @Override
  public List<Map> getLoadBalancers(String provider) {
    return List.of();
  }

  @Override
  public List<Map> getApplicationLoadBalancers(String appName) {
    return List.of();
  }

  @Override
  public Map getLoadBalancer(String provider, String name) {
    return Map.of();
  }

  @Override
  public List<Map> getLoadBalancerDetails(
      String provider, String account, String region, String name) {
    return List.of();
  }

  @Override
  public Map getInstanceDetails(String account, String region, String instanceId) {
    return Map.of();
  }

  @Override
  public Map getConsoleOutput(String account, String region, String instanceId, String provider) {
    return Map.of();
  }

  @Override
  public List<Map> getImageDetails(String provider, String account, String region, String imageId) {
    return List.of();
  }

  @Override
  public List<Map> getProjectClusters(String project) {
    return List.of();
  }

  @Override
  public List<Map> getReservationReports(Map<String, String> filters) {
    return List.of();
  }

  @Override
  public List<Map> getReservationReports(String name, Map<String, String> filters) {
    return List.of();
  }

  @Override
  public List<Map> findImages(
      String provider,
      String query,
      String region,
      String account,
      Integer count,
      Map additionalFilters) {
    return List.of();
  }

  @Override
  public List<String> findTags(String provider, String account, String repository) {
    return List.of();
  }

  @Override
  public List<Map> search(
      String query, String type, String platform, Integer size, Integer offset, Map filters) {
    return List.of();
  }

  @Override
  public Map getSecurityGroups() {
    return Map.of();
  }

  @Override
  public Map getSecurityGroups(String account, String type) {
    return Map.of();
  }

  @Override
  public List getSecurityGroupsForRegion(String account, String type, String region) {
    return List.of();
  }

  @Override
  public Map getSecurityGroup(
      String account, String type, String name, String region, String vpcId) {
    return Map.of();
  }

  @Override
  public List<Map> getServerGroupManagersForApplication(String application) {
    return List.of();
  }

  @Override
  public List<Map> getInstanceTypes() {
    return List.of();
  }

  @Override
  public List<Map> getKeyPairs() {
    return List.of();
  }

  @Override
  public List<Map> getSubnets() {
    return List.of();
  }

  @Override
  public List<Map> getSubnets(String cloudProvider) {
    return List.of();
  }

  @Override
  public Map getNetworks() {
    return Map.of();
  }

  @Override
  public List<Map> getNetworks(String cloudProvider) {
    return List.of();
  }

  @Override
  public List<Map> findAllCloudMetrics(
      String cloudProvider, String account, String region, Map<String, String> filters) {
    return List.of();
  }

  @Override
  public Map getCloudMetricStatistics(
      String cloudProvider,
      String account,
      String region,
      String metricName,
      Long startTime,
      Long endTime,
      Map<String, String> filters) {
    return Map.of();
  }

  @Override
  public List<Map> listEntityTags(Map allParameters) {
    return List.of();
  }

  @Override
  public Map getEntityTags(String id) {
    return Map.of();
  }

  @Override
  public List<Map> getCertificates() {
    return List.of();
  }

  @Override
  public List<Map> getCertificates(String cloudProvider) {
    return List.of();
  }

  @Override
  public Response getStaticData(String id, Map<String, String> filters) {
    return null;
  }

  @Override
  public Response getAdhocData(String groupId, String bucketId, String objectId) {
    return null;
  }

  @Override
  public List<String> getStorageAccounts() {
    return List.of();
  }

  @Override
  public List<Map> getArtifactCredentials() {
    return List.of();
  }

  @Override
  public Response getArtifactContent(Map artifact) {
    return null;
  }

  @Override
  public List<String> getArtifactNames(String accountName, String type) {
    return List.of();
  }

  @Override
  public List<String> getArtifactVersions(String accountName, String type, String artifactName) {
    return List.of();
  }

  @Override
  public List<Map> getRoles(String cloudProvider) {
    return List.of();
  }

  @Override
  public List<Map> getAllEcsClusters() {
    return List.of();
  }

  @Override
  public List<Map> getEcsAllMetricAlarms() {
    return List.of();
  }

  @Override
  public List<Map> getAllEcsSecrets() {
    return List.of();
  }

  @Override
  public List<Map> getEcsClusterDescriptions(String account, String region) {
    return List.of();
  }

  @Override
  public List<Map> getAllEcsServiceDiscoveryRegistries() {
    return List.of();
  }

  @Override
  public Map getManifest(String account, String location, String name) {
    return Map.of();
  }

  @Override
  public List<Map> getServerGroupEvents(
      String application, String account, String serverGroupName, String region, String provider) {
    return List.of();
  }

  @Override
  public List<Map> listServices(String cloudProvider, String region, String account) {
    return List.of();
  }

  @Override
  public Map getServiceInstance(
      String account, String cloudProvider, String region, String serviceInstanceName) {
    return Map.of();
  }

  @Override
  public List<Map> getFunctions(String functionName, String region, String account) {
    return List.of();
  }

  @Override
  public List<Map> getApplicationFunctions(String appName) {
    return List.of();
  }

  @Override
  public List<SpinnakerPluginDescriptor> getInstalledPlugins() {
    return List.of();
  }
}
