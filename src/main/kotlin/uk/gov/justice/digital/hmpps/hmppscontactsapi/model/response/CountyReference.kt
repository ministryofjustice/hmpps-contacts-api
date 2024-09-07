package uk.gov.justice.digital.hmpps.hmppscontactsapi.model.response

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "County reference entity")
data class CountyReference(

  @Schema(description = "Unique identifier of the county", example = "1", nullable = true)
  val countyId: Long? = null,

  @Schema(description = "Nomis code of the county", example = "GBR")
  val nomisCode: String? = null,

  @Schema(description = "Nomis description of the county", example = "United Kingdom")
  val nomisDescription: String? = null,

  @Schema(description = "Display sequence for the county", example = "1")
  val displaySequence: Int? = null,
)
