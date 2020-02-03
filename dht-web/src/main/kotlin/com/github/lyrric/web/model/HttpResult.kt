package com.github.lyrric.web.model

/**
 * Created on 2019-08-21.
 * @author wangxiaodong
 */

data class HttpResult private constructor(val success: Boolean, val code: Int,
                                          val errMsg: String, val data: Any?){


/*    *//**
    * 响应码。
    * 如果没有特别注明，code为0时表示请求访问成功
    *//*
    var code?: int;

    *//**
     * 错误信息
     *//*
    var errMsg?: String

    *//**
     * 是否成功
     *//*
    var success?: Boolean

    *//**
     * 响应数据
     *//*
    var data?: Any*/



    companion object{
        /**
         * 成功代码
         */
        private val SUCCESS_CODE = 0
        /**
         * 业务异常
         */
        private val BUSINESS_EXCEPTION_CODE = 1
        /**
         * 未知异常
         */
        private val UNKNOWN_EXCEPTION_CODE = 2
        /**
         * 成功
         */
        private val SUCCESS_STATUS = true
        /**
         * 失败
         */
        private val FAILURE_STATUS = false
        /**
         * 响应成功
         * @param success 是否成功
         * @param code 响应代码
         * @param msg 响应消息
         * @param data 响应数据
         * @return 响应对象
         */
        fun success(success: Boolean, code: Int, msg: String, data: Any?): HttpResult {
            return HttpResult(success, code, msg, data)
        }

        /**
         * 默认成功响应
         * @param data 数据
         * @return 响应
         */
        fun success(data: Any?): HttpResult {
            return success(SUCCESS_STATUS, SUCCESS_CODE, "请求成功", data)
        }

        /**
         * 响应请求失败
         * @param code 约定code
         * @param msg  信息
         * @return 失败的响应
         */
        fun failure(success: Boolean, code: Int, msg: String, data: Any?): HttpResult {
            return HttpResult(success, code, msg, data)
        }

        /**
         * 响应请求失败
         * @param errMsg 失败信息
         * @return 响应
         */
        fun failure(errMsg: String): HttpResult {
            return failure(FAILURE_STATUS, BUSINESS_EXCEPTION_CODE, errMsg, null)
        }

        /**
         * 出现预料之外的错误
         * @return 错误信息
         */
        fun error(): HttpResult {
            return failure(FAILURE_STATUS, UNKNOWN_EXCEPTION_CODE, "系统错误", null)
        }
    }

}