package uk.gov.justice.digital.hmpps.hmppscontactsapi.entity

import jakarta.persistence.*
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import java.time.Instant

@Entity
@Table(name = "reference_codes")
class ReferenceCode(
  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "reference_code_id_gen")
  @SequenceGenerator(
    name = "reference_code_id_gen",
    sequenceName = "reference_codes_reference_code_id_seq",
    allocationSize = 1,
  )
  @Column(name = "reference_code_id", nullable = false)
  var id: Long? = null,

  @field:Size(max = 40)
  @field:NotNull
  @Column(name = "group_code", nullable = false, length = 40)
  var groupCode: String,

  @field:Size(max = 40)
  @field:NotNull
  @Column(name = "code", nullable = false, length = 40)
  var code: String,

  @field:Size(max = 100)
  @field:NotNull
  @Column(name = "description", nullable = false, length = 100)
  var description: String,

  @field:Size(max = 100)
  @field:NotNull
  @Column(name = "created_by", nullable = false, length = 100)
  var createdBy: String,

  @field:NotNull
  @Column(name = "created_time", nullable = false)
  var createdTime: Instant = Instant.now(),

  @field:Size(max = 100)
  @Column(name = "amended_by", length = 100)
  var amendedBy: String? = null,

  @Column(name = "amended_time")
  var amendedTime: Instant? = null,
)
