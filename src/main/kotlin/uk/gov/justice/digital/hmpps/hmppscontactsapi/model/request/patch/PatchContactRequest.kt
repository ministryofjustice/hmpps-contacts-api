package uk.gov.justice.digital.hmpps.hmppscontactsapi.model.request.patch

import io.swagger.v3.oas.annotations.media.Schema
import uk.gov.justice.digital.hmpps.hmppscontactsapi.model.request.patch.util.Patchable

@Schema(description = "Request to patch a new contact ", nullable = true)
data class PatchContactRequest(

  @Schema(description = "The language code of the contact", example = "EN", nullable = true)
  var languageCode: Patchable<String>? = Patchable.undefined(),

  @Schema(description = "The id of the user who updated the contact", example = "JD000001", nullable = true)
  val updatedBy: String,
)
