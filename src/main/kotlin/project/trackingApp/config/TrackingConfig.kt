package project.trackingApp.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import project.trackingApp.mapper.dhl.DHLMapper
import project.trackingApp.mapper.hellmann.HellmannMapper

@Configuration
class TrackingConfig {

    @Bean
    fun dhlMapper(): DHLMapper {
        return DHLMapper()
    }

    @Bean
    fun hellmannMapper(): HellmannMapper {
        return HellmannMapper()
    }
}

