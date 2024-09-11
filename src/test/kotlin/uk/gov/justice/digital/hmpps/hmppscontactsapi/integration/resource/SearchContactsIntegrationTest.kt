package uk.gov.justice.digital.hmpps.hmppscontactsapi.integration.resource

import org.junit.jupiter.api.Test
import org.springframework.http.MediaType
import uk.gov.justice.digital.hmpps.hmppscontactsapi.integration.IntegrationTestBase

private const val CONTACT_SEARCH_URL = "contact/search?" +
  "lastName=Last" +
  "&firstName=Jack" +
  "&middleName=Middle" +
  "&dateOfBirth=2000-11-21"

class SearchContactsIntegrationTest : IntegrationTestBase() {

  @Test
  fun `should return unauthorized if no token`() {
    webTestClient.get()
      .uri(CONTACT_SEARCH_URL)
      .accept(MediaType.APPLICATION_JSON)
      .exchange()
      .expectStatus()
      .isUnauthorized
  }

  @Test
  fun `should return forbidden if no role`() {
    webTestClient.get()
      .uri(CONTACT_SEARCH_URL)
      .accept(MediaType.APPLICATION_JSON)
      .headers(setAuthorisation())
      .exchange()
      .expectStatus()
      .isForbidden
  }

  @Test
  fun `should return forbidden if wrong role`() {
    webTestClient.get()
      .uri(CONTACT_SEARCH_URL)
      .headers(setAuthorisation(roles = listOf("ROLE_WRONG")))
      .exchange()
      .expectStatus()
      .isForbidden
  }

  @Test
  fun `should return empty list if the contact doesn't exist`() {
    webTestClient.get()
      .uri(
        "contact/search?" +
          "lastName=NEW" +
          "&firstName=NEW" +
          "&middleName=Middle" +
          "&dateOfBirth=2000-11-21",
      )
      .accept(MediaType.APPLICATION_JSON)
      .headers(setAuthorisation(roles = listOf("ROLE_CONTACTS_ADMIN")))
      .exchange()
      .expectStatus()
      .isOk
      .expectHeader().contentType(MediaType.APPLICATION_JSON)
      .expectBody()
      .jsonPath("$.content").isArray()
      .jsonPath("$.content.length()").isEqualTo(0)
  }

  @Test
  fun `should get the contact with all fields when search by first,middle,last and date of birth`() {
    webTestClient.get()
      .uri(CONTACT_SEARCH_URL)
      .accept(MediaType.APPLICATION_JSON)
      .headers(setAuthorisation(roles = listOf("ROLE_CONTACTS_ADMIN")))
      .exchange()
      .expectStatus()
      .isOk
      .expectHeader().contentType(MediaType.APPLICATION_JSON)
      .expectBody()
      .jsonPath("$.content").isArray()
      .jsonPath("$.content.length()").isEqualTo(1)
      .jsonPath("$.totalElements").isNumber()
      .jsonPath("$.totalPages").isNumber()
      .jsonPath("$.content[0].id").isNotEmpty()
      .jsonPath("$.content[0].firstName").isNotEmpty()
      .jsonPath("$.content[0].lastName").isNotEmpty()
  }

  @Test
  fun `should get the contacts when searched by first name and last name`() {
    webTestClient.get()
      .uri(
        "contact/search?" +
          "lastName=Last" +
          "&firstName=Jack",
      )
      .accept(MediaType.APPLICATION_JSON)
      .headers(setAuthorisation(roles = listOf("ROLE_CONTACTS_ADMIN")))
      .exchange()
      .expectStatus()
      .isOk
      .expectHeader().contentType(MediaType.APPLICATION_JSON)
      .expectBody()
      .jsonPath("$.content").isArray()
      .jsonPath("$.content.length()").isEqualTo(1)
      .jsonPath("$.totalElements").isNumber()
      .jsonPath("$.totalPages").isNumber()
      .jsonPath("$.content[0].id").isNotEmpty()
      .jsonPath("$.content[0].firstName").isNotEmpty()
      .jsonPath("$.content[0].lastName").isNotEmpty()
  }

  @Test
  fun `should get not found when searched by not providing last name`() {
    webTestClient.get()
      .uri(
        "contact/search?" +
          "&firstName=Jack",
      )
      .accept(MediaType.APPLICATION_JSON)
      .headers(setAuthorisation(roles = listOf("ROLE_CONTACTS_ADMIN")))
      .exchange()
      .expectStatus()
      .isBadRequest
  }
}
