package uk.gov.justice.digital.hmpps.hmppscontactsapi.integration.resource

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.CsvSource
import org.junit.jupiter.params.provider.MethodSource
import org.springframework.http.MediaType
import org.springframework.test.context.TestPropertySource
import uk.gov.justice.digital.hmpps.hmppscontactsapi.integration.IntegrationTestBase
import uk.gov.justice.digital.hmpps.hmppscontactsapi.model.request.CreateContactRequest
import uk.gov.justice.digital.hmpps.hmppscontactsapi.model.request.EstimatedIsOverEighteen
import uk.gov.justice.digital.hmpps.hmppscontactsapi.model.request.patch.PatchContactRequest
import uk.gov.justice.digital.hmpps.hmppscontactsapi.model.request.patch.PatchContactResponse
import uk.gov.justice.digital.hmpps.hmppscontactsapi.model.response.GetContactResponse
import java.time.LocalDate
import java.time.LocalDateTime

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestPropertySource(properties = ["feature.event.contacts-api.prisoner-contact.amended=true"])
@TestPropertySource(properties = ["feature.events.sns.enabled=false"])
class PatchContactIntegrationTest : IntegrationTestBase() {

  lateinit var contact: GetContactResponse

  @BeforeAll
  fun setUpContact() {
    setupTestApiClient()
    val createContact = CreateContactRequest(
      title = "mr",
      lastName = "last",
      firstName = "first",
      middleNames = "middle",
      dateOfBirth = LocalDate.of(1982, 6, 15),
      createdBy = "created",
    )
    contact = testAPIClient.createAContact(createContact)
  }

  @Test
  fun `should return unauthorized if no token`() {
    webTestClient.patch()
      .uri("/contact/123456")
      .accept(MediaType.APPLICATION_JSON)
      .contentType(MediaType.APPLICATION_JSON)
      .bodyValue(aPatchContactRequest())
      .exchange()
      .expectStatus()
      .isUnauthorized
  }

  @Test
  fun `should return forbidden if no role`() {
    webTestClient.patch()
      .uri("/contact/123456")
      .accept(MediaType.APPLICATION_JSON)
      .contentType(MediaType.APPLICATION_JSON)
      .bodyValue(aPatchContactRequest())
      .headers(setAuthorisation())
      .exchange()
      .expectStatus()
      .isForbidden
  }

  @Test
  fun `should return forbidden if wrong role`() {
    webTestClient.patch()
      .uri("/contact/123456")
      .accept(MediaType.APPLICATION_JSON)
      .contentType(MediaType.APPLICATION_JSON)
      .bodyValue(aPatchContactRequest())
      .headers(setAuthorisation(roles = listOf("ROLE_WRONG")))
      .exchange()
      .expectStatus()
      .isForbidden
  }

  @ParameterizedTest
  @MethodSource("allFieldConstraintViolations")
  fun `should enforce field constraints`(expectedMessage: String, request: PatchContactRequest) {
    val errors = testAPIClient.patchAContactReturnErrors(request, "/contact/${contact.id}")

    assertThat(errors.userMessage).isEqualTo("Validation failure(s): $expectedMessage")
  }

  @ParameterizedTest
  @CsvSource(
    value = [
      "{\"dateOfBirth\": \"1st Jan\"}",
      "{\"dateOfBirth\": \"01-01-1970\"}",
    ],
    delimiter = ';',
  )
  fun `should handle invalid dob formats`(json: String) {
    val errors = testAPIClient.patchAContactReturnErrors(json, "/contact/${contact.id}")

    assertThat(errors.userMessage).isEqualTo("Validation failure: dateOfBirth could not be parsed as a date")
  }

  @Test
  fun `should successfully patch the contact with all fields`() {
    val request = aPatchContactRequest()
    val contactId = contact.id
    val contactReturnedOnPatch = testAPIClient.patchAContact(request, "/contact/$contactId")

    assertContactsAreEqual(contactReturnedOnPatch, request)
    assertSuccessfullyPatched(testAPIClient.getContact(contactId), request)
  }

  private fun assertContactsAreEqual(contact: PatchContactResponse, request: PatchContactRequest) {
    with(contact) {
      assertThat(title).isEqualTo(request.title)
      assertThat(lastName).isEqualTo(request.lastName)
      assertThat(firstName).isEqualTo(request.firstName)
      assertThat(middleNames).isEqualTo(request.middleNames)
      assertThat(dateOfBirth).isEqualTo(request.dateOfBirth)
      assertThat(estimatedIsOverEighteen).isEqualTo(request.estimatedIsOverEighteen)
      assertThat(deceasedFlag).isEqualTo(request.deceasedFlag)
      assertThat(deceasedDate).isEqualTo(request.deceasedDate)
      assertThat(placeOfBirth).isEqualTo(request.placeOfBirth)
      assertThat(active).isEqualTo(request.active)
      assertThat(suspended).isEqualTo(request.suspended)
      assertThat(staffFlag).isEqualTo(request.staffFlag)
      assertThat(coronerNumber).isEqualTo(request.coronerNumber)
      assertThat(gender).isEqualTo(request.gender)
      assertThat(domesticStatus).isEqualTo(request.domesticStatus)
      assertThat(languageCode).isEqualTo(request.languageCode)
      assertThat(nationalityCode).isEqualTo(request.nationalityCode)
      assertThat(interpreterRequired).isEqualTo(request.interpreterRequired)
      assertThat(comments).isEqualTo(request.comments)
      assertThat(createdBy).isEqualTo("created")
      assertThat(createdTime).isInThePast()
      assertThat(amendedBy).isEqualTo("JD000001")
      assertThat(amendedTime).isEqualTo(request.updatedTime)
    }
  }

  private fun assertSuccessfullyPatched(contact: GetContactResponse, request: PatchContactRequest) {
    with(contact) {
      assertThat(title).isEqualTo(request.title)
      assertThat(lastName).isEqualTo(request.lastName)
      assertThat(firstName).isEqualTo(request.firstName)
      assertThat(middleNames).isEqualTo(request.middleNames)
      assertThat(dateOfBirth).isEqualTo(request.dateOfBirth)
      assertThat(estimatedIsOverEighteen).isEqualTo(request.estimatedIsOverEighteen)
      assertThat(deceasedDate).isEqualTo(request.deceasedDate)
      assertThat(languageCode).isEqualTo(request.languageCode)
      assertThat(interpreterRequired).isEqualTo(request.interpreterRequired)
      assertThat(createdBy).isEqualTo("created")
      assertThat(createdTime).isInThePast()
    }
  }

  companion object {
    @JvmStatic
    fun allFieldConstraintViolations(): List<Arguments> {
      return listOf(
        Arguments.of("title must be <= 12 characters", aPatchContactRequest().copy(title = "".padStart(13))),
        Arguments.of(
          "lastName must be <= 35 characters",
          aPatchContactRequest().copy(lastName = "".padStart(36)),
        ),
        Arguments.of(
          "firstName must be <= 35 characters",
          aPatchContactRequest().copy(firstName = "".padStart(36)),
        ),
        Arguments.of(
          "middleNames must be <= 35 characters",
          aPatchContactRequest().copy(middleNames = "".padStart(36)),
        ),
      )
    }

    private fun aPatchContactRequest() = PatchContactRequest(
      title = "Mr",
      firstName = "John",
      lastName = "Doe",
      middleNames = "William",
      dateOfBirth = LocalDate.of(1980, 1, 1),
      estimatedIsOverEighteen = EstimatedIsOverEighteen.YES,
      placeOfBirth = "London",
      active = true,
      suspended = false,
      staffFlag = false,
      deceasedFlag = false,
      deceasedDate = null,
      coronerNumber = null,
      gender = "Male",
      domesticStatus = "S",
      languageCode = "BEN",
      nationalityCode = "AZERB",
      interpreterRequired = false,
      comments = "This contact has special dietary requirements.",
      updatedBy = "JD000001",
      updatedTime = LocalDateTime.now(),
    )
  }
}
