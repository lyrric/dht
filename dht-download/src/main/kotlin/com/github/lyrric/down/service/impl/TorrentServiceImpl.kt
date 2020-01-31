package com.github.lyrric.down.service.impl

import com.github.lyrric.common.entity.Torrent
import com.github.lyrric.down.service.TorrentService
import org.springframework.cloud.stream.annotation.StreamListener
import org.springframework.stereotype.Service

@Service
class TorrentServiceImpl : TorrentService{

    @StreamListener("torrent-message-out")
    override fun torrentMessageIn(torrent: Torrent) {

    }
}