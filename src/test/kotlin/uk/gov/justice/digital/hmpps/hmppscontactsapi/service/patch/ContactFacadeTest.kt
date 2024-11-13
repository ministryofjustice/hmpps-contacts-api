package uk.gov.justice.digital.hmpps.hmppscontactsapi.service.patch

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.kotlin.any
import org.mockito.kotlin.doNothing
import org.mockito.kotlin.never
import org.mockito.kotlin.whenever
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import uk.gov.justice.digital.hmpps.hmppscontactsapi.facade.ContactFacade
import uk.gov.justice.digital.hmpps.hmppscontactsapi.helpers.createContactAddressDetails
import uk.gov.justice.digital.hmpps.hmppscontactsapi.helpers.createContactEmailDetails
import uk.gov.justice.digital.hmpps.hmppscontactsapi.helpers.createContactIdentityDetails
import uk.gov.justice.digital.hmpps.hmppscontactsapi.helpers.createContactPhoneNumberDetails
import uk.gov.justice.digital.hmpps.hmppscontactsapi.model.ContactCreationResult
import uk.gov.justice.digital.hmpps.hmppscontactsapi.model.request.AddContactRelationshipRequest
import uk.gov.justice.digital.hmpps.hmppscontactsapi.model.request.ContactRelationship
import uk.gov.justice.digital.hmpps.hmppscontactsapi.model.request.ContactSearchRequest
import uk.gov.justice.digital.hmpps.hmppscontactsapi.model.request.CreateContactRequest
import uk.gov.justice.digital.hmpps.hmppscontactsapi.model.request.EstimatedIsOverEighteen
import uk.gov.justice.digital.hmpps.hmppscontactsapi.model.request.UpdateRelationshipRequest
import uk.gov.justice.digital.hmpps.hmppscontactsapi.model.request.patch.PatchContactRequest
import uk.gov.justice.digital.hmpps.hmppscontactsapi.model.request.patch.PatchContactResponse
import uk.gov.justice.digital.hmpps.hmppscontactsapi.model.response.ContactDetails
import uk.gov.justice.digital.hmpps.hmppscontactsapi.model.response.ContactSearchResultItem
import uk.gov.justice.digital.hmpps.hmppscontactsapi.service.ContactPatchService
import uk.gov.justice.digital.hmpps.hmppscontactsapi.service.ContactService
import uk.gov.justice.digital.hmpps.hmppscontactsapi.service.events.OutboundEvent
import uk.gov.justice.digital.hmpps.hmppscontactsapi.service.events.OutboundEventsService
import java.time.LocalDateTime

class ContactFacadeTest {

  private val outboundEventsService: OutboundEventsService = mock()
  private val contactPatchService: ContactPatchService = mock()
  private val contactService: ContactService = mock()

  private val contactFacade = ContactFacade(outboundEventsService, contactPatchService, contactService)

  @Test
  fun `patch should patch contact and send domain event`() {
    val contactId = 1L
    val request = mock(PatchContactRequest::class.java)
    val response = mock(PatchContactResponse::class.java)

    whenever(contactPatchService.patch(contactId, request)).thenReturn(response)

    val result = contactFacade.patch(contactId, request)

    assertThat(response).isEqualTo(result)
    verify(contactPatchService).patch(contactId, request)
    verify(outboundEventsService).send(OutboundEvent.CONTACT_AMENDED, contactId)
  }

  @Test
  fun `create contact without relationship should send contact domain event only`() {
    val request = CreateContactRequest(
      lastName = "last",
      firstName = "first",
      createdBy = "created",
    )
    val createdContact = aContactDetails().copy(id = 98765)
    whenever(contactService.createContact(request)).thenReturn(ContactCreationResult(createdContact, null))

    val result = contactFacade.createContact(request)

    assertThat(result).isEqualTo(createdContact)
    verify(outboundEventsService).send(OutboundEvent.CONTACT_CREATED, 98765)
  }

  @Test
  fun `create contact with relationship should send contact and prisoner contact domain events`() {
    val request = CreateContactRequest(
      lastName = "last",
      firstName = "first",
      relationship = ContactRelationship(
        prisonerNumber = "A1234BC",
        relationshipCode = "FRI",
        isNextOfKin = false,
        isEmergencyContact = false,
        comments = null,
      ),
      createdBy = "created",
    )
    val createdContact = aContactDetails().copy(id = 98765)
    whenever(contactService.createContact(request)).thenReturn(ContactCreationResult(createdContact, 123456))

    val result = contactFacade.createContact(request)

    assertThat(result).isEqualTo(createdContact)
    verify(outboundEventsService).send(OutboundEvent.CONTACT_CREATED, 98765)
    verify(outboundEventsService).send(OutboundEvent.PRISONER_CONTACT_CREATED, 123456)
  }

  @Test
  fun `create contact relationship should send prisoner contact domain event`() {
    val request = AddContactRelationshipRequest(
      ContactRelationship(
        prisonerNumber = "A1234BC",
        relationshipCode = "FRI",
        isNextOfKin = false,
        isEmergencyContact = false,
        comments = null,
      ),
      "user",
    )
    val prisonerContactId = 123456L

    whenever(contactService.addContactRelationship(99, request)).thenReturn(prisonerContactId)

    contactFacade.addContactRelationship(99, request)

    verify(outboundEventsService).send(OutboundEvent.PRISONER_CONTACT_CREATED, prisonerContactId)
  }

  @Test
  fun `search should send no domain event`() {
    val pageable = Pageable.unpaged()
    val request = ContactSearchRequest(lastName = "foo", firstName = null, middleNames = null, dateOfBirth = null)
    val result = PageImpl<ContactSearchResultItem>(listOf())

    whenever(contactService.searchContacts(any(), any())).thenReturn(result)

    assertThat(contactFacade.searchContacts(pageable, request)).isEqualTo(result)

    verify(outboundEventsService, never()).send(any(), any())
  }

  @Test
  fun `get by id should send no domain event`() {
    val expectedContact = aContactDetails()

    whenever(contactService.getContact(any())).thenReturn(expectedContact)

    assertThat(contactFacade.getContact(99L)).isEqualTo(expectedContact)

    verify(outboundEventsService, never()).send(any(), any())
  }

  @Test
  fun `patch relationship should send domain event`() {
    val contactId = 1L
    val prisonerContactId = 1L
    val request = mock(UpdateRelationshipRequest::class.java)

    doNothing().whenever(contactService).updateContactRelationship(contactId, prisonerContactId, request)

    contactFacade.patchRelationship(contactId, prisonerContactId, request)

    verify(contactService).updateContactRelationship(contactId, prisonerContactId, request)
    verify(outboundEventsService).send(OutboundEvent.PRISONER_CONTACT_AMENDED, contactId)
  }

  private fun aContactDetails() = ContactDetails(
    id = 99,
    lastName = "Last",
    firstName = "First",
    estimatedIsOverEighteen = EstimatedIsOverEighteen.DO_NOT_KNOW,
    isDeceased = false,
    deceasedDate = null,
    languageCode = null,
    languageDescription = null,
    interpreterRequired = false,
    addresses = listOf(createContactAddressDetails()),
    phoneNumbers = listOf(createContactPhoneNumberDetails()),
    emailAddresses = listOf(createContactEmailDetails()),
    identities = listOf(createContactIdentityDetails()),
    domesticStatusCode = "S",
    domesticStatusDescription = "Single",
    gender = null,
    genderDescription = null,
    createdBy = "user",
    createdTime = LocalDateTime.now(),
  )
}