package project.trackingApp.repository

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import project.trackingApp.model.Tracking
import java.util.UUID

interface TrackingRepository : JpaRepository<Tracking, Long> {

    fun findFirstByHouseAwbAndShipperRefNo(houseAwb: String, shipperRefNo: String): Tracking

    fun findByid(id: UUID): Tracking

    override fun findAll(pageable: Pageable): Page<Tracking>
}
