package project.trackingApp.repository

import org.springframework.data.jpa.repository.JpaRepository
import project.trackingApp.model.Tracking

interface TrackingRepository : JpaRepository<Tracking, Long> {

    fun findByHouseAwb(houseAwb: String): List<Tracking>
    fun findByCarrier(carrier: String): List<Tracking>
    fun findByStatus(status: String): List<Tracking>
    fun findByReceiverContainingIgnoreCase(receiver: String): List<Tracking>
}
