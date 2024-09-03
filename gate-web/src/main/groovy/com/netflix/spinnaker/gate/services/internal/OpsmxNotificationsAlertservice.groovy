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

package com.netflix.spinnaker.gate.services.internal

import retrofit.http.*

interface OpsmxNotificationsAlertservice {

  @POST("/notifications/{type}")
  Object postNotificationServiceResponse1(@Path('type') String type,
                                          @Body Object data)

  @PUT("/notifications/{type}/{id}")
  Object putNotificationServiceResponse2(@Path('type') String type,
                                         @Path('id') String id,
                                          @Body Object data)

  @GET("/notifications/{type}")
  Object getNotificationServiceResponse3(@Path('type') String type)

  @DELETE("/notifications/{type}/{id}")
  Object deleteNotificationServiceResponse4(@Path('type') String type,
                                            @Path('id') String id)

}
