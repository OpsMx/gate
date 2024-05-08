/*
 * Copyright 2024 OpsMx, Inc.
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

import com.opsmx.spinnaker.gate.config.OESSpinnakerVersionProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@ConditionalOnExpression("${services.platform.enabled:false}")
@RestController
@RequestMapping(value = "/oes")
public class OESSpinnakerVersionController {
    @Autowired(required = false)
    private OESSpinnakerVersionProperties oesSpinnakerVersionProperties;

    @GetMapping(value = "/spinnakerVersion", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> getOESSpinnakerVersion() {
        log.debug("Get OES Spinnaker Version API invoked");
        return new ResponseEntity<>(oesSpinnakerVersionProperties.getSpinnakerVersion(), HttpStatus.OK);
    }

}
