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

package com.opsmx.spinnaker.gate.exception

import com.google.gson.Gson
import com.netflix.spinnaker.gate.controllers.OpsmxAutopilotController
import com.netflix.spinnaker.gate.controllers.OpsmxDashboardController
import com.netflix.spinnaker.gate.controllers.OpsmxOesController
import com.netflix.spinnaker.gate.controllers.OpsmxPlatformController
import com.netflix.spinnaker.gate.controllers.OpsmxVisibilityController
import com.netflix.spinnaker.gate.controllers.PipelineController
import com.netflix.spinnaker.gate.exceptions.OesRequestException
import com.opsmx.spinnaker.gate.controllers.OpsmxAuditClientServiceController
import com.opsmx.spinnaker.gate.controllers.OpsmxAuditServiceController
import com.opsmx.spinnaker.gate.controllers.OpsmxSaporPolicyController
import groovy.util.logging.Slf4j
import org.apache.commons.io.IOUtils
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseBody
import retrofit.RetrofitError

@Slf4j
@ControllerAdvice(basePackageClasses = [OpsmxSaporPolicyController.class, OpsmxAutopilotController.class,
OpsmxAuditClientServiceController.class, OpsmxDashboardController.class, OpsmxPlatformController.class,
OpsmxOesController.class, OpsmxVisibilityController.class, OpsmxAuditServiceController.class])
class RetrofitErrorHandler {

  static final Gson gson = new Gson()

  @ExceptionHandler([RetrofitError])
  @ResponseBody ResponseEntity<Object> handleRetrofitError(RetrofitError retrofitError){
    if (retrofitError!=null){
      log.warn("Exception occurred in OES downstream services : {}", retrofitError.getBody())
      if (retrofitError.getKind() == RetrofitError.Kind.NETWORK){
        log.warn("Retrofit Exception occurred of kind NETwork : {}", retrofitError.getBody())
        ErrorResponseModel networkErrorResponse = populateNetworkErrorResponse(retrofitError)
        return new ResponseEntity<Object>(networkErrorResponse, HttpStatus.INTERNAL_SERVER_ERROR)
      }
      if (retrofitError.getResponse()!=null && retrofitError.getResponse().getStatus() > 0){
        log.warn("Exception occurred in  : {}", retrofitError.getBody())
        if (retrofitError.getResponse().getBody() !=null){
          InputStream inputStream = null
          try {
            inputStream = retrofitError.getResponse().getBody().in()
            String errorResponse = new String(IOUtils.toByteArray(inputStream))
            return new ResponseEntity<Object>(gson.fromJson(errorResponse, Map.class), HttpStatus.valueOf(retrofitError.getResponse().getStatus()))
          } finally {
            if (inputStream!=null){
              inputStream.close()
            }
          }
        }
        return new ResponseEntity<Object>(retrofitError.getBody(), HttpStatus.valueOf(retrofitError.getResponse().getStatus()))
      }
      return new ResponseEntity<Object>(retrofitError.getBody(), HttpStatus.INTERNAL_SERVER_ERROR)
    }
    ErrorResponseModel defaultErrorResponse = populateDefaultErrorResponseModel()
    return new ResponseEntity<Object>(defaultErrorResponse, HttpStatus.INTERNAL_SERVER_ERROR)
  }

  @ExceptionHandler(PipelineController.PipelineException)
  @ResponseBody
  ResponseEntity<Map<String, Object>> handlePipelineException(PipelineController.PipelineException ex) {
    Map<String, Object> response = new HashMap<>();
    response.put("error", "Pipeline Save Error");
    response.put("message", ex.getMessage());
    response.put("status", HttpStatus.BAD_REQUEST.value());
    response.put("timestamp", System.currentTimeMillis());

    if (ex.additionalAttributes != null && !ex.additionalAttributes.isEmpty()) {
      response.put("additionalAttributes", ex.additionalAttributes);
    }

    log.error("PipelineException occurred: {}", ex.getMessage(), ex);
    return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(OesRequestException)
  @ResponseBody
  ResponseEntity<Map<String, Object>> handleOesRequestException(OesRequestException ex) {
    Map<String, Object> response = new HashMap<>()  // Groovy map initialization
    response.error = "OES Request Error"
    response.message = ex.message
    response.status = HttpStatus.BAD_REQUEST.value()
    response.timestamp = System.currentTimeMillis()

    if (ex.additionalAttributes != null && !ex.additionalAttributes.isEmpty()) {
      response.put("additionalAttributes", ex.additionalAttributes);
    }

    log.error("PipelineException occurred: {}", ex.getMessage(), ex);
    return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
  }



  private ErrorResponseModel populateDefaultErrorResponseModel() {
    ErrorResponseModel defaultErrorResponse = new ErrorResponseModel()
    defaultErrorResponse.setErrorType("Unknown Error")
    defaultErrorResponse.setErrorMsg("Something went wrong")
    defaultErrorResponse.setTimeStampMillis(System.currentTimeMillis())
    return defaultErrorResponse
  }


  private ErrorResponseModel populateNetworkErrorResponse(RetrofitError retrofitError) {
    ErrorResponseModel errorResponseModel = new ErrorResponseModel()
    errorResponseModel.setErrorType("Network Error")
    errorResponseModel.setErrorMsg(retrofitError.getMessage())
    errorResponseModel.setTimeStampMillis(System.currentTimeMillis())
    errorResponseModel.setUrl(retrofitError.getUrl())
    return errorResponseModel
  }



  /*@ExceptionHandler([RetrofitError, PipelineController.PipelineException, OesRequestException])
  @ResponseBody
  ResponseEntity<Map<String, Object>> handleMultipleExceptions(Exception ex) {
    Map<String, Object> response = [:] // Groovy map initialization
    HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR // Default status

    if (ex instanceof RetrofitError) {
      def retrofitError = (RetrofitError) ex
      if (retrofitError.kind == RetrofitError.Kind.NETWORK) {
        response.errorType = "Network Error"
        response.message = retrofitError.message
        status = HttpStatus.INTERNAL_SERVER_ERROR
      } else if (retrofitError.response && retrofitError.response.status > 0) {
        status = HttpStatus.valueOf(retrofitError.response.status)
        try {
          InputStream inputStream = retrofitError.response.body.in()
          String errorResponse = new String(IOUtils.toByteArray(inputStream))
          response.putAll(gson.fromJson(errorResponse, Map))
        } catch (Exception ignored) {
          // Unable to parse the response body
          response.message = "Failed to read response body"
        }
      } else {
        // Populate default error response
        response.putAll(populateDefaultErrorResponseModel())
      }
    } else if (ex instanceof PipelineController.PipelineException || ex instanceof OesRequestException) {
      // Handle PipelineException
      def pipelineException = (PipelineController.PipelineException) ex
      response.errorType = "Pipeline Error"
      response.message = pipelineException.message
      response.putAll(pipelineException.additionalAttributes)
      status = HttpStatus.BAD_REQUEST
    }

    // Add common response attributes
    response.timestamp = System.currentTimeMillis()
    response.status = status.value()

    return new ResponseEntity<>(response, status)
  }*/

}
