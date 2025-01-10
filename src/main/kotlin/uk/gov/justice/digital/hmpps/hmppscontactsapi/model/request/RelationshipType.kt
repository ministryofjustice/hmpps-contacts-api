package uk.gov.justice.digital.hmpps.hmppscontactsapi.model.request

import io.swagger.v3.oas.annotations.media.Schema
import uk.gov.justice.digital.hmpps.hmppscontactsapi.model.ReferenceCodeGroup

@Schema(description = "Defines types of relationships.")
enum class RelationshipType(
  @Schema(description = "Reference code group associated with the relationship type.")
  val referenceCodeGroup: ReferenceCodeGroup,

  @Schema(description = "Contact type code for the relationship type.")
  val contactType: String,
) {
  @Schema(description = "Represents an official relationship.")
  OFFICIAL(ReferenceCodeGroup.OFFICIAL_RELATIONSHIP, "O"),

  @Schema(description = "Represents a social relationship.")
  SOCIAL(ReferenceCodeGroup.RELATIONSHIP, "S"),
  ;

  fun getGroupCode(): ReferenceCodeGroup {
    return referenceCodeGroup
  }

  companion object {
    fun fromContactType(contactType: String): RelationshipType {
      return entries.firstOrNull { it.contactType == contactType }
        ?: throw IllegalArgumentException("Invalid contact type: $contactType")
    }
  }
}
