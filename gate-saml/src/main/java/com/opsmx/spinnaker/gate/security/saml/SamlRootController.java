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

package com.opsmx.spinnaker.gate.security.saml;

import com.netflix.spinnaker.gate.security.saml.SamlSsoConfig;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class SamlRootController {

  @Value("${services.oesui.externalUrl:}")
  private String uiBaseUrl;

  @Autowired private SamlSsoConfig samlSsoConfig;

  @RequestMapping("/nonadminuser")
  void nonadminuser(HttpServletResponse response) throws Exception {
    log.info("uiBaseUrl : {}", uiBaseUrl);

    HttpSecurity httpSecurity = BeanUtil.getBean(HttpSecurity.class);
    httpSecurity.formLogin().disable();

    //    AuthenticationManagerBuilder authenticationManagerBuilder =
    // BeanUtil.getBean(AuthenticationManagerBuilder.class);
    //    samlSsoConfig.configureGlobal(authenticationManagerBuilder);

    response.sendRedirect(uiBaseUrl + "/application");
  }
}
