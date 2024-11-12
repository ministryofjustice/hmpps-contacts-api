package uk.gov.justice.digital.hmpps.hmppscontactsapi.integration.resource

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.http.MediaType
import uk.gov.justice.digital.hmpps.hmppscontactsapi.integration.H2IntegrationTestBase
import uk.gov.justice.digital.hmpps.hmppscontactsapi.model.response.PrisonerContactSummary
import java.time.LocalDate

class GetPrisonerContactsRelationshipIntegrationTest : H2IntegrationTestBase() {
  companion object {
    private const val GET_PRISONER_CONTACT_RELATIONSHIP = "/prisoner/relationship/1"
  }

  @Test
  fun `should return unauthorized if no token`() {
    webTestClient.get()
      .uri("/prisoner/relationship/1")
      .exchange()
      .expectStatus()
      .isUnauthorized
  }

  @Test
  fun `should return forbidden if no role`() {
    webTestClient.get()
      .uri("/prisoner/relationship/1")
      .headers(setAuthorisation())
      .exchange()
      .expectStatus()
      .isForbidden
  }

  @Test
  fun `should return forbidden if wrong role`() {
    webTestClient.get()
      .uri("/prisoner/relationship/1")
      .headers(setAuthorisation(roles = listOf("ROLE_WRONG")))
      .exchange()
      .expectStatus()
      .isForbidden
  }

  @Test
  fun `should return not found if no prisoner contact relationship found`() {
    stubPrisonSearchWithNotFoundResponse("A4385DZ")

    webTestClient.get()
      .uri("/prisoner/relationship/15453")
      .headers(setAuthorisation(roles = listOf("ROLE_CONTACTS_ADMIN")))
      .exchange()
      .expectStatus()
      .isNotFound
  }

  @Test
  fun `should return OK`() {
    val expectedPrisonerContactSummary = PrisonerContactSummary(
      prisonerContactId = 1,
      contactId = 1,
      prisonerNumber = "A1234BB",
      lastName = "Last",
      firstName = "Jack",
      middleNames = "Middle",
      dateOfBirth = LocalDate.of(2000, 11, 21),
      estimatedIsOverEighteen = null,
      relationshipCode = "FA",
      relationshipDescription = "Father",
      flat = null,
      property = "24",
      street = "Acacia Avenue",
      area = "Bunting",
      cityCode = "25343",
      cityDescription = "Sheffield",
      countyCode = "S.YORKSHIRE",
      countyDescription = "South Yorkshire",
      postCode = "S2 3LK",
      countryCode = "ENG",
      countryDescription = "England",
      primaryAddress = true,
      mailAddress = false,
      phoneType = "HOME",
      phoneTypeDescription = "Home",
      phoneNumber = "01111 777777",
      extNumber = "+0123",
      approvedVisitor = false,
      nextOfKin = false,
      emergencyContact = false,
      isRelationshipActive = true,
      currentTerm = true,
      comments = "Comment",
    )

    val actualPrisonerContactSummary = webTestClient.get()
      .uri(GET_PRISONER_CONTACT_RELATIONSHIP)
      .headers(setAuthorisation(roles = listOf("ROLE_CONTACTS_ADMIN")))
      .exchange()
      .expectStatus()
      .isOk
      .expectHeader().contentType(MediaType.APPLICATION_JSON)
      .expectBody(PrisonerContactSummary::class.java)
      .returnResult().responseBody!!

    assertThat(actualPrisonerContactSummary).isEqualTo(expectedPrisonerContactSummary)
  }
}
