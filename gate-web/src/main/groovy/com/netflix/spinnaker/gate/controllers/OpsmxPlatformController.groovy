/*
 * Copyright 2020 Netflix, Inc.
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

package com.netflix.spinnaker.gate.controllers


import com.netflix.spinnaker.gate.services.internal.OpsmxPlatformService
import com.opsmx.spinnaker.gate.factory.platform.PlatformCachingServiceBeanFactory
import com.opsmx.spinnaker.gate.service.PlatformCachingService
import com.opsmx.spinnaker.gate.util.CacheUtil
import groovy.util.logging.Slf4j
import io.swagger.v3.oas.annotations.Operation

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression
import org.springframework.http.HttpHeaders
import org.springframework.web.bind.annotation.*
import retrofit.client.Response
import org.apache.commons.io.IOUtils
import org.springframework.http.MediaType

import jakarta.servlet.http.HttpServletRequest
import java.util.stream.Collectors
import org.springframework.http.ResponseEntity

@RequestMapping("/platformservice")
@RestController
@Slf4j
@ConditionalOnExpression('${services.platform.enabled:false}')
class OpsmxPlatformController {
/*
 * Copyright 2020 Netflix, Inc.
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

  @Autowired(required = false)
  OpsmxPlatformService opsmxPlatformService

  @Autowired
  PlatformCachingServiceBeanFactory platformCachingServiceBeanFactory

  @Operation(summary = "Endpoint for platform rest services")
  @RequestMapping(value = "/{version}/{type}", method = RequestMethod.GET)
  Object getPlatformResponse1(@PathVariable("version") String version,
                              @PathVariable("type") String type,
                              @RequestParam(value = "datasourceType", required = false) String datasourceType,
                              @RequestParam(value = "accountName", required = false) String accountName,
                              @RequestParam(value = "source", required = false) String source,
                              @RequestParam(value = "permission", required = false) String permission,
                              @RequestParam(value = "search", required = false) String search,
                              @RequestParam(value = "username", required = false) String username,
                              @RequestParam(value = "pageNo", required = false) Integer pageNo,
                              @RequestParam(value = "pageLimit", required = false) Integer pageLimit,
                              @RequestParam(value = "sortBy", required = false) String sortBy,
                              @RequestParam(value = "sortOrder", required = false) String sortOrder,
                              @RequestParam(value = "applicationId", required = false) Integer applicationId,
                              @RequestParam(value = "applicationName", required = false) String applicationName,
                              @RequestParam(value = "noOfDays", required = false) Integer noOfDays,
                              @RequestParam(value = "filterBy", required = false) String filterBy,
                              @RequestParam(value = "cdTool", required = false) String cdTool,
                              @RequestParam(value = "cdlabel", required = false) String cdlabel,
                              @RequestParam(value = "syncstatus", required = false) String syncstatus,
                              @RequestParam(value = "health", required = false) String health,
                              @RequestParam(value = "cluster", required = false) String cluster,
                              @RequestParam(value = "targetrevision", required = false) String targetrevision,
                              @RequestParam(value = "sourcepath", required = false) String sourcepath,
                              @RequestParam(value = "destinationserver", required = false) String destinationserver,
                              @RequestParam(value = "project", required = false) String project,
                              @RequestParam(value = "namespace", required = false) String namespace,
                              @RequestParam(value = "revision", required = false) String revision,
                              @RequestParam(value = "applicationlabel", required = false) String applicationlabel,
                              @RequestParam(value = "description", required = false) String description,
                              @RequestParam(value = "service", required = false) String service,
                              @RequestParam(value = "image", required = false) String image,
                              @RequestParam(value = "clusterlabel", required = false) String clusterlabel,
                              @RequestParam(value = "application", required = false) String  application) {
    return opsmxPlatformService.getPlatformResponse1(version, type, datasourceType, accountName, source, permission,
       search, username, pageNo, pageLimit, sortBy, sortOrder, applicationId, applicationName, noOfDays, filterBy,
      cdTool,cdlabel,syncstatus,health,cluster,targetrevision,sourcepath,destinationserver,project,namespace,revision,applicationlabel,description,service, image,clusterlabel,application)
  }

  @Operation(summary = "Endpoint for platform rest services")
  @RequestMapping(value = "/{version}/{type}/{source}", method = RequestMethod.GET)
  Object getPlatformResponse(@PathVariable("version") String version,
                             @PathVariable("type") String type,
                             @PathVariable("source") String source,
                             @RequestParam(value = "source1", required = false) String source1,
                             @RequestParam(value = "chartId", required = false) Integer chartId,
                             @RequestParam(value = "noOfDays", required = false) Integer noOfDays,
                             @RequestParam(value = "argoName", required = false) String argoName,
                             @RequestParam(value = "agentName", required = false) String agentName) {
    return opsmxPlatformService.getPlatformResponse(version, type, source, source1, chartId, noOfDays,argoName, agentName)
  }
  @Operation(summary = "Endpoint for platform rest services")
  @RequestMapping(value = "/{version}/{type}/{source}/{source1}", method = RequestMethod.GET)
  Object getPlatformResponse4(@PathVariable("version") String version,
                              @PathVariable("type") String type,
                              @PathVariable("source") String source,
                              @PathVariable("source1") String source1,
                              @RequestParam(value = "agentName", required = false) String agentName,
                              @RequestParam(value = "cdName", required = false) String cdName,
                              @RequestParam(value = "datasourceType", required = false) String datasourceType,
                              @RequestParam(value = "permissionId", required = false) String permissionId,
                              @RequestParam(name = "applicationId", required = false) Integer applicationId,
                              @RequestParam(name = "canaryId", required = false) Integer canaryId,
                              @RequestParam(value = "applicationName", required = false) String applicationName,
                              @RequestParam(value = "argoName", required = false) String argoName,
                              @RequestParam(value = "argoId", required = false) String argoId,
                               HttpServletRequest httpServletRequest) {

    String path = httpServletRequest.getRequestURI()
    if (CacheUtil.isRegisteredCachingEndpoint(path)) {
      return handleCaching(path, httpServletRequest, version, type, source, source1, agentName, cdName, datasourceType, permissionId,applicationName,argoName,argoId)
    } else {
      return opsmxPlatformService.getPlatformResponse4(version, type, source, source1, agentName, cdName, datasourceType, permissionId, applicationId, canaryId,applicationName,argoName,argoId)
    }
  }

  private Object handleCaching(String path, HttpServletRequest httpServletRequest, String version, String type, String source, String source1, String agentName, String cdName, String datasourceType, String permissionId,String applicationName,String argoName,String argoId) {
    Object response
    PlatformCachingService platformCachingService = platformCachingServiceBeanFactory.getBean(path)

    String userName = httpServletRequest.getUserPrincipal().getName()
    response = platformCachingService.fetchResponseFromCache(userName)
    if (response == null) {
      response = opsmxPlatformService.getPlatformResponse4(version, type, source, source1, agentName, cdName, datasourceType, permissionId,applicationName,argoName,argoId)
      platformCachingService.cacheResponse(response, userName)
    }
    return response
  }

  @Operation(summary = "Endpoint for platform rest services")
  @RequestMapping(value = "/{version}/{type}/{source}/{source1}/{source2}", method = RequestMethod.GET)
  Object getPlatformResponse5(@PathVariable("version") String version,
                              @PathVariable("type") String type,
                              @PathVariable("source") String source,
                              @PathVariable("source1") String source1,
                              @PathVariable("source2") String source2,
                              @RequestParam(value = "permissionId", required = false) String permissionId,
                              @RequestParam(value = "resourceType", required = false) String resourceType,
                              @RequestParam(value = "featureType", required = false) String featureType,
                              @RequestParam(value = "sourceName", required = false) String sourceName) {

    return opsmxPlatformService.getPlatformResponse5(version, type, source, source1, source2, permissionId, resourceType,featureType,sourceName)
  }

  @Operation(summary = "Endpoint for platform rest services")
  @RequestMapping(value = "/{version}/{type}/{source}/{source1}/{source2}/{source3}", method = RequestMethod.GET)
  Object getPlatformResponse6(@PathVariable("version") String version,
                              @PathVariable("type") String type,
                              @PathVariable("source") String source,
                              @PathVariable("source1") String source1,
                              @PathVariable("source2") String source2,
                              @PathVariable("source3") String source3,
                              @RequestParam(value = "agentName") String agentName,
                              @RequestParam(value = "sourceType") String sourceType) {

    return opsmxPlatformService.getPlatformResponse6(version, type, source, source1, source2, source3, agentName, sourceType)
  }

  @Operation(summary = "Endpoint for platform rest services")
  @RequestMapping(value = "/{version}/{type}/{source}/{source1}/{source2}/{source3}/{source4}", method = RequestMethod.GET)
  Object getPlatformResponse7(@PathVariable("version") String version,
                              @PathVariable("type") String type,
                              @PathVariable("source") String source,
                              @PathVariable("source1") String source1,
                              @PathVariable("source2") String source2,
                              @PathVariable("source3") String source3,
                              @PathVariable("source4") String source4) {

    return opsmxPlatformService.getPlatformResponse7(version, type, source, source1, source2, source3, source4)
  }
  @Operation(summary = "Endpoint for platform rest services")
  @RequestMapping(value = "/{version}/{type}/{source}/{source1}/{source2}/{source3}/{source4}/{source5}/{source6}", method = RequestMethod.GET)
  Object getPlatformResponse8(@PathVariable("version") String version,
                              @PathVariable("type") String type,
                              @PathVariable("source") String source,
                              @PathVariable("source1") String source1,
                              @PathVariable("source2") String source2,
                              @PathVariable("source3") String source3,
                              @PathVariable("source4") String source4,
                              @PathVariable("source5") String source5,
                              @PathVariable("source6") String source6,
                              @RequestParam(value = "type", required = false) String gateType) {
    return opsmxPlatformService.getPlatformResponse8(version, type, source, source1, source2, source3, source4, source5, source6, gateType)
  }

  @Operation(summary = "download the CD Integrator manifest file")
  @GetMapping(value = "/v7/{type}/{source}/manifest", produces = "application/octet-stream")
  @ResponseBody
  Object getDownloadAgentManifestFile(@PathVariable("type") String type,
                                      @PathVariable("source") String source,
                                      @RequestParam(value = "argoName", required = false) String argoName,
                                      @RequestParam(value = "agentName", required = false) String agentName,
                                      @RequestParam(value = "nameSpace", required = false) String nameSpace) {
    Response response = opsmxPlatformService.getCdIntegratorManifestDownloadFile(type, source, argoName, agentName, nameSpace)
    InputStream inputStream = response.getBody().in()
    try {
      byte [] manifestFile = IOUtils.toByteArray(inputStream)
      HttpHeaders headers = new HttpHeaders()
      headers.add("Content-Disposition", response.getHeaders().stream()
        .filter({ header -> header.getName().trim().equalsIgnoreCase("Content-Disposition") })
        .collect(Collectors.toList()).get(0).value)
      return ResponseEntity.ok().headers(headers).body(manifestFile)
    } finally{
      if (inputStream!=null){
        inputStream.close()
      }
    }
  }

  @Operation(summary = "Endpoint for platform rest services")
  @RequestMapping(value ="/v7/datasource/groups", method = RequestMethod.GET)
  Object getPlatformResponse9(@RequestParam(required = false) String name,
    @RequestParam("isArgoEnabled") Boolean isArgoEnabled){
    return opsmxPlatformService.getPlatformResponse9(name,isArgoEnabled)
  }

  @Operation(summary = "Endpoint for Insights controller to download csv file")
  @GetMapping(value = "/{version}/insights/download", produces = "text/csv")
  Object downloadCsvFile(@PathVariable("version") String version,
                         @RequestParam(value = "chartId", required = false) Integer chartId,
                         @RequestParam(value = "noOfDays", required = false) Integer noOfDays) {
    Response response = opsmxPlatformService.downloadCSVFile(version, chartId, noOfDays)
    InputStream inputStream = response.getBody().in()
    try {
      byte[] csvFile = IOUtils.toByteArray(inputStream)
      HttpHeaders headers = new HttpHeaders()
      headers.setContentType(MediaType.parseMediaType("text/csv"))
      headers.add("Content-Disposition", response.getHeaders().stream().filter({ header -> header.getName().trim().equalsIgnoreCase("Content-Disposition") }).collect(Collectors.toList()).get(0).value)
      return ResponseEntity.ok().headers(headers).body(csvFile)

    } finally {
      if (inputStream != null) {
        inputStream.close()
      }
    }
  }

  @Operation(summary = "Endpoint for platform rest services")
  @RequestMapping(value = "/{version}/{type}", method = RequestMethod.DELETE)
  Object deletePlatformResponse(@PathVariable("version") String version,
                                @PathVariable("type") String type,
                                @RequestParam(value = "accountName", required = false) String accountName) {
    return opsmxPlatformService.deletePlatformResponse(version, type, accountName)
  }

  @Operation(summary = "Endpoint for platform rest services")
  @RequestMapping(value = "/{version}/{type}/{source}", method = RequestMethod.DELETE)
  Object deletePlatformResponse1(@PathVariable("version") String version,
                                 @PathVariable("type") String type,
                                 @PathVariable("source") String source,
                                 @RequestParam(value = "argoName", required = false) String argoName,
                                 @RequestParam(value = "agentName", required = false) String agentName,
                                 @RequestParam(name = "checkOnlyDeletedArgo", required = false) boolean checkOnlyDeletedArgo) {

    return opsmxPlatformService.deletePlatformResponse1(version, type, source, argoName, agentName, checkOnlyDeletedArgo)
  }

  @Operation(summary = "Endpoint for platform rest services")
  @RequestMapping(value = "/{version}/{type}/{source}/{source1}", method = RequestMethod.DELETE)
  Object deletePlatformResponse4(@PathVariable("version") String version,
                                 @PathVariable("type") String type,
                                 @PathVariable("source") String source,
                                 @PathVariable("source1") String source1) {

    return opsmxPlatformService.deletePlatformResponse4(version, type, source, source1)
  }

  @Operation(summary = "Endpoint for platform rest services")
  @RequestMapping(value = "/{version}/{type}/{source}/{source1}/{source2}", method = RequestMethod.DELETE)
  Object deletePlatformResponse5(@PathVariable("version") String version,
                                 @PathVariable("type") String type,
                                 @PathVariable("source") String source,
                                 @PathVariable("source1") String source1,
                                 @PathVariable("source2") String source2,
                                 @RequestParam(value = "featureType", required = false) String featureType
  ) {

    return opsmxPlatformService.deletePlatformResponse5(version, type, source, source1,source2,featureType)
  }

  @Operation(summary = "Endpoint for platform rest services")
  @RequestMapping(value = "/{version}/{type}/{source}/{source1}/{source2}/{source3}", method = RequestMethod.DELETE)
  Object deletePlatformResponse6(@PathVariable("version") String version,
                                 @PathVariable("type") String type,
                                 @PathVariable("source") String source,
                                 @PathVariable("source1") String source1,
                                 @PathVariable("source2") String source2,
                                 @PathVariable("source2") String source3) {

    return opsmxPlatformService.deletePlatformResponse6(version, type, source, source1,source2,source3)
  }

  @Operation(summary = "Endpoint for platform rest services")
  @RequestMapping(value = "/{version}/{type}", method = RequestMethod.POST)
  Object postPlatformResponse(@PathVariable("version") String version,
                              @PathVariable("type") String type,
                              @RequestBody(required = false) Object data) {

    return opsmxPlatformService.postPlatformResponse(version, type, data)
  }

  @Operation(summary = "Endpoint for platform rest services")
  @RequestMapping(value = "/{version}/{type}/{source}", method = RequestMethod.POST)
  Object postPlatformResponse3(@PathVariable("version") String version,
                               @PathVariable("type") String type,
                               @PathVariable("source") String source,
                               @RequestBody(required = false) Object data) {

    return opsmxPlatformService.postPlatformResponse3(version, type, source, data)
  }

  @Operation(summary = "Endpoint for platform rest services")
  @RequestMapping(value = "/{version}/{type}/{source}/{source1}", method = RequestMethod.POST)
  Object postPlatformResponse4(@PathVariable("version") String version,
                               @PathVariable("type") String type,
                               @PathVariable("source") String source,
                               @PathVariable("source1") String source1,
                               @RequestParam(value = "isExists",  required = false) Boolean isExists,
                               @RequestParam(value = "description", required = false) String description,
                               @RequestParam(value = "nameSpace", required = false) String nameSpace,
                               @RequestParam(value = "argoCdUrl", required = false) String argoCdUrl,
                               @RequestParam(value = "rolloutsEnabled", required = false) Boolean rolloutsEnabled,
                               @RequestParam(value = "isdUrl", required = false) String isdUrl,
                               @RequestParam(value = "argoName",required = false) String argoName,
                               @RequestParam(value = "agentName",required = false) String agentName,
                               @RequestBody(required = false) Object data) {

    return opsmxPlatformService.postPlatformResponse4(version, type, source, source1, isExists, description, nameSpace, argoCdUrl, rolloutsEnabled, isdUrl, argoName, agentName, data)
  }

  @Operation(summary = "Endpoint for platform rest services")
  @RequestMapping(value = "/{version}/{type}/{source}/{source1}/{source2}", method = RequestMethod.POST)
  Object postPlatformResponse5(@PathVariable("version") String version,
                               @PathVariable("type") String type,
                               @PathVariable("source") String source,
                               @PathVariable("source1") String source1,
                               @PathVariable("source2") String source2,
                               @RequestBody(required = false) Object data) {

    return opsmxPlatformService.postPlatformResponse5(version, type, source, source1, source2, data)
  }

  @Operation(summary = "Endpoint for platform rest services")
  @RequestMapping(value = "/{version}/{type}/{source}/{source1}/{source2}/{source3}", method = RequestMethod.POST)
  Object postPlatformResponse6(@PathVariable("version") String version,
                               @PathVariable("type") String type,
                               @PathVariable("source") String source,
                               @PathVariable("source1") String source1,
                               @PathVariable("source2") String source2,
                               @PathVariable("source3") String source3,
                               @RequestBody(required = false) Object data) {

    return opsmxPlatformService.postPlatformResponse6(version, type, source, source1, source2, source3, data)
  }

  @Operation(summary = "Endpoint for platform rest services")
  @RequestMapping(value = "/{version}/{type}", method = RequestMethod.PUT)
  Object updatePlatformResponse(@PathVariable("version") String version,
                                @PathVariable("type") String type,
                                @RequestBody(required = false) Object data) {

    return opsmxPlatformService.updatePlatformResponse(version, type, data)
  }

  @Operation(summary = "Endpoint for platform rest services")
  @RequestMapping(value = "/{version}/{type}/{source}", method = RequestMethod.PUT)
  Object updatePlatformResponse1(@PathVariable("version") String version,
                                 @PathVariable("type") String type,
                                 @PathVariable("source") String source,
                                 @RequestBody(required = false) Object data) {

    return opsmxPlatformService.updatePlatformResponse1(version, type, source, data)
  }

  @Operation(summary = "Endpoint for platform rest services")
  @RequestMapping(value = "/{version}/{type}/{source}/{source1}", method = RequestMethod.PUT)
  Object updatePlatformResponse2(@PathVariable("version") String version,
                                 @PathVariable("type") String type,
                                 @PathVariable("source") String source,
                                 @PathVariable("source1") String source1,
                                 @RequestParam(value = "argoName", required = false ) String argoName,
                                 @RequestParam(value = "agentName", required = false) String agentName,
                                 @RequestParam(value = "nameSpace", required = false) String nameSpace,
                                 @RequestBody(required = false) Object data) {

    return opsmxPlatformService.updatePlatformResponse2(version, type, source, source1, argoName, agentName, nameSpace, data)
  }

  @Operation(summary = "Endpoint for platform rest services")
  @RequestMapping(value = "/{version}/{type}/{source}/{source1}/{source2}", method = RequestMethod.PUT)
  Object updatePlatformResponse3(@PathVariable("version") String version,
                                 @PathVariable("type") String type,
                                 @PathVariable("source") String source,
                                 @PathVariable("source1") String source1,
                                 @PathVariable("source2") String source2,
                                 @RequestParam(value = "sourceName", required = false) String sourceName,
                                 @RequestBody(required = false) Object data) {

    return opsmxPlatformService.updatePlatformResponse3(version, type, source, source1, source2,sourceName, data)
  }

  @Operation(summary = "Endpoint for platform rest services")
  @RequestMapping(value = "/{version}/{type}/{source}/{source1}/{source2}/{source3}/{source4}", method = RequestMethod.PUT)
  Object updatePlatformResponse4(@PathVariable("version") String version,
                                 @PathVariable("type") String type,
                                 @PathVariable("source") String source,
                                 @PathVariable("source1") String source1,
                                 @PathVariable("source2") String source2,
                                 @PathVariable("source3") String source3,
                                 @PathVariable("source4") String source4,
                                 @RequestParam(value = "featureType", required = false) String featureType,
                                 @RequestParam(name = "createdStatus", required = false) boolean createdStatus,
                                 @RequestBody(required = false) Object data) {

    return opsmxPlatformService.updatePlatformResponse4(version, type, source, source1, source2, source3, source4, featureType,createdStatus,data)
  }

  @Operation(summary = "download metric analysis sample template")
  @GetMapping(value = "/{version}/argo/sampleTemplate", produces = "application/octet-stream")
  @ResponseBody Object downloadSampleTemplate(@PathVariable("version") String version) {
    Response response = opsmxPlatformService.downloadSampleTemplate(version)
    InputStream inputStream = response.getBody().in()
    try {
      byte[] manifestFile = IOUtils.toByteArray(inputStream)
      HttpHeaders headers = new HttpHeaders()
      headers.add("Content-Disposition", response.getHeaders().stream().filter({ header -> header.getName().trim().equalsIgnoreCase("Content-Disposition") }).collect(Collectors.toList()).get(0).value)
      return ResponseEntity.ok().headers(headers).body(manifestFile)
    } finally {
      if (inputStream != null) {
        inputStream.close()
      }
    }
  }
}
