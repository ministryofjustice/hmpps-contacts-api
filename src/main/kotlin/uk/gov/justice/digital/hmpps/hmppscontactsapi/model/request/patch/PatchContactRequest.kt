package uk.gov.justice.digital.hmpps.hmppscontactsapi.model.request.patch

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.Size
import org.openapitools.jackson.nullable.JsonNullable
import org.springframework.format.annotation.DateTimeFormat
import uk.gov.justice.digital.hmpps.hmppscontactsapi.model.request.EstimatedIsOverEighteen
import java.time.LocalDate

@Schema(description = "Request to patch a new contact ", nullable = true)
data class PatchContactRequest(

  @Schema(description = "The title of the contact, if any", example = "Mr", nullable = true, maxLength = 12)
  @field:Size(max = 12, message = "title must be <= 12 characters")
  val title: JsonNullable<String> = JsonNullable.undefined(),

  @Schema(description = "The last name of the contact", example = "Doe", maxLength = 35)
  @field:Size(max = 35, message = "lastName must be <= 35 characters")
  val lastName: JsonNullable<String> = JsonNullable.undefined(),

  @Schema(description = "The first name of the contact", example = "John", maxLength = 35)
  @field:Size(max = 35, message = "firstName must be <= 35 characters")
  val firstName: JsonNullable<String> = JsonNullable.undefined(),

  @Schema(description = "The middle names of the contact, if any", example = "William", nullable = true, maxLength = 35)
  @field:Size(max = 35, message = "middleNames must be <= 35 characters")
  val middleNames: JsonNullable<String> = JsonNullable.undefined(),

  @Schema(description = "The date of birth of the contact, if known", example = "1980-01-01", nullable = true, format = "yyyy-MM-dd")
  @field:DateTimeFormat(pattern = "yyyy-MM-dd")
  val dateOfBirth: JsonNullable<LocalDate> = JsonNullable.undefined(),

  @Schema(
    description = "Whether the contact is over 18, based on their date of birth if it is known",
    example = "YES",
    nullable = true,
  )
  val estimatedIsOverEighteen: JsonNullable<EstimatedIsOverEighteen> = JsonNullable.undefined(),

  @Schema(description = "The place of birth of the contact", example = "London", nullable = true)
  var placeOfBirth: JsonNullable<String> = JsonNullable.undefined(),

  @Schema(description = "Whether the contact is active", example = "true", nullable = true)
  var active: JsonNullable<Boolean> = JsonNullable.undefined(),

  @Schema(description = "Whether the contact is suspended", example = "false", nullable = true)
  var suspended: JsonNullable<Boolean> = JsonNullable.undefined(),

  @Schema(description = "Whether the contact is a staff member", example = "false", nullable = true)
  var staffFlag: JsonNullable<Boolean> = JsonNullable.undefined(),

  @Schema(description = "Whether the contact is deceased", example = "false", nullable = true)
  var deceasedFlag: JsonNullable<Boolean> = JsonNullable.undefined(),

  @Schema(description = "The date the contact was deceased, if applicable", example = "2023-05-01", nullable = true)
  var deceasedDate: JsonNullable<LocalDate> = JsonNullable.undefined(),

  @Schema(description = "The coroner's number, if applicable", example = "CRN12345", nullable = true)
  var coronerNumber: JsonNullable<String> = JsonNullable.undefined(),

  @Schema(description = "The gender of the contact", example = "Male", nullable = true)
  var gender: JsonNullable<String> = JsonNullable.undefined(),

  @Schema(description = "The domestic status code of the contact", example = "S", nullable = true)
  var domesticStatus: JsonNullable<String> = JsonNullable.undefined(),

  @Schema(description = "The language code of the contact", example = "EN", nullable = true)
  var languageCode: JsonNullable<String> = JsonNullable.undefined(),

  @Schema(description = "The nationality code of the contact", example = "GB", nullable = true)
  var nationalityCode: JsonNullable<String> = JsonNullable.undefined(),

  @Schema(description = "Whether an interpreter is required", example = "false", nullable = true)
  var interpreterRequired: JsonNullable<Boolean> = JsonNullable.undefined(),

  @Schema(description = "The id of the user who updated the contact", example = "JD000001", nullable = true)
  val updatedBy: JsonNullable<String> = JsonNullable.undefined(),
)
