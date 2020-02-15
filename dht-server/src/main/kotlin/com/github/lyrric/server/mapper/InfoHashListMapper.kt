package com.github.lyrric.server.mapper

import org.apache.ibatis.annotations.Insert
import org.apache.ibatis.annotations.Mapper
import org.apache.ibatis.annotations.Select

@Mapper
interface InfoHashListMapper{


    @Select("select count(1) from info_hash_list where info_hash = #{hashInfo}")
    fun selectCountByHash(hashInfo:String):Int

    @Insert("insert into info_hash_list(info_hash) values(#{hashInfo})")
    fun insert(hashInfo:String)

}