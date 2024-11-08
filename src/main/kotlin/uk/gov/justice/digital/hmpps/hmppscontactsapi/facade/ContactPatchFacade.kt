
package uk.gov.justice.digital.hmpps.hmppscontactsapi.facade

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import uk.gov.justice.digital.hmpps.hmppscontactsapi.model.request.UpdateRelationshipRequest
import uk.gov.justice.digital.hmpps.hmppscontactsapi.model.request.patch.PatchContactRequest
import uk.gov.justice.digital.hmpps.hmppscontactsapi.model.request.patch.PatchContactResponse
import uk.gov.justice.digital.hmpps.hmppscontactsapi.service.ContactPatchService
import uk.gov.justice.digital.hmpps.hmppscontactsapi.service.ContactService
import uk.gov.justice.digital.hmpps.hmppscontactsapi.service.events.OutboundEvent
import uk.gov.justice.digital.hmpps.hmppscontactsapi.service.events.OutboundEventsService

@Service
class ContactPatchFacade(
  private val outboundEventsService: OutboundEventsService,
  private val contactPatchService: ContactPatchService,
  val contactService: ContactService,
) {
  companion object {
    private val logger = LoggerFactory.getLogger(this::class.java)
  }

  fun patch(id: Long, request: PatchContactRequest): PatchContactResponse {
    return contactPatchService.patch(id, request)
      .also {
        logger.info("Send patch domain event to {} {} ", OutboundEvent.CONTACT_AMENDED, id)
        outboundEventsService.send(OutboundEvent.CONTACT_AMENDED, id)
      }
  }

  fun patchRelationship(id: Long, prisonerContactId: Long, request: UpdateRelationshipRequest) {
    return contactService.updateContactRelationship(id, prisonerContactId, request)
      .also {
        logger.info("Send patch relationship domain event to {} {} ", OutboundEvent.CONTACT_AMENDED, id)
        outboundEventsService.send(OutboundEvent.PRISONER_CONTACT_AMENDED, id)
      }
  }
}