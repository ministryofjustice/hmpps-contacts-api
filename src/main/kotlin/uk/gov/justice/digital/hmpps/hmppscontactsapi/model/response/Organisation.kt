package uk.gov.justice.digital.hmpps.hmppscontactsapi.model.response

import io.swagger.v3.oas.annotations.media.Schema
import java.time.LocalDate
import java.time.LocalDateTime

@Schema(description = "Organisation data")
data class Organisation(

  @Schema(description = "Unique identifier of the Organisation", example = "1")
  val organisationId: Long,

  @Schema(description = "The name of the organisation", example = "Example Limited")
  val organisationName: String,

  @Schema(
    description = "The programme number for the organisation, stored as FEI_NUMBER in NOMIS",
    example = "1",
  )
  val programmeNumber: String?,

  @Schema(description = "The VAT number for the organisation, if known", example = "123456")
  val vatNumber: String?,

  @Schema(
    description = "The id of the caseload for this organisation, this is an agency id in NOMIS",
    example = "BXI",
  )
  val caseloadId: String?,

  @Schema(description = "Any comments on the organisation", example = "Some additional info")
  val comments: String?,

  @Schema(description = "Whether the organisation is active or not", example = "true")
  val active: Boolean,

  @Schema(description = "The date the organisation was deactivated, EXPIRY_DATE in NOMIS")
  val deactivatedDate: LocalDate?,

  @Schema(description = "User who created the entry")
  val createdBy: String,

  @Schema(description = "Timestamp when the entry was created")
  val createdTime: LocalDateTime,

  @Schema(description = "User who updated the entry")
  val updatedBy: String?,

  @Schema(description = "Timestamp when the entry was updated")
  val updatedTime: LocalDateTime?,
)