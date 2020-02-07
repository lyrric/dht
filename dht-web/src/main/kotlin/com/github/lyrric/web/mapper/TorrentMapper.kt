package com.github.lyrric.web.mapper

import com.github.lyrric.web.core.BaseMapper
import com.github.lyrric.web.entity.Torrent
import org.apache.ibatis.annotations.Param

interface TorrentMapper : BaseMapper<Torrent?>{

    /**
     * 获取前一天的数据
     */
    fun selectLastDayData(@Param("offset")offset:Long, @Param("pageSize")pageSize: Int):List<Torrent>
}