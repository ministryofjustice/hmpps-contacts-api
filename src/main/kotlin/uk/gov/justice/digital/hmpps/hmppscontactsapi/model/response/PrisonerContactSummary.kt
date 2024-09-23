package uk.gov.justice.digital.hmpps.hmppscontactsapi.model.response

import io.swagger.v3.oas.annotations.media.Schema
import uk.gov.justice.digital.hmpps.hmppscontactsapi.model.request.EstimatedIsOverEighteen
import java.time.LocalDate

@Schema(description = "Describes the details of a prisoner's contact")
data class PrisonerContactSummary(

  @Schema(description = "The unique identifier for the prisoner contact", example = "123456")
  val prisonerContactId: Long,

  @Schema(description = "The unique identifier for the contact", example = "654321")
  val contactId: Long,

  @Schema(description = "Prisoner number (NOMS ID)", example = "A1234BC")
  val prisonerNumber: String,

  @Schema(description = "The surname of the contact", example = "Doe")
  val surname: String,

  @Schema(description = "The forename of the contact", example = "John")
  val forename: String,

  @Schema(description = "The middle name of the contact, if any", example = "William", nullable = true)
  val middleName: String? = null,

  @Schema(description = "The date of birth of the contact", example = "1980-01-01")
  val dateOfBirth: LocalDate?,

  @Schema(description = "YES if the contact is over 18 years old, NO if under, null if unknown", example = "YES")
  val estimatedIsOverEighteen: EstimatedIsOverEighteen?,

  @Schema(description = "The relationship code between the prisoner and the contact", example = "FRI")
  val relationshipCode: String,

  @Schema(description = "The description of the relationship", example = "Friend")
  val relationshipDescription: String,

  @Schema(description = "Flat number in the address, if any", example = "Flat 1", nullable = true)
  val flat: String?,

  @Schema(description = "Property name or number", example = "123")
  val property: String,

  @Schema(description = "Street name", example = "Baker Street")
  val street: String,

  @Schema(description = "Area or locality, if any", example = "Marylebone", nullable = true)
  val area: String?,

  @Schema(description = "City code", example = "LON")
  val cityCode: String,

  @Schema(description = "County code", example = "GLA")
  val countyCode: String,

  @Schema(description = "Postal code", example = "NW1 6XE")
  val postCode: String,

  @Schema(description = "Country code", example = "GBR")
  val countryCode: String,

  @Schema(description = "Indicates whether the contact is an approved visitor", example = "true")
  val approvedVisitor: Boolean,

  @Schema(description = "Is this contact the prisoner's next of kin?", example = "false")
  val nextOfKin: Boolean,

  @Schema(description = "Is this contact the prisoner's emergency contact?", example = "true")
  val emergencyContact: Boolean,

  @Schema(description = "Is this contact aware of the prisoner's charges?", example = "true")
  val awareOfCharges: Boolean,

  @Schema(description = "Any additional comments", example = "Close family friend", nullable = true)
  val comments: String?,
)
