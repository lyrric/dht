package com.github.lyrric.web.es.repository

import com.github.lyrric.web.es.entity.EsTorrent
import org.springframework.data.repository.CrudRepository

interface EsTorrentRepository : CrudRepository<EsTorrent,String>{

}