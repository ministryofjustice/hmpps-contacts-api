package uk.gov.justice.digital.hmpps.hmppscontactsapi.model.response

import io.swagger.v3.oas.annotations.media.Schema
import java.time.LocalDateTime

@Schema(description = "Identity related to a contact")
data class ContactIdentityDetails(
  @Schema(description = "Unique identifier for the contact identity", example = "1")
  val contactIdentityId: Long,

  @Schema(description = "Unique identifier for the contact", example = "123")
  val contactId: Long,

  @Schema(description = "Type of identity", example = "PASSPORT", nullable = true)
  val identityType: String?,

  @Schema(description = "Description of the type of identity", example = "Passport number", nullable = true)
  val identityTypeDescription: String?,

  @Schema(description = "Identity ", example = "GB123456789", nullable = true)
  val identityValue: String?,

  @Schema(description = "The authority who issued the identity ", example = "UK Passport Office", nullable = true)
  val issuingAuthority: String?,

  @Schema(description = "Whether the identity has been verified", example = "false")
  val verified: Boolean,

  @Schema(description = "The user id of the user who verified the identity", example = "USER1", nullable = true)
  val verifiedBy: String?,

  @Schema(description = "Timestamp when the identity was verified", example = "2023-09-23T10:15:30", nullable = true)
  val verifiedTime: LocalDateTime?,

  @Schema(description = "User who created the entry", example = "admin")
  val createdBy: String,

  @Schema(description = "Timestamp when the entry was created", example = "2023-09-23T10:15:30")
  val createdTime: LocalDateTime,

  @Schema(description = "User who amended the entry", example = "admin2")
  val amendedBy: String?,

  @Schema(description = "Timestamp when the entry was amended", example = "2023-09-24T12:00:00")
  val amendedTime: LocalDateTime?,
)
