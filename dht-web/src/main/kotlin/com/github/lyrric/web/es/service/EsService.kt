package com.github.lyrric.web.es.service

import com.github.lyrric.web.es.entity.EsTorrent
import com.github.lyrric.web.es.repository.EsTorrentRepository
import org.elasticsearch.client.RestHighLevelClient
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate
import org.springframework.stereotype.Service
import javax.annotation.PostConstruct
import javax.annotation.Resource

@Service
class EsService {

    @Resource
    private lateinit var elasticsearchRestTemplate: ElasticsearchRestTemplate
    @Resource
    private lateinit var esTorrentRepository: EsTorrentRepository

    @PostConstruct
    fun test(){
    }
}