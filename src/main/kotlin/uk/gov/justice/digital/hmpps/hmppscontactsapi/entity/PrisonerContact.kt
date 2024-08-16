package uk.gov.justice.digital.hmpps.hmppscontactsapi.entity

import jakarta.persistence.*
import java.time.Instant
import java.time.LocalDate

@Entity
@Table(name = "prisoner_contact")
class PrisonerContact(
  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "prisoner_contact_id_gen")
  @SequenceGenerator(
    name = "prisoner_contact_id_gen",
    sequenceName = "prisoner_contact_prisoner_contact_id_seq",
    allocationSize = 1,
  )
  @Column(name = "prisoner_contact_id", nullable = false)
  var id: Long? = null,

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "contact_id", nullable = false)
  var contact: Contact,

  @Column(name = "prisoner_number", nullable = false, length = 7)
  var prisonerNumber: String,

  @Column(name = "active", nullable = false)
  var active: Boolean = true,

  @Column(name = "relationship_type", nullable = false, length = 12)
  var relationshipType: String,

  @Column(name = "approved_visitor", nullable = false)
  var approvedVisitor: Boolean = false,

  @Column(name = "aware_of_charges", nullable = false)
  var awareOfCharges: Boolean = false,

  @Column(name = "can_be_contacted", nullable = false)
  var canBeContacted: Boolean = false,

  @Column(name = "next_of_kin", nullable = false)
  var nextOfKin: Boolean = false,

  @Column(name = "emergency_contact", nullable = false)
  var emergencyContact: Boolean = false,

  @Column(name = "comments", length = 240)
  var comments: String? = null,

  @Column(name = "approved_by", nullable = false, length = 100)
  var approvedBy: String,

  @Column(name = "approved_time", nullable = false)
  var approvedTime: Instant,

  @Column(name = "expiry_date")
  var expiryDate: LocalDate? = null,

  @Column(name = "created_at_prison", length = 5)
  var createdAtPrison: String? = null,

  @Column(name = "created_by", nullable = false, length = 100)
  var createdBy: String,

  @Column(name = "created_time", nullable = false)
  var createdTime: Instant = Instant.now(),

  @Column(name = "amended_by", length = 100)
  var amendedBy: String? = null,

  @Column(name = "amended_time")
  var amendedTime: Instant? = null,
)
