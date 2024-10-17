package uk.gov.justice.digital.hmpps.hmppscontactsapi.model.request.patch

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.Size
import org.springframework.format.annotation.DateTimeFormat
import uk.gov.justice.digital.hmpps.hmppscontactsapi.model.request.EstimatedIsOverEighteen
import java.time.LocalDate
import java.time.LocalDateTime

@Schema(description = "Request to patch a new contact ", nullable = true)
data class PatchContactRequest(

  @Schema(description = "The title of the contact, if any", example = "Mr", nullable = true, maxLength = 12)
  @field:Size(max = 12, message = "title must be <= 12 characters")
  val title: String? = null,

  @Schema(description = "The last name of the contact", example = "Doe", maxLength = 35)
  @field:Size(max = 35, message = "lastName must be <= 35 characters")
  val lastName: String? = null,

  @Schema(description = "The first name of the contact", example = "John", maxLength = 35)
  @field:Size(max = 35, message = "firstName must be <= 35 characters")
  val firstName: String? = null,

  @Schema(description = "The middle names of the contact, if any", example = "William", nullable = true, maxLength = 35)
  @field:Size(max = 35, message = "middleNames must be <= 35 characters")
  val middleNames: String? = null,

  @Schema(description = "The date of birth of the contact, if known", example = "1980-01-01", nullable = true, format = "yyyy-MM-dd")
  @field:DateTimeFormat(pattern = "yyyy-MM-dd")
  val dateOfBirth: LocalDate? = null,

  @Schema(
    description = "Whether the contact is over 18, based on their date of birth if it is known",
    example = "YES",
    nullable = true,
  )
  val estimatedIsOverEighteen: EstimatedIsOverEighteen? = null,

  @Schema(description = "The place of birth of the contact", example = "London", nullable = true)
  var placeOfBirth: String? = null,

  @Schema(description = "Whether the contact is active", example = "true", nullable = true)
  var active: Boolean? = null,

  @Schema(description = "Whether the contact is suspended", example = "false", nullable = true)
  var suspended: Boolean? = null,

  @Schema(description = "Whether the contact is a staff member", example = "false", nullable = true)
  var staffFlag: Boolean? = null,

  @Schema(description = "Whether the contact is deceased", example = "false", nullable = true)
  var deceasedFlag: Boolean? = null,

  @Schema(description = "The date the contact was deceased, if applicable", example = "2023-05-01", nullable = true)
  var deceasedDate: LocalDate? = null,

  @Schema(description = "The coroner's number, if applicable", example = "CRN12345", nullable = true)
  var coronerNumber: String? = null,

  @Schema(description = "The gender of the contact", example = "Male", nullable = true)
  var gender: String? = null,

  @Schema(description = "The domestic status code of the contact", example = "S", nullable = true)
  var domesticStatus: String? = null,

  @Schema(description = "The language code of the contact", example = "EN", nullable = true)
  var languageCode: String? = null,

  @Schema(description = "The nationality code of the contact", example = "GB", nullable = true)
  var nationalityCode: String? = null,

  @Schema(description = "Whether an interpreter is required", example = "false", nullable = true)
  var interpreterRequired: Boolean? = null,

  @Schema(
    description = "Additional comments about the contact",
    example = "This contact has special dietary requirements.",
    nullable = true,
  )
  var comments: String? = null,

  @Schema(description = "The id of the user who updated the contact", example = "JD000001", nullable = true)
  val updatedBy: String? = null,

  @Schema(description = "The timestamp of when the contact was changed", example = "2024-01-01T00:00:00Z", nullable = true)
  val updatedTime: LocalDateTime? = null,
)
