package uk.gov.justice.digital.hmpps.hmppscontactsapi.mapping.patch

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import uk.gov.justice.digital.hmpps.hmppscontactsapi.entity.ContactEntity
import uk.gov.justice.digital.hmpps.hmppscontactsapi.model.request.EstimatedIsOverEighteen
import uk.gov.justice.digital.hmpps.hmppscontactsapi.model.request.patch.PatchContactRequest
import uk.gov.justice.digital.hmpps.hmppscontactsapi.model.request.patch.util.Patchable
import java.time.LocalDate
import java.time.LocalDateTime

class PatchContactEntityMappersKtTest {

  @Test
  fun `mapToResponse should correctly map ContactEntity to PatchContactResponse`() {
    val contactEntity = contactEntity("EN")

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
    val originalContact = contactEntity("EN")

    val patchRequest = PatchContactRequest(
      languageCode = Patchable.from("FR"),
      updatedBy = "system",
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
    assertThat(updatedContact.domesticStatus).isEqualTo(originalContact.domesticStatus)
    assertThat(updatedContact.nationalityCode).isEqualTo(originalContact.nationalityCode)
    assertThat(updatedContact.interpreterRequired).isEqualTo(originalContact.interpreterRequired)
    assertThat(updatedContact.amendedTime).isInThePast()
    assertThat(updatedContact.languageCode).isEqualTo(patchRequest.languageCode?.get())
    assertThat(updatedContact.amendedBy).isEqualTo(patchRequest.updatedBy)
  }

  @Test
  fun `when language code is not set should correctly update ContactEntity fields`() {
    val originalContact = contactEntity("EN")

    val patchRequest = PatchContactRequest(
      updatedBy = "system",
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
    assertThat(updatedContact.domesticStatus).isEqualTo(originalContact.domesticStatus)
    assertThat(updatedContact.nationalityCode).isEqualTo(originalContact.nationalityCode)
    assertThat(updatedContact.interpreterRequired).isEqualTo(originalContact.interpreterRequired)
    assertThat(updatedContact.amendedTime).isInThePast()
    assertThat(updatedContact.languageCode).isEqualTo(originalContact.languageCode)
    assertThat(updatedContact.amendedBy).isEqualTo(patchRequest.updatedBy)
  }

  @Test
  fun `when language code is null in the entity and request is set with language code then should correctly update ContactEntity fields`() {
    val originalContact = contactEntity(null)

    val patchRequest = PatchContactRequest(
      languageCode = Patchable.from("FR"),
      updatedBy = "system",
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
    assertThat(updatedContact.domesticStatus).isEqualTo(originalContact.domesticStatus)
    assertThat(updatedContact.nationalityCode).isEqualTo(originalContact.nationalityCode)
    assertThat(updatedContact.interpreterRequired).isEqualTo(originalContact.interpreterRequired)
    assertThat(updatedContact.amendedTime).isInThePast()
    assertThat(updatedContact.languageCode).isEqualTo("FR")
    assertThat(updatedContact.amendedBy).isEqualTo(patchRequest.updatedBy)
  }

  @Test
  fun `when languageCode is set to null in patchRequest then should correctly update ContactEntity`() {
    val originalContact = contactEntity("EN")

    val patchRequest = PatchContactRequest(
      languageCode = null,
      updatedBy = "system",
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
    assertThat(updatedContact.domesticStatus).isEqualTo(originalContact.domesticStatus)
    assertThat(updatedContact.interpreterRequired).isEqualTo(originalContact.interpreterRequired)
    assertThat(updatedContact.amendedTime).isInThePast()
    assertThat(updatedContact.nationalityCode).isEqualTo(originalContact.nationalityCode)
    // patched fields
    assertThat(updatedContact.languageCode).isNull()
    assertThat(updatedContact.amendedBy).isEqualTo(patchRequest.updatedBy)
  }

  @Test
  fun `partial patchRequest should correctly update ContactEntity fields`() {
    val originalContact = contactEntity("EN")

    val patchRequest = PatchContactRequest(
      updatedBy = "system",
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
    assertThat(updatedContact.domesticStatus).isEqualTo(originalContact.domesticStatus)
    assertThat(updatedContact.amendedTime).isAfter(originalContact.amendedTime)
    assertThat(updatedContact.languageCode).isEqualTo(originalContact.languageCode)
    // patched fields
    assertThat(updatedContact.amendedBy).isEqualTo(patchRequest.updatedBy)
  }

  private fun contactEntity(languageCode: String?): ContactEntity {
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
      it.languageCode = languageCode
      it.nationalityCode = "GB"
      it.interpreterRequired = false
      it.amendedBy = "admin"
      it.amendedTime = LocalDateTime.now()
    }
    return contactEntity
  }
}
