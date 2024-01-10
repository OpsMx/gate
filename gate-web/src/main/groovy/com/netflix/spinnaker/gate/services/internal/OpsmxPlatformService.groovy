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

package com.netflix.spinnaker.gate.services.internal

import org.mapstruct.Qualifier
import retrofit.client.Response
import retrofit.http.*

interface OpsmxPlatformService {


  @GET("/platformservice/{version}/{type}")
  Object getPlatformResponse1(@Path('version') String version,
                              @Path('type') String type,
                              @Query("datasourceType") String datasourceType,
                              @Query("accountName") String accountName,
                              @Query("source") String source,
                              @Query("permission") String permission,
                              @Query("search") String search,
                              @Query("username") String username,
                              @Query("pageNo") Integer pageNo,
                              @Query("pageLimit") Integer pageLimit,
                              @Query("sortBy") String sortBy,
                              @Query("sortOrder") String sortOrder,
                              @Query("applicationId") Integer applicationId,
                              @Query("applicationName") String applicationName,
                              @Query("noOfDays") Integer noOfDays,
                              @Query("filterBy") String filterBy,
                              @Query(value = "cdTool")String cdTool)

  @GET("/platformservice/{version}/{type}/{source}")
  Object getPlatformResponse(@Path('version') String version,
                             @Path('type') String type,
                             @Path('source') String source,
                             @Query("source1") String source1,
                             @Query("chartId") Integer chartId,
                             @Query("noOfDays") Integer noOfDays,
                             @Query("argoName") String argoName,
                             @Query("agentName") String agentName)

  @GET("/platformservice/{version}/{type}/{source}/{source1}")
  Object getPlatformResponse4(@Path('version') String version,
                              @Path('type') String type,
                              @Path('source') String source,
                              @Path('source1') String source1,
                              @Query("agentName") String agentName,
                              @Query("cdName") String cdName,
                              @Query("datasourceType") String datasourceType,
                              @Query("permissionId") String permissionId,
                              @Query("applicationId") Integer applicationId,
                              @Query("canaryId") Integer canaryId,
                              @Query("applicationName") String applicationName,
                              @Query("argoName") String argoName,
                              @Query("nameSpace") String nameSpace)

  @GET("/platformservice/{version}/{type}/{source}/{source1}/{source2}")
  Object getPlatformResponse5(@Path('version') String version,
                              @Path('type') String type,
                              @Path('source') String source,
                              @Path('source1') String source1,
                              @Path('source2') String source2,
                              @Query("permissionId") String permissionId,
                              @Query("resourceType") String resourceType,
                              @Query("featureType") String featureType,
                              @Query("sourceName") String sourceName)

  @GET("/platformservice/{version}/{type}/{source}/{source1}/{source2}/{source3}")
  Object getPlatformResponse6(@Path('version') String version,
                              @Path('type') String type,
                              @Path('source') String source,
                              @Path('source1') String source1,
                              @Path('source2') String source2,
                              @Path('source3') String source3,
                              @Query('agentName') String agentName,
                              @Query('sourceType') String sourceType)

  @GET("/platformservice/{version}/{type}/{source}/{source1}/{source2}/{source3}/{source4}")
  Object getPlatformResponse7(@Path('version') String version,
                              @Path('type') String type,
                              @Path('source') String source,
                              @Path('source1') String source1,
                              @Path('source2') String source2,
                              @Path('source3') String source3,
                              @Path('source4') String source4)

  @GET("/platformservice/{version}/{type}/{source}/{source1}/{source2}/{source3}/{source4}/{source5}/{source6}")
  Object getPlatformResponse8(@Path('version') String version,
                              @Path('type') String type,
                              @Path('source') String source,
                              @Path('source1') String source1,
                              @Path('source2') String source2,
                              @Path('source3') String source3,
                              @Path('source4') String source4,
                              @Path('source5') String source5,
                              @Path('source6') String source6,
                              @Query("type") String gateType)

  @GET("/platformservice/{version}/{type}/{source}/manifest")
  Response getCdIntegratorManifestDownloadFile(@Path('type') String type,
                                               @Path('source') String source,
                                               @Query("argoName") String argoName,
                                               @Query('agentName') String agentName,
                                               @Query("namespace") String namespace)

  @GET("/platformservice/v7/datasource/groups")
  Object getPlatformResponse9(@Query("name") String name,
                              @Query("isArgoEnabled") boolean isArgoEnabled)


  @GET("/platformservice/{version}/insights/download")
  Response downloadCSVFile(@Path('version') String version,
                           @Query("chartId") Integer chartId,
                           @Query("noOfDays") Integer noOfDays)

  @DELETE("/platformservice/{version}/{type}")
  Object deletePlatformResponse(@Path('version') String version,
                                @Path('type') String type,
                                @Query("accountName") String accountName)

  @DELETE("/platformservice/{version}/{type}/{source}")
  Object deletePlatformResponse1(@Path('version') String version,
                                 @Path('type') String type,
                                 @Path('source') String source,
                                 @Query("argoName") String argoName,
                                 @Query("agentName") String agentName,
                                 @Query("checkOnlyDeletedArgo") boolean checkOnlyDeletedArgo)

  @DELETE("/platformservice/{version}/{type}/{source}/{source1}")
  Object deletePlatformResponse4(@Path('version') String version,
                                 @Path('type') String type,
                                 @Path('source') String source,
                                 @Path('source1') String source1)

  @DELETE("/platformservice/{version}/{type}/{source}/{source1}/{source2}")
  Object deletePlatformResponse5(@Path('version') String version,
                                 @Path('type') String type,
                                 @Path('source') String source,
                                 @Path('source1') String source1,
                                 @Path('source2') String source2,
                                 @Query("featureType") String featureType
  )

  @DELETE("/platformservice/{version}/{type}/{source}/{source1}/{source2}")
  Object deletePlatformResponse6(@Path('version') String version,
                                 @Path('type') String type,
                                 @Path('source') String source,
                                 @Path('source1') String source1,
                                 @Path('source2') String source2,
                                 @Path("source3") String source3)

  @POST("/platformservice/{version}/{type}")
  Object postPlatformResponse(@Path('version') String version,
                              @Path('type') String type,
                              @Body Object data)

  @POST("/platformservice/{version}/{type}/{source}")
  Object postPlatformResponse3(@Path('version') String version,
                               @Path('type') String type,
                               @Path('source') String source,
                               @Body Object data)

  @POST("/platformservice/{version}/{type}/{source}/{source1}")
  Object postPlatformResponse4(@Path('version') String version,
                               @Path('type') String type,
                               @Path('source') String source,
                               @Path('source1') String source1,
                               @Query('isExists') boolean isExists,
                               @Query('argoCdUrl') String argoCdUrl,
                               @Query('rolloutsEnabled') boolean rolloutsEnabled,
                               @Query('isdUrl') String isdUrl,
                               @Query('argoName') String argoName,
                               @Query('agentName') String agentName,
                               @Query('nameSpace') String nameSpace,
                               @Body Object data)

  @POST("/platformservice/{version}/{type}/{source}/{source1}/{source2}")
  Object postPlatformResponse5(@Path('version') String version,
                               @Path('type') String type,
                               @Path('source') String source,
                               @Path('source1') String source1,
                               @Path('source2') String source2,
                               @Body Object data)

  @GET("/platformservice/{version}/{type}/{source}/{source1}")
  Object downloadManifestResponse5(@Path('version') String version,
                                   @Path('type') String type,
                                   @Path('source') String source,
                                   @Path('source1') String source1,
                                   @Query('argoName') String argoName,
                                   @Query('agentName') String agentName,
                                   @Query('nameSpace') String nameSpace)


  @POST("/platformservice/{version}/{type}/{source}/{source1}/{source2}/{source3}")
  Object postPlatformResponse6(@Path('version') String version,
                               @Path('type') String type,
                               @Path('source') String source,
                               @Path('source1') String source1,
                               @Path('source2') String source2,
                               @Path('source3') String source3,
                               @Body Object data)

  @PUT("/platformservice/{version}/{type}")
  Object updatePlatformResponse(@Path('version') String version,
                                @Path('type') String type,
                                @Body Object data)

  @PUT("/platformservice/{version}/{type}/{source}")
  Object updatePlatformResponse1(@Path('version') String version,
                                 @Path('type') String type,
                                 @Path('source') String source,
                                 @Body Object data)

  @PUT("/platformservice/{version}/{type}/{source}/{source1}")
  Object updatePlatformResponse2(@Path('version') String version,
                                 @Path('type') String type,
                                 @Path('source') String source,
                                 @Path('source1') String source1,
                                 @Body Object data)

  @PUT("/platformservice/{version}/{type}/{source}/{source1}/{source2}")
  Object updatePlatformResponse3(@Path('version') String version,
                                 @Path('type') String type,
                                 @Path('source') String source,
                                 @Path('source1') String source1,
                                 @Path('source2') String source2,
                                 @Query("sourceName") String sourceName,
                                 @Query('argoName') String argoName,
                                 @Query('agentName') String agentName,
                                 @Query('nameSpace') String nameSpace,
                                 @Body Object data)

  @PUT("/platformservice/{version}/{type}/{source}/{source1}/{source2}/{source3}/{source4}")
  Object updatePlatformResponse4(@Path('version') String version,
                                 @Path('type') String type,
                                 @Path('source') String source,
                                 @Path('source1') String source1,
                                 @Path('source2') String source2,
                                 @Path('source3') String source3,
                                 @Path('source4') String source4,
                                 @Query("featureType") String featureType,
                                 @Body Object data)

  @GET("/platformservice/{version}/argo/sampleTemplate")
  Response downloadSampleTemplate(@Path('version') String version)

}
