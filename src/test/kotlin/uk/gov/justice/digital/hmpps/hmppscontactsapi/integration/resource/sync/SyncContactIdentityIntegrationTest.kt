package uk.gov.justice.digital.hmpps.hmppscontactsapi.integration.resource.sync

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import org.springframework.http.MediaType
import uk.gov.justice.digital.hmpps.hmppscontactsapi.integration.H2IntegrationTestBase
import uk.gov.justice.digital.hmpps.hmppscontactsapi.model.request.CreateContactRequest
import uk.gov.justice.digital.hmpps.hmppscontactsapi.model.request.sync.SyncCreateContactIdentityRequest
import uk.gov.justice.digital.hmpps.hmppscontactsapi.model.request.sync.SyncUpdateContactIdentityRequest
import uk.gov.justice.digital.hmpps.hmppscontactsapi.model.response.sync.SyncContactIdentity
import java.time.LocalDateTime

class SyncContactIdentityIntegrationTest : H2IntegrationTestBase() {

  @Nested
  inner class ContactIdentitySyncTests {
    private var savedContactId = 0L

    @BeforeEach
    fun initialiseData() {
      savedContactId = testAPIClient.createAContact(aMinimalCreateContactRequest()).id
    }

    @Test
    fun `Sync endpoints should return unauthorized if no token provided`() {
      webTestClient.get()
        .uri("/sync/contact-identity/1")
        .accept(MediaType.APPLICATION_JSON)
        .exchange()
        .expectStatus()
        .isUnauthorized

      webTestClient.put()
        .uri("/sync/contact-identity")
        .accept(MediaType.APPLICATION_JSON)
        .contentType(MediaType.APPLICATION_JSON)
        .bodyValue(createContactIdentityRequest(savedContactId))
        .exchange()
        .expectStatus()
        .isUnauthorized

      webTestClient.post()
        .uri("/sync/contact-identity/1")
        .accept(MediaType.APPLICATION_JSON)
        .contentType(MediaType.APPLICATION_JSON)
        .bodyValue(updateContactIdentityRequest(savedContactId))
        .exchange()
        .expectStatus()
        .isUnauthorized

      webTestClient.delete()
        .uri("/sync/contact-identity/1")
        .accept(MediaType.APPLICATION_JSON)
        .exchange()
        .expectStatus()
        .isUnauthorized
    }

    @ParameterizedTest
    @ValueSource(strings = ["ROLE_CONTACTS_ADMIN", "ROLE_CONTACTS__R", "ROLE_CONTACTS__RW"])
    fun `Sync endpoints should return forbidden without an authorised role on the token`(role: String) {
      webTestClient.get()
        .uri("/sync/contact-identity/1")
        .accept(MediaType.APPLICATION_JSON)
        .headers(setAuthorisation(roles = listOf(role)))
        .exchange()
        .expectStatus()
        .isForbidden

      webTestClient.post()
        .uri("/sync/contact-identity")
        .accept(MediaType.APPLICATION_JSON)
        .contentType(MediaType.APPLICATION_JSON)
        .bodyValue(createContactIdentityRequest(savedContactId))
        .headers(setAuthorisation(roles = listOf(role)))
        .exchange()
        .expectStatus()
        .isForbidden

      webTestClient.put()
        .uri("/sync/contact-identity/1")
        .accept(MediaType.APPLICATION_JSON)
        .contentType(MediaType.APPLICATION_JSON)
        .bodyValue(updateContactIdentityRequest(savedContactId))
        .headers(setAuthorisation(roles = listOf(role)))
        .exchange()
        .expectStatus()
        .isForbidden

      webTestClient.delete()
        .uri("/sync/contact-identity/1")
        .accept(MediaType.APPLICATION_JSON)
        .headers(setAuthorisation(roles = listOf(role)))
        .exchange()
        .expectStatus()
        .isForbidden
    }

    @Test
    fun `should get an existing contact identity`() {
      // From base data
      val contactIdentityId = 2L
      val contactIdentity = webTestClient.get()
        .uri("/sync/contact-identity/{contactIdentityId}", contactIdentityId)
        .accept(MediaType.APPLICATION_JSON)
        .headers(setAuthorisation(roles = listOf("ROLE_CONTACTS_MIGRATION")))
        .exchange()
        .expectStatus()
        .isOk
        .expectHeader().contentType(MediaType.APPLICATION_JSON)
        .expectBody(SyncContactIdentity::class.java)
        .returnResult().responseBody!!

      with(contactIdentity) {
        assertThat(identityType).isEqualTo("PASS")
      }
    }

    @Test
    fun `should create a new contact identity`() {
      val contactIdentity = webTestClient.post()
        .uri("/sync/contact-identity")
        .accept(MediaType.APPLICATION_JSON)
        .contentType(MediaType.APPLICATION_JSON)
        .headers(setAuthorisation(roles = listOf("ROLE_CONTACTS_MIGRATION")))
        .bodyValue(createContactIdentityRequest(savedContactId))
        .exchange()
        .expectStatus()
        .isOk
        .expectHeader().contentType(MediaType.APPLICATION_JSON)
        .expectBody(SyncContactIdentity::class.java)
        .returnResult().responseBody!!

      // The created identity is returned
      with(contactIdentity) {
        assertThat(contactIdentityId).isGreaterThan(3)
        assertThat(contactId).isEqualTo(savedContactId)
        assertThat(identityType).isEqualTo("PASS")
        assertThat(issuingAuthority).isEqualTo("UKBORDER")
        assertThat(createdBy).isEqualTo("CREATE")
        assertThat(createdTime).isAfter(LocalDateTime.now().minusMinutes(5))
      }
    }

    @Test
    fun `should create and then update a contact identity`() {
      val contactIdentity = webTestClient.post()
        .uri("/sync/contact-identity")
        .accept(MediaType.APPLICATION_JSON)
        .contentType(MediaType.APPLICATION_JSON)
        .headers(setAuthorisation(roles = listOf("ROLE_CONTACTS_MIGRATION")))
        .bodyValue(createContactIdentityRequest(savedContactId))
        .exchange()
        .expectStatus()
        .isOk
        .expectHeader().contentType(MediaType.APPLICATION_JSON)
        .expectBody(SyncContactIdentity::class.java)
        .returnResult().responseBody!!

      with(contactIdentity) {
        assertThat(identityType).isEqualTo("PASS")
        assertThat(createdBy).isEqualTo("CREATE")
        assertThat(createdTime).isAfter(LocalDateTime.now().minusMinutes(5))
      }

      val updatedIdentity = webTestClient.put()
        .uri("/sync/contact-identity/{contactIdentityId}", contactIdentity.contactIdentityId)
        .accept(MediaType.APPLICATION_JSON)
        .contentType(MediaType.APPLICATION_JSON)
        .headers(setAuthorisation(roles = listOf("ROLE_CONTACTS_MIGRATION")))
        .bodyValue(updateContactIdentityRequest(savedContactId))
        .exchange()
        .expectStatus()
        .isOk
        .expectHeader().contentType(MediaType.APPLICATION_JSON)
        .expectBody(SyncContactIdentity::class.java)
        .returnResult().responseBody!!

      // Check the updated copy
      with(updatedIdentity) {
        assertThat(contactIdentityId).isGreaterThan(4)
        assertThat(contactId).isEqualTo(savedContactId)
        assertThat(identityType).isEqualTo("PASS")
        assertThat(issuingAuthority).isEqualTo("UKBORDER")
        assertThat(updatedBy).isEqualTo("UPDATE")
        assertThat(updatedTime).isAfter(LocalDateTime.now().minusMinutes(5))
        assertThat(createdBy).isEqualTo("CREATE")
        assertThat(createdTime).isNotNull()
      }
    }

    @Test
    fun `should delete an existing contact identity`() {
      val contactIdentityId = 3L

      webTestClient.delete()
        .uri("/sync/contact-identity/{contactIdentityId}", contactIdentityId)
        .accept(MediaType.APPLICATION_JSON)
        .headers(setAuthorisation(roles = listOf("ROLE_CONTACTS_MIGRATION")))
        .exchange()
        .expectStatus()
        .isOk

      webTestClient.get()
        .uri("/sync/contact-identity/{contactIdentityId}", contactIdentityId)
        .accept(MediaType.APPLICATION_JSON)
        .headers(setAuthorisation(roles = listOf("ROLE_CONTACTS_MIGRATION")))
        .exchange()
        .expectStatus()
        .isNotFound
    }

    private fun updateContactIdentityRequest(contactId: Long) =
      SyncUpdateContactIdentityRequest(
        contactId = contactId,
        identityType = "PASS",
        identityValue = "PP87878787878",
        issuingAuthority = "UKBORDER",
        updatedBy = "UPDATE",
        updatedTime = LocalDateTime.now(),
      )

    private fun createContactIdentityRequest(contactId: Long) =
      SyncCreateContactIdentityRequest(
        contactId = contactId,
        identityType = "PASS",
        identityValue = "PP87878787878",
        issuingAuthority = "UKBORDER",
        createdBy = "CREATE",
      )
  }

  private fun aMinimalCreateContactRequest() = CreateContactRequest(
    lastName = "last",
    firstName = "first",
    createdBy = "created",
  )
}
