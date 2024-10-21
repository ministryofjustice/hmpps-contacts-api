package uk.gov.justice.digital.hmpps.hmppscontactsapi.integration.resource

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.springframework.http.MediaType
import org.springframework.test.context.TestPropertySource
import uk.gov.justice.digital.hmpps.hmppscontactsapi.client.prisonersearchapi.model.ErrorResponse
import uk.gov.justice.digital.hmpps.hmppscontactsapi.integration.IntegrationTestBase
import uk.gov.justice.digital.hmpps.hmppscontactsapi.model.request.CreateContactRequest
import uk.gov.justice.digital.hmpps.hmppscontactsapi.model.request.patch.PatchContactRequest
import uk.gov.justice.digital.hmpps.hmppscontactsapi.model.request.patch.PatchContactResponse
import uk.gov.justice.digital.hmpps.hmppscontactsapi.model.request.patch.util.Patchable
import uk.gov.justice.digital.hmpps.hmppscontactsapi.model.response.GetContactResponse
import java.time.LocalDate

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestPropertySource(properties = ["feature.event.contacts-api.prisoner-contact.amended=true"])
@TestPropertySource(properties = ["feature.events.sns.enabled=false"])
class PatchContactIntegrationTest : IntegrationTestBase() {

  lateinit var contact: GetContactResponse

  @BeforeAll
  fun setUpContact() {
    setupTestApiClient()
    val createContact = CreateContactRequest(
      title = "mr",
      lastName = "last",
      firstName = "first",
      middleNames = "middle",
      dateOfBirth = LocalDate.of(1982, 6, 15),
      createdBy = "created",
    )
    contact = testAPIClient.createAContact(createContact)
  }

  @Test
  fun `should return unauthorized if no token`() {
    webTestClient.patch()
      .uri("/contact/123456")
      .accept(MediaType.APPLICATION_JSON)
      .contentType(MediaType.APPLICATION_JSON)
      .bodyValue(aPatchContactRequest())
      .exchange()
      .expectStatus()
      .isUnauthorized
  }

  @Test
  fun `should return forbidden if no role`() {
    webTestClient.patch()
      .uri("/contact/123456")
      .accept(MediaType.APPLICATION_JSON)
      .contentType(MediaType.APPLICATION_JSON)
      .bodyValue(aPatchContactRequest())
      .headers(setAuthorisation())
      .exchange()
      .expectStatus()
      .isForbidden
  }

  @Test
  fun `should return forbidden if wrong role`() {
    webTestClient.patch()
      .uri("/contact/123456")
      .accept(MediaType.APPLICATION_JSON)
      .contentType(MediaType.APPLICATION_JSON)
      .bodyValue(aPatchContactRequest())
      .headers(setAuthorisation(roles = listOf("ROLE_WRONG")))
      .exchange()
      .expectStatus()
      .isForbidden
  }

  @Test
  fun `should successfully patch the contact with all fields`() {
    val request = aPatchContactRequest()
    val contactId = contact.id
    val contactReturnedOnPatch = testAPIClient.patchAContact(request, "/contact/$contactId")

    assertExpectedResponse(contactReturnedOnPatch, contact, request)
    assertSuccessfullyPatched(testAPIClient.getContact(contactId), contact, request)
  }

  @Test
  fun `should successfully patch the updated by field when its the only field in the request`() {
    val request = PatchContactRequest(
      languageCode = null,
      updatedBy = "JD000001",
    )
    val contactId = contact.id
    val contactReturnedOnPatch = testAPIClient.patchAContact(request, "/contact/$contactId")

    with(contactReturnedOnPatch) {
      assertThat(languageCode).isEqualTo(null)
      assertThat(amendedBy).isEqualTo("JD000001")
    }
    assertExpectedResponse(contactReturnedOnPatch, contact, request)
    assertSuccessfullyPatched(testAPIClient.getContact(contactId), contact, request)
  }

  @Test
  fun `should successfully patch the request`() {
    val request = PatchContactRequest(
      updatedBy = "JD000001",
    )
    val contactId = contact.id
    val contactReturnedOnPatch = testAPIClient.patchAContact(request, "/contact/$contactId")

    assertExpectedResponse(contactReturnedOnPatch, contact, request)
    assertSuccessfullyPatched(testAPIClient.getContact(contactId), contact, request)
  }

  @Test
  fun `should patch do not have amended by then return bad request`() {
    val response = webTestClient.patch()
      .uri("/contact/19")
      .accept(MediaType.APPLICATION_JSON)
      .contentType(MediaType.APPLICATION_JSON)
      .headers(setAuthorisation(roles = listOf("ROLE_CONTACTS_ADMIN")))
      .bodyValue(
        """{
                  }""",
      )
      .exchange()
      .expectStatus()
      .isBadRequest
      .expectHeader().contentType(MediaType.APPLICATION_JSON)
      .expectBody(ErrorResponse::class.java)
      .returnResult().responseBody!!
  }

  private fun assertExpectedResponse(
    patchResponse: PatchContactResponse,
    originalContact: GetContactResponse,
    patchRequest: PatchContactRequest,
  ) {
    with(patchResponse) {
      assertThat(title).isEqualTo(originalContact.title)
      assertThat(lastName).isEqualTo(originalContact.lastName)
      assertThat(firstName).isEqualTo(originalContact.firstName)
      assertThat(middleNames).isEqualTo(originalContact.middleNames)
      assertThat(dateOfBirth).isEqualTo(originalContact.dateOfBirth)
      assertThat(estimatedIsOverEighteen).isEqualTo(originalContact.estimatedIsOverEighteen)
      assertThat(deceasedFlag).isEqualTo(originalContact.isDeceased)
      assertThat(deceasedDate).isEqualTo(originalContact.deceasedDate)
      assertThat(interpreterRequired).isEqualTo(originalContact.interpreterRequired)
      assertThat(createdBy).isEqualTo("created")
      assertThat(createdTime).isInThePast()
      assertThat(amendedTime).isInThePast()
      // patched
      assertThat(languageCode).isEqualTo(patchRequest.languageCode?.get())
      assertThat(amendedBy).isEqualTo(patchRequest.updatedBy)
    }
  }

  private fun assertSuccessfullyPatched(
    patchResponse: GetContactResponse,
    originalContact: GetContactResponse,
    patchRequest: PatchContactRequest,
  ) {
    with(patchResponse) {
      assertThat(title).isEqualTo(originalContact.title)
      assertThat(lastName).isEqualTo(originalContact.lastName)
      assertThat(firstName).isEqualTo(originalContact.firstName)
      assertThat(middleNames).isEqualTo(originalContact.middleNames)
      assertThat(dateOfBirth).isEqualTo(originalContact.dateOfBirth)
      assertThat(estimatedIsOverEighteen).isEqualTo(originalContact.estimatedIsOverEighteen)
      assertThat(deceasedDate).isEqualTo(originalContact.deceasedDate)
      assertThat(interpreterRequired).isEqualTo(originalContact.interpreterRequired)
      assertThat(createdBy).isEqualTo("created")
      assertThat(createdTime).isInThePast()
      assertThat(languageCode).isEqualTo(patchRequest.languageCode?.get())
    }
  }

  private fun aPatchContactRequest() = PatchContactRequest(
    languageCode = Patchable.from("BEN"),
    updatedBy = "JD000001",
  )
}
