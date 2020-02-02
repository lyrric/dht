package com.github.lyrric.down.mapper

import com.github.lyrric.down.core.BaseMapper
import com.github.lyrric.down.entity.InfoHashList
import org.apache.ibatis.annotations.Select

interface InfoHashListMapper : BaseMapper<InfoHashList?>{


    @Select("select count(1) from info_hash_list where info_hash = #{infoHash}")
    fun selectCount(infoHash:String):Int
}