package uk.gov.justice.digital.hmpps.hmppscontactsapi.mapping.patch

import uk.gov.justice.digital.hmpps.hmppscontactsapi.entity.ContactEntity
import uk.gov.justice.digital.hmpps.hmppscontactsapi.model.request.patch.PatchContactResponse

fun ContactEntity.mapToResponse(): PatchContactResponse {
  return PatchContactResponse(
    id = this.contactId,
    title = this.title,
    firstName = this.firstName,
    lastName = this.lastName,
    middleNames = this.middleNames,
    dateOfBirth = this.dateOfBirth,
    estimatedIsOverEighteen = this.estimatedIsOverEighteen,
    createdBy = this.createdBy,
    createdTime = this.createdTime,
    active = this.active,
    suspended = this.suspended,
    isStaff = this.staffFlag,
    deceasedFlag = this.isDeceased,
    deceasedDate = this.deceasedDate,
    gender = this.gender,
    domesticStatus = this.domesticStatus,
    languageCode = this.languageCode,
    nationalityCode = this.nationalityCode,
    interpreterRequired = this.interpreterRequired,
    amendedBy = this.amendedBy,
    amendedTime = this.amendedTime,
  )
}
