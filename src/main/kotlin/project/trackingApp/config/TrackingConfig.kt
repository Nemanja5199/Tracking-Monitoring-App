package project.trackingApp.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import project.trackingApp.csvParser.TrackingCSVParser
import project.trackingApp.mapper.dhl.DHLMapper

@Configuration
class TrackingConfig {
    @Bean
    fun csvParser(): TrackingCSVParser {
        return TrackingCSVParser()
    }

    @Bean
    fun dhlMapper(): DHLMapper {
        return DHLMapper()
    }
}
