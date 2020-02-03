package com.github.lyrric.web.model

/**
 * 业务异常类
 * Created on 2019-08-21.
 * @author wangxiaodong
 */
data class BusinessException(var msg:String) : Exception(msg){

}