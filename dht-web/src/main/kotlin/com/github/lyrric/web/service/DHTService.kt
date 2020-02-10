package com.github.lyrric.web.service

import com.github.lyrric.web.es.entity.EsTorrent
import com.github.lyrric.web.model.PageResult
import com.github.lyrric.web.model.dto.SearchDTO
import javax.servlet.http.HttpServletRequest

interface DHTService {

    fun search(searchDTO: SearchDTO, httpRequest: HttpServletRequest): PageResult<EsTorrent>
    fun addHot(id: String, httpServletRequest: HttpServletRequest)
}