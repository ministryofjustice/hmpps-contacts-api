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

  @Column(name = "contact_id")
  val contactId: Long,

  @Column(name = "address_type")
  var addressType: String,

  @Column(name = "primary_address")
  var primaryAddress: Boolean,

  @Column(name = "flat")
  var flat: String?,

  @Column(name = "property")
  var property: String?,

  @Column(name = "street")
  var street: String?,

  @Column(name = "area")
  var area: String?,

  @Column(name = "city_code")
  var cityCode: String?,

  @Column(name = "county_code")
  var countyCode: String?,

  @Column(name = "post_code")
  var postCode: String?,

  @Column(name = "country_code")
  var countryCode: String?,

  // Has this address been postcode-verified via a lookup?
  @Column(name = "verified")
  var verified: Boolean,

  @Column(name = "verifiedBy")
  var verifiedBy: String? = null,

  @Column(name = "verified_time")
  var verifiedTime: LocalDateTime? = null,

  @Column(updatable = false, name = "created_by")
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
