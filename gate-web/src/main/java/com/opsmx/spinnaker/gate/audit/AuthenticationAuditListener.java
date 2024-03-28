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

import com.opsmx.spinnaker.gate.enums.AuditEventType;
import com.opsmx.spinnaker.gate.model.AuditData;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.security.AbstractAuthenticationAuditListener;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.event.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@ConditionalOnExpression("${services.auditservice.enabled:false}")
@EnableAsync
public class AuthenticationAuditListener extends AbstractAuthenticationAuditListener {

  @Autowired(required = false)
  private AuditHandler auditHandler;

  @Async
  @Override
  public void onApplicationEvent(AbstractAuthenticationEvent event) {

    try {
      log.debug("Authentication audit events received : {}", event);
      // OP-17106: looks like a saml event fetch name and roles to publish
      if (event.getAuthentication().isAuthenticated()
          && event instanceof InteractiveAuthenticationSuccessEvent) {
        log.debug("publishEvent InteractiveAuthenticationSuccessEvent");
        log.info("Login: User '{}' logged in successfully.",event.getAuthentication().getName());
        handleAuthenticationEvent(event, AuditEventType.AUTHENTICATION_SUCCESSFUL_AUDIT);
        return;
      }

      if (event.getAuthentication().isAuthenticated()
          && event instanceof AuthenticationSuccessEvent) {
        log.debug("publishEvent AuthenticationSuccessEvent");
        auditHandler.publishEvent(AuditEventType.AUTHENTICATION_SUCCESSFUL_AUDIT, event);
      } else if (!event.getAuthentication().isAuthenticated()
          && event instanceof AbstractAuthenticationFailureEvent) {
        log.debug("publishEvent AbstractAuthenticationFailureEvent");
        log.info("Login : User '{}' login failed due to incorrect credentials.",event.getAuthentication().getName());
        auditHandler.publishEvent(AuditEventType.AUTHENTICATION_FAILURE_AUDIT, event);
      } else if (event instanceof LogoutSuccessEvent) {
        if (event
            .getAuthentication()
            .getClass()
            .getName()
            .equals("org.springframework.security.providers.ExpiringUsernameAuthenticationToken")) {
          log.debug("publishEvent LogoutSuccessEvent with ExpiringUsernameAuthenticationToken");
          log.info("Logout : User '{}' logged out.",event.getAuthentication().getName());
          handleAuthenticationEvent(event, AuditEventType.SUCCESSFUL_USER_LOGOUT_AUDIT);
          return;
        }
        log.debug("publishEvent LogoutSuccessEvent");
        log.info("Logout : User '{}' logged out manually.",event.getAuthentication().getName());
        auditHandler.publishEvent(AuditEventType.SUCCESSFUL_USER_LOGOUT_AUDIT, event);
      }

    } catch (Exception e) {
      log.error("Exception occured while capturing audit events : {}", e);
    }
  }

  private void handleAuthenticationEvent(
      AbstractAuthenticationEvent event, AuditEventType eventType) {
    AbstractAuthenticationToken auth = (AbstractAuthenticationToken) event.getAuthentication();
    String name = auth.getName();
    List<String> roles =
        Optional.ofNullable(auth.getAuthorities()).orElse(new ArrayList<>()).stream()
            .map(GrantedAuthority::getAuthority)
            .collect(Collectors.toList());
    AuditData data = new AuditData(name, roles);
    auditHandler.publishEvent(eventType, data);
  }
}
