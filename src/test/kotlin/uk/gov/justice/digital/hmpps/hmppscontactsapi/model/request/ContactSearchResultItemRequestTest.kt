package uk.gov.justice.digital.hmpps.hmppscontactsapi.model.request

import jakarta.validation.Validation
import jakarta.validation.ValidatorFactory
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.time.LocalDate

class ContactSearchResultItemRequestTest {

  private val validatorFactory: ValidatorFactory = Validation.buildDefaultValidatorFactory()
  private val validator = validatorFactory.validator

  @Test
  fun `should fail validation if dateOfBirth is in the future`() {
    val futureDate = LocalDate.now().plusDays(1)
    val request = ContactSearchRequest(
      lastName = "Smith",
      firstName = null,
      middleName = null,
      dateOfBirth = futureDate,
    )

    val violations = validator.validate(request)

    assertThat(violations).hasSize(1)
    assertThat(violations.first().message).isEqualTo("The date of birth must be in the past")
  }

  @Test
  fun `should fail validation if Last Name is not set`() {
    val request = ContactSearchRequest(
      lastName = "",
      firstName = null,
      middleName = null,
      dateOfBirth = null,
    )

    val violations = validator.validate(request)

    assertThat(violations).hasSize(1)
    assertThat(violations.first().message).isEqualTo("Last Name cannot be blank.")
  }

  @Test
  fun `should fail validation if any Name field contain any special characters`() {
    val pastDate = LocalDate.now().minusDays(1)
    val request = ContactSearchRequest(
      lastName = "Smith$",
      firstName = "Smith$",
      middleName = "Smith$",
      dateOfBirth = pastDate,
    )

    val violations = validator.validate(request)

    assertThat(violations).hasSize(3)
    assertThat(violations.map { it.message })
      .containsExactlyInAnyOrder(
        "Special characters are not allowed for First Name.",
        "Special characters are not allowed for Middle Name.",
        "Special characters are not allowed for Last Name.",
      )
  }

  @Test
  fun `should pass validation if dateOfBirth is in the past`() {
    val pastDate = LocalDate.now().minusDays(1)
    val request = ContactSearchRequest(
      lastName = "Smith",
      firstName = null,
      middleName = null,
      dateOfBirth = pastDate,
    )

    val violations = validator.validate(request)

    assertThat(violations).isEmpty()
  }
}
