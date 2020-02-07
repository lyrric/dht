package com.github.lyrric.web.model.dto

class SearchDTO{
    /**
     * 关键字
     */
    var key:String? = null

    /**
     * 页码
     */
    var pageNum:Int? = null

    /**
     * 页大小
     */
    var pageSize:Int? = null

    /**
     * 排序方式
     */
    var sort:String? = null
}