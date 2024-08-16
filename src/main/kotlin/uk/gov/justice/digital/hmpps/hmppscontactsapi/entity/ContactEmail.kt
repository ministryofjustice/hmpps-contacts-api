package uk.gov.justice.digital.hmpps.hmppscontactsapi.entity

import jakarta.persistence.*
import java.time.Instant

@Entity
@Table(name = "contact_email")
class ContactEmail(
  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "contact_email_id_gen")
  @SequenceGenerator(
    name = "contact_email_id_gen",
    sequenceName = "contact_email_contact_email_id_seq",
    allocationSize = 1,
  )
  @Column(name = "contact_email_id", nullable = false)
  var id: Long? = null,

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "contact_id", nullable = false)
  var contact: Contact,

  @Column(name = "email_type", nullable = false, length = 20)
  var emailType: String,

  @Column(name = "email_address", nullable = false, length = 240)
  var emailAddress: String,

  @Column(name = "primary_email", nullable = false)
  var primaryEmail: Boolean = false,

  @Column(name = "created_by", nullable = false, length = 100)
  var createdBy: String,

  @Column(name = "created_time", nullable = false)
  var createdTime: Instant = Instant.now(),

  @Column(name = "amended_by", length = 100)
  var amendedBy: String? = null,

  @Column(name = "amended_time")
  var amendedTime: Instant? = null,
)
