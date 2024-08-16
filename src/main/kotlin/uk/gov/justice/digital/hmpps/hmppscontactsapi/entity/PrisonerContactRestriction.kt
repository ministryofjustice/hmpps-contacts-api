package uk.gov.justice.digital.hmpps.hmppscontactsapi.entity

import jakarta.persistence.*
import java.time.Instant
import java.time.LocalDate

@Entity
@Table(name = "prisoner_contact_restriction")
class PrisonerContactRestriction(
  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "prisoner_contact_restriction_id_gen")
  @SequenceGenerator(
    name = "prisoner_contact_restriction_id_gen",
    sequenceName = "prisoner_contact_restriction_prisoner_contact_restriction_i_seq",
    allocationSize = 1,
  )
  @Column(name = "prisoner_contact_restriction_id", nullable = false)
  var id: Long? = null,

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "prisoner_contact_id", nullable = false)
  var prisonerContact: PrisonerContact,

  @Column(name = "restriction_type", nullable = false, length = 12)
  var restrictionType: String,

  @Column(name = "start_date")
  var startDate: LocalDate? = null,

  @Column(name = "expiry_date")
  var expiryDate: LocalDate? = null,

  @Column(name = "comments", length = 255)
  var comments: String? = null,

  @Column(name = "authorised_by", length = 100)
  var authorisedBy: String? = null,

  @Column(name = "authorised_time")
  var authorisedTime: Instant? = null,

  @Column(name = "created_by", nullable = false, length = 100)
  var createdBy: String,

  @Column(name = "created_time", nullable = false)
  var createdTime: Instant = Instant.now(),

  @Column(name = "amended_by", length = 100)
  var amendedBy: String? = null,

  @Column(name = "amended_time")
  var amendedTime: Instant? = null,
)
