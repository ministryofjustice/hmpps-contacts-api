package uk.gov.justice.digital.hmpps.hmppscontactsapi.model
import io.swagger.v3.oas.annotations.media.Schema
import java.time.LocalDate

@Schema(description = "Describes the details of a prisoner's contact")
data class PrisonerContact(

  @Schema(description = "The unique identifier for the prisoner contact", example = "123456")
  val prisonerContactId: Long,

  @Schema(description = "The unique identifier for the contact", example = "654321")
  val contactId: Long,

  @Schema(description = "The surname of the contact", example = "Doe")
  val surname: String,

  @Schema(description = "The forename of the contact", example = "John")
  val forename: String,

  @Schema(description = "The middle name of the contact, if any", example = "William", nullable = true)
  val middleName: String? = null,

  @Schema(description = "The date of birth of the contact", example = "1980-01-01")
  val dateOfBirth: LocalDate,

  @Schema(description = "The relationship code between the prisoner and the contact", example = "FRI")
  val relationshipCode: String,

  @Schema(description = "The description of the relationship", example = "Friend")
  val relationshipDescription: String,

  @Schema(description = "Addresses associated with the contact, one of which should be primary")
  val addresses: String,

  @Schema(description = "Indicates whether the contact is an approved visitor", example = "true")
  val approvedVisitor: Boolean,
)
