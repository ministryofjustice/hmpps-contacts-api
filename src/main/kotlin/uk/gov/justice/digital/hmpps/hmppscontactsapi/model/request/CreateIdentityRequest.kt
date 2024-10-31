package uk.gov.justice.digital.hmpps.hmppscontactsapi.model.request

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.Size

@Schema(description = "Request to create a new contact identity")
data class CreateIdentityRequest(
  @Schema(description = "Type of identity", example = "DL")
  @field:Size(max = 20, message = "identityType must be <= 20 characters")
  val identityType: String,

  @Schema(description = "The identity value such as driving licence number", example = "DL123456789")
  @field:Size(max = 20, message = "identityValue must be <= 20 characters")
  val identityValue: String,

  @Schema(description = "The authority who issued the identity", example = "DVLA", nullable = true)
  @field:Size(max = 40, message = "issuingAuthority must be <= 40 characters")
  val issuingAuthority: String? = null,

  @Schema(description = "User who created the entry", example = "admin")
  @field:Size(max = 100, message = "createdBy must be <= 100 characters")
  val createdBy: String,
)