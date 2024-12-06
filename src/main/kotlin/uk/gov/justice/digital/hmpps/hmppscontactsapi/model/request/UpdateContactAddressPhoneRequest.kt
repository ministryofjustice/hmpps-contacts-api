package uk.gov.justice.digital.hmpps.hmppscontactsapi.model.request

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "Request to update an address-specific phone number")
data class UpdateContactAddressPhoneRequest(
  @Schema(description = "Type of phone", example = "MOB")
  val phoneType: String,

  @Schema(description = "Phone number", example = "+1234567890")
  val phoneNumber: String,

  @Schema(description = "Extension number", example = "123")
  val extNumber: String? = null,

  @Schema(description = "The username of the person who made the update", example = "JD000001")
  val updatedBy: String,
)
