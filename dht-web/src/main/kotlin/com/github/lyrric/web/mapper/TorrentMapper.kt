package com.github.lyrric.web.mapper

import com.github.lyrric.web.core.BaseMapper
import com.github.lyrric.web.entity.Torrent

interface TorrentMapper : BaseMapper<Torrent?>{

    /**
     * 获取前一天的数据
     */
    fun selectLastDayData():List<Torrent>
}