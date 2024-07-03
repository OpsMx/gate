package com.netflix.spinnaker.gate.utils

import groovy.util.logging.Slf4j
import org.springframework.stereotype.Component

import java.nio.charset.StandardCharsets

@Component
@Slf4j
class UrlUtils {
  static String decode(String encodedValue) {
    log.info("encoded: " + encodedValue)
    final decoded = URLDecoder.decode(encodedValue, StandardCharsets.UTF_8.toString())
    log.info("decoded: " + decoded)
    return decoded
  }
}
