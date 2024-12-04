package project.trackingApp

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class TrackingAppApplication

fun main(args: Array<String>) {
	runApplication<TrackingAppApplication>(*args)
}
