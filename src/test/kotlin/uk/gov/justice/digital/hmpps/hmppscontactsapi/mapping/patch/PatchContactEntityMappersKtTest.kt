package uk.gov.justice.digital.hmpps.hmppscontactsapi.mapping.patch

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.openapitools.jackson.nullable.JsonNullable
import uk.gov.justice.digital.hmpps.hmppscontactsapi.entity.ContactEntity
import uk.gov.justice.digital.hmpps.hmppscontactsapi.model.request.EstimatedIsOverEighteen
import uk.gov.justice.digital.hmpps.hmppscontactsapi.model.request.patch.PatchContactRequest
import java.time.LocalDate
import java.time.LocalDateTime

class PatchContactEntityMappersKtTest {

  @Test
  fun `mapToResponse should correctly map ContactEntity to PatchContactResponse`() {
    val contactEntity = contactEntity()

    val response = contactEntity.mapToResponse()

    assertThat(response.id).isEqualTo(contactEntity.contactId)
    assertThat(response.title).isEqualTo(contactEntity.title)
    assertThat(response.firstName).isEqualTo(contactEntity.firstName)
    assertThat(response.lastName).isEqualTo(contactEntity.lastName)
    assertThat(response.middleNames).isEqualTo(contactEntity.middleNames)
    assertThat(response.dateOfBirth).isEqualTo(contactEntity.dateOfBirth)
    assertThat(response.estimatedIsOverEighteen).isEqualTo(contactEntity.estimatedIsOverEighteen)
    assertThat(response.createdBy).isEqualTo(contactEntity.createdBy)
    assertThat(response.createdTime).isEqualTo(contactEntity.createdTime)
    assertThat(response.placeOfBirth).isEqualTo(contactEntity.placeOfBirth)
    assertThat(response.active).isEqualTo(contactEntity.active)
    assertThat(response.suspended).isEqualTo(contactEntity.suspended)
    assertThat(response.staffFlag).isEqualTo(contactEntity.staffFlag)
    assertThat(response.deceasedFlag).isEqualTo(contactEntity.isDeceased)
    assertThat(response.deceasedDate).isEqualTo(contactEntity.deceasedDate)
    assertThat(response.coronerNumber).isEqualTo(contactEntity.coronerNumber)
    assertThat(response.gender).isEqualTo(contactEntity.gender)
    assertThat(response.domesticStatus).isEqualTo(contactEntity.domesticStatus)
    assertThat(response.languageCode).isEqualTo(contactEntity.languageCode)
    assertThat(response.nationalityCode).isEqualTo(contactEntity.nationalityCode)
    assertThat(response.interpreterRequired).isEqualTo(contactEntity.interpreterRequired)
    assertThat(response.amendedBy).isEqualTo(contactEntity.amendedBy)
    assertThat(response.amendedTime).isEqualTo(contactEntity.amendedTime)
  }

  @Test
  fun `full patchRequest should correctly update ContactEntity fields`() {
    val originalContact = contactEntity()

    val patchRequest = patchContactRequest()

    val updatedContact = originalContact.patchRequest(patchRequest)

    assertThat(updatedContact.title).isEqualTo(patchRequest.title.get())
    assertThat(updatedContact.firstName).isEqualTo(patchRequest.firstName.get())
    assertThat(updatedContact.lastName).isEqualTo(patchRequest.lastName.get())
    assertThat(updatedContact.middleNames).isEqualTo(patchRequest.middleNames.get())
    assertThat(updatedContact.dateOfBirth).isEqualTo(patchRequest.dateOfBirth.get())
    assertThat(updatedContact.placeOfBirth).isEqualTo(patchRequest.placeOfBirth.get())
    assertThat(updatedContact.active).isEqualTo(patchRequest.active.get())
    assertThat(updatedContact.suspended).isEqualTo(patchRequest.suspended.get())
    assertThat(updatedContact.staffFlag).isEqualTo(patchRequest.staffFlag.get())
    assertThat(updatedContact.coronerNumber).isEqualTo(patchRequest.coronerNumber.get())
    assertThat(updatedContact.gender).isEqualTo(patchRequest.gender.get())
    assertThat(updatedContact.domesticStatus).isEqualTo(patchRequest.domesticStatus.get())
    assertThat(updatedContact.languageCode).isEqualTo(patchRequest.languageCode.get())
    assertThat(updatedContact.nationalityCode).isEqualTo(patchRequest.nationalityCode.get())
    assertThat(updatedContact.interpreterRequired).isEqualTo(patchRequest.interpreterRequired.get())
    assertThat(updatedContact.amendedBy).isEqualTo(patchRequest.updatedBy.get())
    assertThat(updatedContact.amendedTime).isInThePast()
  }

  @Test
  fun `partial patchRequest should correctly update ContactEntity fields`() {
    val originalContact = contactEntity()

    val patchRequest = PatchContactRequest(
      domesticStatus = JsonNullable.of("Married"),
      languageCode = JsonNullable.of("FR"),
      updatedBy = JsonNullable.of("system"),
    )

    val updatedContact = originalContact.patchRequest(patchRequest)

    assertThat(updatedContact.title).isEqualTo(originalContact.title)
    assertThat(updatedContact.firstName).isEqualTo(originalContact.firstName)
    assertThat(updatedContact.lastName).isEqualTo(originalContact.lastName)
    assertThat(updatedContact.middleNames).isEqualTo(originalContact.middleNames)
    assertThat(updatedContact.dateOfBirth).isEqualTo(originalContact.dateOfBirth)
    assertThat(updatedContact.placeOfBirth).isEqualTo(originalContact.placeOfBirth)
    assertThat(updatedContact.active).isEqualTo(originalContact.active)
    assertThat(updatedContact.suspended).isEqualTo(originalContact.suspended)
    assertThat(updatedContact.staffFlag).isEqualTo(originalContact.staffFlag)
    assertThat(updatedContact.coronerNumber).isEqualTo(originalContact.coronerNumber)
    assertThat(updatedContact.gender).isEqualTo(originalContact.gender)
    assertThat(updatedContact.nationalityCode).isEqualTo(originalContact.nationalityCode)
    assertThat(updatedContact.interpreterRequired).isEqualTo(originalContact.interpreterRequired)
    // Updated fields
    assertThat(updatedContact.domesticStatus).isEqualTo(patchRequest.domesticStatus.get())
    assertThat(updatedContact.languageCode).isEqualTo(patchRequest.languageCode.get())
    assertThat(updatedContact.amendedBy).isEqualTo(patchRequest.updatedBy.get())
    assertThat(updatedContact.amendedTime).isAfter(originalContact.amendedTime)
  }

  private fun patchContactRequest(): PatchContactRequest {
    val patchRequest = PatchContactRequest(
      title = JsonNullable.of("Dr"),
      firstName = JsonNullable.of("Jane"),
      lastName = JsonNullable.of("Doe"),
      middleNames = JsonNullable.of(null),
      dateOfBirth = JsonNullable.of(LocalDate.of(1985, 5, 5)),
      deceasedFlag = JsonNullable.of(false),
      deceasedDate = JsonNullable.of(null),
      estimatedIsOverEighteen = JsonNullable.of(EstimatedIsOverEighteen.YES),
      placeOfBirth = JsonNullable.of("Paris"),
      active = JsonNullable.of(false),
      suspended = JsonNullable.of(true),
      staffFlag = JsonNullable.of(true),
      coronerNumber = JsonNullable.of("5678"),
      gender = JsonNullable.of("Female"),
      domesticStatus = JsonNullable.of("Married"),
      languageCode = JsonNullable.of("FR"),
      nationalityCode = JsonNullable.of("FR"),
      interpreterRequired = JsonNullable.of(true),
      updatedBy = JsonNullable.of("system"),
    )
    return patchRequest
  }

  private fun contactEntity(): ContactEntity {
    val contactEntity = ContactEntity(
      contactId = 1L,
      title = "Mr",
      firstName = "John",
      lastName = "Doe",
      middleNames = "A B",
      dateOfBirth = LocalDate.of(1980, 1, 1),
      estimatedIsOverEighteen = EstimatedIsOverEighteen.YES,
      createdBy = "system",
      isDeceased = false,
      deceasedDate = null,
      createdTime = LocalDateTime.now(),
    ).also {
      it.placeOfBirth = "London"
      it.active = true
      it.suspended = false
      it.staffFlag = false
      it.coronerNumber = "1234"
      it.gender = "Male"
      it.domesticStatus = "Single"
      it.languageCode = "EN"
      it.nationalityCode = "GB"
      it.interpreterRequired = false
      it.amendedBy = "admin"
      it.amendedTime = LocalDateTime.now()
    }
    return contactEntity
  }
}
