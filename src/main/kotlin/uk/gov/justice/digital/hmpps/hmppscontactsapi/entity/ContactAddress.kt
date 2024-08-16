package uk.gov.justice.digital.hmpps.hmppscontactsapi.entity

import jakarta.persistence.*
import java.time.Instant

@Entity
@Table(name = "contact_address")
class ContactAddress(
  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "contact_address_id_gen")
  @SequenceGenerator(
    name = "contact_address_id_gen",
    sequenceName = "contact_address_contact_address_id_seq",
    allocationSize = 1,
  )
  @Column(name = "contact_address_id", nullable = false)
  var id: Long? = null,

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "contact_id", nullable = false)
  var contact: Contact,

  @Column(name = "address_type", nullable = false, length = 12)
  var addressType: String,

  @Column(name = "primary_address", nullable = false)
  var primaryAddress: Boolean = false,

  @Column(name = "flat", length = 20)
  var flat: String? = null,

  @Column(name = "property", nullable = false, length = 50)
  var property: String,

  @Column(name = "street", nullable = false, length = 160)
  var street: String,

  @Column(name = "area", length = 70)
  var area: String? = null,

  @Column(name = "city_code", nullable = false, length = 12)
  var cityCode: String,

  @Column(name = "county_code", nullable = false, length = 12)
  var countyCode: String,

  @Column(name = "post_code", length = 12)
  var postCode: String? = null,

  @Column(name = "country_code", nullable = false, length = 12)
  var countryCode: String,

  @Column(name = "verified", nullable = false)
  var verified: Boolean = false,

  @Column(name = "verified_by", length = 100)
  var verifiedBy: String? = null,

  @Column(name = "verified_time")
  var verifiedTime: Instant? = null,

  @Column(name = "created_by", nullable = false, length = 100)
  var createdBy: String,

  @Column(name = "created_time", nullable = false)
  var createdTime: Instant = Instant.now(),

  @Column(name = "amended_by", length = 100)
  var amendedBy: String? = null,

  @Column(name = "amended_time")
  var amendedTime: Instant? = null,
)
