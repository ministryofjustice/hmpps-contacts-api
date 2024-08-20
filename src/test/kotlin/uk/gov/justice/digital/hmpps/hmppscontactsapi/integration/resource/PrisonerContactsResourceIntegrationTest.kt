package uk.gov.justice.digital.hmpps.hmppscontactsapi.integration.resource

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.http.MediaType
import org.springframework.test.web.reactive.server.WebTestClient
import uk.gov.justice.digital.hmpps.hmppscontactsapi.integration.IntegrationTestBase
import uk.gov.justice.digital.hmpps.hmppscontactsapi.model.PrisonerContact

private const val GET_PRISONER_CONTACT = "/prisoner-contacts/prisoner/P001"

class PrisonerContactsResourceIntegrationTest : IntegrationTestBase() {

  @Nested
  inner class ContactsPrisonerEndpoint {

    @Test
    fun `should return unauthorized if no token`() {
      webTestClient.get()
        .uri("/prisoner-contacts/prisoner/P001")
        .exchange()
        .expectStatus()
        .isUnauthorized
    }

    @Test
    fun `should return forbidden if no role`() {
      webTestClient.get()
        .uri("/prisoner-contacts/prisoner/P001")
        .headers(setAuthorisation())
        .exchange()
        .expectStatus()
        .isForbidden
    }

    @Test
    fun `should return forbidden if wrong role`() {
      webTestClient.get()
        .uri("/prisoner-contacts/prisoner/P001")
        .headers(setAuthorisation(roles = listOf("ROLE_WRONG")))
        .exchange()
        .expectStatus()
        .isForbidden
    }

    @Test
    fun `should return OK`() {
      stubPrisonSearchWithResponse("P001")

      var contacts = webTestClient.getContacts()

      assertThat(contacts).extracting("surname").contains("Last")
      assertThat(contacts).hasSize(1)
    }
  }

  private fun WebTestClient.getContacts(): MutableList<PrisonerContact> =
    get()
      .uri(GET_PRISONER_CONTACT)
      .headers(setAuthorisation(roles = listOf("ROLE_CONTACTS_ADMIN")))
      .exchange()
      .expectStatus()
      .isOk
      .expectHeader().contentType(MediaType.APPLICATION_JSON)
      .expectBodyList(PrisonerContact::class.java)
      .returnResult().responseBody!!
}
