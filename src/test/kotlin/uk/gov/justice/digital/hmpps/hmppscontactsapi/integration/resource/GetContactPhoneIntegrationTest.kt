package uk.gov.justice.digital.hmpps.hmppscontactsapi.integration.resource

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.http.MediaType
import uk.gov.justice.digital.hmpps.hmppscontactsapi.integration.H2IntegrationTestBase

class GetContactPhoneIntegrationTest : H2IntegrationTestBase() {

  @Test
  fun `should return unauthorized if no token`() {
    webTestClient.get()
      .uri("/contact/1/phone/1")
      .accept(MediaType.APPLICATION_JSON)
      .exchange()
      .expectStatus()
      .isUnauthorized
  }

  @Test
  fun `should return forbidden if no role`() {
    webTestClient.get()
      .uri("/contact/1/phone/1")
      .accept(MediaType.APPLICATION_JSON)
      .headers(setAuthorisation())
      .exchange()
      .expectStatus()
      .isForbidden
  }

  @Test
  fun `should return forbidden if wrong role`() {
    webTestClient.get()
      .uri("/contact/1/phone/1")
      .accept(MediaType.APPLICATION_JSON)
      .headers(setAuthorisation(roles = listOf("ROLE_WRONG")))
      .exchange()
      .expectStatus()
      .isForbidden
  }

  @Test
  fun `should get phone details`() {
    val phone = testAPIClient.getContactPhone(1, 2)

    with(phone) {
      assertThat(contactPhoneId).isEqualTo(2)
      assertThat(contactId).isEqualTo(1)
      assertThat(phoneType).isEqualTo("HOME")
      assertThat(phoneTypeDescription).isEqualTo("Home")
      assertThat(phoneNumber).isEqualTo("01111 777777")
      assertThat(extNumber).isEqualTo("+0123")
      assertThat(createdBy).isEqualTo("JAMES")
    }
  }
}