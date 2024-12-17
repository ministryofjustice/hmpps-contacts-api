package uk.gov.justice.digital.hmpps.hmppscontactsapi.integration.resource

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import org.springframework.http.MediaType
import uk.gov.justice.digital.hmpps.hmppscontactsapi.integration.H2IntegrationTestBase
import uk.gov.justice.digital.hmpps.hmppscontactsapi.model.response.PrisonerContactRelationshipDetails

class GetPrisonerContactsRelationshipIntegrationTest : H2IntegrationTestBase() {
  companion object {
    private const val GET_PRISONER_CONTACT_RELATIONSHIP = "/prisoner-contact/1"
  }

  @Test
  fun `should return unauthorized if no token`() {
    webTestClient.get()
      .uri("/prisoner-contact/1")
      .exchange()
      .expectStatus()
      .isUnauthorized
  }

  @Test
  fun `should return forbidden if no role`() {
    webTestClient.get()
      .uri("/prisoner-contact/1")
      .headers(setAuthorisation())
      .exchange()
      .expectStatus()
      .isForbidden
  }

  @Test
  fun `should return forbidden if wrong role`() {
    webTestClient.get()
      .uri("/prisoner-contact/1")
      .headers(setAuthorisation(roles = listOf("ROLE_WRONG")))
      .exchange()
      .expectStatus()
      .isForbidden
  }

  @Test
  fun `should return not found if no prisoner contact relationship found`() {
    stubPrisonSearchWithNotFoundResponse("A4385DZ")

    webTestClient.get()
      .uri("/prisoner-contact/15453")
      .headers(setAuthorisation(roles = listOf("ROLE_CONTACTS_ADMIN")))
      .exchange()
      .expectStatus()
      .isNotFound
  }

  @ParameterizedTest
  @ValueSource(strings = ["ROLE_CONTACTS_ADMIN", "ROLE_CONTACTS__R", "ROLE_CONTACTS__RW"])
  fun `should return OK`(role: String) {
    val expectedPrisonerContactRelationship = PrisonerContactRelationshipDetails(
      prisonerContactId = 1,
      contactId = 1,
      prisonerNumber = "A1234BB",
      relationshipCode = "FA",
      relationshipDescription = "Father",
      nextOfKin = false,
      emergencyContact = false,
      isRelationshipActive = true,
      isApprovedVisitor = false,
      comments = "Comment",
    )

    val actualPrisonerContactSummary = webTestClient.get()
      .uri(GET_PRISONER_CONTACT_RELATIONSHIP)
      .headers(setAuthorisation(roles = listOf(role)))
      .exchange()
      .expectStatus()
      .isOk
      .expectHeader().contentType(MediaType.APPLICATION_JSON)
      .expectBody(PrisonerContactRelationshipDetails::class.java)
      .returnResult().responseBody!!

    assertThat(actualPrisonerContactSummary).isEqualTo(expectedPrisonerContactRelationship)
  }
}
