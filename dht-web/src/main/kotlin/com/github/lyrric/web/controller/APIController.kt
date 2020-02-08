package com.github.lyrric.web.controller

import com.github.lyrric.web.es.entity.EsTorrent
import com.github.lyrric.web.model.BusinessException
import com.github.lyrric.web.model.PageResult
import com.github.lyrric.web.model.dto.SearchDTO
import com.github.lyrric.web.service.DHTService
import com.github.pagehelper.PageInfo
import org.apache.commons.lang3.StringUtils
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import javax.annotation.Resource
import javax.servlet.http.HttpServletRequest


@RestController
@RequestMapping(value = ["/api"])
class APIController{

    @Resource
    private lateinit var dhtService: DHTService
    /**
     * 搜索接口
     */
    @PostMapping(value = ["/search"])
    fun search(@RequestBody searchDTO: SearchDTO, httpRequest: HttpServletRequest): PageResult<EsTorrent> {
        if(searchDTO.key == null || searchDTO.key!!.length < 2){
            throw BusinessException("搜索关键字不能小于2个字符")
        }
        if(searchDTO.pageNum == null || searchDTO.pageNum!! !in 1..10){
            searchDTO.pageNum = 1
        }
        searchDTO.pageSize = 20
        return dhtService.search(searchDTO, httpRequest)
    }


}