package uk.gov.justice.digital.hmpps.hmppscontactsapi.mapping

import uk.gov.justice.digital.hmpps.hmppscontactsapi.entity.PrisonerContactViewEntity
import uk.gov.justice.digital.hmpps.hmppscontactsapi.model.PrisonerContact

fun PrisonerContactViewEntity.toModel() = PrisonerContact(
  prisonerContactId = prisonerContactId!!,
  contactId = contactId,
  surname = surname,
  forename = forename,
  middleName = middleName,
  dateOfBirth = dateOfBirth,
  relationshipCode = relationshipCode!!,
  relationshipDescription = relationshipDescription!!,
  addresses = addresses,
  approvedVisitor = approvedVisitor,
)
fun List<PrisonerContactViewEntity>.toModel() = map { it.toModel() }
