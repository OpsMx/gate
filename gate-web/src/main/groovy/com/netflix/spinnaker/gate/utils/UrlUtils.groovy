package com.netflix.spinnaker.gate.utils

import org.springframework.stereotype.Component

import java.nio.charset.StandardCharsets

@Component
class UrlUtils {
  static String decode(String encodedValue) {
    return URLDecoder.decode(encodedValue, StandardCharsets.UTF_8.toString())
  }
}
