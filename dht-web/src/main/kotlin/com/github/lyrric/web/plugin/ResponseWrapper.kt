package com.github.lyrric.web.plugin

import com.fasterxml.jackson.databind.ObjectMapper
import com.github.lyrric.web.model.HttpResult
import org.springframework.core.MethodParameter
import org.springframework.http.MediaType
import org.springframework.http.converter.HttpMessageConverter
import org.springframework.http.server.ServerHttpRequest
import org.springframework.http.server.ServerHttpResponse
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice

/**
 * Created on 2019-08-21.
 * @author wangxiaodong
 */
@ControllerAdvice(annotations = [Controller::class, RestController::class])
class ResponseWrapper : ResponseBodyAdvice<Any>{

    val objectMapper: ObjectMapper = ObjectMapper()

    override fun beforeBodyWrite(o: Any?, methodParameter: MethodParameter, mediaType: MediaType,
                                 aClass: Class<out HttpMessageConverter<*>>, serverHttpRequest: ServerHttpRequest,
                                 serverHttpResponse: ServerHttpResponse): Any? {
        val path = serverHttpRequest.uri.path
        if("/swagger-resources" == path || "/v2/api-docs" == path)
            return o
        if(o is  String){
            return objectMapper.writeValueAsString(HttpResult.success(o))
        }
        return HttpResult.success(o)
    }

    override fun supports(p0: MethodParameter, p1: Class<out HttpMessageConverter<*>>): Boolean {
        return true
    }
}