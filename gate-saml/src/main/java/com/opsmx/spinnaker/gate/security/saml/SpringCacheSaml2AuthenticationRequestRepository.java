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
 *
 */

package com.opsmx.spinnaker.gate.security.saml;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.security.saml2.core.Saml2ParameterNames;
import org.springframework.security.saml2.provider.service.authentication.AbstractSaml2AuthenticationRequest;
import org.springframework.security.saml2.provider.service.web.Saml2AuthenticationRequestRepository;
import org.springframework.session.data.redis.config.annotation.SpringSessionRedisConnectionFactory;
import org.springframework.stereotype.Component;

@Configuration
@Component
@Slf4j
public class SpringCacheSaml2AuthenticationRequestRepository
    implements Saml2AuthenticationRequestRepository<AbstractSaml2AuthenticationRequest> {
  private RedisTemplate<String, Object> redisTemplate = createRedisTemplate();
  private static final String STRING_KEY_PREFIX = "spring:saml2:SAML2_AUTHN_REQUEST:";

  private RedisConnectionFactory redisConnectionFactory;
  private RedisSerializer<Object> defaultRedisSerializer;
  private ClassLoader classLoader;

  @Override
  public AbstractSaml2AuthenticationRequest loadAuthenticationRequest(HttpServletRequest request) {
    log.debug("********* loadAuthenticationRequest ********************");
    String relayState = request.getParameter(Saml2ParameterNames.RELAY_STATE);
    getLogRelayState(relayState);
    return (AbstractSaml2AuthenticationRequest)
        this.redisTemplate.opsForValue().get(STRING_KEY_PREFIX + relayState);
  }

  @Override
  public void saveAuthenticationRequest(
      AbstractSaml2AuthenticationRequest authenticationRequest,
      HttpServletRequest request,
      HttpServletResponse response) {
    log.debug("********* saveAuthenticationRequest ********************");
    String relayState = authenticationRequest.getRelayState();
    getLogRelayState(relayState);
    this.redisTemplate.opsForValue().set(STRING_KEY_PREFIX + relayState, authenticationRequest);
  }

  @Override
  public AbstractSaml2AuthenticationRequest removeAuthenticationRequest(
      HttpServletRequest request, HttpServletResponse response) {
    log.debug("********* removeAuthenticationRequest ********************");
    String relayState = request.getParameter(Saml2ParameterNames.RELAY_STATE);
    getLogRelayState(relayState);
    AbstractSaml2AuthenticationRequest authenticationRequest = loadAuthenticationRequest(request);
    if (authenticationRequest == null) {
      return null;
    }
    this.redisTemplate.opsForValue().getAndDelete(STRING_KEY_PREFIX + relayState);
    return authenticationRequest;
  }

  protected RedisTemplate<String, Object> createRedisTemplate() {
    log.debug(
        "*********Start of the  createRedisTemplate - SpringCacheSaml2AuthenticationRequestRepository ********************");
    RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
    redisTemplate.setKeySerializer(new StringRedisSerializer());
    redisTemplate.setHashKeySerializer(new StringRedisSerializer());
    if (getDefaultRedisSerializer() != null) {
      redisTemplate.setDefaultSerializer(getDefaultRedisSerializer());
    }
    redisTemplate.setConnectionFactory(getRedisConnectionFactory());
    redisTemplate.setBeanClassLoader(this.classLoader);
    redisTemplate.afterPropertiesSet();
    log.debug(
        "*********End of the  createRedisTemplate - SpringCacheSaml2AuthenticationRequestRepository ********************");
    return redisTemplate;
  }

  @Autowired(required = false)
  @Qualifier("springSessionDefaultRedisSerializer")
  public void setDefaultRedisSerializer(RedisSerializer<Object> defaultRedisSerializer) {
    log.debug(
        "*********setDefaultRedisSerializer - SpringCacheSaml2AuthenticationRequestRepository ********************");
    this.defaultRedisSerializer = defaultRedisSerializer;
  }

  public RedisSerializer<Object> getDefaultRedisSerializer() {
    log.debug(
        "*********getDefaultRedisSerializer - SpringCacheSaml2AuthenticationRequestRepository ********************");
    return this.defaultRedisSerializer;
  }

  private void getLogRelayState(String relayState) {
    log.debug("********* relayState : {}", relayState);
  }

  @Autowired
  public void setRedisConnectionFactory(
      @SpringSessionRedisConnectionFactory
          ObjectProvider<RedisConnectionFactory> springSessionRedisConnectionFactory,
      ObjectProvider<RedisConnectionFactory> redisConnectionFactory) {
    log.debug(
        "*********setRedisConnectionFactory - SpringCacheSaml2AuthenticationRequestRepository ********************");
    this.redisConnectionFactory =
        springSessionRedisConnectionFactory.getIfAvailable(redisConnectionFactory::getObject);
  }

  public RedisConnectionFactory getRedisConnectionFactory() {
    log.debug(
        "*********getRedisConnectionFactory - SpringCacheSaml2AuthenticationRequestRepository ********************");
    return this.redisConnectionFactory;
  }
}
