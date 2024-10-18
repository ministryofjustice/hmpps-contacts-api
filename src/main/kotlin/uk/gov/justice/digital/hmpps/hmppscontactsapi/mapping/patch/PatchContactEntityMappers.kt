package uk.gov.justice.digital.hmpps.hmppscontactsapi.mapping.patch

import uk.gov.justice.digital.hmpps.hmppscontactsapi.entity.ContactEntity
import uk.gov.justice.digital.hmpps.hmppscontactsapi.model.request.patch.PatchContactRequest
import uk.gov.justice.digital.hmpps.hmppscontactsapi.model.request.patch.PatchContactResponse
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
    title = request.title.orElse(this.title),
    firstName = request.firstName.orElse(this.firstName),
    lastName = request.lastName.orElse(this.lastName),
    middleNames = request.middleNames.orElse(this.middleNames),
    dateOfBirth = request.dateOfBirth.orElse(this.dateOfBirth),
    isDeceased = request.deceasedFlag.orElse(this.isDeceased),
    deceasedDate = request.deceasedDate.orElse(this.deceasedDate),
    estimatedIsOverEighteen = request.estimatedIsOverEighteen.orElse(this.estimatedIsOverEighteen),
  ).also {
    it.placeOfBirth = request.placeOfBirth.orElse(this.placeOfBirth)
    it.active = request.active.orElse(this.active)
    it.suspended = request.suspended.orElse(this.suspended)
    it.staffFlag = request.staffFlag.orElse(this.staffFlag)
    it.coronerNumber = request.coronerNumber.orElse(this.coronerNumber)
    it.gender = request.gender.orElse(this.gender)
    it.domesticStatus = request.domesticStatus.orElse(this.domesticStatus)
    it.languageCode = request.languageCode.orElse(this.languageCode)
    it.nationalityCode = request.nationalityCode.orElse(this.nationalityCode)
    it.interpreterRequired = request.interpreterRequired.orElse(this.interpreterRequired)
    it.amendedBy = request.updatedBy.orElse(this.amendedBy)
    it.amendedTime = LocalDateTime.now()
  }

  return changedContact
}
