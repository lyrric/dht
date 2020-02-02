package com.github.lyrric.down.mapper

import com.github.lyrric.down.core.BaseMapper
import com.github.lyrric.down.entity.Torrent
import org.apache.ibatis.annotations.Select

interface TorrentMapper : BaseMapper<Torrent?>{

    @Select("select count(1) from info_hash where info_hash")
    fun selectCount():Int
}