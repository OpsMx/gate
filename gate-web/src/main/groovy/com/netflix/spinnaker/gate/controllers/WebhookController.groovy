/*
 * Copyright 2014 Netflix, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.netflix.spinnaker.gate.controllers

import com.netflix.spinnaker.gate.services.WebhookService
import io.swagger.v3.oas.annotations.Operation
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/webhooks")
class WebhookController {

  @Autowired
  WebhookService webhookService

  @Operation(summary = "Endpoint for posting webhooks to Spinnaker's webhook service")
  @RequestMapping(value = "/{type}/{source}", method = RequestMethod.POST)
  Map webhooks(@PathVariable("type") String type,
               @PathVariable("source") String source,
               @RequestBody(required = false) Map event,
               @RequestHeader(value = "X-Hub-Signature", required = false) String gitHubSignature,
               @RequestHeader(value = "X-Event-Key", required = false) String bitBucketEventType)
  {
    if (gitHubSignature || bitBucketEventType) {
      webhookService.webhooks(type, source, event, gitHubSignature, bitBucketEventType)
    } else {
      webhookService.webhooks(type, source, event)
    }
  }

  @Operation(summary = "Retrieve a list of preconfigured webhooks in Orca")
  @RequestMapping(value = "/preconfigured", method = RequestMethod.GET)
  List preconfiguredWebhooks() {
    return webhookService.preconfiguredWebhooks()
  }
}
