package uk.gov.justice.digital.hmpps.hmppscontactsapi.entity

import jakarta.persistence.*
import java.time.Instant

@Entity
@Table(name = "contact_phone")
class ContactPhone(
  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "contact_phone_id_gen")
  @SequenceGenerator(
    name = "contact_phone_id_gen",
    sequenceName = "contact_phone_contact_phone_id_seq",
    allocationSize = 1,
  )
  @Column(name = "contact_phone_id", nullable = false)
  var id: Long? = null,

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "contact_id", nullable = false)
  var contact: Contact,

  @Column(name = "phone_type", nullable = false, length = 20)
  var phoneType: String,

  @Column(name = "phone_number", nullable = false, length = 240)
  var phoneNumber: String,

  @Column(name = "ext_number", length = 10)
  var extNumber: String? = null,

  @Column(name = "primary_phone", nullable = false)
  var primaryPhone: Boolean = false,

  @Column(name = "created_by", nullable = false, length = 100)
  var createdBy: String,

  @Column(name = "created_time", nullable = false)
  var createdTime: Instant = Instant.now(),

  @Column(name = "amended_by", length = 100)
  var amendedBy: String? = null,

  @Column(name = "amended_time")
  var amendedTime: Instant? = null,
)
