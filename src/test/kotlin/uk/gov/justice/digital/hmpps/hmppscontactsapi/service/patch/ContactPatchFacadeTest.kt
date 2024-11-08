
package uk.gov.justice.digital.hmpps.hmppscontactsapi.service.patch

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.kotlin.doNothing
import org.mockito.kotlin.whenever
import uk.gov.justice.digital.hmpps.hmppscontactsapi.facade.ContactPatchFacade
import uk.gov.justice.digital.hmpps.hmppscontactsapi.model.request.UpdateRelationshipRequest
import uk.gov.justice.digital.hmpps.hmppscontactsapi.model.request.patch.PatchContactRequest
import uk.gov.justice.digital.hmpps.hmppscontactsapi.model.request.patch.PatchContactResponse
import uk.gov.justice.digital.hmpps.hmppscontactsapi.service.ContactPatchService
import uk.gov.justice.digital.hmpps.hmppscontactsapi.service.ContactService
import uk.gov.justice.digital.hmpps.hmppscontactsapi.service.events.OutboundEvent
import uk.gov.justice.digital.hmpps.hmppscontactsapi.service.events.OutboundEventsService

class ContactPatchFacadeTest {

  private val contactPatchService: ContactPatchService = mock()
  private val contactService: ContactService = mock()

  private val outboundEventsService: OutboundEventsService = mock()

  private val contactPatchFacade = ContactPatchFacade(outboundEventsService, contactPatchService, contactService)

  @Test
  fun `patch should patch contact and send domain event`() {
    val contactId = 1L
    val request = mock(PatchContactRequest::class.java)
    val response = mock(PatchContactResponse::class.java)

    whenever(contactPatchService.patch(contactId, request)).thenReturn(response)

    val result = contactPatchFacade.patch(contactId, request)

    assertThat(response).isEqualTo(result)
    verify(contactPatchService).patch(contactId, request)
    verify(outboundEventsService).send(OutboundEvent.CONTACT_AMENDED, contactId)
  }

  @Test
  fun `patch relationship should send domain event`() {
    val contactId = 1L
    val prisonerContactId = 1L
    val request = mock(UpdateRelationshipRequest::class.java)

    doNothing().whenever(contactService).updateContactRelationship(contactId, prisonerContactId, request)

    contactPatchFacade.patchRelationship(contactId, prisonerContactId, request)

    verify(contactService).updateContactRelationship(contactId, prisonerContactId, request)
    verify(outboundEventsService).send(OutboundEvent.PRISONER_CONTACT_AMENDED, contactId)
  }
}