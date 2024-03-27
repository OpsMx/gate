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

package com.opsmx.spinnaker.gate.audit;

import com.google.gson.Gson;
import com.opsmx.spinnaker.gate.enums.AuditEventType;
import com.opsmx.spinnaker.gate.feignclient.AuditService;
import com.opsmx.spinnaker.gate.model.OesAuditModel;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.stereotype.Component;

@ConditionalOnExpression("${services.auditservice.enabled:false}")
@Component
@EnableFeignClients(basePackageClasses = AuditService.class)
@Slf4j
public class AuditRestApiHandler implements AuditHandler {

  @Autowired(required = false)
  private AuditService auditService;

  Gson gson = new Gson();

  @Override
  public String publishEvent(AuditEventType auditEventType, Object auditData) {
    OesAuditModel oesAuditModel = new OesAuditModel();
    oesAuditModel.setEventId(UUID.randomUUID().toString());
    oesAuditModel.setAuditData(auditData);
    oesAuditModel.setEventType(auditEventType);
    auditService.publishAuditData(oesAuditModel, "OES");
    String model = gson.toJson(oesAuditModel, OesAuditModel.class);
    log.debug("model: {}", model);
    return model;
  }
}
