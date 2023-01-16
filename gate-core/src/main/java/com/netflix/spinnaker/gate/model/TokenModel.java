package com.netflix.spinnaker.gate.model;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class TokenModel {
  boolean isValidToken;
  String userName;
}
