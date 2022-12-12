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
 *
 */

package com.netflix.spinnaker.gate.security.saml

import groovy.util.logging.Slf4j
import org.springframework.beans.MutablePropertyValues
import org.springframework.beans.factory.annotation.Value
import org.springframework.beans.factory.support.DefaultListableBeanFactory
import org.springframework.beans.factory.support.GenericBeanDefinition
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

import javax.servlet.http.HttpServletResponse

@Slf4j
@RestController
class SamlRootController  {

  @Value('${services.oesui.externalUrl:}')
  String uiBaseUrl

  @RequestMapping("/nonadminuser")
  void root(HttpServletResponse response) {
    log.info("uiBaseUrl : {}", uiBaseUrl)
    DefaultListableBeanFactory defaultListableBeanFactory = new DefaultListableBeanFactory()
    defaultListableBeanFactory.removeBeanDefinition("adminAuthConfig")
    defaultListableBeanFactory.destroySingleton("adminAuthConfig")
    GenericBeanDefinition genericBeanDefinition = new GenericBeanDefinition()
    genericBeanDefinition.setBeanClass(SamlSsoUIConfig.class)

    defaultListableBeanFactory.registerBeanDefinition("samlSsoUiConfig", genericBeanDefinition)

    response.sendRedirect("/ui" + "/application")
  }
}
