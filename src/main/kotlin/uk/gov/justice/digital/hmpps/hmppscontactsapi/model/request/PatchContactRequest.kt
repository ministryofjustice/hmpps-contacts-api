package uk.gov.justice.digital.hmpps.hmppscontactsapi.model.request

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.Size
import org.openapitools.jackson.nullable.JsonNullable

class PatchContactRequest {
  @Schema(description = "The language code of the contact", example = "EN", nullable = true)
  var languageCode: JsonNullable<String?> = JsonNullable.undefined()

  @Schema(description = "The id of the user updating the contact", example = "JD000001", maxLength = 100)
  @field:Size(max = 100, message = "updatedBy must be <= 100 characters")
  lateinit var updatedBy: String
}
