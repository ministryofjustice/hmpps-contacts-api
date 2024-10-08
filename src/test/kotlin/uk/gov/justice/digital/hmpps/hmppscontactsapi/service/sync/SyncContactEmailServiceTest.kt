package uk.gov.justice.digital.hmpps.hmppscontactsapi.service.sync

import jakarta.persistence.EntityNotFoundException
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito.mock
import org.mockito.kotlin.any
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoInteractions
import org.mockito.kotlin.whenever
import uk.gov.justice.digital.hmpps.hmppscontactsapi.entity.ContactEmailEntity
import uk.gov.justice.digital.hmpps.hmppscontactsapi.entity.ContactEntity
import uk.gov.justice.digital.hmpps.hmppscontactsapi.mapping.sync.toEntity
import uk.gov.justice.digital.hmpps.hmppscontactsapi.model.request.EstimatedIsOverEighteen
import uk.gov.justice.digital.hmpps.hmppscontactsapi.model.request.sync.CreateContactEmailRequest
import uk.gov.justice.digital.hmpps.hmppscontactsapi.model.request.sync.UpdateContactEmailRequest
import uk.gov.justice.digital.hmpps.hmppscontactsapi.repository.ContactEmailRepository
import uk.gov.justice.digital.hmpps.hmppscontactsapi.repository.ContactRepository
import java.time.LocalDateTime
import java.util.*

class SyncContactEmailServiceTest {
  private val contactRepository: ContactRepository = mock()
  private val contactEmailRepository: ContactEmailRepository = mock()
  private val syncService = SyncContactEmailService(contactRepository, contactEmailRepository)

  @Nested
  inner class ContactEmailTests {
    @Test
    fun `should get a contact email by ID`() {
      whenever(contactEmailRepository.findById(1L)).thenReturn(Optional.of(contactEmailEntity()))
      val contactEmail = syncService.getContactEmailById(1L)
      with(contactEmail) {
        assertThat(emailType).isEqualTo("Personal")
        assertThat(emailAddress).isEqualTo("test@test.com")
        assertThat(emailAddress).isEqualTo("test@test.com")
        assertThat(primaryEmail).isTrue()
      }
      verify(contactEmailRepository).findById(1L)
    }

    @Test
    fun `should fail to get a contact email by ID when not found`() {
      whenever(contactEmailRepository.findById(1L)).thenReturn(Optional.empty())
      assertThrows<EntityNotFoundException> {
        syncService.getContactEmailById(1L)
      }
      verify(contactEmailRepository).findById(1L)
    }

    @Test
    fun `should create a contact email`() {
      val request = createContactEmailRequest()
      whenever(contactRepository.findById(1L)).thenReturn(Optional.of(contactEntity()))
      whenever(contactEmailRepository.saveAndFlush(request.toEntity())).thenReturn(request.toEntity())

      val contactEmail = syncService.createContactEmail(request)
      val emailCaptor = argumentCaptor<ContactEmailEntity>()

      verify(contactEmailRepository).saveAndFlush(emailCaptor.capture())

      // Checks the entity saved
      with(emailCaptor.firstValue) {
        assertThat(emailType).isEqualTo(request.emailType)
        assertThat(emailAddress).isEqualTo(request.emailAddress)
        assertThat(primaryEmail).isEqualTo(request.primaryEmail)
        assertThat(createdBy).isEqualTo(request.createdBy)
      }

      // Checks the model response
      with(contactEmail) {
        assertThat(contactEmailId).isEqualTo(0L)
        assertThat(emailType).isEqualTo(request.emailType)
        assertThat(emailAddress).isEqualTo(request.emailAddress)
        assertThat(primaryEmail).isEqualTo(request.primaryEmail)
        assertThat(createdBy).isEqualTo(request.createdBy)
      }

      verify(contactRepository).findById(1L)
    }

    @Test
    fun `should fail to create a contact email when the contact ID is not present`() {
      val request = createContactEmailRequest()
      whenever(contactRepository.findById(1L)).thenReturn(Optional.empty())
      assertThrows<EntityNotFoundException> {
        syncService.createContactEmail(request)
      }
      verifyNoInteractions(contactEmailRepository)
    }

    @Test
    fun `should delete contact email by ID`() {
      whenever(contactEmailRepository.findById(1L)).thenReturn(Optional.of(contactEmailEntity()))
      syncService.deleteContactEmail(1L)
      verify(contactEmailRepository).deleteById(1L)
    }

    @Test
    fun `should fail to delete contact email by ID when not found`() {
      whenever(contactEmailRepository.findById(1L)).thenReturn(Optional.empty())
      assertThrows<EntityNotFoundException> {
        syncService.deleteContactEmail(1L)
      }
      verify(contactEmailRepository).findById(1L)
    }

    @Test
    fun `should update a contact email by ID`() {
      val request = updateContactEmailRequest()
      whenever(contactRepository.findById(1L)).thenReturn(Optional.of(contactEntity()))
      whenever(contactEmailRepository.findById(1L)).thenReturn(Optional.of(request.toEntity()))
      whenever(contactEmailRepository.saveAndFlush(any())).thenReturn(request.toEntity())

      val updated = syncService.updateContactEmail(1L, request)

      val emailCaptor = argumentCaptor<ContactEmailEntity>()

      verify(contactEmailRepository).saveAndFlush(emailCaptor.capture())

      // Checks the entity saved
      with(emailCaptor.firstValue) {
        assertThat(emailType).isEqualTo(request.emailType)
        assertThat(emailAddress).isEqualTo(request.emailAddress)
        assertThat(primaryEmail).isEqualTo(request.primaryEmail)
        assertThat(amendedBy).isEqualTo(request.updatedBy)
        assertThat(amendedTime).isEqualTo(request.updatedTime)
      }

      // Checks the model returned
      with(updated) {
        assertThat(emailType).isEqualTo(request.emailType)
        assertThat(emailAddress).isEqualTo(request.emailAddress)
        assertThat(primaryEmail).isEqualTo(request.primaryEmail)
        assertThat(amendedBy).isEqualTo(request.updatedBy)
        assertThat(amendedTime).isEqualTo(request.updatedTime)
      }
    }

    @Test
    fun `should fail to update a contact email by ID when contact is not found`() {
      val updateRequest = updateContactEmailRequest()
      whenever(contactRepository.findById(1L)).thenReturn(Optional.empty())
      assertThrows<EntityNotFoundException> {
        syncService.updateContactEmail(1L, updateRequest)
      }
      verifyNoInteractions(contactEmailRepository)
    }

    @Test
    fun `should fail to update a contact email when contact email is not found`() {
      val updateRequest = updateContactEmailRequest()
      whenever(contactRepository.findById(1L)).thenReturn(Optional.of(contactEntity(1L)))
      whenever(contactEmailRepository.findById(updateRequest.contactId)).thenReturn(Optional.empty())
      assertThrows<EntityNotFoundException> {
        syncService.updateContactEmail(1L, updateRequest)
      }
      verify(contactRepository).findById(updateRequest.contactId)
      verify(contactEmailRepository).findById(1L)
    }
  }

  private fun updateContactEmailRequest(contactId: Long = 1L) =
    UpdateContactEmailRequest(
      contactId = contactId,
      emailType = "Personal",
      emailAddress = "test@test.com",
      primaryEmail = true,
      updatedBy = "TEST",
      updatedTime = LocalDateTime.now(),
    )

  private fun createContactEmailRequest() =
    CreateContactEmailRequest(
      contactId = 1L,
      emailType = "Personal",
      emailAddress = "test@test.com",
      primaryEmail = true,
      createdBy = "TEST",
    )

  private fun contactEntity(contactId: Long = 1L) =
    ContactEntity(
      contactId = contactId,
      title = "Mr",
      firstName = "John",
      middleName = null,
      lastName = "Smith",
      dateOfBirth = null,
      estimatedIsOverEighteen = EstimatedIsOverEighteen.NO,
      isDeceased = false,
      deceasedDate = null,
      createdBy = "TEST",
      createdTime = LocalDateTime.now(),
    )

  private fun contactEmailEntity() =
    ContactEmailEntity(
      contactEmailId = 1L,
      contactId = 1L,
      emailType = "Personal",
      emailAddress = "test@test.com",
      primaryEmail = true,
      createdBy = "TEST",
    )

  private fun UpdateContactEmailRequest.toEntity(contactEmailId: Long = 1L): ContactEmailEntity {
    val updatedBy = this.updatedBy
    val updatedTime = this.updatedTime

    return ContactEmailEntity(
      contactEmailId = contactEmailId,
      contactId = this.contactId,
      emailType = this.emailType,
      emailAddress = this.emailAddress,
      primaryEmail = this.primaryEmail,
      createdBy = "TEST",
    ).also {
      it.amendedBy = updatedBy
      it.amendedTime = updatedTime
    }
  }
}