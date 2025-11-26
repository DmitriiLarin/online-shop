package org.example.reviews.config

import feign.codec.ErrorDecoder
import org.example.reviews.config.CustomErrorDecoder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class FeignConfig {
    @Bean
    fun errorDecoder(): ErrorDecoder {
        return CustomErrorDecoder()
    }
}