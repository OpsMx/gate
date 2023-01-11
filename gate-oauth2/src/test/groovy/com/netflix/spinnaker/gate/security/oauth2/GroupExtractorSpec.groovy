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

import spock.lang.Specification

class GroupExtractorSpec extends Specification {

  def "should extract groups from details for non-idp auth"() {
    given:
    def groupExtractor = new GroupExtractor()
    groupExtractor.groupAttribute = "groups"
    groupExtractor.idpGroupAttribute = "idpGroups"
    def details = [
      groups: rolesValue,
      sub: "2acf7aaf-487c-4181-a6ab-322fedfba349",
      "preferred_username": "pra@gmail.com"
    ]

    expect:
    groupExtractor.apply("jwtToken", details) == expectedRoles

    where:
    rolesValue                || expectedRoles
    ["/foo", "/bar"]          || ["foo", "bar"]
    []                        || null
  }

  def "should extract groups from details for idp auth"() {
    given:
    def groupExtractor = new GroupExtractor()
    groupExtractor.groupAttribute = "groups"
    groupExtractor.idpGroupAttribute = "idpGroups"
    def details = [
      groups: [],
      idpGroups: rolesValue,
      sub: "2acf7aaf-487c-4181-a6ab-322fedfba349",
      "preferred_username": "pra@gmail.com"
    ]

    expect:
    groupExtractor.apply("jwtToken", details) == expectedRoles

    where:
    rolesValue                || expectedRoles
    ["foo", "bar"]            || ["foo", "bar"]
    null                      || null
  }
}
