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

package com.opsmx.spinnaker.gate.controllers

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.netflix.spinnaker.gate.config.ServiceConfiguration
import com.opsmx.spinnaker.gate.services.OpsmxAuditService
import groovy.util.logging.Slf4j
import io.swagger.v3.oas.annotations.Operation
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile

import java.nio.charset.StandardCharsets

@Slf4j
@RestController
@RequestMapping("/auditservice")
@ConditionalOnExpression('${services.auditservice.enabled:false}')
class OpsmxAuditServiceController {

  @Autowired
  OpsmxAuditService opsmxAuditService

  @Autowired
  ServiceConfiguration serviceConfiguration

  @Autowired
  OkHttpClient okHttpClient


  @Operation(summary = "Endpoint for audit rest services")
  @RequestMapping(value = "/{version}/{type}/{source}/{source1}", method = RequestMethod.POST)
  Object postAuditService1(@PathVariable("version") String version,
                                 @PathVariable("type") String type,
                                 @PathVariable("source") String source,
                                 @PathVariable("source1") String source1,
                                 @RequestBody(required = false) Object data) {

    return opsmxAuditService.postAuditService1(version, type, source, source1, data)
  }

  @Operation(summary = "Rest api for audit save account environment mapping")
  @RequestMapping(value = "/v1/acctEnvMapping", method = RequestMethod.POST)
  Object saveAcctEnvMapping(@RequestBody Object data) {

    return opsmxAuditService.saveAccountEnvironmentMapping(data)
  }

  @Operation(summary = "Rest api for updating an account environment mapping")
  @RequestMapping(value = "/v1/acctEnvMapping/", method = RequestMethod.PUT)
  Object updateAcctEnvMapping(@RequestBody Object data) {

    return opsmxAuditService.updateAccountEnvironmentMapping(data)
  }

  @Operation(summary = "Rest api for fetching all account environment mapping records")
  @RequestMapping(value = "/v1/acctEnvMapping", method = RequestMethod.GET)
  Object getAllAcctEnvMappings() {

    return opsmxAuditService.getAllAccountEnvironmentMappings();
  }

  @Operation(summary = "Rest api for fetching account environment mapping record with id")
  @RequestMapping(value = "/v1/acctEnvMapping/{id}", method = RequestMethod.GET)
  Object getAcctEnvMappingWithId(@PathVariable("id") Integer id) {
    return opsmxAuditService.getAccountEnvironmentMappingWithId(id);
  }


  @Operation(summary = "Rest api for bulk import of account environment mappings")
  @RequestMapping(value = "/v1/acctEnvMapping/bulkimport", method = RequestMethod.POST, consumes = "multipart/form-data")
  String bulkImportAcctEnvironmentMappings(@RequestParam("file") MultipartFile data) {
    /*String processedData = processBulkImportMappings(data);
    return opsmxAuditService.saveBulkImportMappings(processedData);*/
    try {
      byte[] fileBytes = data.bytes

      verifyJsonFormat(fileBytes)

      // Upload to AuditService
      return uploadToAuditService(fileBytes)
    } catch (Exception e) {
      throw new RuntimeException("Failed to process file: ${e.message}", e)
    }
  }

  private static void verifyJsonFormat(byte[] fileBytes) {
    try {
      ObjectMapper objectMapper = new ObjectMapper()
      JsonNode jsonNode = objectMapper.readTree(fileBytes)
      if (!jsonNode) {
        throw new IllegalArgumentException("Invalid JSON format: content is empty or null")
      }
    } catch (Exception e) {
      throw new RuntimeException("Invalid JSON format in file", e)
    }
  }

  private String uploadToAuditService(byte[] fileBytes) {
    def request = new Request.Builder()
      .url(serviceConfiguration.getServiceEndpoint("opsmx").url +"/auditservice/v1/acctEnvMapping/bulkimport")
      .post(RequestBody.create(fileBytes, MediaType.parse("application/json")))
      .build()

    try {
      def response = okHttpClient.newCall(request).execute()
      if (!response.isSuccessful()) {
        String reason = response.body()?.string() ?: "Unknown reason: ${response.code()}"
        throw new RuntimeException("Failed to upload environment mappings: $reason")
      }
      return response.body()?.string() ?: "Success"
    } catch (IOException e) {
      throw new RuntimeException("Error during file upload to AuditService", e)
    }
  }

  private static String processBulkImportMappings(MultipartFile data) {
    try {
      // Convert multipart file json content to String using UTF-8 encoding
      return new String(data.bytes, StandardCharsets.UTF_8)
    } catch (IOException e) {
      throw new RuntimeException("Failed to read JSON content", e)
    }
  }

}
