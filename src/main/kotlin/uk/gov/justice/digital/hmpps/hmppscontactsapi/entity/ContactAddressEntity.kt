package uk.gov.justice.digital.hmpps.hmppscontactsapi.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import org.hibernate.annotations.CreationTimestamp
import java.time.LocalDateTime

@Entity
@Table(name = "contact_address")
data class ContactAddressEntity(
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  val contactAddressId: Long = 0,

  val contactId: Long,

  var addressType: String,

  var primaryAddress: Boolean,

  var flat: String?,

  var property: String?,

  var street: String?,

  var area: String?,

  var cityCode: String?,

  var countyCode: String?,

  var postCode: String?,

  var countryCode: String?,

  // Has this address been postcode-verified via a lookup?
  var verified: Boolean,

  var verifiedBy: String? = null,

  var verifiedTime: LocalDateTime? = null,

  val createdBy: String,

  @Column(updatable = false, name = "created_time")
  @CreationTimestamp
  val createdTime: LocalDateTime,

  @Column(updatable = false, name = "amended_by")
  var amendedBy: String? = null,

  @Column(updatable = false, name = "amended_time")
  var amendedTime: LocalDateTime? = null,
) {
  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (javaClass != other?.javaClass) return false

    other as ContactAddressEntity

    return contactAddressId == other.contactAddressId
  }

  override fun hashCode(): Int {
    return contactAddressId.hashCode()
  }
}
