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

  @POST("/notification/{type}")
  Object postNotificationServiceResponse1(@Path('type') String type,
                                          @Body Object data)

  @PUT("/notification/{type}")
  Object putNotificationServiceResponse2(@Path('type') String type,
                                          @Body Object data)

  @GET("/notification/{type}")
  Object getNotificationServiceResponse3(@Path('type') String type)

  @DELETE("/notification/{id}")
  Object deleteNotificationServiceResponse4(@Path('id') String type,
                                            @Body Object data)


}
