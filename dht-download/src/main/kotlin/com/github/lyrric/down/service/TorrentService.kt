package com.github.lyrric.down.service

import com.github.lyrric.common.entity.Torrent

interface TorrentService {

    fun torrentMessageIn(torrent: Torrent)
}