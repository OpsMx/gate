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

import com.netflix.spinnaker.gate.security.saml.util.BeanUtil
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Value
import org.springframework.beans.factory.config.AutowireCapableBeanFactory
import org.springframework.beans.factory.config.BeanDefinition
import org.springframework.beans.factory.support.BeanDefinitionRegistry
import org.springframework.beans.factory.support.DefaultListableBeanFactory
import org.springframework.beans.factory.support.GenericBeanDefinition
import org.springframework.context.annotation.AnnotationConfigApplicationContext
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
//    AnnotationConfigApplicationContext context =
//      new AnnotationConfigApplicationContext(AdminAuthConfig.class)
//
//    //defaultListableBeanFactory.destroySingleton("adminAuthConfig")
//    GenericBeanDefinition genericBeanDefinition = new GenericBeanDefinition()
//    genericBeanDefinition.setBeanClass(SamlSsoUIConfig.class)
//
////    defaultListableBeanFactory.registerBeanDefinition("samlSsoUiConfig", genericBeanDefinition)
//
//    context.registerBeanDefinition("samlSsoUiConfig", genericBeanDefinition)

    AutowireCapableBeanFactory autowireCapableBeanFactory = BeanUtil.getApplicationContext().getAutowireCapableBeanFactory()
    BeanDefinitionRegistry registry = (BeanDefinitionRegistry) autowireCapableBeanFactory

    if (registry.containsBeanDefinition("adminAuthConfiguration")) {
      log.info("removing bean definition adminAuthConfiguration")
      registry.removeBeanDefinition("adminAuthConfiguration")
    }


    if (registry.containsBeanDefinition("basicAuthProvider")){
      log.info("removing bean definition basicAuthProvider")
      registry.removeBeanDefinition("basicAuthProvider")
    }

    if (registry.containsBeanDefinition("authManagerBuilder")){
      log.info("removing bean definition authManagerBuilder")
      registry.removeBeanDefinition("authManagerBuilder")
    }

    GenericBeanDefinition myBeanDefinition = new GenericBeanDefinition()
    myBeanDefinition.setBeanClass(SamlSsoUIConfig.class);

    registry.registerBeanDefinition("samlSsoConfiguration", myBeanDefinition)

    response.sendRedirect("/ui" + "/application")
  }
}
