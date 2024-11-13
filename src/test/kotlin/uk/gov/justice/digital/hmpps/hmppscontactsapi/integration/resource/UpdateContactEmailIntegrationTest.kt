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
import uk.gov.justice.digital.hmpps.hmppscontactsapi.model.request.CreateEmailRequest
import uk.gov.justice.digital.hmpps.hmppscontactsapi.model.request.UpdateEmailRequest
import uk.gov.justice.digital.hmpps.hmppscontactsapi.service.events.ContactEmailInfo
import uk.gov.justice.digital.hmpps.hmppscontactsapi.service.events.OutboundEvent

class UpdateContactEmailIntegrationTest : H2IntegrationTestBase() {
  private var savedContactId = 0L
  private var savedContactEmailId = 0L

  @BeforeEach
  fun initialiseData() {
    savedContactId = testAPIClient.createAContact(
      CreateContactRequest(
        lastName = "email",
        firstName = "has",
        createdBy = "created",
      ),
    ).id
    savedContactEmailId = testAPIClient.createAContactEmail(
      savedContactId,
      CreateEmailRequest(
        emailAddress = "test@example.com",
        createdBy = "created",
      ),
    ).contactEmailId
  }

  @Test
  fun `should return unauthorized if no token`() {
    webTestClient.put()
      .uri("/contact/$savedContactId/email/$savedContactEmailId")
      .accept(MediaType.APPLICATION_JSON)
      .contentType(MediaType.APPLICATION_JSON)
      .bodyValue(aMinimalRequest())
      .exchange()
      .expectStatus()
      .isUnauthorized
  }

  @Test
  fun `should return forbidden if no role`() {
    webTestClient.put()
      .uri("/contact/$savedContactId/email/$savedContactEmailId")
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
    webTestClient.put()
      .uri("/contact/$savedContactId/email/$savedContactEmailId")
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
      "emailAddress must not be null;{\"emailAddress\": null, \"emailValue\": \"0123456789\", \"amendedBy\": \"created\"}",
      "emailAddress must not be null;{\"emailValue\": \"test@example.com\", \"amendedBy\": \"created\"}",
      "amendedBy must not be null;{\"emailAddress\": \"DL\", \"emailValue\": \"test@example.com\", \"amendedBy\": null}",
      "amendedBy must not be null;{\"emailAddress\": \"DL\", \"emailValue\": \"test@example.com\"}",
    ],
    delimiter = ';',
  )
  fun `should return bad request if required fields are null`(expectedMessage: String, json: String) {
    val errors = webTestClient.put()
      .uri("/contact/$savedContactId/email/$savedContactEmailId")
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
    stubEvents.assertHasNoEvents(OutboundEvent.CONTACT_EMAIL_AMENDED, ContactEmailInfo(savedContactEmailId))
  }

  @ParameterizedTest
  @MethodSource("allFieldConstraintViolations")
  fun `should enforce field constraints`(expectedMessage: String, request: UpdateEmailRequest) {
    val errors = webTestClient.put()
      .uri("/contact/$savedContactId/email/$savedContactEmailId")
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
    stubEvents.assertHasNoEvents(OutboundEvent.CONTACT_EMAIL_AMENDED, ContactEmailInfo(savedContactEmailId))
  }

  @Test
  fun `should not update the email if the contact is not found`() {
    val request = aMinimalRequest()

    val errors = webTestClient.put()
      .uri("/contact/-321/email/$savedContactEmailId")
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
    stubEvents.assertHasNoEvents(OutboundEvent.CONTACT_EMAIL_AMENDED, ContactEmailInfo(savedContactEmailId))
  }

  @Test
  fun `should not update the email if the email is not found`() {
    val request = aMinimalRequest()

    val errors = webTestClient.put()
      .uri("/contact/$savedContactId/email/-99")
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

    assertThat(errors.userMessage).isEqualTo("Entity not found : Contact email (-99) not found")
    stubEvents.assertHasNoEvents(OutboundEvent.CONTACT_EMAIL_AMENDED, ContactEmailInfo(-99))
  }

  @Test
  fun `should not update the email if the email address is invalid`() {
    val request = UpdateEmailRequest(
      emailAddress = "@example.com",
      amendedBy = "amended",
    )

    val errors = webTestClient.put()
      .uri("/contact/$savedContactId/email/$savedContactEmailId")
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

    assertThat(errors.userMessage).isEqualTo("Validation failure: Email address is invalid")
    stubEvents.assertHasNoEvents(OutboundEvent.CONTACT_EMAIL_AMENDED, ContactEmailInfo(-99))
  }

  @Test
  fun `should update the email`() {
    val request = UpdateEmailRequest(
      emailAddress = "updated@example.com",
      amendedBy = "amended",
    )
    val updated = testAPIClient.updateAContactEmail(savedContactId, savedContactEmailId, request)

    with(updated) {
      assertThat(emailAddress).isEqualTo("updated@example.com")
      assertThat(createdBy).isEqualTo("created")
      assertThat(createdTime).isNotNull()
      assertThat(amendedBy).isEqualTo("amended")
      assertThat(amendedTime).isNotNull()
    }
    stubEvents.assertHasEvent(OutboundEvent.CONTACT_EMAIL_AMENDED, ContactEmailInfo(savedContactEmailId))
  }

  companion object {
    @JvmStatic
    fun allFieldConstraintViolations(): List<Arguments> {
      return listOf(
        Arguments.of("emailAddress must be <= 240 characters", aMinimalRequest().copy(emailAddress = "".padStart(241))),
        Arguments.of(
          "amendedBy must be <= 100 characters",
          aMinimalRequest().copy(amendedBy = "".padStart(101)),
        ),
      )
    }

    private fun aMinimalRequest() = UpdateEmailRequest(
      emailAddress = "updated@example.com",
      amendedBy = "amended",
    )
  }
}