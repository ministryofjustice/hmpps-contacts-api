package uk.gov.justice.digital.hmpps.hmppscontactsapi.model.response

import io.swagger.v3.oas.annotations.media.Schema
import java.time.LocalDateTime

@Schema(description = "A phone number related to a contact with descriptions of all reference data")
data class ContactPhoneNumberDetails(
  @Schema(description = "Unique identifier for the contact phone", example = "1")
  val contactPhoneId: Long,

  @Schema(description = "Unique identifier for the contact", example = "123")
  val contactId: Long,

  @Schema(description = "Type of phone", example = "MOBILE")
  val phoneType: String,

  @Schema(description = "Description of the type of phone", example = "Mobile phone")
  val phoneTypeDescription: String,

  @Schema(description = "Phone number", example = "+1234567890")
  val phoneNumber: String,

  @Schema(description = "Extension number", example = "123")
  val extNumber: String?,

  @Schema(description = "Indicates if this is the primary phone number", example = "true")
  val primaryPhone: Boolean,

  @Schema(description = "User who created the entry", example = "admin")
  val createdBy: String,

  @Schema(description = "Timestamp when the entry was created", example = "2023-09-23T10:15:30")
  val createdTime: LocalDateTime,

  @Schema(description = "User who amended the entry", example = "admin2")
  val amendedBy: String?,

  @Schema(description = "Timestamp when the entry was amended", example = "2023-09-24T12:00:00")
  val amendedTime: LocalDateTime?,
)
