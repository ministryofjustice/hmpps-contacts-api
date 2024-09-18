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
  val addressType: String,

  @Column(name = "primary_address")
  val primaryAddress: Boolean,

  @Column(name = "flat")
  val flat: String?,

  @Column(name = "property")
  val property: String?,

  @Column(name = "street")
  val street: String?,

  @Column(name = "area")
  val area: String?,

  @Column(name = "city_code")
  val cityCode: String?,

  @Column(name = "county_code")
  val countyCode: String?,

  @Column(name = "post_code")
  val postCode: String?,

  @Column(name = "country_code")
  val countryCode: String?,

  // Has this address been postcode-verified via a lookup?
  @Column(name = "verified")
  val verified: Boolean,

  @Column(name = "verifiedBy")
  val verifiedBy: String? = null,

  @Column(name = "verified_time")
  val verifiedTime: LocalDateTime? = null,

  @Column(updatable = false, name = "created_by")
  val createdBy: String,

  @Column(updatable = false, name = "created_time")
  @CreationTimestamp
  val createdTime: LocalDateTime,

  @Column(updatable = false, name = "amended_by")
  val amendedBy: String? = null,

  @Column(updatable = false, name = "amended_time")
  val amendedTime: LocalDateTime? = null,
)
