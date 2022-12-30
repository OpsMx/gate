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

package com.netflix.spinnaker.gate.security.oauth2

import groovy.util.logging.Slf4j
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression
import org.springframework.stereotype.Component

import java.util.function.BiFunction
import java.util.stream.Collectors

@Slf4j
@Component(value = "spinnaker-oauth2-group-extractor")
@ConditionalOnExpression('${feature.auth-provider.flag:false}')
class GroupExtractor implements BiFunction<String, Map, List<String>> {

  @Override
  List<String> apply(String s, Map map) {

    List<String> groups = null

    log.debug("extracted values : {}", map)

    if(map.containsKey("groups")){
      List<String> groupMemberships = ((List<String>)map.get("groups"))
      if (groupMemberships!=null && !groupMemberships.isEmpty()) {
        groups = groupMemberships.stream().map({ group -> group.substring(1) }).collect(Collectors.toList())
        log.debug("groups : {}", groups)
      }
    }

    if (map.containsKey("idpGroups")){
      groups = ((List<String>)map.get("idpGroups"))
      log.debug("idpGroups : {}", groups)
    }

    return groups
  }
}
