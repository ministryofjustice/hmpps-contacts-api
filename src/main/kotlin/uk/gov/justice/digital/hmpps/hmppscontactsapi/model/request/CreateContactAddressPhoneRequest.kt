package uk.gov.justice.digital.hmpps.hmppscontactsapi.model.request

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.Size

@Schema(description = "Request to create a new address-linked phone number")
data class CreateContactAddressPhoneRequest(
  @Schema(description = "Unique identifier for the contact address", example = "123")
  val contactAddressId: Long,

  @Schema(description = "Type of phone", example = "MOB")
  @field:Size(max = 12, message = "phoneType must be <= 12 characters")
  val phoneType: String,

  @Schema(description = "Phone number", example = "+1234567890")
  @field:Size(max = 240, message = "phoneNumber must be <= 240 characters")
  val phoneNumber: String,

  @Schema(description = "Extension number", example = "123")
  @field:Size(max = 7, message = "extNumber must be <= 7 characters")
  val extNumber: String? = null,

  @Schema(description = "User who created the entry", example = "admin")
  @field:Size(max = 100, message = "createdBy must be <= 100 characters")
  val createdBy: String,
)