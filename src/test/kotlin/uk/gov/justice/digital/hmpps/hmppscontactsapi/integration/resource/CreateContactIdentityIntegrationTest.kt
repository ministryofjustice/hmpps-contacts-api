package uk.gov.justice.digital.hmpps.hmppscontactsapi.integration.resource

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.CsvSource
import org.junit.jupiter.params.provider.MethodSource
import org.springframework.http.MediaType
import uk.gov.justice.digital.hmpps.hmppscontactsapi.client.prisonersearchapi.model.ErrorResponse
import uk.gov.justice.digital.hmpps.hmppscontactsapi.integration.H2IntegrationTestBase
import uk.gov.justice.digital.hmpps.hmppscontactsapi.model.request.CreateContactRequest
import uk.gov.justice.digital.hmpps.hmppscontactsapi.model.request.CreateIdentityRequest
import uk.gov.justice.digital.hmpps.hmppscontactsapi.model.response.ContactIdentityDetails
import uk.gov.justice.digital.hmpps.hmppscontactsapi.service.events.ContactIdentityInfo
import uk.gov.justice.digital.hmpps.hmppscontactsapi.service.events.OutboundEvent

class CreateContactIdentityIntegrationTest : H2IntegrationTestBase() {
  private var savedContactId = 0L

  @BeforeEach
  fun initialiseData() {
    savedContactId = testAPIClient.createAContact(
      CreateContactRequest(
        lastName = "identity",
        firstName = "has",
        createdBy = "created",
      ),
    ).id
  }

  @Test
  fun `should return unauthorized if no token`() {
    webTestClient.post()
      .uri("/contact/$savedContactId/identity")
      .accept(MediaType.APPLICATION_JSON)
      .contentType(MediaType.APPLICATION_JSON)
      .bodyValue(aMinimalRequest())
      .exchange()
      .expectStatus()
      .isUnauthorized
  }

  @Test
  fun `should return forbidden if no role`() {
    webTestClient.post()
      .uri("/contact/$savedContactId/identity")
      .accept(MediaType.APPLICATION_JSON)
      .contentType(MediaType.APPLICATION_JSON)
      .bodyValue(aMinimalRequest())
      .headers(setAuthorisation())
      .exchange()
      .expectStatus()
      .isForbidden
  }

  @Test
  fun `should return forbidden if wrong role`() {
    webTestClient.post()
      .uri("/contact/$savedContactId/identity")
      .accept(MediaType.APPLICATION_JSON)
      .contentType(MediaType.APPLICATION_JSON)
      .bodyValue(aMinimalRequest())
      .headers(setAuthorisation(roles = listOf("ROLE_WRONG")))
      .exchange()
      .expectStatus()
      .isForbidden
  }

  @ParameterizedTest
  @CsvSource(
    value = [
      "identityType must not be null;{\"identityType\": null, \"identityValue\": \"0123456789\", \"createdBy\": \"created\"}",
      "identityType must not be null;{\"identityValue\": \"0123456789\", \"createdBy\": \"created\"}",
      "identityValue must not be null;{\"identityType\": \"MOB\", \"identityValue\": null, \"createdBy\": \"created\"}",
      "identityValue must not be null;{\"identityType\": \"MOB\", \"createdBy\": \"created\"}",
      "createdBy must not be null;{\"identityType\": \"MOB\", \"identityValue\": \"0123456789\", \"createdBy\": null}",
      "createdBy must not be null;{\"identityType\": \"MOB\", \"identityValue\": \"0123456789\"}",
    ],
    delimiter = ';',
  )
  fun `should return bad request if required fields are null`(expectedMessage: String, json: String) {
    val errors = webTestClient.post()
      .uri("/contact/$savedContactId/identity")
      .accept(MediaType.APPLICATION_JSON)
      .contentType(MediaType.APPLICATION_JSON)
      .headers(setAuthorisation(roles = listOf("ROLE_CONTACTS_ADMIN")))
      .bodyValue(json)
      .exchange()
      .expectStatus()
      .isBadRequest
      .expectHeader().contentType(MediaType.APPLICATION_JSON)
      .expectBody(ErrorResponse::class.java)
      .returnResult().responseBody!!

    assertThat(errors.userMessage).isEqualTo("Validation failure: $expectedMessage")
  }

  @ParameterizedTest
  @MethodSource("allFieldConstraintViolations")
  fun `should enforce field constraints`(expectedMessage: String, request: CreateIdentityRequest) {
    val errors = webTestClient.post()
      .uri("/contact/$savedContactId/identity")
      .accept(MediaType.APPLICATION_JSON)
      .contentType(MediaType.APPLICATION_JSON)
      .headers(setAuthorisation(roles = listOf("ROLE_CONTACTS_ADMIN")))
      .bodyValue(request)
      .exchange()
      .expectStatus()
      .isBadRequest
      .expectHeader().contentType(MediaType.APPLICATION_JSON)
      .expectBody(ErrorResponse::class.java)
      .returnResult().responseBody!!

    assertThat(errors.userMessage).isEqualTo("Validation failure(s): $expectedMessage")
  }

  @Test
  fun `should not create the identity if the type is not supported`() {
    val request = CreateIdentityRequest(
      identityType = "MACRO CARD",
      identityValue = "DL123456789",
      createdBy = "created",
    )

    val errors = webTestClient.post()
      .uri("/contact/$savedContactId/identity")
      .accept(MediaType.APPLICATION_JSON)
      .contentType(MediaType.APPLICATION_JSON)
      .headers(setAuthorisation(roles = listOf("ROLE_CONTACTS_ADMIN")))
      .bodyValue(request)
      .exchange()
      .expectStatus()
      .isBadRequest
      .expectHeader().contentType(MediaType.APPLICATION_JSON)
      .expectBody(ErrorResponse::class.java)
      .returnResult().responseBody!!

    assertThat(errors.userMessage).isEqualTo("Validation failure: Unsupported identity type (MACRO CARD)")
  }

  @Test
  fun `should not create the identity if the contact is not found`() {
    val request = aMinimalRequest()

    val errors = webTestClient.post()
      .uri("/contact/-321/identity")
      .accept(MediaType.APPLICATION_JSON)
      .contentType(MediaType.APPLICATION_JSON)
      .headers(setAuthorisation(roles = listOf("ROLE_CONTACTS_ADMIN")))
      .bodyValue(request)
      .exchange()
      .expectStatus()
      .isNotFound
      .expectHeader().contentType(MediaType.APPLICATION_JSON)
      .expectBody(ErrorResponse::class.java)
      .returnResult().responseBody!!

    assertThat(errors.userMessage).isEqualTo("Entity not found : Contact (-321) not found")
  }

  @Test
  fun `should create the identity with minimal fields`() {
    val request = aMinimalRequest()

    val created = testAPIClient.createAContactIdentity(savedContactId, request)

    assertEqualsExcludingTimestamps(created, request)
    stubEvents.assertHasEvent(OutboundEvent.CONTACT_IDENTITY_CREATED, ContactIdentityInfo(created.contactIdentityId))
  }

  @Test
  fun `should create the identity with all fields`() {
    val request = CreateIdentityRequest(
      identityType = "DRIVING_LIC",
      identityValue = "DL123456789",
      issuingAuthority = "DVLA",
      createdBy = "created",
    )

    val created = testAPIClient.createAContactIdentity(savedContactId, request)

    assertEqualsExcludingTimestamps(created, request)
    stubEvents.assertHasEvent(OutboundEvent.CONTACT_IDENTITY_CREATED, ContactIdentityInfo(created.contactIdentityId))
  }

  private fun assertEqualsExcludingTimestamps(identity: ContactIdentityDetails, request: CreateIdentityRequest) {
    with(identity) {
      assertThat(identityType).isEqualTo(request.identityType)
      assertThat(identityValue).isEqualTo(request.identityValue)
      assertThat(issuingAuthority).isEqualTo(request.issuingAuthority)
      assertThat(createdBy).isEqualTo(request.createdBy)
      assertThat(createdTime).isNotNull()
    }
  }

  companion object {
    @JvmStatic
    fun allFieldConstraintViolations(): List<Arguments> {
      return listOf(
        Arguments.of("identityType must be <= 20 characters", aMinimalRequest().copy(identityType = "".padStart(21))),
        Arguments.of("identityValue must be <= 20 characters", aMinimalRequest().copy(identityValue = "".padStart(21))),
        Arguments.of(
          "issuingAuthority must be <= 40 characters",
          aMinimalRequest().copy(issuingAuthority = "".padStart(41)),
        ),
        Arguments.of(
          "createdBy must be <= 100 characters",
          aMinimalRequest().copy(createdBy = "".padStart(101)),
        ),
      )
    }

    private fun aMinimalRequest() = CreateIdentityRequest(
      identityType = "DRIVING_LIC",
      identityValue = "DL123456789",
      createdBy = "created",
    )
  }
}