package uk.gov.justice.digital.hmpps.hmppscontactsapi.integration.resource

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.http.MediaType
import uk.gov.justice.digital.hmpps.hmppscontactsapi.client.prisonersearchapi.model.ErrorResponse
import uk.gov.justice.digital.hmpps.hmppscontactsapi.integration.IntegrationTestBase
import java.time.LocalDate
import java.time.format.DateTimeFormatter

private const val CONTACT_SEARCH_URL = "contact/search?" +
  "lastName=Last" +
  "&firstName=Jack" +
  "&middleName=Middle" +
  "&dateOfBirth=21/11/2000"

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
          "&dateOfBirth=21/11/2000",
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
  fun `should return validation errors for date of birth when the date is in the future and special characters on name fields `() {
    val futureTime = getFormattedDateOneDayInFuture()
    val errors = webTestClient.get()
      .uri(
        "contact/search?" +
          "lastName=NEW$" +
          "&firstName=NEW$" +
          "&middleName=Middle$" +
          "&dateOfBirth=$futureTime" +
          "&page=0&size=10&sort=lastName,asc&sort=firstName,desc",
      )
      .accept(MediaType.APPLICATION_JSON)
      .headers(setAuthorisation(roles = listOf("ROLE_CONTACTS_ADMIN")))
      .exchange()
      .expectStatus()
      .isBadRequest
      .expectHeader().contentType(MediaType.APPLICATION_JSON)
      .expectBody(ErrorResponse::class.java)
      .returnResult().responseBody!!

    assertThat(errors.userMessage).isEqualTo(
      "Validation failure(s): " +
        "Special characters are not allowed for First Name.\n" +
        "Special characters are not allowed for Last Name.\n" +
        "Special characters are not allowed for Middle Name.\n" +
        "The date of birth must be in the past",
    )
  }

  @Test
  fun `should return contacts when fist, middle names and date of birth is not in request parameters`() {
    webTestClient.get()
      .uri(
        "contact/search?" +
          "lastName=Twelve",
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
  fun `should get the contact with when search by first, middle, last and date of birth`() {
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
  fun `should get the contacts when searched by first name and last name with partial match`() {
    webTestClient.get()
      .uri(
        "contact/search?" +
          "lastName=Las" +
          "&firstName=ck",
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
      .jsonPath("$.content[0].firstName").isEqualTo("Jack")
      .jsonPath("$.content[0].lastName").isEqualTo("Last")
      .jsonPath("$.content[0].middleName").isEqualTo("Middle")
      .jsonPath("$.content[0].dateOfBirth").isEqualTo("2000-11-21")
      .jsonPath("$.content[0].createdBy").isEqualTo("TIM")
      .jsonPath("$.content[0].createdTime").isNotEmpty
      .jsonPath("$.content[0].property").isEqualTo("24")
      .jsonPath("$.content[0].street").isEqualTo("Acacia Avenue")
      .jsonPath("$.content[0].area").isEqualTo("Bunting")
      .jsonPath("$.content[0].cityCode").isEqualTo("SHEF")
      .jsonPath("$.content[0].countyCode").isEqualTo("SYORKS")
      .jsonPath("$.content[0].postCode").isEqualTo("S2 3LK")
      .jsonPath("$.content[0].countryCode").isEqualTo("UK")
  }

  @Test
  fun `should get bad request when searched with empty last name`() {
    val errors = webTestClient.get()
      .uri(
        "contact/search?" +
          "lastName=" +
          "&firstName=Jack",
      )
      .accept(MediaType.APPLICATION_JSON)
      .headers(setAuthorisation(roles = listOf("ROLE_CONTACTS_ADMIN")))
      .exchange()
      .expectStatus()
      .isBadRequest
      .expectHeader().contentType(MediaType.APPLICATION_JSON)
      .expectBody(ErrorResponse::class.java)
      .returnResult().responseBody!!

    assertThat(errors.userMessage).isEqualTo("Validation failure(s): Last Name cannot be blank.")
  }

  @Test
  fun `should get bad request when searched with invalid date format for date of birth`() {
    val errors = webTestClient.get()
      .uri(
        "contact/search?" +
          "lastName=Eleven" +
          "&dateOfBirth=01-10-2001",
      )
      .accept(MediaType.APPLICATION_JSON)
      .headers(setAuthorisation(roles = listOf("ROLE_CONTACTS_ADMIN")))
      .exchange()
      .expectStatus()
      .isBadRequest
      .expectHeader().contentType(MediaType.APPLICATION_JSON)
      .expectBody(ErrorResponse::class.java)
      .returnResult().responseBody!!

    assertThat(errors.userMessage).contains("Validation failure(s): Failed to convert value of type 'java.lang.String' to required type 'java.time.LocalDate';")
  }

  @Test
  fun `should get bad request when searched with no last name`() {
    val errors = webTestClient.get()
      .uri(
        "contact/search?",
      )
      .accept(MediaType.APPLICATION_JSON)
      .headers(setAuthorisation(roles = listOf("ROLE_CONTACTS_ADMIN")))
      .exchange()
      .expectStatus()
      .isBadRequest
      .expectHeader().contentType(MediaType.APPLICATION_JSON)
      .expectBody(ErrorResponse::class.java)
      .returnResult().responseBody!!

    assertThat(errors.userMessage).contains("Validation failure(s): Parameter specified as non-null is null: ")
  }

  fun getFormattedDateOneDayInFuture(): String {
    val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
    val futureDate = LocalDate.now().plusDays(1)
    return futureDate.format(formatter)
  }
}
