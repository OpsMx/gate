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
import com.netflix.spinnaker.gate.exceptions.OesRequestException
import com.netflix.spinnaker.security.AuthenticatedRequest
import com.opsmx.spinnaker.gate.services.OpsmxAuditService
import groovy.util.logging.Slf4j
import io.swagger.v3.oas.annotations.Operation
import okhttp3.Headers
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okio.Buffer
import okio.BufferedSink
import okio.Okio
import okio.Source
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

  @Operation(summary = "Rest api for deleting account environment mapping record with id")
  @RequestMapping(value = "/v1/acctEnvMapping/{id}", method = RequestMethod.DELETE)
  Object deleteAcctEnvMapping(@PathVariable("id") Integer id) {
    return opsmxAuditService.deleteAccountEnvironmentMappingWithId(id);
  }


  @Operation(summary = "Rest api for bulk import of account environment mappings")
  @RequestMapping(value = "/v1/acctEnvMapping/bulkimport", method = RequestMethod.POST, consumes = "multipart/form-data")
  String bulkImportAcctEnvironmentMappings(@RequestParam("file") MultipartFile data) {
    try {
      return uploadToAuditService(data)
    } catch (Exception e) {
      throw new RuntimeException("Failed to process file: ${e.message}", e)
    }
  }


  private String uploadToAuditService(MultipartFile data) {
    def obj = AuthenticatedRequest.propagate {
      def request = new Request.Builder()
        .url(serviceConfiguration.getServiceEndpoint("auditservice").url +"/auditservice/v1/acctEnvMapping/bulkimport")
        .post(uploadFileOkHttp(data))
        .build()
      def response = okHttpClient.newCall(request).execute()
      return response
    }.call() as okhttp3.Response

    if (!obj.isSuccessful()) {
      def error = obj.body().string();
      log.error("Failed to setup the Spinnaker : {}", error)
      throw new OesRequestException(error)
    } else{
      return obj.body()?.string() ?: "Unknown reason: " + obj.code() as Object
    }
  }

  private okhttp3.RequestBody uploadFileOkHttp(MultipartFile multiPartfile) throws IOException {

    String fileName = multiPartfile.getOriginalFilename();
    MultipartBody.Builder builder = new MultipartBody.Builder();
    builder.setType(MultipartBody.FORM);
    builder.addFormDataPart("file", fileName, new okhttp3.RequestBody() {
      @Override
      public MediaType contentType() {
        return MediaType.parse("application/octet-stream");
      }

      @Override
      public void writeTo(BufferedSink sink) throws IOException {
        try {
          Source source = Okio.source(multiPartfile.getInputStream());
          Buffer buf = new Buffer();

          long totalRead = 0;
          long totalSize = multiPartfile.getSize();
          long remaining = totalSize;

          for (long readCount; (readCount = source.read(buf, 32000)) != -1;) {

            totalRead += readCount;
            remaining -= readCount;

            sink.write(buf, readCount);
            sink.flush();

          }
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    });
    return builder.build();
  }

}
