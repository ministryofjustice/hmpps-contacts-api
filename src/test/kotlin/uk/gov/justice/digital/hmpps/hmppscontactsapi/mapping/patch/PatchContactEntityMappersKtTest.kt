package uk.gov.justice.digital.hmpps.hmppscontactsapi.mapping.patch

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import uk.gov.justice.digital.hmpps.hmppscontactsapi.entity.ContactEntity
import uk.gov.justice.digital.hmpps.hmppscontactsapi.model.request.EstimatedIsOverEighteen
import uk.gov.justice.digital.hmpps.hmppscontactsapi.model.request.patch.PatchContactRequest
import java.time.LocalDate
import java.time.LocalDateTime

class ContactEntityMappingTest {

  @Test
  fun `mapToResponse should correctly map ContactEntity to PatchContactResponse`() {
    val contactEntity = contactEntity()

    val response = contactEntity.mapToResponse()

    assertEquals(contactEntity.contactId, response.id)
    assertEquals(contactEntity.title, response.title)
    assertEquals(contactEntity.firstName, response.firstName)
    assertEquals(contactEntity.lastName, response.lastName)
    assertEquals(contactEntity.middleNames, response.middleNames)
    assertEquals(contactEntity.dateOfBirth, response.dateOfBirth)
    assertEquals(contactEntity.estimatedIsOverEighteen, response.estimatedIsOverEighteen)
    assertEquals(contactEntity.createdBy, response.createdBy)
    assertEquals(contactEntity.createdTime, response.createdTime)
    assertEquals(contactEntity.placeOfBirth, response.placeOfBirth)
    assertEquals(contactEntity.active, response.active)
    assertEquals(contactEntity.suspended, response.suspended)
    assertEquals(contactEntity.staffFlag, response.staffFlag)
    assertEquals(contactEntity.isDeceased, response.deceasedFlag)
    assertEquals(contactEntity.deceasedDate, response.deceasedDate)
    assertEquals(contactEntity.coronerNumber, response.coronerNumber)
    assertEquals(contactEntity.gender, response.gender)
    assertEquals(contactEntity.domesticStatus, response.domesticStatus)
    assertEquals(contactEntity.languageCode, response.languageCode)
    assertEquals(contactEntity.nationalityCode, response.nationalityCode)
    assertEquals(contactEntity.interpreterRequired, response.interpreterRequired)
    assertEquals(contactEntity.comments, response.comments)
    assertEquals(contactEntity.amendedBy, response.amendedBy)
    assertEquals(contactEntity.amendedTime, response.amendedTime)
  }

  @Test
  fun `patchRequest should correctly update ContactEntity fields`() {
    val originalContact = contactEntity()

    val patchRequest = patchContactRequest()

    val updatedContact = originalContact.patchRequest(patchRequest)

    assertEquals(patchRequest.title, updatedContact.title)
    assertEquals(patchRequest.firstName, updatedContact.firstName)
    assertEquals(patchRequest.lastName, updatedContact.lastName)
    assertEquals(originalContact.middleNames, updatedContact.middleNames) // unchanged
    assertEquals(patchRequest.dateOfBirth, updatedContact.dateOfBirth)
    assertEquals(patchRequest.placeOfBirth, updatedContact.placeOfBirth)
    assertEquals(patchRequest.active, updatedContact.active)
    assertEquals(patchRequest.suspended, updatedContact.suspended)
    assertEquals(patchRequest.staffFlag, updatedContact.staffFlag)
    assertEquals(patchRequest.coronerNumber, updatedContact.coronerNumber)
    assertEquals(patchRequest.gender, updatedContact.gender)
    assertEquals(patchRequest.domesticStatus, updatedContact.domesticStatus)
    assertEquals(patchRequest.languageCode, updatedContact.languageCode)
    assertEquals(patchRequest.nationalityCode, updatedContact.nationalityCode)
    assertEquals(patchRequest.interpreterRequired, updatedContact.interpreterRequired)
    assertEquals(patchRequest.comments, updatedContact.comments)
    assertEquals(patchRequest.updatedBy, updatedContact.amendedBy)
    assertEquals(patchRequest.updatedTime, updatedContact.amendedTime)
  }

  private fun patchContactRequest(): PatchContactRequest {
    val patchRequest = PatchContactRequest(
      title = "Dr",
      firstName = "Jane",
      lastName = "Doe",
      middleNames = null,
      dateOfBirth = LocalDate.of(1985, 5, 5),
      deceasedFlag = false,
      deceasedDate = null,
      estimatedIsOverEighteen = EstimatedIsOverEighteen.YES,
      placeOfBirth = "Paris",
      active = false,
      suspended = true,
      staffFlag = true,
      coronerNumber = "5678",
      gender = "Female",
      domesticStatus = "Married",
      languageCode = "FR",
      nationalityCode = "FR",
      interpreterRequired = true,
      comments = "Updated comments",
      updatedBy = "system",
      updatedTime = LocalDateTime.now(),
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
      it.comments = "No comments"
      it.amendedBy = "admin"
      it.amendedTime = LocalDateTime.now()
    }
    return contactEntity
  }
}
