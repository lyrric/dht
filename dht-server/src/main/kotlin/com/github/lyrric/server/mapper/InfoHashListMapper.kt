package com.github.lyrric.server.mapper

import com.github.lyrric.server.core.BaseMapper
import com.github.lyrric.server.entity.InfoHashList
import org.apache.ibatis.annotations.Select

interface InfoHashListMapper : BaseMapper<InfoHashList?> {


    @Select("select count(1) from info_hash_list where info_hash = #{infoHash}")
    fun selectCountByHash(infoHash:String):Int
}