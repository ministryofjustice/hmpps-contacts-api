package uk.gov.justice.digital.hmpps.hmppscontactsapi.service

import jakarta.persistence.EntityNotFoundException
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import uk.gov.justice.digital.hmpps.hmppscontactsapi.mapping.syncFromEntityToModel
import uk.gov.justice.digital.hmpps.hmppscontactsapi.mapping.toEntity
import uk.gov.justice.digital.hmpps.hmppscontactsapi.model.request.sync.CreateContactRequest
import uk.gov.justice.digital.hmpps.hmppscontactsapi.model.request.sync.UpdateContactRequest
import uk.gov.justice.digital.hmpps.hmppscontactsapi.model.response.sync.Contact
import uk.gov.justice.digital.hmpps.hmppscontactsapi.repository.ContactRepository

@Service
@Transactional
class SyncContactService(
  val contactRepository: ContactRepository,
) {

  companion object {
    private val logger = LoggerFactory.getLogger(this::class.java)
  }

  @Transactional(readOnly = true)
  fun getContactById(contactId: Long): Contact {
    val contactEntity = contactRepository.findById(contactId)
      .orElseThrow { EntityNotFoundException("Contact with ID $contactId not found") }
    return contactEntity.syncFromEntityToModel()
  }

  fun deleteContact(contactId: Long) {
    contactRepository.findById(contactId)
      .orElseThrow { EntityNotFoundException("Contact with ID $contactId not found") }
    contactRepository.deleteById(contactId)
  }

  fun createContact(request: CreateContactRequest): Contact {
    return contactRepository.saveAndFlush(request.toEntity()).syncFromEntityToModel()
  }

  fun updateContact(contactId: Long, request: UpdateContactRequest): Contact {
    val contact = contactRepository.findById(contactId)
      .orElseThrow { EntityNotFoundException("Contact with ID $contactId not found") }

    val changedContact = contact.copy(
      title = request.title,
      firstName = request.firstName,
      lastName = request.lastName,
      middleName = request.middleName,
      dateOfBirth = request.dateOfBirth,
      estimatedIsOverEighteen = request.estimatedIsOverEighteen,
    ).also {
      it.contactTypeCode = request.contactTypeCode
      it.placeOfBirth = request.placeOfBirth
      it.active = request.active
      it.suspended = request.suspended
      it.staffFlag = request.staffFlag
      it.deceasedFlag = request.deceasedFlag
      it.deceasedDate = request.deceasedDate
      it.coronerNumber = request.coronerNumber
      it.gender = request.gender
      it.maritalStatus = request.maritalStatus
      it.languageCode = request.languageCode
      it.nationalityCode = request.nationalityCode
      it.interpreterRequired = request.interpreterRequired
      it.comments = request.comments
      it.amendedBy = request.updatedBy
      it.amendedTime = request.updatedTime
    }

    return contactRepository.saveAndFlush(changedContact).syncFromEntityToModel()
  }
}
