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

import com.netflix.spinnaker.gate.services.internal.OpsmxDashboardService
import com.opsmx.spinnaker.gate.enums.GateInstallationModes
import com.opsmx.spinnaker.gate.factory.dashboard.DashboardCachingServiceBeanFactory
import com.opsmx.spinnaker.gate.service.DashboardCachingService
import com.opsmx.spinnaker.gate.util.CacheUtil
import groovy.util.logging.Slf4j
import io.swagger.v3.oas.annotations.Operation
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression
import org.springframework.context.annotation.ComponentScan
import org.springframework.web.bind.annotation.*

import jakarta.servlet.http.HttpServletRequest

@RequestMapping("/dashboardservice")
@RestController
@Slf4j
@ConditionalOnExpression('${services.dashboard.enabled:false}')
class OpsmxDashboardController {

  @Autowired
  OpsmxDashboardService opsmxDashboardService

  @Autowired
  DashboardCachingServiceBeanFactory dashboardCachingServiceBeanFactory

  @Value('oes')
  GateInstallationModes gateInstallationMode

  @Operation(summary = "Endpoint for dashboard rest services")
  @RequestMapping(value = "/{version}/{type}", method = RequestMethod.GET)
  Object getDashboardResponse1(@PathVariable("version") String version,
                               @PathVariable("type") String type,
                               @RequestParam(value = "datasourceType", required = false) String datasourceType,
                               @RequestParam(value = "pageNo", required = false) Integer pageNo,
                               @RequestParam(value = "pageLimit", required = false) Integer pageLimit,
                               @RequestParam(value = "search", required = false) String search, HttpServletRequest httpServletRequest) {

    Object response = null
    String path = httpServletRequest.getRequestURI()

    if (CacheUtil.isRegisteredCachingEndpoint(path)) {
      DashboardCachingService dashboardCachingService = dashboardCachingServiceBeanFactory.getBean(path)
      response = handleCaching(httpServletRequest, version, type, datasourceType, pageNo, pageLimit, search, dashboardCachingService)
    } else {
      response = opsmxDashboardService.getDashboardResponse1(version, type, datasourceType, pageNo, pageLimit, search)
    }

    return response
  }

  private Object handleCaching(
                HttpServletRequest httpServletRequest,
                String version,
                String type,
                String datasourceType,
                Integer pageNo,
                Integer pageLimit,
                String search,
                DashboardCachingService dashboardCachingService) {

    String userName = httpServletRequest.getUserPrincipal().getName()
    Object response

    if (dashboardCachingService.isCacheNotEmpty(userName)) {
      response = dashboardCachingService.fetchResponseFromCache(userName)
    } else {
      response = opsmxDashboardService.getDashboardResponse1(version, type, datasourceType, pageNo, pageLimit, search)
      dashboardCachingService.cacheResponse(response, userName)
    }
    return response
  }


  @Operation(summary = "Endpoint for dashboard rest services")
  @RequestMapping(value = "/{version}/{type}/{source}", method = RequestMethod.GET)
  Object getDashboardResponse(@PathVariable("version") String version,
                              @PathVariable("type") String type,
                              @PathVariable("source") String source,
                              @RequestParam(value = "pageNo", required = false) Integer pageNo,
                              @RequestParam(value = "pageLimit", required = false) Integer pageLimit,
                              @RequestParam(value = "search", required = false) String search) {
    return opsmxDashboardService.getDashboardResponse(version, type, source,pageNo, pageLimit, search)
  }

  @Operation(summary = "Endpoint for dashboard rest services")
  @RequestMapping(value = "/{version}/{type}/{source}/{source1}", method = RequestMethod.GET)
  Object getDashboardResponse4(@PathVariable("version") String version,
                               @PathVariable("type") String type,
                               @PathVariable("source") String source,
                               @PathVariable("source1") String source1,
                               @RequestParam(value = "pageNo", required = false) Integer pageNo,
                               @RequestParam(value = "pageLimit", required = false) Integer pageLimit,
                               @RequestParam(value = "sortBy", required = false) String sortBy,
                               @RequestParam(value = "sortOrder", required = false) String sortOrder,
                               @RequestParam(value = "search", required = false) String search,
                               @RequestParam(value = "noOfDays", required = false) Integer noOfDays) {

    return opsmxDashboardService.getDashboardResponse4(version, type, source, source1, pageNo, pageLimit, sortBy, sortOrder, search, noOfDays)
  }

  @Operation(summary = "Endpoint for dashboard rest services")
  @RequestMapping(value = "/{version}/{type}/{source}/{source1}/{source2}", method = RequestMethod.GET)
  Object getDashboardResponse5(@PathVariable("version") String version,
                               @PathVariable("type") String type,
                               @PathVariable("source") String source,
                               @PathVariable("source1") String source1,
                               @PathVariable("source2") String source2,
                               @RequestParam(value = "pageNo", required = false) Integer pageNo,
                               @RequestParam(value = "pageLimit", required = false) Integer pageLimit,
                               @RequestParam(value = "sortBy", required = false) String sortBy,
                               @RequestParam(value = "sortOrder", required = false) String sortOrder,
                               @RequestParam(value = "noOfDays", required = false) Integer noOfDays) {

    return opsmxDashboardService.getDashboardResponse5(version, type, source, source1, source2, pageNo, pageLimit, sortBy, sortOrder, noOfDays)
  }

  @Operation(summary = "Endpoint for dashboard rest services")
  @RequestMapping(value = "/{version}/{type}/{source}/{source1}/{source2}/{source3}", method = RequestMethod.GET)
  Object getDashboardResponse6(@PathVariable("version") String version,
                          @PathVariable("type") String type,
                          @PathVariable("source") String source,
                          @PathVariable("source1") String source1,
                          @PathVariable("source2") String source2,
                          @PathVariable("source3") String source3,
                               @RequestParam(value = "noOfDays", required = false) Integer noOfDays) {

    return opsmxDashboardService.getDashboardResponse6(version, type, source, source1, source2, source3, noOfDays)
  }

  @Operation(summary = "Endpoint for dashboard rest services")
  @RequestMapping(value = "/{version}/{type}/{source}/{source1}/{source2}/{source3}/{source4}", method = RequestMethod.GET)
  Object getDashboardResponse7(@PathVariable("version") String version,
                               @PathVariable("type") String type,
                               @PathVariable("source") String source,
                               @PathVariable("source1") String source1,
                               @PathVariable("source2") String source2,
                               @PathVariable("source3") String source3,
                               @PathVariable("source4") String source4,
                               @RequestParam(value = "pageNo", required = false) Integer pageNo,
                               @RequestParam(value = "pageLimit", required = false) Integer pageLimit,
                               @RequestParam(value = "sortBy", required = false) String sortBy,
                               @RequestParam(value = "sortOrder", required = false) String sortOrder) {

    return opsmxDashboardService.getDashboardResponse7(version, type, source, source1, source2, source3, source4, pageNo, pageLimit, sortBy, sortOrder)
  }

  @Operation(summary = "Endpoint for dashboard rest services")
  @RequestMapping(value = "/{version}/{type}/{source}/{source1}/{source2}/{source3}/{source4}/{source5}", method = RequestMethod.GET)
  Object getDashboardResponse8(@PathVariable("version") String version,
                               @PathVariable("type") String type,
                               @PathVariable("source") String source,
                               @PathVariable("source1") String source1,
                               @PathVariable("source2") String source2,
                               @PathVariable("source3") String source3,
                               @PathVariable("source4") String source4,
                               @PathVariable("source5") String source5) {

    return opsmxDashboardService.getDashboardResponse8(version, type, source, source1, source2, source3, source4, source5)
  }

  @Operation(summary = "Endpoint for dashboard rest services")
  @RequestMapping(value = "/{version}/{type}/{source}/{source1}/{source2}/{source3}/{source4}/{source5}/{source6}", method = RequestMethod.GET)
  Object getDashboardResponse9(@PathVariable("version") String version,
                               @PathVariable("type") String type,
                               @PathVariable("source") String source,
                               @PathVariable("source1") String source1,
                               @PathVariable("source2") String source2,
                               @PathVariable("source3") String source3,
                               @PathVariable("source4") String source4,
                               @PathVariable("source5") String source5,
                               @PathVariable("source6") String source6) {

    return opsmxDashboardService.getDashboardResponse9(version, type, source, source1, source2, source3, source4, source5, source6)
  }


  @Operation(summary = "Endpoint for dashboard rest services")
  @RequestMapping(value = "/{version}/{type}/{source}/{source1}/{source2}/{source3}/{source4}/{source5}/{source6}/{source7}", method = RequestMethod.GET)
  Object getDashboardResponse10(@PathVariable("version") String version,
                               @PathVariable("type") String type,
                               @PathVariable("source") String source,
                               @PathVariable("source1") String source1,
                               @PathVariable("source2") String source2,
                               @PathVariable("source3") String source3,
                               @PathVariable("source4") String source4,
                               @PathVariable("source5") String source5,
                               @PathVariable("source6") String source6,
                                @PathVariable("source7") String source7) {

    return opsmxDashboardService.getDashboardResponse10(version, type, source, source1, source2, source3, source4, source5, source6,source7)
  }

  @Operation(summary = "Endpoint for dashboard rest services")
  @RequestMapping(value = "/{version}/{type}", method = RequestMethod.DELETE)
  Object deleteDashboardResponse(@PathVariable("version") String version,
                                @PathVariable("type") String type) {

    return opsmxDashboardService.deleteDashboardResponse(version, type)
  }

  @Operation(summary = "Endpoint for dashboard rest services")
  @RequestMapping(value = "/{version}/{type}/{source}", method = RequestMethod.DELETE)
  Object deleteDashboardResponse1(@PathVariable("version") String version,
                            @PathVariable("type") String type,
                            @PathVariable("source") String source) {

    return opsmxDashboardService.deleteDashboardResponse1(version, type, source)
  }

  @Operation(summary = "Endpoint for dashboard rest services")
  @RequestMapping(value = "/{version}/{type}/{source}/{source1}", method = RequestMethod.DELETE)
  Object deleteDashboardResponse4(@PathVariable("version") String version,
                             @PathVariable("type") String type,
                             @PathVariable("source") String source,
                             @PathVariable("source1") String source1) {

    return opsmxDashboardService.deleteDashboardResponse4(version, type, source, source1)
  }

  @Operation(summary = "Endpoint for dashboard rest services")
  @RequestMapping(value = "/{version}/{type}/{source}/{source1}/{source2}", method = RequestMethod.DELETE)
  Object deleteDashboardResponse5(@PathVariable("version") String version,
                                  @PathVariable("type") String type,
                                  @PathVariable("source") String source,
                                  @PathVariable("source1") String source1,
                                  @PathVariable("source2") String source2) {

    return opsmxDashboardService.deleteDashboardResponse5(version, type, source, source1, source2)
  }

  @Operation(summary = "Endpoint for dashboard rest services")
  @RequestMapping(value = "/{version}/{type}/{source}/{source1}/{source2}/{source3}", method = RequestMethod.DELETE)
  Object deleteDashboardResponse6(@PathVariable("version") String version,
                                  @PathVariable("type") String type,
                                  @PathVariable("source") String source,
                                  @PathVariable("source1") String source1,
                                  @PathVariable("source2") String source2,
                                  @PathVariable("source3") String source3) {

    return opsmxDashboardService.deleteDashboardResponse6(version, type, source, source1, source2, source3)
  }

  @Operation(summary = "Endpoint for dashboard rest services")
  @RequestMapping(value = "/{version}/{type}/{source}/{source1}/{source2}/{source3}/{source4}", method = RequestMethod.DELETE)
  Object deleteDashboardResponse7(@PathVariable("version") String version,
                                  @PathVariable("type") String type,
                                  @PathVariable("source") String source,
                                  @PathVariable("source1") String source1,
                                  @PathVariable("source2") String source2,
                                  @PathVariable("source3") String source3,
                                  @PathVariable("source4") String source4,
                                  HttpServletRequest request) {
    String cookie = "no-cookie"
    if(gateInstallationMode.equals(GateInstallationModes.common)){
      cookie = request.getHeader("Cookie")
    }
    return opsmxDashboardService.deleteDashboardResponse7(version, type, source, source1, source2, source3, source4, cookie)
  }

  @Operation(summary = "Endpoint for dashboard rest services")
  @RequestMapping(value = "/{version}/{type}/{source}/{source1}/{source2}/{source3}/{source4}/{source5}", method = RequestMethod.DELETE)
  Object deleteDashboardResponse8(@PathVariable("version") String version,
                                  @PathVariable("type") String type,
                                  @PathVariable("source") String source,
                                  @PathVariable("source1") String source1,
                                  @PathVariable("source2") String source2,
                                  @PathVariable("source3") String source3,
                                  @PathVariable("source4") String source4,
                                  @PathVariable("source5") String source5) {
    return opsmxDashboardService.deleteDashboardResponse8(version, type, source, source1, source2, source3, source4, source5)
  }

  @Operation(summary = "Endpoint for dashboard rest services")
  @RequestMapping(value = "/{version}/{type}", method = RequestMethod.POST)
  Object postDashboardResponse(@PathVariable("version") String version,
                          @PathVariable("type") String type,
                          @RequestBody(required = false) Object data) {

    return opsmxDashboardService.postDashboardResponse(version, type,data)
  }

  @Operation(summary = "Endpoint for dashboard rest services")
  @RequestMapping(value = "/{version}/{type}/{source}", method = RequestMethod.POST)
  Object postDashboardResponse3(@PathVariable("version") String version,
                               @PathVariable("type") String type,
                               @PathVariable("source") String source,
                               @RequestBody(required = false) Object data) {

    return opsmxDashboardService.postDashboardResponse3(version, type, source, data)
  }

  @Operation(summary = "Endpoint for dashboard rest services")
  @RequestMapping(value = "/{version}/{type}/{source}/{source1}", method = RequestMethod.POST)
  Object postDashboardResponse4(@PathVariable("version") String version,
                           @PathVariable("type") String type,
                           @PathVariable("source") String source,
                           @PathVariable("source1") String source1,
                           @RequestBody(required = false) Object data,
                            HttpServletRequest request) {
    String cookie = "no-cookie"
    if(gateInstallationMode.equals(GateInstallationModes.common)){
      cookie = request.getHeader("Cookie")
    }
    return opsmxDashboardService.postDashboardResponse4(version, type, source, source1, cookie, data)
  }

  @Operation(summary = "Endpoint for dashboard rest services")
  @RequestMapping(value = "/{version}/{type}/{source}/{source1}/{source2}", method = RequestMethod.POST)
  Object postDashboardResponse5(@PathVariable("version") String version,
                           @PathVariable("type") String type,
                           @PathVariable("source") String source,
                           @PathVariable("source1") String source1,
                           @PathVariable("source2") String source2,
                           @RequestBody(required = false) Object data) {

    return opsmxDashboardService.postDashboardResponse5(version, type, source, source1, source2, data)
  }

  @Operation(summary = "Endpoint for dashboard rest services")
  @RequestMapping(value = "/{version}/{type}/{source}/{source1}/{source2}/{source3}", method = RequestMethod.POST)
  Object postDashboardResponse6(@PathVariable("version") String version,
                           @PathVariable("type") String type,
                           @PathVariable("source") String source,
                           @PathVariable("source1") String source1,
                           @PathVariable("source2") String source2,
                           @PathVariable("source3") String source3,
                           @RequestBody(required = false) Object data) {

    return opsmxDashboardService.postDashboardResponse6(version, type, source, source1, source2, source3, data)
  }

  @Operation(summary = "Endpoint for dashboard rest services")
  @RequestMapping(value = "/{version}/{type}/{source}/{source1}/{source2}/{source3}/{source4}", method = RequestMethod.POST)
  Object postDashboardResponse7(@PathVariable("version") String version,
                                @PathVariable("type") String type,
                                @PathVariable("source") String source,
                                @PathVariable("source1") String source1,
                                @PathVariable("source2") String source2,
                                @PathVariable("source3") String source3,
                                @PathVariable("source4") String source4,
                                @RequestBody(required = false) Object data) {

    return opsmxDashboardService.postDashboardResponse7(version, type, source, source1, source2, source3, source4, data)
  }

  @Operation(summary = "Endpoint for dashboard rest services")
  @RequestMapping(value = "/{version}/{type}/{source}/{source1}/{source2}/{source3}/{source4}/{source5}", method = RequestMethod.POST)
  Object postDashboardResponse8(@PathVariable("version") String version,
                                @PathVariable("type") String type,
                                @PathVariable("source") String source,
                                @PathVariable("source1") String source1,
                                @PathVariable("source2") String source2,
                                @PathVariable("source3") String source3,
                                @PathVariable("source4") String source4,
                                @PathVariable("source5") String source5,
                                @RequestBody(required = false) Object data) {

    return opsmxDashboardService.postDashboardResponse8(version, type, source, source1, source2, source3, source4, source5, data)
  }

  @Operation(summary = "Endpoint for dashboard rest services")
  @RequestMapping(value = "/{version}/{type}", method = RequestMethod.PUT)
  Object updateDashboardResponse(@PathVariable("version") String version,
                           @PathVariable("type") String type,
                           @RequestBody(required = false) Object data) {

    return opsmxDashboardService.updateDashboardResponse(version, type, data)
  }

  @Operation(summary = "Endpoint for dashboard rest services")
  @RequestMapping(value = "/{version}/{type}/{source}", method = RequestMethod.PUT)
  Object updateDashboardResponse1(@PathVariable("version") String version,
                                @PathVariable("type") String type,
                                @PathVariable("source") String source,
                                @RequestBody(required = false) Object data) {

    return opsmxDashboardService.updateDashboardResponse1(version, type, source, data)
  }

  @Operation(summary = "Endpoint for dashboard rest services")
  @RequestMapping(value = "/{version}/{type}/{source}/{source1}", method = RequestMethod.PUT)
  Object updateDashboardResponse2(@PathVariable("version") String version,
                                 @PathVariable("type") String type,
                                 @PathVariable("source") String source,
                                 @PathVariable("source1") String source1,
                                 @RequestBody(required = false) Object data) {

    return opsmxDashboardService.updateDashboardResponse2(version, type, source, source1, data)
  }

  @Operation(summary = "Endpoint for dashboard rest services")
  @RequestMapping(value = "/{version}/{type}/{source}/{source1}/{source2}", method = RequestMethod.PUT)
  Object updateDashboardResponse3(@PathVariable("version") String version,
                                 @PathVariable("type") String type,
                                 @PathVariable("source") String source,
                                 @PathVariable("source1") String source1,
                                 @PathVariable("source2") String source2,
                                 @RequestBody(required = false) Object data,
                                  HttpServletRequest request) {
    String cookie = "no-cookie"
    if(gateInstallationMode != null && gateInstallationMode.equals(GateInstallationModes.common)){
      cookie = request.getHeader("Cookie")
    }

    return opsmxDashboardService.updateDashboardResponse3(version, type, source, source1, source2, data, cookie)
  }

  @Operation(summary = "Endpoint for dashboard rest services")
  @RequestMapping(value = "/{version}/{type}/{source}/{source1}/{source2}/{source3}", method = RequestMethod.PUT)
  Object updateDashboardResponse4(@PathVariable("version") String version,
                                  @PathVariable("type") String type,
                                  @PathVariable("source") String source,
                                  @PathVariable("source1") String source1,
                                  @PathVariable("source2") String source2,
                                  @PathVariable("source3") String source3,
                                  @RequestBody(required = false) Object data) {

    return opsmxDashboardService.updateDashboardResponse4(version, type, source, source1, source2, source3, data)
  }

  @Operation(summary = "Endpoint for dashboard rest services")
  @RequestMapping(value = "/{version}/{type}/{source}/{source1}/{source2}/{source3}/{source4}", method = RequestMethod.PUT)
  Object updateDashboardResponse5(@PathVariable("version") String version,
                                  @PathVariable("type") String type,
                                  @PathVariable("source") String source,
                                  @PathVariable("source1") String source1,
                                  @PathVariable("source2") String source2,
                                  @PathVariable("source3") String source3,
                                  @PathVariable("source4") String source4,
                                  @RequestBody(required = false) Object data) {

    return opsmxDashboardService.updateDashboardResponse5(version, type, source, source1, source2, source3, source4, data)
  }

  @Operation(summary = "Endpoint for dashboard rest services")
  @RequestMapping(value = "/{version}/{type}/{source}/{source1}/{source2}/{source3}/{source4}/{source5}", method = RequestMethod.PUT)
  Object updateDashboardResponse6(@PathVariable("version") String version,
                                  @PathVariable("type") String type,
                                  @PathVariable("source") String source,
                                  @PathVariable("source1") String source1,
                                  @PathVariable("source2") String source2,
                                  @PathVariable("source3") String source3,
                                  @PathVariable("source4") String source4,
                                  @PathVariable("source5") String source5,
                                  @RequestBody(required = false) Object data) {

    return opsmxDashboardService.updateDashboardResponse6(version, type, source, source1, source2, source3, source4, source5, data)
  }
}
