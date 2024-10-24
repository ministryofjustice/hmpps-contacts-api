package uk.gov.justice.digital.hmpps.hmppscontactsapi.facade

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito.mock
import org.mockito.Mockito.never
import org.mockito.Mockito.verify
import org.mockito.kotlin.any
import org.mockito.kotlin.whenever
import uk.gov.justice.digital.hmpps.hmppscontactsapi.helpers.createContactPhoneNumberDetails
import uk.gov.justice.digital.hmpps.hmppscontactsapi.model.request.CreatePhoneRequest
import uk.gov.justice.digital.hmpps.hmppscontactsapi.model.request.UpdatePhoneRequest
import uk.gov.justice.digital.hmpps.hmppscontactsapi.service.ContactPhoneService
import uk.gov.justice.digital.hmpps.hmppscontactsapi.service.events.OutboundEvent
import uk.gov.justice.digital.hmpps.hmppscontactsapi.service.events.OutboundEventsService

class ContactPhoneFacadeTest {

  private val phoneService: ContactPhoneService = mock()
  private val eventsService: OutboundEventsService = mock()
  private val facade = ContactPhoneFacade(phoneService, eventsService)

  private val contactId = 11L
  private val contactPhoneId = 99L
  private val contactPhoneDetails = createContactPhoneNumberDetails(id = contactPhoneId, contactId = contactId)

  @Test
  fun `should send event if create success`() {
    whenever(phoneService.create(any(), any())).thenReturn(contactPhoneDetails)
    whenever(eventsService.send(any(), any())).then {}
    val request = CreatePhoneRequest(
      phoneType = "MOB",
      phoneNumber = "0777777777",
      createdBy = "created",
    )

    val result = facade.create(contactId, request)

    assertThat(result).isEqualTo(contactPhoneDetails)
    verify(phoneService).create(contactId, request)
    verify(eventsService).send(OutboundEvent.CONTACT_PHONE_CREATED, contactPhoneId)
  }

  @Test
  fun `should not send event if create throws exception and propagate the exception`() {
    val expectedException = RuntimeException("Bang!")
    whenever(phoneService.create(any(), any())).thenThrow(expectedException)
    whenever(eventsService.send(any(), any())).then {}
    val request = CreatePhoneRequest(
      phoneType = "MOB",
      phoneNumber = "0777777777",
      createdBy = "created",
    )

    val exception = assertThrows<RuntimeException> {
      facade.create(contactId, request)
    }

    assertThat(exception).isEqualTo(exception)
    verify(phoneService).create(contactId, request)
    verify(eventsService, never()).send(any(), any())
  }

  @Test
  fun `should send event if update success`() {
    whenever(phoneService.update(any(), any(), any())).thenReturn(contactPhoneDetails)
    whenever(eventsService.send(any(), any())).then {}
    val request = UpdatePhoneRequest(
      phoneType = "MOB",
      phoneNumber = "0777777777",
      amendedBy = "amended",
    )

    val result = facade.update(contactId, contactPhoneId, request)

    assertThat(result).isEqualTo(contactPhoneDetails)
    verify(phoneService).update(contactId, contactPhoneId, request)
    verify(eventsService).send(OutboundEvent.CONTACT_PHONE_AMENDED, contactPhoneId)
  }

  @Test
  fun `should not send event if update throws exception and propagate the exception`() {
    val expectedException = RuntimeException("Bang!")
    whenever(phoneService.update(any(), any(), any())).thenThrow(expectedException)
    whenever(eventsService.send(any(), any())).then {}
    val request = UpdatePhoneRequest(
      phoneType = "MOB",
      phoneNumber = "0777777777",
      amendedBy = "amended",
    )

    val exception = assertThrows<RuntimeException> {
      facade.update(contactId, contactPhoneId, request)
    }

    assertThat(exception).isEqualTo(exception)
    verify(phoneService).update(contactId, contactPhoneId, request)
    verify(eventsService, never()).send(any(), any())
  }

  @Test
  fun `should send event if delete success`() {
    whenever(phoneService.delete(any(), any())).then {}
    whenever(eventsService.send(any(), any())).then {}

    facade.delete(contactId, contactPhoneId)

    verify(phoneService).delete(contactId, contactPhoneId)
    verify(eventsService).send(OutboundEvent.CONTACT_PHONE_DELETED, contactPhoneId)
  }

  @Test
  fun `should not send event if delete throws exception and propagate the exception`() {
    val expectedException = RuntimeException("Bang!")
    whenever(phoneService.delete(any(), any())).thenThrow(expectedException)
    whenever(eventsService.send(any(), any())).then {}

    val exception = assertThrows<RuntimeException> {
      facade.delete(contactId, contactPhoneId)
    }

    assertThat(exception).isEqualTo(exception)
    verify(phoneService).delete(contactId, contactPhoneId)
    verify(eventsService, never()).send(any(), any())
  }

  @Test
  fun `should not send no event on get`() {
    whenever(phoneService.get(any(), any())).thenReturn(contactPhoneDetails)

    val result = facade.get(contactId, contactPhoneId)

    assertThat(result).isEqualTo(contactPhoneDetails)
    verify(phoneService).get(contactId, contactPhoneId)
    verify(eventsService, never()).send(any(), any())
  }
}
