package uk.gov.justice.digital.hmpps.hmppscontactsapi.model.request

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "Wrapper object containing relationship type and code")
data class Relationship(
  @Schema(
    description = "The relationship type between the prisoner and the contact, allowed values SOCIAL or OFFICIAL",
    example = "SOCIAL/OFFICIAL",
    exampleClasses = [RelationshipType::class],
    nullable = false,
    type = "string",
    requiredMode = Schema.RequiredMode.REQUIRED,
  )
  val type: RelationshipType,

  @Schema(
    description = "The relationship reference code which represents the relationship between the prisoner and the contact",
    example = "FRI",
    nullable = false,
    type = "string",
    requiredMode = Schema.RequiredMode.REQUIRED,
  )
  val code: String,
)
