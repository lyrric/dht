package com.github.lyrric.web.es.service

import com.github.lyrric.web.constant.EsConstant
import com.github.lyrric.web.es.entity.EsTorrent
import com.github.lyrric.web.es.repository.EsTorrentRepository
import com.github.lyrric.web.model.PageResult
import com.github.lyrric.web.model.dto.SearchDTO
import org.elasticsearch.index.query.QueryBuilders
import org.elasticsearch.search.sort.SortBuilders
import org.elasticsearch.search.sort.SortMode
import org.springframework.data.domain.PageRequest
import org.springframework.data.elasticsearch.core.ElasticsearchOperations
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder
import org.springframework.stereotype.Component
import java.util.function.Consumer
import java.util.stream.Collectors
import javax.annotation.Resource

@Component
class EsService {

    @Resource
    private lateinit var elasticsearchRestTemplate: ElasticsearchRestTemplate
    @Resource
    private lateinit var elasticsearchOperations : ElasticsearchOperations
    @Resource
    private lateinit var esTorrentRepository: EsTorrentRepository


    fun search(searchDTO: SearchDTO):PageResult<EsTorrent>{
        val index = IndexCoordinates.of(EsConstant.TORRENT_INDEX)
        val query = NativeSearchQueryBuilder()
                    .withQuery(QueryBuilders
                            .boolQuery()
                            .must(QueryBuilders.matchQuery("fileName", searchDTO.key)))
                            .withPageable(PageRequest.of(searchDTO.pageNum!!,searchDTO.pageSize!!))
                            .build()
        val searchHits = elasticsearchRestTemplate.search(query, EsTorrent::class.java, index)
        val pageResult = PageResult<EsTorrent>()
        pageResult.total = searchHits.totalHits
        val max = (searchDTO.pageSize!! * 20).toLong()
        if(pageResult.total!! > max){
            pageResult.total = max
        }
        pageResult.data = searchHits.stream().map { t -> t.content }.collect(Collectors.toList())
        return pageResult
    }

    fun addHost(id: String){
        esTorrentRepository.findById(id).ifPresent(Consumer { torrent->run{
            torrent.hot = torrent.hot?.plus(1)
            esTorrentRepository.save(torrent)
        }})
    }
}