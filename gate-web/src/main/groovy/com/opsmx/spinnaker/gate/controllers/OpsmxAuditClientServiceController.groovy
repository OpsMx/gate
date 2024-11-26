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

import com.netflix.spinnaker.gate.config.ServiceConfiguration
import com.netflix.spinnaker.gate.exceptions.OesRequestException
import com.netflix.spinnaker.security.AuthenticatedRequest
import com.opsmx.spinnaker.gate.services.OpsmxAuditClientService
import groovy.util.logging.Slf4j
import io.swagger.v3.oas.annotations.Operation
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okio.Buffer
import okio.BufferedSink
import okio.Okio
import okio.Source
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.http.HttpHeaders
import org.springframework.web.multipart.MultipartFile
import retrofit.client.Response
import org.apache.commons.io.IOUtils
import org.springframework.http.MediaType
import java.util.stream.Collectors
import org.springframework.http.ResponseEntity

@RequestMapping("/auditclientservice")
@RestController
@Slf4j
@ConditionalOnExpression('${services.auditclient.enabled:false}')
class OpsmxAuditClientServiceController {

  @Autowired
  OpsmxAuditClientService opsmxAuditClientService

  @Autowired
  ServiceConfiguration serviceConfiguration

  @Autowired
  OkHttpClient okHttpClient

  @Operation(summary = "Endpoint for audit-client rest services")
  @RequestMapping(value = "/{version}/{type}", method = RequestMethod.GET)
  Object getAuditClientResponse1(@PathVariable("version") String version,
                              @PathVariable("type") String type,
                                 @RequestParam(value = "applicationName",required = false) String applicationName,
                                 @RequestParam(value = "noOfDays", required = false) Integer noOfDays,
                                 @RequestParam(value = "pageNo",required = false) Integer page,
                                 @RequestParam(value = "size", required = false) Integer size,
                                 @RequestParam(value = "policyStatus",required = false) String policyStatus,
                                 @RequestParam(value = "search", required = false) String search,
                                 @RequestParam(value = "sortOrder", required = false) String sortOrder) {
    return opsmxAuditClientService.getAuditClientResponse1(version, type,applicationName,noOfDays,page,size,policyStatus,search,sortOrder)
  }

  @Operation(summary = "Endpoint for audit-client rest services")
  @RequestMapping(value = "/{version}/{type}/{source}", method = RequestMethod.GET)
  Object getDeliveryInsightCharts(@PathVariable("version") String version,
                                 @PathVariable("type") String type,
                                 @PathVariable("source") String source,
                                 @RequestParam(value = "chartId", required = false) Integer chartId,
                                 @RequestParam(value = "startTime", required = false) Long startTime,
                                 @RequestParam(value = "endTime", required = false) Long endTime,
                                 @RequestParam(value = "days", required = false) Integer days,
                                  @RequestParam(value = "pageNo",required = false) Integer pageNo,
                                  @RequestParam(value = "pageLimit",required = false) Integer pageLimit,
                                  @RequestParam(value = "search", required = false) String search,
                                  @RequestParam(value = "sortBy",required = false) String sortBy,
                                  @RequestParam(value = "sortOrder", required = false) String sortOrder,
                                  @RequestParam(value = "filterBy", required = false) String filterBy) {
    return opsmxAuditClientService.getDeliveryInsightCharts(version, type, source, chartId, startTime, endTime, days, pageNo, pageLimit, search, sortBy, sortOrder, filterBy)
  }

  @Operation(summary = "Endpoint for audit-client rest services")
  @RequestMapping(value = "/{version}/{type}/{source}/{source1}", method = RequestMethod.GET)
  Object getAuditClientResponse3(@PathVariable("version") String version,
                                 @PathVariable("type") String type,
                                 @PathVariable("source") String source,
                                 @PathVariable("source1") String source1,
                                 @RequestParam(value = "isTreeView", required = false) Boolean isTreeView,
                                 @RequestParam(value = "isLatest", required = false) Boolean isLatest,
                                 @RequestParam(value = "pageNo", required = false) Integer pageNo,
                                 @RequestParam(value = "pageLimit", required = false) Integer pageLimit,
                                 @RequestParam(value = "noOfDays", required = false) String noOfDays,
                                 @RequestParam(value = "search", required = false) String search,
                                 @RequestParam(value = "sortOrder", required = false) String sortOrder,
                                 @RequestParam(value = "sortBy", required = false) String sortBy,
                                 @RequestParam(value = "startDate", required = false) Long startDate,
                                 @RequestParam(value = "endDate", required = false) Long endDate,
                                 @RequestParam(value = "cdName", required = false) List<String> cdNames) {
    return opsmxAuditClientService.getAuditClientResponse3(version, type, source, source1, isTreeView, isLatest, pageNo, pageLimit, noOfDays, search, sortOrder, sortBy, startDate, endDate, cdNames)
  }

  @Operation(summary = "Endpoint for audit-client rest services")
  @RequestMapping(value = "/{version}/{type}/{source}/{source1}/{source2}", method = RequestMethod.GET)
  Object getAuditClientResponse4(@PathVariable("version") String version,
                                 @PathVariable("type") String type,
                                 @PathVariable("source") String source,
                                 @PathVariable("source1") String source1,
                                 @PathVariable("source2") String source2,
                                 @RequestParam(value = "noOfDays", required = false) String noOfDays,
                                 @RequestParam(value = "updatedTimestamp", required = false) Long updatedTimestamp,
                                 @RequestParam(value = "size", required = false) Integer size,
                                 @RequestParam(value = "startDate", required = false) Long startDate,
                                 @RequestParam(value = "endDate", required = false) Long endDate,
                                 @RequestParam(value = "cdName", required = false) List<String> cdNames) {

    return opsmxAuditClientService.getAuditClientResponse4(version, type, source, source1, source2, noOfDays, updatedTimestamp, size, startDate, endDate, cdNames)
  }

  @Operation(summary = "Endpoint for audit-client rest services")
  @RequestMapping(value = "/{version}/{type}/{source}/{source1}/{source2}/{source3}", method = RequestMethod.GET)
  Object getAuditClientResponse5(@PathVariable("version") String version,
                                 @PathVariable("type") String type,
                                 @PathVariable("source") String source,
                                 @PathVariable("source1") String source1,
                                 @PathVariable("source2") String source2,
                                 @PathVariable("source3") String source3,
                                 @RequestParam(value = "noOfDays", required = false) String noOfDays,
                                 @RequestParam(value = "startDate", required = false) Long startDate,
                                 @RequestParam(value = "endDate", required = false) Long endDate,
                                 @RequestParam(value = "cdName", required = false) List<String> cdNames) {
    return opsmxAuditClientService.getAuditClientResponse5(version, type, source, source1, source2, source3, noOfDays, startDate, endDate, cdNames)
  }

  @Operation(summary = "Endpoint for audit-client rest services")
  @RequestMapping(value = "/{version}/{type}/{source}/{source1}/{source2}/{source3}/{source4}", method = RequestMethod.GET)
  Object getAuditClientResponse6(@PathVariable("version") String version,
                              @PathVariable("type") String type,
                              @PathVariable("source") String source,
                              @PathVariable("source1") String source1,
                              @PathVariable("source2") String source2,
                              @PathVariable("source3") String source3,
                              @PathVariable("source4") String source4) {

    return opsmxAuditClientService.getAuditClientResponse6(version, type, source, source1, source2, source3, source4)
  }

  @Operation(summary = "Endpoint for audit-client rest services")
  @RequestMapping(value = "/{version}/{type}/{source}/{source1}/{source2}/{source3}/{source4}/{source5}", method = RequestMethod.GET)
  Object getAuditClientResponse7(@PathVariable("version") String version,
                                 @PathVariable("type") String type,
                                 @PathVariable("source") String source,
                                 @PathVariable("source1") String source1,
                                 @PathVariable("source2") String source2,
                                 @PathVariable("source3") String source3,
                                 @PathVariable("source4") String source4,
                                 @PathVariable("source4") String source5) {

    return opsmxAuditClientService.getAuditClientResponse7(version, type, source, source1, source2, source3, source4, source5)
  }

  @Operation(summary = "Endpoint for Insights controller to download csv file")
  @RequestMapping(value = "/{version}/users/{username}/{source}/download", produces = "text/csv", method = RequestMethod.GET)
  Object downloadCSVFileAuditService(@PathVariable("version") String version,
                                     @PathVariable("username") String username,
                                     @PathVariable("source") String source,
                                     @RequestParam(value = "isTreeView", required = false) Boolean isTreeView,
                                     @RequestParam(value = "isLatest", required = false) Boolean isLatest,
                                     @RequestParam(value = "pageNo", required = false) Integer pageNo,
                                     @RequestParam(value = "size", required = false) Integer size,
                                     @RequestParam(value = "noOfDays", required = false) String noOfDays,
                                     @RequestParam(value = "startDate", required = false) Long startDate,
                                     @RequestParam(value = "endDate", required = false) Long endDate,
                                     @RequestParam(value = "cdName", required = false) List<String> cdNames) {
    Response response = opsmxAuditClientService.downloadCSVFile(version, username, source, isTreeView, isLatest, pageNo, size, noOfDays, startDate, endDate, cdNames)
    log.info("response for the insgiths endpoint:" + response.getHeaders())
    if (response.getBody()!=null) {
      InputStream inputStream = response.getBody().in()
      try {
        byte[] csvFile = IOUtils.toByteArray(inputStream)
        HttpHeaders headers = new HttpHeaders()
        headers.setContentType(MediaType.parseMediaType("text/csv"));
        headers.add("Content-Disposition", response.getHeaders().stream().filter({ header -> header.getName().trim().equalsIgnoreCase("Content-Disposition") }).collect(Collectors.toList()).get(0).value)
        return ResponseEntity.ok().headers(headers).body(csvFile)
      } finally {
        if (inputStream != null) {
          inputStream.close()
        }
      }
    }
    return ResponseEntity.status(response.getStatus()).build()
  }

  @Operation(summary = "Endpoint for Delivery Insights controller to download csv file")
  @RequestMapping(value = "/{version}/{type}/{source}/download", produces = "text/csv", method = RequestMethod.GET)
  Object downloadCSVFileAuditService(@PathVariable("version") String version,
                                     @PathVariable("type") String type,
                                     @PathVariable("source") String source,
                                     @RequestParam(value = "chartId", required = false) Integer chartId,
                                     @RequestParam(value = "startTime", required = false) Long startTime,
                                     @RequestParam(value = "endTime", required = false) Long endTime,
                                     @RequestParam(value = "days", required = false) Integer days,
                                     @RequestParam(value = "filterBy", required = false) String filterBy) {
    Response response = opsmxAuditClientService.downloadDeliveryInsightsCSVFile(version, type, source, chartId, startTime, endTime, days, filterBy)
    log.info("response for the delivery insights endpoint:" + response.getHeaders())
    if (response.getBody()!=null) {
      InputStream inputStream = response.getBody().in()
      try {
        byte[] csvFile = IOUtils.toByteArray(inputStream)
        HttpHeaders headers = new HttpHeaders()
        headers.setContentType(MediaType.parseMediaType("text/csv"));
        headers.add("Content-Disposition", response.getHeaders().stream().filter({ header -> header.getName().trim().equalsIgnoreCase("Content-Disposition") }).collect(Collectors.toList()).get(0).value)
        return ResponseEntity.ok().headers(headers).body(csvFile)
      } finally {
        if (inputStream != null) {
          inputStream.close()
        }
      }
    }
    return ResponseEntity.status(response.getStatus()).build()
  }

  @Operation(summary = "Rest api for audit save account environment mapping")
  @RequestMapping(value = "/v3/acctEnvMapping", method = RequestMethod.POST)
  Object saveAcctEnvMapping(@RequestBody Object data) {

    return opsmxAuditClientService.saveAccountEnvironmentMapping(data)
  }

  @Operation(summary = "Rest api for updating an account environment mapping")
  @RequestMapping(value = "/v3/acctEnvMapping/{id}", method = RequestMethod.PUT)
  Object updateAcctEnvMapping(@PathVariable("id") Integer id, @RequestBody Object data) {

    return opsmxAuditClientService.updateAccountEnvironmentMapping(id, data)
  }

  @Operation(summary = "Rest api for fetching all account environment mapping records")
  @RequestMapping(value = "/v3/acctEnvMapping", method = RequestMethod.GET)
  Object getAllAcctEnvMappings() {

    return opsmxAuditClientService.getAllAccountEnvironmentMappings();
  }

  @Operation(summary = "Rest api for fetching account environment mapping record with id")
  @RequestMapping(value = "/v3/acctEnvMapping/{id}", method = RequestMethod.GET)
  Object getAcctEnvMappingWithId(@PathVariable("id") Integer id) {
    return opsmxAuditClientService.getAccountEnvironmentMappingWithId(id);
  }

  @Operation(summary = "Rest api for deleting account environment mapping record with id")
  @RequestMapping(value = "/v3/acctEnvMapping/{id}", method = RequestMethod.DELETE)
  Object deleteAcctEnvMapping(@PathVariable("id") Integer id) {
    return opsmxAuditClientService.deleteAccountEnvironmentMappingWithId(id);
  }


  @Operation(summary = "Rest api for bulk import of account environment mappings")
  @RequestMapping(value = "/v3/acctEnvMapping/bulkimport", method = RequestMethod.POST, consumes = "multipart/form-data")
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
        .url(serviceConfiguration.getServiceEndpoint("auditclient").url +"/auditclientservice/v3/acctEnvMapping/bulkimport")
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
      public okhttp3.MediaType contentType() {
        return okhttp3.MediaType.parse("application/octet-stream");
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
