package uk.gov.justice.digital.hmpps.hmppscontactsapi.facade

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import uk.gov.justice.digital.hmpps.hmppscontactsapi.model.request.sync.CreateContactAddressRequest
import uk.gov.justice.digital.hmpps.hmppscontactsapi.model.request.sync.CreateContactIdentityRequest
import uk.gov.justice.digital.hmpps.hmppscontactsapi.model.request.sync.CreateContactRestrictionRequest
import uk.gov.justice.digital.hmpps.hmppscontactsapi.model.request.sync.CreatePrisonerContactRequest
import uk.gov.justice.digital.hmpps.hmppscontactsapi.model.request.sync.CreatePrisonerContactRestrictionRequest
import uk.gov.justice.digital.hmpps.hmppscontactsapi.model.request.sync.SyncCreateContactEmailRequest
import uk.gov.justice.digital.hmpps.hmppscontactsapi.model.request.sync.SyncCreateContactPhoneRequest
import uk.gov.justice.digital.hmpps.hmppscontactsapi.model.request.sync.SyncCreateContactRequest
import uk.gov.justice.digital.hmpps.hmppscontactsapi.model.request.sync.SyncUpdateContactEmailRequest
import uk.gov.justice.digital.hmpps.hmppscontactsapi.model.request.sync.SyncUpdateContactPhoneRequest
import uk.gov.justice.digital.hmpps.hmppscontactsapi.model.request.sync.UpdateContactAddressRequest
import uk.gov.justice.digital.hmpps.hmppscontactsapi.model.request.sync.UpdateContactIdentityRequest
import uk.gov.justice.digital.hmpps.hmppscontactsapi.model.request.sync.UpdateContactRequest
import uk.gov.justice.digital.hmpps.hmppscontactsapi.model.request.sync.UpdateContactRestrictionRequest
import uk.gov.justice.digital.hmpps.hmppscontactsapi.model.request.sync.UpdatePrisonerContactRequest
import uk.gov.justice.digital.hmpps.hmppscontactsapi.model.request.sync.UpdatePrisonerContactRestrictionRequest
import uk.gov.justice.digital.hmpps.hmppscontactsapi.service.events.OutboundEventsService
import uk.gov.justice.digital.hmpps.hmppscontactsapi.service.sync.SyncContactAddressService
import uk.gov.justice.digital.hmpps.hmppscontactsapi.service.sync.SyncContactEmailService
import uk.gov.justice.digital.hmpps.hmppscontactsapi.service.sync.SyncContactIdentityService
import uk.gov.justice.digital.hmpps.hmppscontactsapi.service.sync.SyncContactPhoneService
import uk.gov.justice.digital.hmpps.hmppscontactsapi.service.sync.SyncContactRestrictionService
import uk.gov.justice.digital.hmpps.hmppscontactsapi.service.sync.SyncContactService
import uk.gov.justice.digital.hmpps.hmppscontactsapi.service.sync.SyncPrisonerContactRestrictionService
import uk.gov.justice.digital.hmpps.hmppscontactsapi.service.sync.SyncPrisonerContactService

/**
 * This class is a facade over the sync services as a thin layer
 * which is called by the sync controllers and in-turn calls the sync
 * service methods.
 *
 * Each method provides two purposes:
 * - To call the underlying sync services and apply the changes in a transactional method.
 * - To generate a domain event to inform subscribed services what has happened.
 *
 * All events generated as a result of a sync operation should generate domain events with the
 * additionalInformation.source = "NOMIS", which indicates that the actual source of the change
 * was in NOMIS.
 *
 * This is important as the Syscon sync service will ignore domain events with
 * a source of NOMIS but will action those with a source of DPS (changes originating within
 * this service).
 */
@Service
class SyncFacade(
  private val syncContactService: SyncContactService,
  private val syncContactPhoneService: SyncContactPhoneService,
  private val syncContactAddressService: SyncContactAddressService,
  private val syncContactEmailService: SyncContactEmailService,
  private val syncContactIdentityService: SyncContactIdentityService,
  private val syncContactRestrictionService: SyncContactRestrictionService,
  private val syncPrisonerContactService: SyncPrisonerContactService,
  private val syncPrisonerContactRestrictionService: SyncPrisonerContactRestrictionService,
  private val outboundEventsService: OutboundEventsService,
) {
  companion object {
    private val logger = LoggerFactory.getLogger(this::class.java)
  }

  // ================================================================
  //  Contact
  // ================================================================

  fun getContactById(contactId: Long) =
    syncContactService.getContactById(contactId)

  fun createContact(request: SyncCreateContactRequest) =
    syncContactService.createContact(request)
      .also {
        logger.info("Create contact domain event")
      }

  fun updateContact(contactId: Long, request: UpdateContactRequest) =
    syncContactService.updateContact(contactId, request)
      .also {
        logger.info("Update contact domain event")
      }

  fun deleteContact(contactId: Long) =
    syncContactService.deleteContact(contactId)
      .also {
        logger.info("Delete contact domain event")
      }

  // ================================================================
  //  Contact Phone
  // ================================================================

  fun getContactPhoneById(contactPhoneId: Long) =
    syncContactPhoneService.getContactPhoneById(contactPhoneId)

  fun createContactPhone(request: SyncCreateContactPhoneRequest) =
    syncContactPhoneService.createContactPhone(request)
      .also {
        logger.info("Create contact phone domain event")
      }

  fun updateContactPhone(contactPhoneId: Long, request: SyncUpdateContactPhoneRequest) =
    syncContactPhoneService.updateContactPhone(contactPhoneId, request)
      .also {
        logger.info("Update contact phone domain event")
      }

  fun deleteContactPhone(contactPhoneId: Long) =
    syncContactPhoneService.deleteContactPhone(contactPhoneId)
      .also {
        logger.info("Delete contact phone domain event")
      }

  // ================================================================
  //  Contact Email
  // ================================================================

  fun getContactEmailById(contactEmailId: Long) =
    syncContactEmailService.getContactEmailById(contactEmailId)

  fun createContactEmail(request: SyncCreateContactEmailRequest) =
    syncContactEmailService.createContactEmail(request)
      .also {
        logger.info("Create contact email domain event")
      }

  fun updateContactEmail(contactEmailId: Long, request: SyncUpdateContactEmailRequest) =
    syncContactEmailService.updateContactEmail(contactEmailId, request)
      .also {
        logger.info("Update contact email domain event")
      }

  fun deleteContactEmail(contactEmailId: Long) =
    syncContactEmailService.deleteContactEmail(contactEmailId)
      .also {
        logger.info("Delete email contact domain event")
      }

  // ================================================================
  //  Contact Identity
  // ================================================================

  fun getContactIdentityById(contactIdentityId: Long) =
    syncContactIdentityService.getContactIdentityById(contactIdentityId)

  fun createContactIdentity(request: CreateContactIdentityRequest) =
    syncContactIdentityService.createContactIdentity(request)
      .also {
        logger.info("Create contact identity domain event")
      }

  fun updateContactIdentity(contactIdentityId: Long, request: UpdateContactIdentityRequest) =
    syncContactIdentityService.updateContactIdentity(contactIdentityId, request)
      .also {
        logger.info("Update contact identity domain event")
      }

  fun deleteContactIdentity(contactIdentityId: Long) =
    syncContactIdentityService.deleteContactIdentity(contactIdentityId)
      .also {
        logger.info("Delete contact identity domain event")
      }

  // ================================================================
  //  Contact Restriction
  // ================================================================

  fun getContactRestrictionById(contactRestrictionId: Long) =
    syncContactRestrictionService.getContactRestrictionById(contactRestrictionId)

  fun createContactRestriction(request: CreateContactRestrictionRequest) =
    syncContactRestrictionService.createContactRestriction(request)
      .also {
        logger.info("Create contact restriction domain event")
      }

  fun updateContactRestriction(contactRestrictionId: Long, request: UpdateContactRestrictionRequest) =
    syncContactRestrictionService.updateContactRestriction(contactRestrictionId, request)
      .also {
        logger.info("Update contact restriction domain event")
      }

  fun deleteContactRestriction(contactRestrictionId: Long) =
    syncContactRestrictionService.deleteContactRestriction(contactRestrictionId)
      .also {
        logger.info("Delete contact restriction domain event")
      }

  // ================================================================
  //  Contact Address
  // ================================================================

  fun getContactAddressById(contactAddressId: Long) =
    syncContactAddressService.getContactAddressById(contactAddressId)

  fun createContactAddress(request: CreateContactAddressRequest) =
    syncContactAddressService.createContactAddress(request)
      .also {
        logger.info("Create contact address domain event")
      }

  fun updateContactAddress(contactAddressId: Long, request: UpdateContactAddressRequest) =
    syncContactAddressService.updateContactAddress(contactAddressId, request)
      .also {
        logger.info("Update contact address domain event")
      }

  fun deleteContactAddress(contactAddressId: Long) =
    syncContactAddressService.deleteContactAddress(contactAddressId)
      .also {
        logger.info("Delete contact address domain event")
      }

  // ================================================================
  //  Prisoner Contact
  // ================================================================

  fun getPrisonerContactById(prisonerContactId: Long) =
    syncPrisonerContactService.getPrisonerContactById(prisonerContactId)

  fun createPrisonerContact(request: CreatePrisonerContactRequest) =
    syncPrisonerContactService.createPrisonerContact(request)
      .also {
        logger.info("Create prisoner contact domain event")
      }

  fun updatePrisonerContact(prisonerContactId: Long, request: UpdatePrisonerContactRequest) =
    syncPrisonerContactService.updatePrisonerContact(prisonerContactId, request)
      .also {
        logger.info("Update prisoner contact domain event")
      }

  fun deletePrisonerContact(prisonerContactId: Long) =
    syncPrisonerContactService.deletePrisonerContact(prisonerContactId)
      .also {
        logger.info("Delete prisoner contact domain event")
      }

  // ================================================================
  //  Prisoner Contact Restriction
  // ================================================================

  fun getPrisonerContactRestrictionById(prisonerContactRestrictionId: Long) =
    syncPrisonerContactRestrictionService.getPrisonerContactRestrictionById(prisonerContactRestrictionId)

  fun createPrisonerContactRestriction(request: CreatePrisonerContactRestrictionRequest) =
    syncPrisonerContactRestrictionService.createPrisonerContactRestriction(request)
      .also {
        logger.info("Create prisoner contact restriction domain event")
      }

  fun updatePrisonerContactRestriction(prisonerContactRestrictionId: Long, request: UpdatePrisonerContactRestrictionRequest) =
    syncPrisonerContactRestrictionService.updatePrisonerContactRestriction(prisonerContactRestrictionId, request)
      .also {
        logger.info("Update prisoner contact restriction domain event")
      }

  fun deletePrisonerContactRestriction(prisonerContactRestrictionId: Long) =
    syncPrisonerContactRestrictionService.deletePrisonerContactRestriction(prisonerContactRestrictionId)
      .also {
        logger.info("Delete prisoner contact restriction domain event")
      }
}
