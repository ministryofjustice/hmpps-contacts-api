package uk.gov.justice.digital.hmpps.hmppscontactsapi.model.response

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "Describes the prisoner contact relationship")
data class PrisonerContactRelationshipDetails(
  @Schema(description = "The unique identifier for the prisoner contact", example = "123456")
  val prisonerContactId: Long,

  @Schema(description = "The unique identifier for the contact", example = "654321")
  val contactId: Long,

  @Schema(description = "Prisoner number (NOMS ID)", example = "A1234BC")
  val prisonerNumber: String,

  @Schema(description = "The relationship to the prisoner. A code from SOCIAL_RELATIONSHIP or OFFICIAL_RELATIONSHIP reference data groups depending on the relationship type.", example = "FRI")
  val relationshipToPrisonerCode: String,

  @Schema(description = "The description of the relationship", example = "Friend")
  val relationshipToPrisonerDescription: String,

  @Schema(description = "Is this contact the prisoner's emergency contact?", example = "true")
  val emergencyContact: Boolean,

  @Schema(description = "Is this contact the prisoner's next of kin?", example = "false")
  val nextOfKin: Boolean,

  @Schema(description = "Is this a approved visitor for the prisoner?", example = "true")
  val isApprovedVisitor: Boolean,

  @Schema(description = "Is this prisoner's contact relationship active?", example = "true")
  val isRelationshipActive: Boolean,

  @Schema(description = "Any additional comments", example = "Close family friend", nullable = true)
  val comments: String?,
)
