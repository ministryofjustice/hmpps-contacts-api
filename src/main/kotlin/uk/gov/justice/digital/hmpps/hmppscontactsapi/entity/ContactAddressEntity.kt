package uk.gov.justice.digital.hmpps.hmppscontactsapi.entity

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import org.hibernate.annotations.CreationTimestamp
import java.time.LocalDateTime
import java.time.LocalDateTime.now

@Entity
@Table(name = "contact_address")
data class ContactAddressEntity(
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  val contactAddressId: Long = 0,

  val contactId: Long? = null,

  var addressType: String,

  var primaryAddress: Boolean = false,

  var flat: String? = null,

  var property: String? = null,

  var street: String? = null,

  var area: String? = null,

  var cityCode: String? = null,

  var countyCode: String? = null,

  var postCode: String? = null,

  var countryCode: String? = null,

  var verified: Boolean = false,

  var verifiedBy: String? = null,

  var verifiedTime: LocalDateTime? = null,

  var createdBy: String,

  @CreationTimestamp
  val createdTime: LocalDateTime = now(),
) {
  var amendedBy: String? = null
  var amendedTime: LocalDateTime? = null

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
