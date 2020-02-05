package com.github.lyrric.web.core

import org.elasticsearch.client.RestClientBuilder
import org.elasticsearch.client.RestHighLevelClient
import org.elasticsearch.client.transport.TransportClient
import org.elasticsearch.common.settings.Settings
import org.elasticsearch.common.transport.TransportAddress
import org.elasticsearch.xpack.client.PreBuiltXPackTransportClient
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.elasticsearch.client.ClientConfiguration
import org.springframework.data.elasticsearch.client.RestClients
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate
import springfox.documentation.builders.ApiInfoBuilder
import springfox.documentation.builders.PathSelectors
import springfox.documentation.builders.RequestHandlerSelectors
import springfox.documentation.spi.DocumentationType
import springfox.documentation.spring.web.plugins.Docket
import springfox.documentation.swagger2.annotations.EnableSwagger2
import java.net.InetAddress


/**
 * Created on 2019-08-21.
 * @author wangxiaodong
 */
@EnableSwagger2
@Configuration
class WebConfig {

    @Value("\${elastic.username}")
    private var username:String? = null
    @Value("\${elastic.password}")
    private var password:String? = null
    @Value("\${elastic.host}")
    private var host:String? = null
    @Value("\${elastic.port:9200}")
    private var port:Int = 9200

    @Bean
    fun initDocket():Docket{
        return Docket(DocumentationType.SWAGGER_2)
                .apiInfo(ApiInfoBuilder().title("swagger-ui-~~~~").build())
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.github.lyrric.web"))
                .paths(PathSelectors.any())
                .build()
    }

    @Bean
    fun restHighLevelClient(): RestHighLevelClient? {
        val clientConfiguration: ClientConfiguration = ClientConfiguration.builder()
                .connectedTo("${host}:${port}")
                .withBasicAuth(username, password)
                .build();

        return RestClients.create(clientConfiguration).rest();


    }

    @Bean(name = ["elasticsearchTemplate"])
    @Autowired
    fun elasticsearchRestTemplate(restHighLevelClient: RestHighLevelClient):ElasticsearchRestTemplate{
        return ElasticsearchRestTemplate(restHighLevelClient)
    }
}