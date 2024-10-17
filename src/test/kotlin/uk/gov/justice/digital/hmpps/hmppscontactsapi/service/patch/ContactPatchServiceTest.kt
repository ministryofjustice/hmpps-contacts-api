package uk.gov.justice.digital.hmpps.hmppscontactsapi.service.patch

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.kotlin.whenever
import uk.gov.justice.digital.hmpps.hmppscontactsapi.model.request.patch.PatchContactRequest
import uk.gov.justice.digital.hmpps.hmppscontactsapi.model.request.patch.PatchContactResponse
import uk.gov.justice.digital.hmpps.hmppscontactsapi.service.ContactService
import uk.gov.justice.digital.hmpps.hmppscontactsapi.service.events.OutboundEvent
import uk.gov.justice.digital.hmpps.hmppscontactsapi.service.events.OutboundEventsService

class ContactPatchServiceTest {

  private val contactService: ContactService = mock()

  private val outboundEventsService: OutboundEventsService = mock()

  private val contactPatchService = ContactPatchService(outboundEventsService, contactService)

  @Test
  fun `patch should patch contact and send domain event`() {
    val contactId = 1L
    val request = mock(PatchContactRequest::class.java)
    val response = mock(PatchContactResponse::class.java)

    whenever(contactService.patch(contactId, request)).thenReturn(response)

    val result = contactPatchService.patch(contactId, request)

    assertThat(response).isEqualTo(result)
    verify(contactService).patch(contactId, request)
    verify(outboundEventsService).send(OutboundEvent.PRISONER_CONTACT_AMENDED, contactId)
  }
}
