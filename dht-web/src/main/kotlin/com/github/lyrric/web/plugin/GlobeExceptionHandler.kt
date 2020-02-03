package com.github.lyrric.web.plugin


import com.github.lyrric.web.model.BusinessException
import com.github.lyrric.web.model.HttpResult
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseBody


/**
 * 统一异常处理
 * Created on 2019-08-21.
 * @author wangxiaodong
 */
@ControllerAdvice
@ResponseBody
class GlobeExceptionHandler{

    private val logger: Logger = LoggerFactory.getLogger(GlobeExceptionHandler::class.java)

    /**
     * 处理异常
     */
    @ExceptionHandler(value = [Exception::class])
    fun allException(e:Exception): HttpResult {
        if(e is BusinessException){
            return HttpResult.failure(e.msg)
        }
        e.printStackTrace()
        logger.error("系统发生了未知异常:"+e.message)
        return HttpResult.error()
    }
}