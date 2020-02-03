package com.github.lyrric.web.core

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import springfox.documentation.builders.ApiInfoBuilder
import springfox.documentation.builders.PathSelectors
import springfox.documentation.builders.RequestHandlerSelectors
import springfox.documentation.spi.DocumentationType
import springfox.documentation.spring.web.plugins.Docket
import springfox.documentation.swagger2.annotations.EnableSwagger2

/**
 * Created on 2019-08-21.
 * @author wangxiaodong
 */
@EnableSwagger2
@Configuration
class SwaggerConfig {

    @Bean
    fun initDocket():Docket{
        return Docket(DocumentationType.SWAGGER_2)
                .apiInfo(ApiInfoBuilder().title("swagger-ui-~~~~").build())
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.github.lyrric.web"))
                .paths(PathSelectors.any())
                .build()
    }
}