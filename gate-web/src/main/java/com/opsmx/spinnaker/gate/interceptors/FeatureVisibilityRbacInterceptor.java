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
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Slf4j
@Component
@ConditionalOnExpression("${rbac.feature.application.enabled:true}")
public class FeatureVisibilityRbacInterceptor implements HandlerInterceptor {

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
    log.debug("***********Start of the preHandle -- FeatureVisibilityRbacInterceptor");
    log.info("request intercepted to authorize if the user is having feature visibility ");
    String origin = request.getHeader(HttpHeaders.ORIGIN);
    if (origin != null && customGatePlugins.contains(origin)) {
      return true;
    }
    applicationFeatureRbac.authorizeUserForFeatureVisibility(request.getUserPrincipal().getName());
    log.debug("***********End of the preHandle -- FeatureVisibilityRbacInterceptor");
    return true;
  }
}
