/*
 * Copyright 2022 Netflix, Inc.
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
import org.jetbrains.annotations.NotNull
import org.springframework.stereotype.Component

import java.util.function.BiFunction
import java.util.function.Function

@Slf4j
@Component(value = "spinnaker-oauth2-group-extractor")
class SpinnakerGroupExtractor implements BiFunction<String, Map, List<String>>{
  @Override
  List<String> apply(String s, Map map) {

    log.info("SpinnakerGroupExtractor string value : {}", s)
    log.info("SpinnakerGroupExtractor map value : {}", map)
    List<String> groups = new ArrayList<>()
    groups.add("isdgroups")
    return groups
  }

  @Override
  def <V> BiFunction<String, Map, V> andThen(@NotNull Function<? super List<String>, ? extends V> after) {
    return super.andThen(after)
  }
}
