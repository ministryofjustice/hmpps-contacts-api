package uk.gov.justice.digital.hmpps.hmppscontactsapi.integration.resource

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import uk.gov.justice.digital.hmpps.hmppscontactsapi.client.manage.users.User
import uk.gov.justice.digital.hmpps.hmppscontactsapi.integration.H2IntegrationTestBase
import uk.gov.justice.digital.hmpps.hmppscontactsapi.model.request.CreateContactRequest
import java.time.LocalDate

class GetContactGlobalRestrictionsIntegrationTest : H2IntegrationTestBase() {

  @Test
  fun `should return unauthorized if no token`() {
    webTestClient.get()
      .uri("/contact/1/restriction")
      .exchange()
      .expectStatus()
      .isUnauthorized
  }

  @Test
  fun `should return forbidden if no role`() {
    webTestClient.get()
      .uri("/contact/1/restriction")
      .headers(setAuthorisation())
      .exchange()
      .expectStatus()
      .isForbidden
  }

  @Test
  fun `should return forbidden if wrong role`() {
    webTestClient.get()
      .uri("/contact/1/restriction")
      .headers(setAuthorisation(roles = listOf("ROLE_WRONG")))
      .exchange()
      .expectStatus()
      .isForbidden
  }

  @Test
  fun `should return not found if no contact found`() {
    webTestClient.get()
      .uri("/contact/-1/restriction")
      .headers(setAuthorisation(roles = listOf("ROLE_CONTACTS_ADMIN")))
      .exchange()
      .expectStatus()
      .isNotFound
  }

  @ParameterizedTest
  @ValueSource(strings = ["ROLE_CONTACTS_ADMIN", "ROLE_CONTACTS__R", "ROLE_CONTACTS__RW"])
  fun `should return all global restrictions for a contact`(role: String) {
    stubGetUserByUsername(User("JBAKER_GEN", "James Baker"))
    val restrictions = testAPIClient.getContactGlobalRestrictions(3, role)
    assertThat(restrictions).hasSize(2)
    with(restrictions[0]) {
      assertThat(contactRestrictionId).isNotNull()
      assertThat(contactId).isEqualTo(3)
      assertThat(restrictionType).isEqualTo("CCTV")
      assertThat(restrictionTypeDescription).isEqualTo("CCTV")
      assertThat(startDate).isEqualTo(LocalDate.of(2000, 11, 21))
      assertThat(expiryDate).isEqualTo(LocalDate.of(2001, 11, 21))
      assertThat(comments).isEqualTo("N/A")
      assertThat(enteredByUsername).isEqualTo("JBAKER_GEN")
      assertThat(enteredByDisplayName).isEqualTo("James Baker")
    }
    with(restrictions[1]) {
      assertThat(contactRestrictionId).isNotNull()
      assertThat(contactId).isEqualTo(3)
      assertThat(restrictionType).isEqualTo("BAN")
      assertThat(restrictionTypeDescription).isEqualTo("Banned")
      assertThat(startDate).isNull()
      assertThat(expiryDate).isNull()
      assertThat(comments).isNull()
      assertThat(enteredByUsername).isEqualTo("FOO_USER")
      assertThat(enteredByDisplayName).isEqualTo("FOO_USER")
    }
  }

  @Test
  fun `should return empty list if no restrictions for a contact`() {
    val createdContact = testAPIClient.createAContact(
      CreateContactRequest(firstName = "First", lastName = "Last", createdBy = "USER1"),
      "ROLE_CONTACTS_ADMIN",
    )
    val restrictions = testAPIClient.getContactGlobalRestrictions(createdContact.id, "ROLE_CONTACTS_ADMIN")
    assertThat(restrictions).isEmpty()
  }
}
