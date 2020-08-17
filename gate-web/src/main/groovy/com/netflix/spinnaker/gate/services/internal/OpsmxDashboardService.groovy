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


import retrofit.http.*

interface OpsmxDashboardService {

  @GET("/dashboardservice/{version}/{type}")
  Object getDashboardResponse1(@Path('version') String version,
                              @Path('type') String type)

  @GET("/dashboardservice/{version}/{type}/{source}")
  Object getDashboardResponse(@Path('version') String version,
                         @Path('type') String type,
                         @Path('source') String source)

  @GET("/dashboardservice/{version}/{type}/{source}/{source1}")
  Object getDashboardResponse4(@Path('version') String version,
                         @Path('type') String type,
                         @Path('source') String source,
                         @Path('source1') String source1)

  @GET("/dashboardservice/{version}/{type}/{source}/{source1}/{source2}")
  Object getDashboardResponse5(@Path('version') String version,
                         @Path('type') String type,
                         @Path('source') String source,
                         @Path('source1') String source1,
                         @Path('source2') String source2)

  @GET("/dashboardservice/{version}/{type}/{source}/{source1}/{source2}/{source3}")
  Object getDashboardResponse6(@Path('version') String version,
                         @Path('type') String type,
                         @Path('source') String source,
                         @Path('source1') String source1,
                         @Path('source2') String source2,
                         @Path('source3') String source3)

  @DELETE("/dashboardservice/{version}/{type}")
  Object deleteDashboardResponse(@Path('version') String version,
                           @Path('type') String type)

  @DELETE("/dashboardservice/{version}/{type}/{source}")
  Object deleteDashboardResponse1(@Path('version') String version,
                                 @Path('type') String type,
                                 @Path('source') String source)

  @DELETE("/dashboardservice/{version}/{type}/{source}/{source1}")
  Object deleteDashboardResponse4(@Path('version') String version,
                            @Path('type') String type,
                            @Path('source') String source,
                            @Path('source1') String source1)

  @POST("/dashboardservice/{version}/{type}")
  Object postDashboardResponse(@Path('version') String version,
                         @Path('type') String type,
                         @Body Object data)

  @POST("/dashboardservice/{version}/{type}/{source}")
  Object postDashboardResponse3(@Path('version') String version,
                               @Path('type') String type,
                               @Path('source') String source,
                               @Body Object data)

  @POST("/dashboardservice/{version}/{type}/{source}/{source1}")
  Object postDashboardResponse4(@Path('version') String version,
                          @Path('type') String type,
                          @Path('source') String source,
                          @Path('source1') String source1,
                          @Body Object data)

  @POST("/dashboardservice/{version}/{type}/{source}/{source1}/{source2}")
  Object postDashboardResponse5(@Path('version') String version,
                          @Path('type') String type,
                          @Path('source') String source,
                          @Path('source1') String source1,
                          @Path('source2') String source2,
                          @Body Object data)

  @POST("/dashboardservice/{version}/{type}/{source}/{source1}/{source2}/{source3}")
  Object postDashboardResponse6(@Path('version') String version,
                          @Path('type') String type,
                          @Path('source') String source,
                          @Path('source1') String source1,
                          @Path('source2') String source2,
                          @Path('source3') String source3,
                          @Body Object data)

  @PUT("/dashboardservice/{version}/{type}")
  Object updateDashboardResponse(@Path('version') String version,
                           @Path('type') String type,
                           @Body Object data)

  @PUT("/dashboardservice/{version}/{type}/{source}")
  Object updateDashboardResponse1(@Path('version') String version,
                                @Path('type') String type,
                                @Path('source') String source,
                                @Body Object data)

  @PUT("/dashboardservice/{version}/{type}/{source}/{source1}")
  Object updateDashboardResponse2(@Path('version') String version,
                                 @Path('type') String type,
                                 @Path('source') String source,
                                 @Path('source1') String source1,
                                 @Body Object data)

  @PUT("/dashboardservice/{version}/{type}/{source}/{source1}/{source2}")
  Object updateDashboardResponse3(@Path('version') String version,
                                  @Path('type') String type,
                                  @Path('source') String source,
                                  @Path('source1') String source1,
                                  @Path('source2') String source2,
                                  @Body Object data)

}
