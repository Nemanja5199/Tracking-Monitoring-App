package project.trackingApp.model
import com.github.michaelbull.result.Result
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import project.trackingApp.error.TrackingError
import java.time.LocalDateTime

@Entity
@Table(name = "tracking")
data class Tracking(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(nullable = true)
    val status: String?,

    @Column(nullable = true)
    val poNumber: String?,

    @Column(nullable = true)
    val etd: LocalDateTime?,

    @Column(nullable = true)
    val eta: LocalDateTime?,

    @Column(nullable = true)
    val atd: LocalDateTime?,

    @Column(nullable = true)
    val ata: LocalDateTime?,

    @Column(nullable = true)
    val packages: Int?,

    @Column(nullable = true)
    val weight: Double?,

    @Column(nullable = true)
    val volume: Double?,

    @Column(nullable = true)
    val shipper: String?,

    @Column(name = "shipper_country", nullable = true)
    val shipperCountry: String?,

    @Column(nullable = true)
    val receiver: String?,

    @Column(name = "receiver_country", nullable = true)
    val receiverCountry: String?,

    @Column(name = "house_awb", nullable = true)
    val houseAwb: String?,

    @Column(name = "shipper_ref_no", nullable = true)
    val shipperRefNo: String?,

    @Column(nullable = true)
    val carrier: String?,

    @Column(name = "inco_term", nullable = true)
    val incoTerm: String?,

    @Column(name = "flight_no", nullable = true)
    val flightNo: String?,

    @Column(name = "pick_up_date", nullable = true)
    val pickUpDate: LocalDateTime?,

    @Column(name = "latest_checkpoint", nullable = true)
    val latestCheckpoint: String?
)
