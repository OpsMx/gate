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

package com.opsmx.spinnaker.gate.model;

import java.util.List;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "keycloak.auth")
@ConditionalOnExpression("${feature.auth-provider.flag:false}")
public class KeycloakAuthProvider {

  private List<Provider> providers;

  @Data
  @EqualsAndHashCode(callSuper = false)
  public static class BaseUIElement {

    private String name;
    private String display;
    private String helpText;
    private String type;
  }

  @Data
  @EqualsAndHashCode(callSuper = false)
  public static class Provider extends BaseUIElement {

    private List<Section> sections;
  }

  @Data
  @EqualsAndHashCode(callSuper = false)
  public static class Section extends BaseUIElement {

    private List<InputParameter> inputparameters;
    private List<NestedSection> nestedSections;
  }

  @Data
  @EqualsAndHashCode(callSuper = false)
  public static class NestedSection extends BaseUIElement {
    private List<InputParameter> inputparameters;
  }

  @Data
  @EqualsAndHashCode(callSuper = false)
  public static class InputParameter {
    private String name;
    private String display;
    private String inputType;
    private Boolean required;
    private String helpText;
    private Boolean readonly;
    private String placeholder;
    private String defaultValue;
    private List<String> loadValues;
  }
}
