/*
 * Copyright 2021 OpsMx, Inc.
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

package com.opsmx.spinnaker.gate.cache;

import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Response;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class DatasourceCachingImpl implements DatasourceCaching {

  @Override
  public Response cacheResponse(String cacheKey, Response response) {
    log.debug("caching the retrofit response");
    return response;
  }

  @Override
  public Response getResponse(String cacheKey) {
    log.debug("returning response");
    return null;
  }

  @Override
  public Map<String, Object> populateDatasourceCache(
      String datasourceKey, Map<String, Object> datasource) {
    log.debug("Populating datasource cache with key : {}", datasourceKey);
    return datasource;
  }
}