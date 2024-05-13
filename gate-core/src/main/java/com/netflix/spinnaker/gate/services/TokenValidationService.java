package com.netflix.spinnaker.gate.services;

import com.netflix.spinnaker.gate.model.TokenModel;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "Autopilot", url = "http://oes-autopilot:8090")
public interface TokenValidationService {

  @GetMapping(
      value = "/autopilot/api/v5/validate/token",
      produces = MediaType.APPLICATION_JSON_VALUE)
  ResponseEntity<TokenModel> validateTokenByCanaryRunId(
      @RequestParam(value = "opsmx_id") Integer opsmxId,
      @RequestParam(value = "token") String token);
}
