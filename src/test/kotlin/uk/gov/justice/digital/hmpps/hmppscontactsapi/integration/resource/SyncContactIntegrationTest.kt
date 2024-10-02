package uk.gov.justice.digital.hmpps.hmppscontactsapi.integration.resource

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.http.MediaType
import uk.gov.justice.digital.hmpps.hmppscontactsapi.integration.IntegrationTestBase
import uk.gov.justice.digital.hmpps.hmppscontactsapi.model.request.EstimatedIsOverEighteen
import uk.gov.justice.digital.hmpps.hmppscontactsapi.model.request.sync.CreateContactRequest
import uk.gov.justice.digital.hmpps.hmppscontactsapi.model.request.sync.UpdateContactRequest
import uk.gov.justice.digital.hmpps.hmppscontactsapi.model.response.sync.Contact
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime

class SyncContactIntegrationTest : IntegrationTestBase() {

  @Nested
  inner class ContactSyncTests {
    private var savedContactId = 0L

    @BeforeEach
    fun initialiseData() {
      val request = aMinimalCreateContactRequest()
      val contactReturnedOnCreate = testAPIClient.createAFullContact(request)
      assertContactsAreEqualExcludingTimestamps(contactReturnedOnCreate, request)
      assertThat(contactReturnedOnCreate).isEqualTo(testAPIClient.getContact(contactReturnedOnCreate.id))
      savedContactId = contactReturnedOnCreate.id
    }

    @Test
    fun `Sync endpoints should return unauthorized if no token provided`() {
      webTestClient.get()
        .uri("/sync/contact-restriction/1")
        .accept(MediaType.APPLICATION_JSON)
        .exchange()
        .expectStatus()
        .isUnauthorized

      webTestClient.put()
        .uri("/sync/contact-restriction")
        .accept(MediaType.APPLICATION_JSON)
        .contentType(MediaType.APPLICATION_JSON)
        .bodyValue(createContactRequest(savedContactId))
        .exchange()
        .expectStatus()
        .isUnauthorized

      webTestClient.post()
        .uri("/sync/contact-restriction/1")
        .accept(MediaType.APPLICATION_JSON)
        .contentType(MediaType.APPLICATION_JSON)
        .bodyValue(updateContactRequest(savedContactId))
        .exchange()
        .expectStatus()
        .isUnauthorized

      webTestClient.delete()
        .uri("/sync/contact-restriction/1")
        .accept(MediaType.APPLICATION_JSON)
        .exchange()
        .expectStatus()
        .isUnauthorized
    }

    @Test
    fun `Sync endpoints should return forbidden without an authorised role on the token`() {
      webTestClient.get()
        .uri("/sync/contact-restriction/1")
        .accept(MediaType.APPLICATION_JSON)
        .headers(setAuthorisation(roles = listOf("ROLE_WRONG")))
        .exchange()
        .expectStatus()
        .isForbidden

      webTestClient.put()
        .uri("/sync/contact-restriction")
        .accept(MediaType.APPLICATION_JSON)
        .contentType(MediaType.APPLICATION_JSON)
        .bodyValue(createContactRequest(savedContactId))
        .headers(setAuthorisation(roles = listOf("ROLE_WRONG")))
        .exchange()
        .expectStatus()
        .isForbidden

      webTestClient.post()
        .uri("/sync/contact-restriction/1")
        .accept(MediaType.APPLICATION_JSON)
        .contentType(MediaType.APPLICATION_JSON)
        .bodyValue(updateContactRequest(savedContactId))
        .headers(setAuthorisation(roles = listOf("ROLE_WRONG")))
        .exchange()
        .expectStatus()
        .isForbidden

      webTestClient.delete()
        .uri("/sync/contact-restriction/1")
        .accept(MediaType.APPLICATION_JSON)
        .headers(setAuthorisation(roles = listOf("ROLE_WRONG")))
        .exchange()
        .expectStatus()
        .isForbidden
    }

    @Test
    fun `should get an existing contact`() {
      // From base data
      val contactId = 2L
      val contact = webTestClient.get()
        .uri("/sync/contact-restriction/{contactId}", contactId)
        .accept(MediaType.APPLICATION_JSON)
        .headers(setAuthorisation(roles = listOf("ROLE_CONTACTS_MIGRATION")))
        .exchange()
        .expectStatus()
        .isOk
        .expectHeader().contentType(MediaType.APPLICATION_JSON)
        .expectBody(Contact::class.java)
        .returnResult().responseBody!!

      with(contact) {
        assertThat(title).isEqualTo("PERSONAL")
        assertThat(lastName).isEqualTo("miss.last@hotmail.com")
        assertThat(active).isTrue()
      }
    }

    @Test
    fun `should create a new contact`() {
      val contact = webTestClient.put()
        .uri("/sync/contact-restriction")
        .accept(MediaType.APPLICATION_JSON)
        .contentType(MediaType.APPLICATION_JSON)
        .headers(setAuthorisation(roles = listOf("ROLE_CONTACTS_MIGRATION")))
        .bodyValue(createContactRequest(savedContactId))
        .exchange()
        .expectStatus()
        .isOk
        .expectHeader().contentType(MediaType.APPLICATION_JSON)
        .expectBody(Contact::class.java)
        .returnResult().responseBody!!

      // The created is returned
      with(contact) {
        assertThat(id).isGreaterThan(3)
        assertThat(title).isEqualTo("Mr")
        assertThat(firstName).isEqualTo("John")
        assertThat(lastName).isEqualTo("Doe")
        assertThat(middleName).isEqualTo("William")
        assertThat(dateOfBirth).isEqualTo(LocalDate.of(1980, 1, 1))
        assertThat(estimatedIsOverEighteen).isEqualTo(EstimatedIsOverEighteen.YES)
        assertThat(createdBy).isEqualTo("JD000001")
        assertThat(createdTime).isEqualTo(LocalDateTime.of(2024, 1, 1, 0, 0, 0))
        assertThat(contactTypeCode).isEqualTo("PERSON")
        assertThat(placeOfBirth).isEqualTo("London")
        assertThat(active).isTrue
        assertThat(suspended).isFalse
        assertThat(staffFlag).isFalse
        assertThat(deceasedFlag).isFalse
        assertThat(deceasedDate).isNull()
        assertThat(coronerNumber).isNull()
        assertThat(gender).isEqualTo("Male")
        assertThat(maritalStatus).isEqualTo("Single")
        assertThat(languageCode).isEqualTo("EN")
        assertThat(nationalityCode).isEqualTo("GB")
        assertThat(interpreterRequired).isFalse
        assertThat(comments).isEqualTo("Special requirements for ")
        assertThat(amendedBy).isEqualTo("JD000002")
        assertThat(amendedTime).isEqualTo(Instant.parse("2024-01-02T12:00:00Z"))
      }
    }

    @Test
    fun `should create and then update a contact`() {
      val contact = webTestClient.put()
        .uri("/sync/contact-restriction")
        .accept(MediaType.APPLICATION_JSON)
        .contentType(MediaType.APPLICATION_JSON)
        .headers(setAuthorisation(roles = listOf("ROLE_CONTACTS_MIGRATION")))
        .bodyValue(createContactRequest(savedContactId))
        .exchange()
        .expectStatus()
        .isOk
        .expectHeader().contentType(MediaType.APPLICATION_JSON)
        .expectBody(Contact::class.java)
        .returnResult().responseBody!!

      with(contact) {
        assertThat(title).isEqualTo("Personal")
        assertThat(lastName).isEqualTo("test@test.co.uk")
        assertThat(active).isTrue()
        assertThat(createdBy).isEqualTo("CREATE")
        assertThat(createdTime).isAfter(LocalDateTime.now().minusMinutes(5))
      }

      val updatedRestriction = webTestClient.post()
        .uri("/sync/contact-restriction/{contactId}", contact.id)
        .accept(MediaType.APPLICATION_JSON)
        .contentType(MediaType.APPLICATION_JSON)
        .headers(setAuthorisation(roles = listOf("ROLE_CONTACTS_MIGRATION")))
        .bodyValue(updateContactRequest(savedContactId))
        .exchange()
        .expectStatus()
        .isOk
        .expectHeader().contentType(MediaType.APPLICATION_JSON)
        .expectBody(Contact::class.java)
        .returnResult().responseBody!!

      // Check the updated copy
      with(updatedRestriction) {
        assertThat(id).isGreaterThan(3)
        assertThat(title).isEqualTo("Mr")
        assertThat(firstName).isEqualTo("John")
        assertThat(lastName).isEqualTo("Doe")
        assertThat(middleName).isEqualTo("William")
        assertThat(dateOfBirth).isEqualTo(LocalDate.of(1980, 1, 1))
        assertThat(estimatedIsOverEighteen).isEqualTo(EstimatedIsOverEighteen.YES)
        assertThat(createdBy).isEqualTo("JD000001")
        assertThat(createdTime).isEqualTo(LocalDateTime.of(2024, 1, 1, 0, 0, 0))
        assertThat(contactTypeCode).isEqualTo("PERSON")
        assertThat(placeOfBirth).isEqualTo("London")
        assertThat(active).isTrue
        assertThat(suspended).isFalse
        assertThat(staffFlag).isFalse
        assertThat(deceasedFlag).isFalse
        assertThat(deceasedDate).isNull()
        assertThat(coronerNumber).isNull()
        assertThat(gender).isEqualTo("Male")
        assertThat(maritalStatus).isEqualTo("Single")
        assertThat(languageCode).isEqualTo("EN")
        assertThat(nationalityCode).isEqualTo("GB")
        assertThat(interpreterRequired).isFalse
        assertThat(comments).isEqualTo("Special requirements for ")
        assertThat(amendedBy).isEqualTo("JD000002")
        assertThat(amendedTime).isEqualTo(Instant.parse("2024-01-02T12:00:00Z"))
      }
    }

    @Test
    fun `should delete an existing contact`() {
      val contactId = 3L

      webTestClient.delete()
        .uri("/sync/contact-restriction/{contactId}", contactId)
        .accept(MediaType.APPLICATION_JSON)
        .headers(setAuthorisation(roles = listOf("ROLE_CONTACTS_MIGRATION")))
        .exchange()
        .expectStatus()
        .isOk

      webTestClient.get()
        .uri("/sync/contact-restriction/{contactId}", contactId)
        .accept(MediaType.APPLICATION_JSON)
        .headers(setAuthorisation(roles = listOf("ROLE_CONTACTS_MIGRATION")))
        .exchange()
        .expectStatus()
        .isNotFound
    }

    private fun assertContactsAreEqualExcludingTimestamps(contact: Contact, request: CreateContactRequest) {
      with(contact) {
        assertThat(title).isEqualTo(request.title)
        assertThat(lastName).isEqualTo(request.lastName)
        assertThat(firstName).isEqualTo(request.firstName)
        assertThat(middleName).isEqualTo(request.middleName)
        assertThat(dateOfBirth).isEqualTo(request.dateOfBirth)
        if (request.estimatedIsOverEighteen != null) {
          assertThat(estimatedIsOverEighteen).isEqualTo(request.estimatedIsOverEighteen)
        }
        assertThat(createdBy).isEqualTo(request.createdBy)
      }
    }

    private fun updateContactRequest(contactId: Long) =
      UpdateContactRequest(
        firstName = "John",
        lastName = "Doe",
        middleName = "William",
        dateOfBirth = LocalDate.of(1980, 1, 1),
        estimatedIsOverEighteen = EstimatedIsOverEighteen.YES,
        contactTypeCode = "PERSON",
        placeOfBirth = "London",
        active = true,
        suspended = false,
        staffFlag = false,
        deceasedFlag = false,
        deceasedDate = null,
        coronerNumber = null,
        gender = "Male",
        maritalStatus = "Single",
        languageCode = "EN",
        nationalityCode = "GB",
        interpreterRequired = false,
        comments = "Special requirements for contact.",
        updatedBy = "UPDATE",
        updatedTime = LocalDateTime.now(),
      )

    private fun createContactRequest(contactId: Long) =
      CreateContactRequest(
        firstName = "John",
        lastName = "Doe",
        middleName = "William",
        dateOfBirth = LocalDate.of(1980, 1, 1),
        estimatedIsOverEighteen = EstimatedIsOverEighteen.YES,
        createdBy = "JD000001",
        createdTime = LocalDateTime.of(2024, 1, 1, 0, 0, 0),
        contactTypeCode = "PERSON",
        placeOfBirth = "London",
        active = true,
        suspended = false,
        staffFlag = false,
        deceasedFlag = false,
        deceasedDate = null,
        coronerNumber = null,
        gender = "Male",
        maritalStatus = "Single",
        languageCode = "EN",
        nationalityCode = "GB",
        interpreterRequired = false,
        comments = "Special requirements for contact.",
      )
  }

  private fun aMinimalCreateContactRequest() = CreateContactRequest(
    firstName = "John",
    lastName = "Doe",
    middleName = "William",
    dateOfBirth = LocalDate.of(1980, 1, 1),
    estimatedIsOverEighteen = EstimatedIsOverEighteen.YES,
    createdBy = "JD000001",
    createdTime = LocalDateTime.of(2024, 1, 1, 0, 0, 0),
    contactTypeCode = "PERSON",
    placeOfBirth = "London",
    active = true,
    suspended = false,
    staffFlag = false,
    deceasedFlag = false,
    deceasedDate = null,
    coronerNumber = null,
    gender = "Male",
    maritalStatus = "Single",
    languageCode = "EN",
    nationalityCode = "GB",
    interpreterRequired = false,
    comments = "Special requirements for contact.",
  )
}
