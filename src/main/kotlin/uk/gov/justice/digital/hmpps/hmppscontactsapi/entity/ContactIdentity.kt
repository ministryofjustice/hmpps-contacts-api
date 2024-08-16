package uk.gov.justice.digital.hmpps.hmppscontactsapi.entity

import jakarta.persistence.*
import java.time.Instant

@Entity
@Table(name = "contact_identity")
class ContactIdentity(
  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "contact_identity_id_gen")
  @SequenceGenerator(
    name = "contact_identity_id_gen",
    sequenceName = "contact_identity_contact_identity_id_seq",
    allocationSize = 1,
  )
  @Column(name = "contact_identity_id", nullable = false)
  var id: Long? = null,

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "contact_id", nullable = false)
  var contact: Contact,

  @Column(name = "identity_type", length = 20)
  var identityType: String? = null,

  @Column(name = "identity_value", length = 100)
  var identityValue: String? = null,

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
