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

import com.netflix.spinnaker.fiat.model.resources.ServiceAccount;
import com.netflix.spinnaker.gate.services.internal.Front50Service;
import com.netflix.spinnaker.kork.plugins.SpinnakerPluginDescriptor;
import java.util.List;
import java.util.Map;
import retrofit.client.Response;

public class NoOpFront50Service implements Front50Service {
  @Override
  public List<Map> getCredentials() {
    return List.of();
  }

  @Override
  public List<Map> getAllApplicationsUnrestricted() {
    return List.of();
  }

  @Override
  public Map getApplication(String applicationName) {
    return Map.of();
  }

  @Override
  public List<Map> getApplicationHistory(String applicationName, int limit) {
    return List.of();
  }

  @Override
  public List<Map> getAllPipelineConfigs() {
    return List.of();
  }

  @Override
  public List<Map> getPipelineConfigsForApplication(String app, boolean refresh) {
    return List.of();
  }

  @Override
  public Response deletePipelineConfig(String app, String name) {
    return null;
  }

  @Override
  public Response savePipelineConfig(Map pipelineConfig) {
    return null;
  }

  @Override
  public Response movePipelineConfig(Map moveCommand) {
    return null;
  }

  @Override
  public List<Map> getPipelineConfigHistory(String pipelineConfigId, int limit) {
    return List.of();
  }

  @Override
  public Map updatePipeline(String pipelineId, Map pipeline) {
    return Map.of();
  }

  @Override
  public List<Map> getAllStrategyConfigs() {
    return List.of();
  }

  @Override
  public List<Map> getStrategyConfigs(String app) {
    return List.of();
  }

  @Override
  public Response deleteStrategyConfig(String app, String name) {
    return null;
  }

  @Override
  public Response saveStrategyConfig(Map strategyConfig) {
    return null;
  }

  @Override
  public Response moveStrategyConfig(Map moveCommand) {
    return null;
  }

  @Override
  public List<Map> getStrategyConfigHistory(String strategyConfigId, int limit) {
    return List.of();
  }

  @Override
  public Map updateStrategy(String strategyId, Map strategy) {
    return Map.of();
  }

  @Override
  public List<Map> getPipelineTemplates(String... scopes) {
    return List.of();
  }

  @Override
  public Map getPipelineTemplate(String pipelineTemplateId) {
    return Map.of();
  }

  @Override
  public List<Map<String, Object>> getPipelineTemplateDependents(
      String pipelineTemplateId, boolean recursive) {
    return List.of();
  }

  @Override
  public Map getV2PipelineTemplate(String pipelineTemplateId, String tag, String digest) {
    return Map.of();
  }

  @Override
  public List<Map> getV2PipelineTemplates(String... scopes) {
    return List.of();
  }

  @Override
  public Map<String, List<Map>> getV2PipelineTemplatesVersions(String... scopes) {
    return Map.of();
  }

  @Override
  public List<Map<String, Object>> getV2PipelineTemplateDependents(String pipelineTemplateId) {
    return List.of();
  }

  @Override
  public Map getNotificationConfigs(String type, String app) {
    return Map.of();
  }

  @Override
  public Response deleteNotificationConfig(String type, String app) {
    return null;
  }

  @Override
  public Response saveNotificationConfig(String type, String app, Map notificationConfig) {
    return null;
  }

  @Override
  public List<Map> getAllProjects() {
    return List.of();
  }

  @Override
  public Map getProject(String projectId) {
    return Map.of();
  }

  @Override
  public Map getCurrentSnapshot(String id) {
    return Map.of();
  }

  @Override
  public List<Map> getSnapshotHistory(String id, int limit) {
    return List.of();
  }

  @Override
  public List<ServiceAccount> getServiceAccounts() {
    return List.of();
  }

  @Override
  public List<Map> getDeliveries() {
    return List.of();
  }

  @Override
  public Map getDelivery(String id) {
    return Map.of();
  }

  @Override
  public List<Map> getPluginInfo(String service) {
    return List.of();
  }

  @Override
  public List<SpinnakerPluginDescriptor> getInstalledPlugins() {
    return List.of();
  }
}
