package uk.gov.justice.digital.hmpps.hmppscontactsapi.entity

import jakarta.persistence.*
import java.time.Instant

@Entity
@Table(name = "contact_nationality")
class ContactNationality(
  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "contact_nationality_id_gen")
  @SequenceGenerator(
    name = "contact_nationality_id_gen",
    sequenceName = "contact_nationality_contact_nationality_id_seq",
    allocationSize = 1,
  )
  @Column(name = "contact_nationality_id", nullable = false)
  var id: Long? = null,

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "contact_id", nullable = false)
  var contact: Contact,

  @Column(name = "nationality_code", nullable = false, length = 12)
  var nationalityCode: String,

  @Column(name = "created_by", nullable = false, length = 100)
  var createdBy: String,

  @Column(name = "created_time", nullable = false)
  var createdTime: Instant = Instant.now(),

  @Column(name = "amended_by", length = 100)
  var amendedBy: String? = null,

  @Column(name = "amended_time")
  var amendedTime: Instant? = null,
)
