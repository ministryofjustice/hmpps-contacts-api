package uk.gov.justice.digital.hmpps.hmppscontactsapi.integration.resource

import org.junit.jupiter.api.Test
import org.springframework.http.MediaType
import uk.gov.justice.digital.hmpps.hmppscontactsapi.integration.IntegrationTestBase

class PatchContactIntegrationTest : IntegrationTestBase() {

  @Test
  fun `should update the language code to a value`() {
    webTestClient.patch()
      .uri("/contact/1")
      .accept(MediaType.APPLICATION_JSON)
      .contentType(MediaType.APPLICATION_JSON)
      .headers(setAuthorisation(roles = listOf("ROLE_CONTACTS_ADMIN")))
      .bodyValue("""{"languageCode":  "foo" }""")
      .exchange()
      .expectStatus()
      .isCreated
  }

  @Test
  fun `should update the language code to null`() {
    webTestClient.patch()
      .uri("/contact/1")
      .accept(MediaType.APPLICATION_JSON)
      .contentType(MediaType.APPLICATION_JSON)
      .headers(setAuthorisation(roles = listOf("ROLE_CONTACTS_ADMIN")))
      .bodyValue("""{"languageCode":  null }""")
      .exchange()
      .expectStatus()
      .isAccepted
  }

  @Test
  fun `should not update the language code`() {
    webTestClient.patch()
      .uri("/contact/1")
      .accept(MediaType.APPLICATION_JSON)
      .contentType(MediaType.APPLICATION_JSON)
      .headers(setAuthorisation(roles = listOf("ROLE_CONTACTS_ADMIN")))
      .bodyValue("""{}""")
      .exchange()
      .expectStatus()
      .isNoContent
  }
}
