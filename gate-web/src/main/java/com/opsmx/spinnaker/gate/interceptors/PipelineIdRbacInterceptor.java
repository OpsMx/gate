/*
 * Copyright 2022 OpsMx, Inc.
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

import com.opsmx.spinnaker.gate.rbac.ApplicationFeatureRbac;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Slf4j
@Component
@ConditionalOnExpression("${rbac.feature.application.enabled:true}")
public class PipelineIdRbacInterceptor implements HandlerInterceptor {

  @Autowired private ApplicationFeatureRbac applicationFeatureRbac;

  private final List<String> customGatePlugins = new ArrayList<>();

  {
    customGatePlugins.add("OpsMxApprovalStagePlugin");
    customGatePlugins.add("OpsMxPolicyStagePlugin");
    customGatePlugins.add("OpsMxVerificationStagePlugin");
  }

  @Override
  public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
      throws Exception {
    log.debug("***********Start of the preHandle -- PipelineIdRbacInterceptor");
    try {
      log.info(
          "Request intercepted for authorizing if the user is having enough access to perform the action ******PipelineIdRbacInterceptor*****");
      String origin = request.getHeader(HttpHeaders.ORIGIN);
      if (origin != null && customGatePlugins.contains(origin)) {
        return true;
      }

      applicationFeatureRbac.authorizeUserForPipelineId(
          request.getUserPrincipal().getName(), request.getRequestURI(), request.getMethod());
    } catch (NumberFormatException nfe) {
      log.debug("Ignoring the rbac check as it threw number format exception");
    }
    log.debug("***********End of the preHandle -- PipelineIdRbacInterceptor");
    return true;
  }
}
