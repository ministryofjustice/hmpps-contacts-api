package uk.gov.justice.digital.hmpps.hmppscontactsapi.model.response

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "Country reference entity")
data class CountryReference(

  @Schema(description = "Unique identifier of the country", example = "1", nullable = true)
  val countryId: Long? = null,

  @Schema(description = "Nomis code of the country", example = "GBR")
  val nomisCode: String? = null,

  @Schema(description = "Nomis description of the country", example = "United Kingdom")
  val nomisDescription: String? = null,

  @Schema(description = "ISO numeric code of the country", example = "826")
  val isoNumeric: Int? = null,

  @Schema(description = "ISO Alpha-2 code of the country", example = "GB")
  val isoAlpha2: String? = null,

  @Schema(description = "ISO Alpha-3 code of the country", example = "GBR")
  val isoAlpha3: String? = null,

  @Schema(description = "ISO country description", example = "United Kingdom of Great Britain and Northern Ireland")
  val isoCountryDesc: String? = null,

  @Schema(description = "Display sequence for the country", example = "1")
  val displaySequence: Int? = null,
)
