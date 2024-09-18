package uk.gov.justice.digital.hmpps.hmppscontactsapi.model

import io.swagger.v3.oas.annotations.media.Schema
import java.time.LocalDateTime

@Schema(description = "An address related to a contact")
data class ContactAddress(

  @Schema(description = "The id of the contact address", example = "123456")
  val contactAddressId: Long,

  @Schema(description = "The id of the contact", example = "123456")
  val contactId: Long,

  @Schema(description = "The type of address", example = "HOME")
  val addressType: String,

  @Schema(description = "True if this is the primary address otherwise false", example = "true")
  val primaryAddress: Boolean,

  @Schema(description = "Flat number or name", example = "Flat 2B", nullable = true)
  val flat: String? = null,

  @Schema(description = "Building or house number or name", example = "Mansion House", nullable = true)
  val property: String? = null,

  @Schema(description = "Street or road name", example = "Acacia Avenue", nullable = true)
  val street: String? = null,

  @Schema(description = "Area", example = "Morton Heights", nullable = true)
  val area: String? = null,

  @Schema(description = "City code - from NOMIS", example = "BIRM", nullable = true)
  val cityCode: String? = null,

  @Schema(description = "County code - from NOMIS", example = "WMIDS", nullable = true)
  val countyCode: String? = null,

  @Schema(description = "Postcode", example = "S13 4FH", nullable = true)
  val postcode: String? = null,

  @Schema(description = "Country code - from NOMIS", example = "UK", nullable = true)
  val countryCode: String? = null,

  @Schema(description = "Whether the address has been verified by postcode lookup", example = "false")
  val verified: Boolean = false,

  @Schema(description = "Which username ran the postcode lookup check", example = "NJKG44D")
  val verifiedBy: String? = null,

  @Schema(description = "The timestamp of when the postcode lookup was done", example = "2024-01-01T00:00:00Z")
  val verifiedTime: LocalDateTime? = null,

  @Schema(description = "The id of the user who created the contact", example = "JD000001")
  val createdBy: String,

  @Schema(description = "The timestamp of when the contact was created", example = "2024-01-01T00:00:00Z")
  val createdTime: LocalDateTime,

  @Schema(description = "The id of the user who last amended the contact address", example = "JD000001")
  val amendedBy: String? = null,

  @Schema(description = "The timestamp of when the contact address was last amended", example = "2024-01-01T00:00:00Z")
  val amendedTime: LocalDateTime? = null,
)
