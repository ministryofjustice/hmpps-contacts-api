package uk.gov.justice.digital.hmpps.hmppscontactsapi.model.response.sync

import io.swagger.v3.oas.annotations.media.Schema
import java.time.LocalDate
import java.time.LocalDateTime

@Schema(description = "Response object with prisoner contact details")
data class PrisonerContact(

  @Schema(description = "The id of the contact", example = "123456")
  val id: Long,

  @Schema(description = "The ID of the prisoner contact", example = "12345")
  val contactId: Long,

  @Schema(description = "The prisoner number", example = "A1234BC")
  val prisonerNumber: String,

  @Schema(description = "The type of relationship", example = "Friend")
  val relationshipType: String,

  @Schema(description = "Indicates if the prisoner contact is next of kin", example = "true")
  val nextOfKin: Boolean,

  @Schema(description = "Indicates if the prisoner contact is an emergency contact", example = "true")
  val emergencyContact: Boolean,

  @Schema(description = "Additional comments about the prisoner contact", example = "Close family friend", nullable = true)
  val comments: String?,

  @Schema(description = "Indicates if the prisoner contact is active", example = "true", nullable = true)
  val active: Boolean? = false,

  @Schema(description = "Indicates if the prisoner contact is an approved visitor", example = "true", nullable = true)
  val approvedVisitor: Boolean? = false,

  @Schema(description = "Indicates if the prisoner contact is aware of charges", example = "true", nullable = true)
  val awareOfCharges: Boolean? = false,

  @Schema(description = "Indicates if the prisoner contact can be contacted", example = "true", nullable = true)
  val canBeContacted: Boolean? = false,

  @Schema(description = "The user who approved the prisoner contact", example = "officer123", nullable = true)
  val approvedBy: String? = null,

  @Schema(
    description = "The timestamp when the prisoner contact was approved",
    example = "2024-01-01T14:00:00Z",
    nullable = true,
  )
  val approvedTime: LocalDateTime?,

  @Schema(description = "The expiry date of the prisoner contact", example = "2025-01-01", nullable = true)
  val expiryDate: LocalDate? = null,

  @Schema(description = "The prison where the prisoner contact was created", example = "HMP Belmarsh", nullable = true)
  val createdAtPrison: String? = null,

  @Schema(description = "The user who created the prisoner contact", example = "system")
  val createdBy: String,

  @Schema(description = "The timestamp when the prisoner contact was created", example = "2024-01-01T12:00:00")
  val createdTime: LocalDateTime,

  @Schema(description = "The user who last amended the prisoner contact", example = "adminUser", nullable = true)
  val amendedBy: String? = null,

  @Schema(
    description = "The timestamp when the prisoner contact was last amended",
    example = "2024-02-01T16:00:00Z",
    nullable = true,
  )
  val amendedTime: LocalDateTime?,
)