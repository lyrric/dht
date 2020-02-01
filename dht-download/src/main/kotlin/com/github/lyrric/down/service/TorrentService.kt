package com.github.lyrric.down.service

import com.github.lyrric.common.entity.TorrentInfo

interface TorrentService {

    fun torrentMessageIn(torrentInfo: TorrentInfo)
}