package com.github.lyrric.web.mapper

import com.github.lyrric.web.core.BaseMapper
import com.github.lyrric.web.entity.TorrentStat

interface TorrentStatMapper : BaseMapper<TorrentStat?> {

    fun stat()
}