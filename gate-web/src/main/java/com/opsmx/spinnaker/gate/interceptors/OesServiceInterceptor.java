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

package com.opsmx.spinnaker.gate.interceptors;

import com.opsmx.spinnaker.gate.exception.InvalidApiKeyException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.HandlerInterceptor;

@Slf4j
public class OesServiceInterceptor implements HandlerInterceptor {

  private static final String apiKey = "b5c6b2d5-88e2-4866-9df3-0e17bf5e72c5";

  @Override
  public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
      throws Exception {

    if (request.getHeader("apiKey") == null
        || request.getHeader("apiKey").isBlank()
        || !request.getHeader("apiKey").equals(apiKey)) {

      throw new InvalidApiKeyException("Access forbidden. Invalid API key");
    }
    return true;
  }
}
