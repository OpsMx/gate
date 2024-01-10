/*
 * Copyright 2023 OpsMx, Inc.
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

package com.netflix.spinnaker.gate.services.internal

import org.springframework.web.bind.annotation.RequestParam
import retrofit.client.Response
import retrofit.http.Body
import retrofit.http.DELETE
import retrofit.http.GET
import retrofit.http.POST
import retrofit.http.PUT
import retrofit.http.Path
import retrofit.http.Query

interface OpsmxSsdService {
  @GET("/ssdservice/{version}/{type}")
  Object getSddResponse1(@Path('version') String version,
                         @Path('type') String type,
                         @Query("account") String account,
                         @Query("appId") String appId,
                         @Query("image") String image,
                         @Query("imageTag") String imageTag,
                         @Query('stage') String stage,
                         @Query("deployedAt") String deployedAt,
                         @Query("appName") String appName,
                         @Query("pageNo") Integer pageNo,
                         @Query("pageLimit") Integer pageLimit,
                         @Query("sortBy") String sortBy,
                         @Query("sortOrder") String sortOrder,
                         @Query("search") String search,
                         @Query("noOfDays") Integer noOfDays,
                         @Query("policy") String policy,
                         @Query("typeList") String typeList,
                         @Query("alertName") String alertName,
                         @Query("id") String id,
                         @Query("startTime") String startTime,
                         @Query("endTime") String endTime,
                         @Query("severity") String severity,
                         @Query("scope") String scope,
                         @Query("current") String current,
                         @Query("tag") String tag,
                         @Query("tool") String tool,
                         @Query("tags") String tags,
                         @Query("action") String action,
                         @Query("integrationType") String integrationType,
                         @Query("name") String name)

  @GET("/ssdservice/{version}/{type}/{source}")
  Object getSddResponse2(@Path('version') String version,
                         @Path('type') String type,
                         @Path('source') String source,
                         @Query("account") String account,
                         @Query("appId") String appId,
                         @Query("image") String image,
                         @Query("imageTag") String imageTag,
                         @Query("stage") String stage,
                         @Query("deployedAt") String deployedAt,
                         @Query("appName") String appName,
                         @Query("pageNo") Integer pageNo,
                         @Query("pageLimit") Integer pageLimit,
                         @Query("sortBy") String sortBy,
                         @Query("sortOrder") String sortOrder,
                         @Query("search") String search,
                         @Query("noOfDays") Integer noOfDays,
                         @Query("alertName") String alertName,
                         @Query("riskStatus") String riskStatus,
                         @Query("id") String id,
                         @Query("Vulnerability") String Vulnerability,
                         @Query("Component") String Component,
                         @Query("ComponentVersion") String ComponentVersion,
                         @Query("Image") String Image,
                         @Query("ImageVersion") String ImageVersion,
                         @Query("service") String service,
                         @Query("scope") String scope,
                         @Query("name") String name,
                         @Query("value") String value,
                         @Query("current") String current,
                         @Query("integratorType") String integratorType)

  @GET("/ssdservice/{version}/{type}/{source}/{source1}")
  Object getSddResponse3(@Path('version') String version,
                         @Path('type') String type,
                         @Path('source') String source,
                         @Path('source1') String source1,
                         @Query("account") String account,
                         @Query("appId") Integer appId,
                         @Query("image") String image,
                         @Query("appName") String appName,
                         @Query("noOfDays") Integer noOfDays,
                         @Query("pageNo") Integer pageNo,
                         @Query("pageLimit") Integer pageLimit,
                         @Query("kind") String kind,
                         @Query("search") String search,
                         @Query("service") String service,
                         @Query("sortBy") String sortBy,
                         @Query("sortOrder") String sortOrder,
                         @Query("id") String id,
                         @Query("dbomType") String dbomType)

  @GET("/ssdservice/{version}/{type}/{source}/{source1}/{source2}")
  Object getSddResponse4(@Path('version') String version,
                         @Path('type') String type,
                         @Path('source') String source,
                         @Path('source1') String source1,
                         @Path('source2') String source2,
                         @Query("account") String account,
                         @Query("appId") Integer appId,
                         @Query("image") String image,
                         @Query("imageTag") String imageTag,
                         @Query("stage") String stage,
                         @Query("deployedAt") String deployedAt,
                         @Query("appName") String appName,
                         @Query("pageNo") Integer pageNo,
                         @Query("pageLimit") Integer pageLimit,
                         @Query("sortBy") String sortBy,
                         @Query("sortOrder") String sortOrder,
                         @Query("search") String search,
                         @Query("noOfDays") Integer noOfDays,
                         @Query("id") String id,
                         @Query("fromImage") String fromImage,
                         @Query("toImage") String toImage,
                         @Query("fromImageTag") String fromImageTag,
                         @Query("toImageTag") String toImageTag,
                         @Query("fromAccount") String fromAccount,
                         @Query("toAccount") String toAccount,
                         @Query("kind") String kind,
                         @Query("active") String active,
                         @Query("Vulnerability") String Vulnerability,
                         @Query("Component") String Component,
                         @Query("ComponentVersion") String ComponentVersion,
                         @Query("fromApp") String fromApp,
                         @Query("service") String service,
                         @Query("toApp") String toApp,
                         @Query("dbomType") String dbomType)

  @GET("/ssdservice/{version}/{type}/{source}/{source1}/{source2}/{source3}")
  Object getSddResponse5(@Path('version') String version,
                         @Path('type') String type,
                         @Path('source') String source,
                         @Path('source1') String source1,
                         @Path('source2') String source2,
                         @Path('source3') String source3,
                         @Query("appId") Integer appId,
                         @Query("image") String image,
                         @Query("appName") String appName)

  @GET("/ssdservice/{version}/{type}/{source}/{source1}/{source2}/{source3}/{source4}")
  Object getSddResponse6(@Path('version') String version,
                         @Path('type') String type,
                         @Path('source') String source,
                         @Path('source1') String source1,
                         @Path('source2') String source2,
                         @Path('source3') String source3,
                         @Path('source4') String source4,
                         @Query("appId") Integer appId,
                         @Query("image") String image,
                         @Query("appName") String appName)

  @GET("/ssdservice/{version}/{type}/{source}/{source1}/{source2}/{source3}/{source4}/{source5}")
  Object getSddResponse7(@Path('version') String version,
                         @Path('type') String type,
                         @Path('source') String source,
                         @Path('source1') String source1,
                         @Path('source2') String source2,
                         @Path('source3') String source3,
                         @Path('source4') String source4,
                         @Path('source5') String source5,
                         @Query("appId") Integer appId,
                         @Query("image") String image,
                         @Query("appName") String appName)

  @GET("/ssdservice/{version}/{type}/{source}/download")
  Response downloadCSVFile(@Path('version') String version,
                           @Path('type') String type,
                           @Path('source') String source,
                           @Query("appId") String appId,
                           @Query("image") String image,
                           @Query("appName") String appName,
                           @Query("account") String account)


  @POST("/ssdservice/{version}/{type}")
  Object postSsdServiceResponse(@Path('version') String version,
                                @Path('type') String type,
                                @Query("stage") String stage,
                                @Query("policy") String policy,
                                @Query("policyId") Integer policyId,
                                @Query("id") Integer id,
                                @Query("scope") String scope,
                                @Query("appId") String appId,
                                @Query("vulnAlert") String vulnAlert,
                                @Body Object data)

  @POST("/ssdservice/{version}/{type}/{source}")
  Object postSsdServiceResponse3(@Path('version') String version,
                                 @Path('type') String type,
                                 @Path('source') String source,
                                 @Query("id") String id,
                                 @Body Object data)

  @PUT("/ssdservice/{version}/{type}")
  Object updateSsdServiceResponse(@Path('version') String version,
                                  @Path('type') String type,
                                  @Query("stage") String stage,
                                  @Query("policy") String policy,
                                  @Query("policyId") Integer policyId,
                                  @Query("id") Integer id,
                                  @Query("scope") String scope,
                                  @Query("appId") String appId,
                                  @Query("vulnAlert") String vulnAlert,
                                  @Query("integratorType") String integratorType,
                                  @Query("name") String name,
                                  @Body Object data)

  @GET("/ssdservice/{version}/{type}/{source}/download/json")
  Response downloadJsonFile(@Path('version') String version,
                            @Path('type') String type,
                            @Path('source') String source,
                            @Query("appId") String appId,
                            @Query("image") String image,
                            @Query("appName") String appName,
                            @Query("account") String account,
                            @Query("scoreCardName") String scoreCardName,
                            @Query("semgrep") String semgrep)


  @DELETE("/ssdservice/{version}/{type}")
  Object deleteSddResponse1(@Path('version') String version,
                            @Path('type') String type,
                            @Query("account") String account,
                            @Query("appId") String appId,
                            @Query("image") String image,
                            @Query("imageTag") String imageTag,
                            @Query('stage') String stage,
                            @Query("deployedAt") String deployedAt,
                            @Query("appName") String appName,
                            @Query("pageNo") Integer pageNo,
                            @Query("pageLimit") Integer pageLimit,
                            @Query("sortBy") String sortBy,
                            @Query("sortOrder") String sortOrder,
                            @Query("search") String search,
                            @Query("noOfDays") Integer noOfDays,
                            @Query("policy") String policy,
                            @Query("typeList") String typeList,
                            @Query("alertName") String alertName,
                            @Query("id") String id,
                            @Query("startTime") String startTime,
                            @Query("endTime") String endTime,
                            @Query("severity") String severity,
                            @Query("scope") String scope,
                            @Query("current") String current,
                            @Query("tag") String tag,
                            @Query("integratorType") String integratorType)

  @DELETE("/ssdservice/{version}/{type}/{source}")
  Object deleteSddResponse2(@Path('version') String version,
                            @Path('type') String type,
                            @Path('source') String source,
                            @Query("account") String account,
                            @Query("appId") String appId,
                            @Query("image") String image,
                            @Query("imageTag") String imageTag,
                            @Query("stage") String stage,
                            @Query("deployedAt") String deployedAt,
                            @Query("appName") String appName,
                            @Query("pageNo") Integer pageNo,
                            @Query("pageLimit") Integer pageLimit,
                            @Query("sortBy") String sortBy,
                            @Query("sortOrder") String sortOrder,
                            @Query("search") String search,
                            @Query("noOfDays") Integer noOfDays,
                            @Query("alertName") String alertName,
                            @Query("riskStatus") String riskStatus,
                            @Query("id") String id,
                            @Query("Vulnerability") String Vulnerability,
                            @Query("Component") String Component,
                            @Query("ComponentVersion") String ComponentVersion,
                            @Query("Image") String Image,
                            @Query("ImageVersion") String ImageVersion,
                            @Query("service") String service,
                            @Query("scope") String scope,
                            @Query("name") String name,
                            @Query("value") String value)

  @DELETE("/ssdservice/{version}/{type}/{source}/{source1}")
  Object deleteSddResponse3(@Path('version') String version,
                            @Path('type') String type,
                            @Path('source') String source,
                            @Path('source1') String source1,
                            @Query("account") String account,
                            @Query("appId") Integer appId,
                            @Query("image") String image,
                            @Query("appName") String appName,
                            @Query("noOfDays") Integer noOfDays,
                            @Query("pageNo") Integer pageNo,
                            @Query("pageLimit") Integer pageLimit,
                            @Query("kind") String kind,
                            @Query("search") String search,
                            @Query("service") String service,
                            @Query("sortBy") String sortBy,
                            @Query("sortOrder") String sortOrder,
                            @Query("id") String id,
                            @Query("dbomType") String dbomType)

  @DELETE("/ssdservice/{version}/{type}/{source}/{source1}/{source2}")
  Object deleteSddResponse4(@Path('version') String version,
                            @Path('type') String type,
                            @Path('source') String source,
                            @Path('source1') String source1,
                            @Path('source2') String source2,
                            @Query("account") String account,
                            @Query("appId") Integer appId,
                            @Query("image") String image,
                            @Query("imageTag") String imageTag,
                            @Query("stage") String stage,
                            @Query("deployedAt") String deployedAt,
                            @Query("appName") String appName,
                            @Query("pageNo") Integer pageNo,
                            @Query("pageLimit") Integer pageLimit,
                            @Query("sortBy") String sortBy,
                            @Query("sortOrder") String sortOrder,
                            @Query("search") String search,
                            @Query("noOfDays") Integer noOfDays,
                            @Query("id") String id,
                            @Query("fromImage") String fromImage,
                            @Query("toImage") String toImage,
                            @Query("fromImageTag") String fromImageTag,
                            @Query("toImageTag") String toImageTag,
                            @Query("fromAccount") String fromAccount,
                            @Query("toAccount") String toAccount,
                            @Query("kind") String kind,
                            @Query("active") String active,
                            @Query("Vulnerability") String Vulnerability,
                            @Query("Component") String Component,
                            @Query("ComponentVersion") String ComponentVersion,
                            @Query("fromApp") String fromApp,
                            @Query("service") String service,
                            @Query("toApp") String toApp,
                            @Query("dbomType") String dbomType)

  @DELETE("/ssdservice/{version}/{type}/{source}/{source1}/{source2}/{source3}")
  Object deleteSddResponse5(@Path('version') String version,
                            @Path('type') String type,
                            @Path('source') String source,
                            @Path('source1') String source1,
                            @Path('source2') String source2,
                            @Path('source3') String source3,
                            @Query("appId") Integer appId,
                            @Query("image") String image,
                            @Query("appName") String appName)

  @DELETE("/ssdservice/{version}/{type}/{source}/{source1}/{source2}/{source3}/{source4}")
  Object deleteSddResponse6(@Path('version') String version,
                            @Path('type') String type,
                            @Path('source') String source,
                            @Path('source1') String source1,
                            @Path('source2') String source2,
                            @Path('source3') String source3,
                            @Path('source4') String source4,
                            @Query("appId") Integer appId,
                            @Query("image") String image,
                            @Query("appName") String appName)

  @DELETE("/ssdservice/{version}/{type}/{source}/{source1}/{source2}/{source3}/{source4}/{source5}")
  Object deleteSddResponse7(@Path('version') String version,
                            @Path('type') String type,
                            @Path('source') String source,
                            @Path('source1') String source1,
                            @Path('source2') String source2,
                            @Path('source3') String source3,
                            @Path('source4') String source4,
                            @Path('source5') String source5,
                            @Query("appId") Integer appId,
                            @Query("image") String image,
                            @Query("appName") String appName)
}
