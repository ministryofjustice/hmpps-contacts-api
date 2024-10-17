package uk.gov.justice.digital.hmpps.hmppscontactsapi.mapping.patch

import uk.gov.justice.digital.hmpps.hmppscontactsapi.entity.ContactEntity
import uk.gov.justice.digital.hmpps.hmppscontactsapi.model.request.patch.PatchContactRequest
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
    placeOfBirth = this.placeOfBirth,
    active = this.active,
    suspended = this.suspended,
    staffFlag = this.staffFlag,
    deceasedFlag = this.isDeceased,
    deceasedDate = this.deceasedDate,
    coronerNumber = this.coronerNumber,
    gender = this.gender,
    domesticStatus = this.domesticStatus,
    languageCode = this.languageCode,
    nationalityCode = this.nationalityCode,
    interpreterRequired = this.interpreterRequired,
    comments = this.comments,
    amendedBy = this.amendedBy,
    amendedTime = this.amendedTime,
  )
}

fun ContactEntity.patchRequest(
  request: PatchContactRequest,
): ContactEntity {
  val changedContact = this.copy(
    title = request.title ?: this.title,
    firstName = request.firstName ?: this.firstName,
    lastName = request.lastName ?: this.lastName,
    middleNames = request.middleNames ?: this.middleNames,
    dateOfBirth = request.dateOfBirth ?: this.dateOfBirth,
    isDeceased = request.deceasedFlag ?: this.isDeceased,
    deceasedDate = request.deceasedDate ?: this.deceasedDate,
    estimatedIsOverEighteen = request.estimatedIsOverEighteen ?: this.estimatedIsOverEighteen,
  ).also {
    it.placeOfBirth = request.placeOfBirth ?: this.placeOfBirth
    it.active = request.active ?: this.active
    it.suspended = request.suspended ?: this.suspended
    it.staffFlag = request.staffFlag ?: this.staffFlag
    it.coronerNumber = request.coronerNumber ?: this.coronerNumber
    it.gender = request.gender ?: this.gender
    it.domesticStatus = request.domesticStatus ?: this.domesticStatus
    it.languageCode = request.languageCode ?: this.languageCode
    it.nationalityCode = request.nationalityCode ?: this.nationalityCode
    it.interpreterRequired = request.interpreterRequired ?: this.interpreterRequired
    it.comments = request.comments ?: this.comments
    it.amendedBy = request.updatedBy ?: this.amendedBy
    it.amendedTime = request.updatedTime ?: this.amendedTime
  }
  return changedContact
}
