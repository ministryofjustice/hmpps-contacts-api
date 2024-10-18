package uk.gov.justice.digital.hmpps.hmppscontactsapi.mapping.patch

import uk.gov.justice.digital.hmpps.hmppscontactsapi.entity.ContactEntity
import uk.gov.justice.digital.hmpps.hmppscontactsapi.model.request.patch.PatchContactRequest
import uk.gov.justice.digital.hmpps.hmppscontactsapi.model.request.patch.PatchContactResponse
import uk.gov.justice.digital.hmpps.hmppscontactsapi.model.request.patch.util.Patchable
import java.time.LocalDateTime

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
    amendedBy = this.amendedBy,
    amendedTime = this.amendedTime,
  )
}

fun ContactEntity.patchRequest(
  request: PatchContactRequest,
): ContactEntity {
  val changedContact = this.copy(
    title = this.title,
    firstName = this.firstName,
    lastName = this.lastName,
    middleNames = this.middleNames,
    dateOfBirth = this.dateOfBirth,
    isDeceased = this.isDeceased,
    deceasedDate = this.deceasedDate,
    estimatedIsOverEighteen = this.estimatedIsOverEighteen,
  ).also {
    it.placeOfBirth = this.placeOfBirth
    it.active = this.active
    it.suspended = this.suspended
    it.staffFlag = this.staffFlag
    it.coronerNumber = this.coronerNumber
    it.gender = this.gender
    it.domesticStatus = this.domesticStatus
    it.nationalityCode = this.nationalityCode
    it.interpreterRequired = this.interpreterRequired
    it.languageCode = resolveLanguageCode(this.languageCode, request.languageCode)
    it.amendedBy = request.updatedBy
    it.amendedTime = LocalDateTime.now()
  }

  return changedContact
}

fun resolveLanguageCode(dbLanguageCode: String?, requestLanguageCode: Patchable<String>?): String? {
  return if (requestLanguageCode == null || (requestLanguageCode.get() != null)) requestLanguageCode?.get() else dbLanguageCode
}
