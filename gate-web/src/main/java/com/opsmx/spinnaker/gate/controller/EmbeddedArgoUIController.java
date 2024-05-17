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

package com.opsmx.spinnaker.gate.controller;

import com.netflix.spinnaker.security.AuthenticatedRequest;
import com.opsmx.spinnaker.gate.service.EmbeddedArgoUIService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping(value = "/argoui")
public class EmbeddedArgoUIController {

  @Autowired private EmbeddedArgoUIService embeddedArgoUIService;

  @GetMapping(value = "/load")
  public void load(
      @RequestParam(value = "argoid") String argoid,
      @RequestParam(value = "path") String path,
      HttpServletResponse response) {
    String username = AuthenticatedRequest.getSpinnakerUser().orElse(null);
    String location = embeddedArgoUIService.getBounceEndpoint(username, argoid, path);
    response.setStatus(302);
    response.setHeader("Location", location);
    log.debug(
        "Successfully generated token and redirecting to path {} where argoid is : {} and username is {}",
        path,
        argoid,
        username);
  }
}
