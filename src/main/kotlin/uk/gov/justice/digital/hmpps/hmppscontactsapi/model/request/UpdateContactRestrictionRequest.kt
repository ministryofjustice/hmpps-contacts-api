package uk.gov.justice.digital.hmpps.hmppscontactsapi.model.request

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.Size
import java.time.LocalDate

@Schema(description = "Request to update an existing restriction on a contact, a.k.a an estate-wide restriction")
data class UpdateContactRestrictionRequest(
  @Schema(
    description =
    """
    The coded type of restriction that applies to this contact.
    This is a coded value from the group RESTRICTION in reference codes.
    Example values include ACC, BAN, CHILD, CLOSED, RESTRICTED, DIHCON, NONCON.
    """,
    example = "BAN",
  )
  val restrictionType: String,

  @Schema(description = "Restriction start date", example = "2024-01-01")
  val startDate: LocalDate,

  @Schema(description = "Restriction end date", example = "2024-01-01", nullable = true)
  val expiryDate: LocalDate?,

  @Schema(description = "Comments for the restriction", example = "N/A", nullable = true)
  @field:Size(max = 240, message = "comments must be <= 240 characters")
  val comments: String?,

  @Schema(description = "User who updated the entry", example = "admin")
  @field:Size(max = 100, message = "updatedBy must be <= 100 characters")
  val updatedBy: String,
)
