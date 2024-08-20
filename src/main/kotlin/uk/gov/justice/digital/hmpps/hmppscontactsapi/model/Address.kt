package uk.gov.justice.digital.hmpps.hmppscontactsapi.model

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "Describes an address associated with a contact")
data class Address(

  @Schema(description = "The street address", example = "123 Main St")
  val street: String,

  @Schema(description = "The city of the address", example = "London")
  val city: String,

  @Schema(description = "The postal code of the address", example = "NW1 6XE")
  val postalCode: String,

  @Schema(description = "The country of the address", example = "UK")
  val country: String,

  @Schema(description = "Indicates if this address is the primary address", example = "true")
  val primary: Boolean,
)
