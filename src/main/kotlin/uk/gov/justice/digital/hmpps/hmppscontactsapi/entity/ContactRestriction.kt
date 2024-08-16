package uk.gov.justice.digital.hmpps.hmppscontactsapi.entity

import jakarta.persistence.*
import java.time.Instant
import java.time.LocalDate

@Entity
@Table(name = "contact_restriction")
class ContactRestriction(
  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "contact_restriction_id_gen")
  @SequenceGenerator(
    name = "contact_restriction_id_gen",
    sequenceName = "contact_restriction_contact_restriction_id_seq",
    allocationSize = 1,
  )
  @Column(name = "contact_restriction_id", nullable = false)
  var id: Long? = null,

  @OneToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "contact_id", nullable = false)
  var contact: Contact,

  @Column(name = "restriction_type", nullable = false, length = 12)
  var restrictionType: String,

  @Column(name = "start_date")
  var startDate: LocalDate? = null,

  @Column(name = "expiry_date")
  var expiryDate: LocalDate? = null,

  @Column(name = "comments", length = 240)
  var comments: String? = null,

  @Column(name = "created_by", nullable = false, length = 100)
  var createdBy: String,

  @Column(name = "created_time", nullable = false)
  var createdTime: Instant = Instant.now(),

  @Column(name = "amended_by", length = 100)
  var amendedBy: String? = null,

  @Column(name = "amended_time")
  var amendedTime: Instant? = null,
)
