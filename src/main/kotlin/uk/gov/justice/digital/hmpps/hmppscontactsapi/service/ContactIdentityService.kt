package uk.gov.justice.digital.hmpps.hmppscontactsapi.service

import jakarta.persistence.EntityNotFoundException
import jakarta.transaction.Transactional
import jakarta.validation.ValidationException
import org.springframework.stereotype.Service
import uk.gov.justice.digital.hmpps.hmppscontactsapi.entity.ContactIdentityEntity
import uk.gov.justice.digital.hmpps.hmppscontactsapi.mapping.toModel
import uk.gov.justice.digital.hmpps.hmppscontactsapi.model.request.CreateIdentityRequest
import uk.gov.justice.digital.hmpps.hmppscontactsapi.model.response.ContactIdentityDetails
import uk.gov.justice.digital.hmpps.hmppscontactsapi.model.response.ReferenceCode
import uk.gov.justice.digital.hmpps.hmppscontactsapi.repository.ContactIdentityDetailsRepository
import uk.gov.justice.digital.hmpps.hmppscontactsapi.repository.ContactIdentityRepository
import uk.gov.justice.digital.hmpps.hmppscontactsapi.repository.ContactRepository

@Service
class ContactIdentityService(
  private val contactRepository: ContactRepository,
  private val contactIdentityRepository: ContactIdentityRepository,
  private val contactIdentityDetailsRepository: ContactIdentityDetailsRepository,
  private val referenceCodeService: ReferenceCodeService,
) {

  @Transactional
  fun create(contactId: Long, request: CreateIdentityRequest): ContactIdentityDetails {
    validateContactExists(contactId)
    val type = validateIdentityType(request.identityType)
    val created = contactIdentityRepository.saveAndFlush(
      ContactIdentityEntity(
        contactIdentityId = 0,
        contactId = contactId,
        identityType = request.identityType,
        identityValue = request.identityValue,
        issuingAuthority = request.issuingAuthority,
        createdBy = request.createdBy,
      ),
    )
    return created.toDomainWithType(type)
  }

  fun get(contactId: Long, contactIdentityId: Long): ContactIdentityDetails? {
    return contactIdentityDetailsRepository.findByContactIdAndContactIdentityId(contactId, contactIdentityId)?.toModel()
  }

  private fun validateIdentityType(identityType: String): ReferenceCode {
    val type = referenceCodeService.getReferenceDataByGroupAndCode("ID_TYPE", identityType)
      ?: throw ValidationException("Unsupported identity type ($identityType)")
    return type
  }

  private fun validateContactExists(contactId: Long) {
    contactRepository.findById(contactId)
      .orElseThrow { EntityNotFoundException("Contact ($contactId) not found") }
  }

  private fun ContactIdentityEntity.toDomainWithType(
    type: ReferenceCode,
  ) = ContactIdentityDetails(
    this.contactIdentityId,
    this.contactId,
    this.identityType,
    type.description,
    this.identityValue,
    this.issuingAuthority,
    this.createdBy,
    this.createdTime,
    this.amendedBy,
    this.amendedTime,
  )
}