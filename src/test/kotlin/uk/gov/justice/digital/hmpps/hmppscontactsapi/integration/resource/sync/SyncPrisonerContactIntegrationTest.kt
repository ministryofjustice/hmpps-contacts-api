package uk.gov.justice.digital.hmpps.hmppscontactsapi.integration.resource.sync

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.http.MediaType
import uk.gov.justice.digital.hmpps.hmppscontactsapi.integration.IntegrationTestBase
import uk.gov.justice.digital.hmpps.hmppscontactsapi.model.request.sync.CreatePrisonerContactRequest
import uk.gov.justice.digital.hmpps.hmppscontactsapi.model.request.sync.UpdatePrisonerContactRequest
import uk.gov.justice.digital.hmpps.hmppscontactsapi.model.response.sync.PrisonerContact
import java.time.LocalDate
import java.time.LocalDateTime

class SyncPrisonerContactIntegrationTest : IntegrationTestBase() {

  @Nested
  inner class PrisonerContactSyncTests {

    @Test
    fun `Sync endpoints should return unauthorized if no token provided`() {
      webTestClient.get()
        .uri("/sync/prisoner-contact/1")
        .accept(MediaType.APPLICATION_JSON)
        .exchange()
        .expectStatus()
        .isUnauthorized

      webTestClient.put()
        .uri("/sync/prisoner-contact")
        .accept(MediaType.APPLICATION_JSON)
        .contentType(MediaType.APPLICATION_JSON)
        .bodyValue(createPrisonerContactRequest())
        .exchange()
        .expectStatus()
        .isUnauthorized

      webTestClient.post()
        .uri("/sync/prisoner-contact/1")
        .accept(MediaType.APPLICATION_JSON)
        .contentType(MediaType.APPLICATION_JSON)
        .bodyValue(updatePrisonerContactRequest())
        .exchange()
        .expectStatus()
        .isUnauthorized

      webTestClient.delete()
        .uri("/sync/prisoner-contact/1")
        .accept(MediaType.APPLICATION_JSON)
        .exchange()
        .expectStatus()
        .isUnauthorized
    }

    @Test
    fun `Sync endpoints should return forbidden without an authorised role on the token`() {
      webTestClient.get()
        .uri("/sync/prisoner-contact/1")
        .accept(MediaType.APPLICATION_JSON)
        .headers(setAuthorisation(roles = listOf("ROLE_WRONG")))
        .exchange()
        .expectStatus()
        .isForbidden

      webTestClient.post()
        .uri("/sync/prisoner-contact")
        .accept(MediaType.APPLICATION_JSON)
        .contentType(MediaType.APPLICATION_JSON)
        .bodyValue(createPrisonerContactRequest())
        .headers(setAuthorisation(roles = listOf("ROLE_WRONG")))
        .exchange()
        .expectStatus()
        .isForbidden

      webTestClient.put()
        .uri("/sync/prisoner-contact/1")
        .accept(MediaType.APPLICATION_JSON)
        .contentType(MediaType.APPLICATION_JSON)
        .bodyValue(updatePrisonerContactRequest())
        .headers(setAuthorisation(roles = listOf("ROLE_WRONG")))
        .exchange()
        .expectStatus()
        .isForbidden

      webTestClient.delete()
        .uri("/sync/prisoner-contact/1")
        .accept(MediaType.APPLICATION_JSON)
        .headers(setAuthorisation(roles = listOf("ROLE_WRONG")))
        .exchange()
        .expectStatus()
        .isForbidden
    }

    @Test
    fun `should get an existing prisoner contact`() {
      // From base data
      val contactId = 15L
      val prisonerContact = webTestClient.get()
        .uri("/sync/prisoner-contact/{prisonerContactId}", contactId)
        .accept(MediaType.APPLICATION_JSON)
        .headers(setAuthorisation(roles = listOf("ROLE_CONTACTS_MIGRATION")))
        .exchange()
        .expectStatus()
        .isOk
        .expectHeader().contentType(MediaType.APPLICATION_JSON)
        .expectBody(PrisonerContact::class.java)
        .returnResult().responseBody!!

      with(prisonerContact) {
        assertThat(contactId).isEqualTo(15L)
        assertThat(prisonerNumber).isEqualTo("G4793VF")
        assertThat(relationshipType).isEqualTo("UN")
        assertThat(nextOfKin).isFalse
        assertThat(emergencyContact).isFalse
        assertThat(active).isTrue
        assertThat(approvedVisitor).isFalse
        assertThat(awareOfCharges).isFalse
        assertThat(canBeContacted).isFalse
        assertThat(approvedBy).isNull()
        assertThat(approvedTime).isNull()
        assertThat(expiryDate).isNull()
        assertThat(createdAtPrison).isEqualTo("MDI")
        assertThat(comments).isEqualTo("Comment")
        assertThat(createdBy).isEqualTo("TIM")
        assertThat(createdTime).isAfter(LocalDateTime.now().minusYears(1))
        assertThat(amendedBy).isNull()
        assertThat(amendedTime).isNull()
      }
    }

    @Test
    fun `should create a new prisoner contact`() {
      val prisonerContact = webTestClient.post()
        .uri("/sync/prisoner-contact")
        .accept(MediaType.APPLICATION_JSON)
        .contentType(MediaType.APPLICATION_JSON)
        .headers(setAuthorisation(roles = listOf("ROLE_CONTACTS_MIGRATION")))
        .bodyValue(createPrisonerContactRequest())
        .exchange()
        .expectStatus()
        .isOk
        .expectHeader().contentType(MediaType.APPLICATION_JSON)
        .expectBody(PrisonerContact::class.java)
        .returnResult().responseBody!!

      // The created is returned
      with(prisonerContact) {
        assertThat(id).isGreaterThan(29L)
        assertThat(contactId).isEqualTo(1L)
        assertThat(prisonerNumber).isEqualTo("A1234BC")
        assertThat(relationshipType).isEqualTo("Family")
        assertThat(nextOfKin).isTrue
        assertThat(emergencyContact).isFalse
        assertThat(comments).isEqualTo("Create relationship")
        assertThat(active).isTrue
        assertThat(approvedVisitor).isTrue
        assertThat(awareOfCharges).isFalse
        assertThat(canBeContacted).isTrue
        assertThat(approvedBy).isEqualTo("officer456")
        assertThat(approvedTime).isNotNull
        assertThat(expiryDate).isEqualTo(LocalDate.of(2025, 12, 31))
        assertThat(createdAtPrison).isEqualTo("LONDN")
        assertThat(amendedBy).isNull()
        assertThat(amendedTime).isNull()
        assertThat(createdBy).isEqualTo("adminUser")
        assertThat(createdTime).isAfter(LocalDateTime.now().minusMinutes(5))
      }
    }

    @Test
    fun `should create and then update a prisoner contact`() {
      val prisonerContact = webTestClient.post()
        .uri("/sync/prisoner-contact")
        .accept(MediaType.APPLICATION_JSON)
        .contentType(MediaType.APPLICATION_JSON)
        .headers(setAuthorisation(roles = listOf("ROLE_CONTACTS_MIGRATION")))
        .bodyValue(createPrisonerContactRequest())
        .exchange()
        .expectStatus()
        .isOk
        .expectHeader().contentType(MediaType.APPLICATION_JSON)
        .expectBody(PrisonerContact::class.java)
        .returnResult().responseBody!!

      with(prisonerContact) {
        assertThat(id).isGreaterThan(29L)
        assertThat(contactId).isEqualTo(1L)
        assertThat(prisonerNumber).isEqualTo("A1234BC")
        assertThat(relationshipType).isEqualTo("Family")
        assertThat(nextOfKin).isTrue
        assertThat(emergencyContact).isFalse
        assertThat(comments).isEqualTo("Create relationship")
        assertThat(active).isTrue
        assertThat(approvedVisitor).isTrue
        assertThat(awareOfCharges).isFalse
        assertThat(canBeContacted).isTrue
        assertThat(approvedBy).isEqualTo("officer456")
        assertThat(approvedTime).isNotNull
        assertThat(expiryDate).isEqualTo(LocalDate.of(2025, 12, 31))
        assertThat(createdAtPrison).isEqualTo("LONDN")
        assertThat(amendedBy).isNull()
        assertThat(amendedTime).isNull()
        assertThat(createdBy).isEqualTo("adminUser")
        assertThat(createdTime).isAfter(LocalDateTime.now().minusMinutes(5))
      }

      val updatedPrisonerContact = webTestClient.put()
        .uri("/sync/prisoner-contact/{prisonerContactId}", prisonerContact.id)
        .accept(MediaType.APPLICATION_JSON)
        .contentType(MediaType.APPLICATION_JSON)
        .headers(setAuthorisation(roles = listOf("ROLE_CONTACTS_MIGRATION")))
        .bodyValue(updatePrisonerContactRequest())
        .exchange()
        .expectStatus()
        .isOk
        .expectHeader().contentType(MediaType.APPLICATION_JSON)
        .expectBody(PrisonerContact::class.java)
        .returnResult().responseBody!!

      // Check the updated copy
      with(updatedPrisonerContact) {
        assertThat(id).isGreaterThan(29L)
        assertThat(contactId).isEqualTo(1L)
        assertThat(prisonerNumber).isEqualTo("A1234BC")
        assertThat(relationshipType).isEqualTo("Family")
        assertThat(nextOfKin).isTrue
        assertThat(emergencyContact).isFalse
        assertThat(comments).isEqualTo("Updated relationship type to family")
        assertThat(active).isTrue
        assertThat(approvedVisitor).isTrue
        assertThat(awareOfCharges).isFalse
        assertThat(canBeContacted).isTrue
        assertThat(approvedBy).isEqualTo("officer456")
        assertThat(approvedTime).isNotNull
        assertThat(expiryDate).isEqualTo(LocalDate.of(2025, 12, 31))
        assertThat(createdAtPrison).isEqualTo("LONDN")
        assertThat(amendedBy).isEqualTo("UpdatedUser")
        assertThat(amendedTime).isNotNull
      }
    }

    @Test
    fun `should delete an existing prisoner contact`() {
      val prisonerContact = webTestClient.post()
        .uri("/sync/prisoner-contact")
        .accept(MediaType.APPLICATION_JSON)
        .contentType(MediaType.APPLICATION_JSON)
        .headers(setAuthorisation(roles = listOf("ROLE_CONTACTS_MIGRATION")))
        .bodyValue(createPrisonerContactRequest())
        .exchange()
        .expectStatus()
        .isOk
        .expectHeader().contentType(MediaType.APPLICATION_JSON)
        .expectBody(PrisonerContact::class.java)
        .returnResult().responseBody!!

      webTestClient.delete()
        .uri("/sync/prisoner-contact/{prisonerContactId}", prisonerContact.id)
        .accept(MediaType.APPLICATION_JSON)
        .headers(setAuthorisation(roles = listOf("ROLE_CONTACTS_MIGRATION")))
        .exchange()
        .expectStatus()
        .isOk

      webTestClient.get()
        .uri("/sync/prisoner-contact/{prisonerContactId}", prisonerContact.id)
        .accept(MediaType.APPLICATION_JSON)
        .headers(setAuthorisation(roles = listOf("ROLE_CONTACTS_MIGRATION")))
        .exchange()
        .expectStatus()
        .isNotFound
    }

    private fun updatePrisonerContactRequest() =
      UpdatePrisonerContactRequest(
        contactId = 1L,
        prisonerNumber = "A1234BC",
        relationshipType = "Family",
        nextOfKin = true,
        emergencyContact = false,
        comments = "Updated relationship type to family",
        active = true,
        approvedVisitor = true,
        awareOfCharges = false,
        canBeContacted = true,
        approvedBy = "officer456",
        approvedTime = LocalDateTime.now(),
        expiryDate = LocalDate.of(2025, 12, 31),
        createdAtPrison = "LONDN",
        amendedBy = "UpdatedUser",
        updatedTime = LocalDateTime.now(),
      )

    private fun createPrisonerContactRequest() =
      CreatePrisonerContactRequest(
        contactId = 1L,
        prisonerNumber = "A1234BC",
        relationshipType = "Family",
        nextOfKin = true,
        emergencyContact = false,
        comments = "Create relationship",
        active = true,
        approvedVisitor = true,
        awareOfCharges = false,
        canBeContacted = true,
        approvedBy = "officer456",
        approvedTime = LocalDateTime.now(),
        expiryDate = LocalDate.of(2025, 12, 31),
        createdAtPrison = "LONDN",
        createdBy = "adminUser",
        createdTime = LocalDateTime.now(),
      )
  }
}