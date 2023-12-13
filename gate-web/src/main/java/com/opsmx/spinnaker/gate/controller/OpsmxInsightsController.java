/*
 * Copyright 2023 OpsMx, Inc.
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

import io.swagger.annotations.ApiOperation;
import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

// @RequestMapping("/insights")
// @RestController
@Slf4j
public class OpsmxInsightsController {

  String username = "admin";
  String password = "opsmxadmin123";
  public static final MediaType JSON = MediaType.get("application/json");

  @ApiOperation(value = "get session timeout")
  @GetMapping(value = "/get")
  ResponseEntity<Void> getInsights(@RequestParam("type") String type) {
    OkHttpClient client = new OkHttpClient();
    String json =
        "{\n" + "    \"user\": \"admin\",\n" + "    \"password\": \"opsmxadmin123\"\n" + "}";
    log.info("json is: " + json);
    RequestBody body = RequestBody.create(JSON, json);
    String url = "https://new-grafana.isd-dev.opsmx.net/login";
    Request request = new Request.Builder().url(url).post(body).build();

    Map<String, String> hdrs = new HashMap<>();

    try (Response response = client.newCall(request).execute()) {
      populateHeaders(response, hdrs);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }

    String redirectUrl = null;
    if (type.equalsIgnoreCase("pipeline-insights")) {
      redirectUrl =
          "https://new-grafana.isd-dev.opsmx.net/d/a3de564f-60de-46ec-89b2-a46f169a3823/pipeinsight?orgId=1&refresh=5s&from=1702107951546&to=1702280751546";
    }

    if (type.equalsIgnoreCase("stage-insights")) {
      redirectUrl =
          "https://new-grafana.isd-dev.opsmx.net/d/cdc08946-b140-4ee1-a769-7705ed/stageinsight?orgId=1&refresh=5s&from=1702107989203&to=1702280789203";
    }
    log.info("The map is: " + hdrs);
    String cookie =
        "grafana_session="
            + hdrs.get("grafana_session")
            + ";"
            + "grafana_session_expiry="
            + hdrs.get("grafana_session_expiry");
    cookie = cookie + "; domain=opsmx.net; SameSite=None; secure";
    log.info("The Cookie is: " + cookie);
    HttpHeaders headers = new HttpHeaders();
    headers.setLocation(URI.create(redirectUrl));
    headers.add("Set-Cookie", cookie);
    return ResponseEntity.status(302).headers(headers).build();
  }

  private void populateHeaders(Response response, Map<String, String> hdrs) {
    List<String> cookies = response.headers("Set-Cookie");
    log.info("cookies are: " + cookies);
    for (String str : cookies) {
      log.info("str is: " + str);
      String[] semicolonSeparated = str.split(";");
      for (String scSplitStr : semicolonSeparated) {
        log.info("scSplitStr is: " + scSplitStr);
        String[] equalsSeparated = scSplitStr.split("=");
        if (equalsSeparated.length == 2) {
          hdrs.put(equalsSeparated[0].trim(), equalsSeparated[1].trim());
        }
      }
    }
  }
}
