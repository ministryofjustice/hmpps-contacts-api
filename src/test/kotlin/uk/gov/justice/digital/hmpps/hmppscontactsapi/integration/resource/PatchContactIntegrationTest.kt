package uk.gov.justice.digital.hmpps.hmppscontactsapi.integration.resource

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.CsvSource
import org.junit.jupiter.params.provider.MethodSource
import org.openapitools.jackson.nullable.JsonNullable
import org.springframework.http.MediaType
import org.springframework.test.context.TestPropertySource
import uk.gov.justice.digital.hmpps.hmppscontactsapi.integration.IntegrationTestBase
import uk.gov.justice.digital.hmpps.hmppscontactsapi.model.request.CreateContactRequest
import uk.gov.justice.digital.hmpps.hmppscontactsapi.model.request.EstimatedIsOverEighteen
import uk.gov.justice.digital.hmpps.hmppscontactsapi.model.request.patch.PatchContactRequest
import uk.gov.justice.digital.hmpps.hmppscontactsapi.model.request.patch.PatchContactResponse
import uk.gov.justice.digital.hmpps.hmppscontactsapi.model.response.GetContactResponse
import java.time.LocalDate

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
      assertThat(title).isEqualTo(request.title.get())
      assertThat(lastName).isEqualTo(request.lastName.get())
      assertThat(firstName).isEqualTo(request.firstName.get())
      assertThat(middleNames).isEqualTo(request.middleNames.get())
      assertThat(dateOfBirth).isEqualTo(request.dateOfBirth.get())
      assertThat(estimatedIsOverEighteen).isEqualTo(request.estimatedIsOverEighteen.get())
      assertThat(deceasedFlag).isEqualTo(request.deceasedFlag.get())
      assertThat(deceasedDate).isEqualTo(request.deceasedDate.get())
      assertThat(placeOfBirth).isEqualTo(request.placeOfBirth.get())
      assertThat(active).isEqualTo(request.active.get())
      assertThat(suspended).isEqualTo(request.suspended.get())
      assertThat(staffFlag).isEqualTo(request.staffFlag.get())
      assertThat(coronerNumber).isEqualTo(request.coronerNumber.get())
      assertThat(gender).isEqualTo(request.gender.get())
      assertThat(domesticStatus).isEqualTo(request.domesticStatus.get())
      assertThat(languageCode).isEqualTo(request.languageCode.get())
      assertThat(nationalityCode).isEqualTo(request.nationalityCode.get())
      assertThat(interpreterRequired).isEqualTo(request.interpreterRequired.get())
      assertThat(createdBy).isEqualTo("created")
      assertThat(createdTime).isInThePast()
      assertThat(amendedBy).isEqualTo("JD000001")
      assertThat(amendedTime).isInThePast()
    }
  }

  private fun assertSuccessfullyPatched(contact: GetContactResponse, request: PatchContactRequest) {
    with(contact) {
      assertThat(title).isEqualTo(request.title.get())
      assertThat(lastName).isEqualTo(request.lastName.get())
      assertThat(firstName).isEqualTo(request.firstName.get())
      assertThat(middleNames).isEqualTo(request.middleNames.get())
      assertThat(dateOfBirth).isEqualTo(request.dateOfBirth.get())
      assertThat(estimatedIsOverEighteen).isEqualTo(request.estimatedIsOverEighteen.get())
      assertThat(deceasedDate).isEqualTo(request.deceasedDate.get())
      assertThat(languageCode).isEqualTo(request.languageCode.get())
      assertThat(interpreterRequired).isEqualTo(request.interpreterRequired.get())
      assertThat(createdBy).isEqualTo("created")
      assertThat(createdTime).isInThePast()
    }
  }

  companion object {
    @JvmStatic
    fun allFieldConstraintViolations(): List<Arguments> {
      return listOf(
        Arguments.of("title must be <= 12 characters", aPatchContactRequest().copy(title = JsonNullable.of("Mr".padStart(13)))),
        Arguments.of(
          "lastName must be <= 35 characters",
          aPatchContactRequest().copy(lastName = JsonNullable.of("".padStart(36))),
        ),
        Arguments.of(
          "firstName must be <= 35 characters",
          aPatchContactRequest().copy(firstName = JsonNullable.of("".padStart(36))),
        ),
        Arguments.of(
          "middleNames must be <= 35 characters",
          aPatchContactRequest().copy(middleNames = JsonNullable.of("".padStart(36))),
        ),
      )
    }

    private fun aPatchContactRequest() = PatchContactRequest(
      title = JsonNullable.of("Mr"),
      firstName = JsonNullable.of("John"),
      lastName = JsonNullable.of("Doe"),
      middleNames = JsonNullable.of("William"),
      dateOfBirth = JsonNullable.of(LocalDate.of(1980, 1, 1)),
      estimatedIsOverEighteen = JsonNullable.of(EstimatedIsOverEighteen.YES),
      placeOfBirth = JsonNullable.of("London"),
      active = JsonNullable.of(true),
      suspended = JsonNullable.of(false),
      staffFlag = JsonNullable.of(false),
      deceasedFlag = JsonNullable.of(false),
      deceasedDate = JsonNullable.of(null),
      coronerNumber = JsonNullable.of(null),
      gender = JsonNullable.of("Male"),
      domesticStatus = JsonNullable.of("S"),
      languageCode = JsonNullable.of("BEN"),
      nationalityCode = JsonNullable.of("AZERB"),
      interpreterRequired = JsonNullable.of(false),
      updatedBy = JsonNullable.of("JD000001"),
    )
  }
}
