package uk.gov.justice.digital.hmpps.hmppscontactsapi.mapping

import uk.gov.justice.digital.hmpps.hmppscontactsapi.entity.ContactEntity
import uk.gov.justice.digital.hmpps.hmppscontactsapi.model.request.sync.CreateContactRequest
import uk.gov.justice.digital.hmpps.hmppscontactsapi.model.response.sync.Contact

fun ContactEntity.syncFromEntityToModel(): Contact {
  return Contact(
    id = this.contactId,
    title = this.title,
    firstName = this.firstName,
    lastName = this.lastName,
    middleName = this.middleName,
    dateOfBirth = this.dateOfBirth,
    estimatedIsOverEighteen = this.estimatedIsOverEighteen,
    createdBy = this.createdBy,
    createdTime = this.createdTime,
    contactTypeCode = this.contactTypeCode,
    placeOfBirth = this.placeOfBirth,
    active = this.active,
    suspended = this.suspended,
    staffFlag = this.staffFlag,
    deceasedFlag = this.deceasedFlag,
    deceasedDate = this.deceasedDate,
    coronerNumber = this.coronerNumber,
    gender = this.gender,
    maritalStatus = this.maritalStatus,
    languageCode = this.languageCode,
    nationalityCode = this.nationalityCode,
    interpreterRequired = this.interpreterRequired,
    comments = this.comments,
    amendedBy = this.amendedBy,
    amendedTime = this.amendedTime,
  )
}

fun List<ContactEntity>.toModel() = map { it.toModel() }

fun CreateContactRequest.toEntity() = ContactEntity(
  contactId = 0L,
  title = this.title,
  firstName = this.firstName,
  lastName = this.lastName,
  middleName = this.middleName,
  dateOfBirth = this.dateOfBirth,
  estimatedIsOverEighteen = this.estimatedIsOverEighteen,
  createdBy = this.createdBy,
  createdTime = this.createdTime,
).also {
  it.contactTypeCode = this.contactTypeCode
  it.placeOfBirth = this.placeOfBirth
  it.active = this.active
  it.suspended = this.suspended
  it.staffFlag = this.staffFlag
  it.deceasedFlag = this.deceasedFlag
  it.deceasedDate = this.deceasedDate
  it.coronerNumber = this.coronerNumber
  it.gender = this.gender
  it.maritalStatus = this.maritalStatus
  it.languageCode = this.languageCode
  it.nationalityCode = this.nationalityCode
  it.interpreterRequired = this.interpreterRequired
  it.comments = this.comments
}
