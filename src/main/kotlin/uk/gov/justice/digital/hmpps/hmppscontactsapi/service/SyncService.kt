package uk.gov.justice.digital.hmpps.hmppscontactsapi.service

import jakarta.persistence.EntityNotFoundException
import jakarta.validation.ValidationException
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import uk.gov.justice.digital.hmpps.hmppscontactsapi.mapping.toEntity
import uk.gov.justice.digital.hmpps.hmppscontactsapi.mapping.toModel
import uk.gov.justice.digital.hmpps.hmppscontactsapi.model.ContactAddress
import uk.gov.justice.digital.hmpps.hmppscontactsapi.model.request.CreateContactAddressRequest
import uk.gov.justice.digital.hmpps.hmppscontactsapi.model.request.UpdateContactAddressRequest
import uk.gov.justice.digital.hmpps.hmppscontactsapi.repository.ContactAddressRepository
import uk.gov.justice.digital.hmpps.hmppscontactsapi.repository.ContactRepository

/**
 * The SyncService contains methods to manage the synchronisation of data to/from NOMIS.
 *
 * The UI services should not use these endpoints as they may have relaxed validation to cater
 * for incomplete or poor quality data from NOMIS, and the data may contain reference data which is
 * useful only for the purpose of synchronisation with NOMIS e.g. CITY code, COUNTY code.
 *
 * Entities here can be individually created, amended or deleted in NOMIS, and these endpoints
 * support the delivery of that operation to this service.
 *
 * Whenever these entities are inserted, updated or deleted in the contacts service, a sync event is
 * published on the hmpps-domain-events-topic, and the Syscon sync service will call the GET
 * endpoint on our sync controller to retrieve the data.
 */

@Service
@Transactional
class SyncService(
  private val contactRepository: ContactRepository,
  private val contactAddressRepository: ContactAddressRepository,
) {
  companion object {
    private val logger = LoggerFactory.getLogger(this::class.java)
  }

  @Transactional(readOnly = true)
  fun getContactAddressById(contactAddressId: Long): ContactAddress {
    val contactAddress = contactAddressRepository.findById(contactAddressId)
      .orElseThrow { EntityNotFoundException("Contact address with ID $contactAddressId not found") }
    return contactAddress.toModel()
  }

  fun deleteContactAddressById(contactAddressId: Long) {
    contactAddressRepository.findById(contactAddressId)
      .orElseThrow { EntityNotFoundException("Contact address with ID $contactAddressId not found") }
    contactAddressRepository.deleteById(contactAddressId)
  }

  fun createContactAddress(request: CreateContactAddressRequest): ContactAddress {
    contactRepository.findById(request.contactId)
      .orElseThrow { EntityNotFoundException("Contact with ID ${request.contactId} not found") }
    return contactAddressRepository.saveAndFlush(request.toEntity()).toModel()
  }

  fun updateContactAddress(contactAddressId: Long, request: UpdateContactAddressRequest): ContactAddress {
    val contact = contactRepository.findById(request.contactId)
      .orElseThrow { EntityNotFoundException("Contact with ID ${request.contactId} not found") }

    val contactAddress = contactAddressRepository.findById(contactAddressId)
      .orElseThrow { EntityNotFoundException("Contact address with ID $contactAddressId not found") }

    if (contact.contactId != contactAddress.contactId) {
      logger.error("Contact address update specified for a contact not linked to this address")
      throw ValidationException("Contact ID ${contact.contactId} is not linked to the address ${contactAddress.contactAddressId}")
    }

    with(contactAddress) {
      this.primaryAddress = request.primaryAddress
      this.addressType = request.addressType
      this.flat = request.flat
      this.property = request.property
      this.street = request.street
      this.area = request.area
      this.cityCode = request.cityCode
      this.countyCode = request.countyCode
      this.countryCode = request.countryCode
      this.postCode = request.postcode
      this.verified = request.verified
      this.amendedBy = request.updatedBy
      this.amendedTime = request.updatedTime
    }

    return contactAddressRepository.saveAndFlush(contactAddress).toModel()
  }

  // TODO: Similar methods for the other entity types
  // TODO: Contact
  // TODO: ContactIdentity
  // TODO: ContactPhone
  // TODO: ContactEmail
  // TODO: ContactRestriction
  // TODO: PrisonerContact
  // TODO: PrisonerContactRestriction
}
