/*
 * Copyright 2024 Netflix, Inc.
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

package com.opsmx.spinnaker.gate.services;

import com.netflix.spinnaker.gate.services.internal.ClouddriverService;
import com.netflix.spinnaker.gate.services.internal.ClouddriverServiceSelector;
import com.netflix.spinnaker.kork.dynamicconfig.DynamicConfigService;
import com.netflix.spinnaker.kork.web.context.RequestContextProvider;
import com.netflix.spinnaker.kork.web.selector.SelectableService;

public class NoOpClouddriverServiceSelector extends ClouddriverServiceSelector {

  public NoOpClouddriverServiceSelector(
      SelectableService<ClouddriverService> selectableService,
      DynamicConfigService dynamicConfigService,
      RequestContextProvider contextProvider) {
    super(selectableService, dynamicConfigService, contextProvider);
  }

  @Override
  public ClouddriverService select() {
    return super.select();
  }

  @Override
  public int hashCode() {
    return super.hashCode();
  }

  @Override
  public boolean equals(Object obj) {
    return super.equals(obj);
  }

  @Override
  protected Object clone() throws CloneNotSupportedException {
    return super.clone();
  }

  @Override
  public String toString() {
    return super.toString();
  }

  @Override
  protected void finalize() throws Throwable {
    super.finalize();
  }
}
