package uk.gov.justice.digital.hmpps.hmppscontactsapi.service

import jakarta.persistence.EntityNotFoundException
import jakarta.validation.ValidationException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import uk.gov.justice.digital.hmpps.hmppscontactsapi.entity.ContactAddressEntity
import uk.gov.justice.digital.hmpps.hmppscontactsapi.entity.ContactAddressPhoneEntity
import uk.gov.justice.digital.hmpps.hmppscontactsapi.entity.ContactEntity
import uk.gov.justice.digital.hmpps.hmppscontactsapi.entity.ContactPhoneEntity
import uk.gov.justice.digital.hmpps.hmppscontactsapi.model.request.CreateContactAddressPhoneRequest
import uk.gov.justice.digital.hmpps.hmppscontactsapi.model.request.UpdateContactAddressPhoneRequest
import uk.gov.justice.digital.hmpps.hmppscontactsapi.model.response.ContactAddressPhoneResponse
import uk.gov.justice.digital.hmpps.hmppscontactsapi.model.response.ReferenceCode
import uk.gov.justice.digital.hmpps.hmppscontactsapi.repository.ContactAddressPhoneRepository
import uk.gov.justice.digital.hmpps.hmppscontactsapi.repository.ContactAddressRepository
import uk.gov.justice.digital.hmpps.hmppscontactsapi.repository.ContactPhoneRepository
import uk.gov.justice.digital.hmpps.hmppscontactsapi.repository.ContactRepository
import java.time.LocalDateTime

@Service
@Transactional
class ContactAddressPhoneService(
  private val contactRepository: ContactRepository,
  private val contactPhoneRepository: ContactPhoneRepository,
  private val contactAddressRepository: ContactAddressRepository,
  private val contactAddressPhoneRepository: ContactAddressPhoneRepository,
  private val referenceCodeService: ReferenceCodeService,
) {

  fun create(contactId: Long, contactAddressId: Long, request: CreateContactAddressPhoneRequest): ContactAddressPhoneResponse {
    // Validate the request
    validateContactExists(contactId)
    validateContactAddressExists(contactAddressId)
    validatePhoneNumber(request.phoneNumber)
    val phoneTypeReference = validatePhoneType(request.phoneType)

    // Save the phone number
    val createdPhone = contactPhoneRepository.saveAndFlush(
      ContactPhoneEntity(
        contactPhoneId = 0,
        contactId = contactId,
        phoneType = request.phoneType,
        phoneNumber = request.phoneNumber,
        extNumber = request.extNumber,
        createdBy = request.createdBy,
      ),
    )

    // Save the contact address phone
    val createdAddressPhone = contactAddressPhoneRepository.saveAndFlush(
      ContactAddressPhoneEntity(
        contactAddressPhoneId = 0,
        contactId = contactId,
        contactAddressId = contactAddressId,
        contactPhoneId = createdPhone.contactPhoneId,
        createdBy = request.createdBy,
      ),
    )

    return createdAddressPhone.toModel(createdPhone, phoneTypeReference)
  }

  @Transactional(readOnly = true)
  fun get(contactId: Long, contactAddressPhoneId: Long): ContactAddressPhoneResponse {
    val addressPhone = validateContactAddressPhoneExists(contactAddressPhoneId)
    val phone = validatePhoneExists(addressPhone.contactPhoneId)
    val phoneTypeReference = validatePhoneType(phone.phoneType)
    return addressPhone.toModel(phone, phoneTypeReference)
  }

  fun update(contactId: Long, contactAddressPhoneId: Long, request: UpdateContactAddressPhoneRequest): ContactAddressPhoneResponse {
    validateContactExists(contactId)
    val existing = validateContactAddressPhoneExists(contactAddressPhoneId)
    val phone = validatePhoneExists(existing.contactPhoneId)
    val newPhoneType = validatePhoneType(request.phoneType)

    validatePhoneNumber(request.phoneNumber)

    val updatingPhone = phone.copy(
      phoneType = request.phoneType,
      phoneNumber = request.phoneNumber,
      extNumber = request.extNumber,
      amendedBy = request.updatedBy,
      amendedTime = LocalDateTime.now(),
    )

    val updatedPhone = contactPhoneRepository.saveAndFlush(updatingPhone)

    val updatingContactAddressPhone = existing.copy(
      amendedBy = request.updatedBy,
      amendedTime = LocalDateTime.now(),
    )

    val updatedContactAddressPhone = contactAddressPhoneRepository.saveAndFlush(updatingContactAddressPhone)

    return updatedContactAddressPhone.toModel(updatedPhone, newPhoneType)
  }

  fun delete(contactId: Long, contactAddressPhoneId: Long): ContactAddressPhoneResponse {
    validateContactExists(contactId)
    val existingContactAddressPhone = validateContactAddressPhoneExists(contactAddressPhoneId)
    val existingPhone = validatePhoneExists(contactAddressPhoneId)
    val phoneTypeRef = validatePhoneType(existingPhone.phoneType)

    contactAddressPhoneRepository.deleteById(contactAddressPhoneId)
    contactPhoneRepository.deleteById(existingPhone.contactPhoneId)

    return existingContactAddressPhone.toModel(existingPhone, phoneTypeRef)
  }

  private fun validatePhoneNumber(phoneNumber: String) {
    if (!phoneNumber.matches(Regex("\\+?[\\d\\s()]+"))) {
      throw ValidationException("Phone number invalid, it can only contain numbers, () and whitespace with an optional + at the start")
    }
  }

  private fun validatePhoneType(phoneType: String): ReferenceCode =
    referenceCodeService.getReferenceDataByGroupAndCode("PHONE_TYPE", phoneType)
      ?: throw ValidationException("Unsupported phone type ($phoneType)")

  private fun validatePhoneExists(contactPhoneId: Long): ContactPhoneEntity =
    contactPhoneRepository.findById(contactPhoneId)
      .orElseThrow { EntityNotFoundException("Contact phone ($contactPhoneId) not found") }

  private fun validateContactExists(contactId: Long): ContactEntity =
    contactRepository.findById(contactId).orElseThrow { EntityNotFoundException("Contact ($contactId) not found") }

  private fun validateContactAddressExists(contactAddressId: Long): ContactAddressEntity =
    contactAddressRepository.findById(contactAddressId)
      .orElseThrow { EntityNotFoundException("Contact address ($contactAddressId) not found") }

  private fun validateContactAddressPhoneExists(contactAddressPhoneId: Long): ContactAddressPhoneEntity =
    contactAddressPhoneRepository.findById(contactAddressPhoneId)
      .orElseThrow { EntityNotFoundException("Contact address phone ($contactAddressPhoneId) not found") }

  private fun ContactAddressPhoneEntity.toModel(phone: ContactPhoneEntity, type: ReferenceCode) =
    ContactAddressPhoneResponse(
      contactAddressPhoneId = this.contactAddressPhoneId,
      contactPhoneId = this.contactPhoneId,
      contactAddressId = this.contactAddressId,
      contactId = this.contactId,
      phoneType = phone.phoneType,
      phoneTypeDescription = type.description,
      phoneNumber = phone.phoneNumber,
      extNumber = phone.extNumber,
      createdBy = this.createdBy,
      createdTime = this.createdTime,
      updatedBy = this.amendedBy,
      updatedTime = this.amendedTime,
    )
}
