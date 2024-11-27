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
import uk.gov.justice.digital.hmpps.hmppscontactsapi.model.request.CreateContactRestrictionRequest
import uk.gov.justice.digital.hmpps.hmppscontactsapi.model.request.UpdateContactRestrictionRequest
import uk.gov.justice.digital.hmpps.hmppscontactsapi.service.events.ContactRestrictionInfo
import uk.gov.justice.digital.hmpps.hmppscontactsapi.service.events.OutboundEvent
import uk.gov.justice.digital.hmpps.hmppscontactsapi.service.events.PersonReference
import uk.gov.justice.digital.hmpps.hmppscontactsapi.service.events.Source
import java.time.LocalDate

class UpdateContactRestrictionIntegrationTest : H2IntegrationTestBase() {
  private var savedContactId = 0L
  private var savedContactRestrictionId = 0L

  @BeforeEach
  fun initialiseData() {
    savedContactId = testAPIClient.createAContact(
      CreateContactRequest(
        lastName = "last",
        firstName = "first",
        createdBy = "created",
      ),
    ).id
    savedContactRestrictionId = testAPIClient.createContactEstateWideRestrictions(
      savedContactId,
      CreateContactRestrictionRequest(
        restrictionType = "BAN",
        startDate = LocalDate.of(2020, 1, 1),
        expiryDate = LocalDate.of(2022, 2, 2),
        comments = "Some comments",
        createdBy = "created",
      ),
    ).contactRestrictionId
  }

  @Test
  fun `should return unauthorized if no token`() {
    webTestClient.put()
      .uri("/contact/$savedContactId/estate-wide-restrictions/$savedContactRestrictionId")
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
      .uri("/contact/$savedContactId/estate-wide-restrictions/$savedContactRestrictionId")
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
      .uri("/contact/$savedContactId/estate-wide-restrictions/$savedContactRestrictionId")
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
      "restrictionType must not be null;{\"restrictionType\": null, \"startDate\": \"2020-01-01\", \"updatedBy\": \"updated\"}",
      "restrictionType must not be null;{\"startDate\": \"2020-01-01\", \"updatedBy\": \"updated\"}",
      "startDate must not be null;{\"restrictionType\": \"BAN\", \"startDate\": null, \"updatedBy\": \"updated\"}",
      "startDate must not be null;{\"restrictionType\": \"BAN\", \"updatedBy\": \"updated\"}",
      "updatedBy must not be null;{\"restrictionType\": \"BAN\", \"startDate\": \"2020-01-01\", \"updatedBy\": null}",
      "updatedBy must not be null;{\"restrictionType\": \"BAN\", \"startDate\": \"2020-01-01\"}",
    ],
    delimiter = ';',
  )
  fun `should return bad request if required fields are null`(expectedMessage: String, json: String) {
    val errors = webTestClient.put()
      .uri("/contact/$savedContactId/estate-wide-restrictions/$savedContactRestrictionId")
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
  fun `should enforce field constraints`(expectedMessage: String, request: UpdateContactRestrictionRequest) {
    val errors = webTestClient.put()
      .uri("/contact/$savedContactId/estate-wide-restrictions/$savedContactRestrictionId")
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
  fun `should not update the restriction if the contact is not found`() {
    val request = aMinimalRequest()

    val errors = webTestClient.put()
      .uri("/contact/-321/estate-wide-restrictions/$savedContactRestrictionId")
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

    assertThat(errors.userMessage).isEqualTo("Entity not found : Contact (-321) could not be found")
  }

  @Test
  fun `should not update the restriction if the contact restriction is not found`() {
    val request = aMinimalRequest()

    val errors = webTestClient.put()
      .uri("/contact/$savedContactId/estate-wide-restrictions/-321")
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

    assertThat(errors.userMessage).isEqualTo("Entity not found : Contact restriction (-321) could not be found")
  }

  @Test
  fun `should not update the restriction if the type is not valid`() {
    val request = aMinimalRequest().copy(restrictionType = "FOO")

    val errors = webTestClient.put()
      .uri("/contact/$savedContactId/estate-wide-restrictions/$savedContactRestrictionId")
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

    assertThat(errors.userMessage).isEqualTo("Validation failure: Unsupported restriction type (FOO)")
  }

  @Test
  fun `should update the restriction with minimal fields`() {
    val request = UpdateContactRestrictionRequest(
      restrictionType = "CCTV",
      startDate = LocalDate.of(1990, 1, 1),
      expiryDate = null,
      comments = null,
      updatedBy = "updated",
    )

    val updated = testAPIClient.updateContactEstateWideRestrictions(savedContactId, savedContactRestrictionId, request)

    with(updated) {
      assertThat(contactRestrictionId).isGreaterThan(0)
      assertThat(contactId).isEqualTo(savedContactId)
      assertThat(restrictionType).isEqualTo(request.restrictionType)
      assertThat(startDate).isEqualTo(request.startDate)
      assertThat(expiryDate).isNull()
      assertThat(comments).isNull()
      assertThat(createdBy).isEqualTo("created")
      assertThat(createdTime).isNotNull()
      assertThat(updatedBy).isEqualTo("updated")
      assertThat(updatedTime).isNotNull()
    }

    stubEvents.assertHasEvent(
      event = OutboundEvent.CONTACT_RESTRICTION_UPDATED,
      additionalInfo = ContactRestrictionInfo(updated.contactRestrictionId, Source.DPS),
      personReference = PersonReference(dpsContactId = updated.contactId),
    )
  }

  @Test
  fun `should update the restriction with all fields`() {
    val request = UpdateContactRestrictionRequest(
      restrictionType = "CCTV",
      startDate = LocalDate.of(1990, 1, 1),
      expiryDate = LocalDate.of(1992, 2, 2),
      comments = "Updated comments",
      updatedBy = "updated",
    )

    val updated = testAPIClient.updateContactEstateWideRestrictions(savedContactId, savedContactRestrictionId, request)

    with(updated) {
      assertThat(contactRestrictionId).isGreaterThan(0)
      assertThat(contactId).isEqualTo(savedContactId)
      assertThat(restrictionType).isEqualTo(request.restrictionType)
      assertThat(startDate).isEqualTo(request.startDate)
      assertThat(expiryDate).isEqualTo(request.expiryDate)
      assertThat(comments).isEqualTo(request.comments)
      assertThat(createdBy).isEqualTo("created")
      assertThat(createdTime).isNotNull()
      assertThat(updatedBy).isEqualTo("updated")
      assertThat(updatedTime).isNotNull()
    }

    stubEvents.assertHasEvent(
      event = OutboundEvent.CONTACT_RESTRICTION_UPDATED,
      additionalInfo = ContactRestrictionInfo(updated.contactRestrictionId, Source.DPS),
      personReference = PersonReference(dpsContactId = updated.contactId),
    )
  }

  companion object {
    @JvmStatic
    fun allFieldConstraintViolations(): List<Arguments> {
      return listOf(
        Arguments.of("comments must be <= 240 characters", aMinimalRequest().copy(comments = "".padStart(241))),
        Arguments.of(
          "updatedBy must be <= 100 characters",
          aMinimalRequest().copy(updatedBy = "".padStart(101)),
        ),
      )
    }

    private fun aMinimalRequest() = UpdateContactRestrictionRequest(
      restrictionType = "CCTV",
      startDate = LocalDate.of(1990, 1, 1),
      expiryDate = null,
      comments = null,
      updatedBy = "updated",
    )
  }
}