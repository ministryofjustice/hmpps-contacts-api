package uk.gov.justice.digital.hmpps.hmppscontactsapi.facade

import org.springframework.stereotype.Service
import uk.gov.justice.digital.hmpps.hmppscontactsapi.model.request.CreateContactAddressPhoneRequest
import uk.gov.justice.digital.hmpps.hmppscontactsapi.model.request.UpdateContactAddressPhoneRequest
import uk.gov.justice.digital.hmpps.hmppscontactsapi.model.response.ContactAddressPhoneResponse
import uk.gov.justice.digital.hmpps.hmppscontactsapi.service.ContactAddressPhoneService
import uk.gov.justice.digital.hmpps.hmppscontactsapi.service.events.OutboundEvent
import uk.gov.justice.digital.hmpps.hmppscontactsapi.service.events.OutboundEventsService

@Service
class ContactAddressPhoneFacade(
  private val contactAddressPhoneService: ContactAddressPhoneService,
  private val outboundEventsService: OutboundEventsService,
) {

  fun create(contactId: Long, contactAddressId: Long, request: CreateContactAddressPhoneRequest): ContactAddressPhoneResponse {
    return contactAddressPhoneService.create(contactId, contactAddressId, request).also {
      outboundEventsService.send(
        outboundEvent = OutboundEvent.CONTACT_ADDRESS_PHONE_CREATED,
        identifier = it.contactAddressPhoneId,
        contactId = contactId,
      )
    }
  }

  fun update(contactId: Long, contactAddressPhoneId: Long, request: UpdateContactAddressPhoneRequest): ContactAddressPhoneResponse {
    return contactAddressPhoneService.update(contactId, contactAddressPhoneId, request).also {
      outboundEventsService.send(
        outboundEvent = OutboundEvent.CONTACT_ADDRESS_PHONE_UPDATED,
        identifier = it.contactAddressPhoneId,
        contactId = contactId,
      )
    }
  }

  fun delete(contactId: Long, contactAddressPhoneId: Long) {
    contactAddressPhoneService.delete(contactId, contactAddressPhoneId).also {
      outboundEventsService.send(
        outboundEvent = OutboundEvent.CONTACT_ADDRESS_PHONE_DELETED,
        identifier = it.contactAddressPhoneId,
        contactId = contactId,
      )
    }
  }

  fun get(contactId: Long, contactAddressPhoneId: Long) = contactAddressPhoneService.get(contactId, contactAddressPhoneId)
}