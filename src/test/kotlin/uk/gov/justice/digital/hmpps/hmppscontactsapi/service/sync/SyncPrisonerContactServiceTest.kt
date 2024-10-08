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
import org.mockito.kotlin.whenever
import uk.gov.justice.digital.hmpps.hmppscontactsapi.entity.PrisonerContactEntity
import uk.gov.justice.digital.hmpps.hmppscontactsapi.mapping.sync.toEntity
import uk.gov.justice.digital.hmpps.hmppscontactsapi.model.request.sync.CreatePrisonerContactRequest
import uk.gov.justice.digital.hmpps.hmppscontactsapi.model.request.sync.UpdatePrisonerContactRequest
import uk.gov.justice.digital.hmpps.hmppscontactsapi.repository.PrisonerContactRepository
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*

class SyncPrisonerContactServiceTest {
  private val prisonerContactRepository: PrisonerContactRepository = mock()
  private val syncService = SyncPrisonerContactService(prisonerContactRepository)

  @Nested
  inner class PrisonerContactTests {
    @Test
    fun `should get a prisoner contact by ID`() {
      whenever(prisonerContactRepository.findById(1L)).thenReturn(Optional.of(contactEntity()))
      val prisonerContact = syncService.getPrisonerContactById(1L)
      with(prisonerContact) {
        assertThat(id).isEqualTo(1L)
        assertThat(contactId).isEqualTo(12345L)
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
        assertThat(approvedTime).isAfter(LocalDateTime.now().minusMinutes(5))
        assertThat(expiryDate).isEqualTo(LocalDate.of(2025, 12, 31))
        assertThat(createdAtPrison).isEqualTo("LONDN")
        assertThat(createdBy).isEqualTo("TEST")
        assertThat(createdTime).isAfter(LocalDateTime.now().minusMinutes(5))
        assertThat(amendedBy).isEqualTo("adminUser")
        assertThat(amendedTime).isAfter(LocalDateTime.now().minusMinutes(5))
      }
      verify(prisonerContactRepository).findById(1L)
    }

    @Test
    fun `should fail to get a prisoner contact by ID when not found`() {
      whenever(prisonerContactRepository.findById(1L)).thenReturn(Optional.empty())
      assertThrows<EntityNotFoundException> {
        syncService.getPrisonerContactById(1L)
      }
      verify(prisonerContactRepository).findById(1L)
    }

    @Test
    fun `should create a prisoner contact`() {
      val request = createPrisonerContactRequest()
      whenever(prisonerContactRepository.saveAndFlush(request.toEntity())).thenReturn(request.toEntity())

      val contact = syncService.createPrisonerContact(request)
      val contactCaptor = argumentCaptor<PrisonerContactEntity>()

      verify(prisonerContactRepository).saveAndFlush(contactCaptor.capture())

      // Checks the entity saved
      with(contactCaptor.firstValue) {
        assertThat(contactId).isEqualTo(12345L)
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
        assertThat(createdBy).isEqualTo("adminUser")
        assertThat(createdTime).isAfter(LocalDateTime.now().minusMinutes(5))
        assertThat(amendedBy).isNull()
        assertThat(amendedTime).isNull()
      }

      // Checks the model response
      with(contact) {
        assertThat(contactId).isEqualTo(12345L)
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
        assertThat(createdBy).isEqualTo("adminUser")
        assertThat(createdTime).isAfter(LocalDateTime.now().minusMinutes(5))
        assertThat(amendedBy).isNull()
        assertThat(amendedTime).isNull()
      }
    }

    @Test
    fun `should delete prisoner contact by ID`() {
      whenever(prisonerContactRepository.findById(1L)).thenReturn(Optional.of(contactEntity()))
      syncService.deletePrisonerContact(1L)
      verify(prisonerContactRepository).deleteById(1L)
    }

    @Test
    fun `should fail to delete prisoner contact by ID when not found`() {
      whenever(prisonerContactRepository.findById(1L)).thenReturn(Optional.empty())
      assertThrows<EntityNotFoundException> {
        syncService.deletePrisonerContact(1L)
      }
      verify(prisonerContactRepository).findById(1L)
    }

    @Test
    fun `should update a prisoner contact by ID`() {
      val request = updatePrisonerContactRequest()
      val prisonerContactID = 1L
      whenever(prisonerContactRepository.findById(prisonerContactID)).thenReturn(Optional.of(contactEntity()))
      whenever(prisonerContactRepository.saveAndFlush(any())).thenReturn(request.toEntity())

      val updated = syncService.updatePrisonerContact(prisonerContactID, request)

      val contactCaptor = argumentCaptor<PrisonerContactEntity>()

      verify(prisonerContactRepository).saveAndFlush(contactCaptor.capture())

      // Checks the entity saved
      with(contactCaptor.firstValue) {
        assertThat(prisonerContactId).isEqualTo(prisonerContactID)
        assertThat(contactId).isEqualTo(12345L)
        assertThat(prisonerNumber).isEqualTo("A1234BC")
        assertThat(relationshipType).isEqualTo("Family")
        assertThat(nextOfKin).isTrue
        assertThat(emergencyContact).isFalse
        assertThat(comments).isEqualTo("Updated prison location")
        assertThat(active).isTrue
        assertThat(approvedVisitor).isTrue
        assertThat(awareOfCharges).isFalse
        assertThat(canBeContacted).isTrue
        assertThat(approvedBy).isEqualTo("officer123")
        assertThat(approvedTime).isNotNull
        assertThat(expiryDate).isEqualTo(LocalDate.of(2025, 12, 31))
        assertThat(createdAtPrison).isEqualTo("HMP Wales")
        assertThat(createdBy).isEqualTo("TEST")
        assertThat(createdTime).isAfter(LocalDateTime.now().minusMinutes(5))
        assertThat(amendedBy).isEqualTo("adminUser")
        assertThat(amendedTime).isNotNull
      }

      // Checks the model returned
      with(updated) {
        assertThat(contactId).isEqualTo(12345L)
        assertThat(prisonerNumber).isEqualTo("A1234BC")
        assertThat(relationshipType).isEqualTo("Social")
        assertThat(nextOfKin).isTrue
        assertThat(emergencyContact).isFalse
        assertThat(comments).isEqualTo("Updated relationship type to social")
        assertThat(active).isFalse
        assertThat(approvedVisitor).isFalse
        assertThat(awareOfCharges).isFalse
        assertThat(canBeContacted).isFalse
        assertThat(approvedBy).isEqualTo("ADMIN")
        assertThat(approvedTime).isNotNull
        assertThat(expiryDate).isEqualTo(LocalDate.of(2025, 12, 31))
        assertThat(createdAtPrison).isEqualTo("HMP Wales")
        assertThat(createdBy).isEqualTo("TEST")
        assertThat(createdTime).isAfter(LocalDateTime.now().minusMinutes(5))
        assertThat(amendedBy).isEqualTo("adminUser")
        assertThat(amendedTime).isNotNull
      }
    }

    @Test
    fun `should fail to update a prisoner contact when prisoner contact is not found`() {
      val updateRequest = updatePrisonerContactRequest()
      whenever(prisonerContactRepository.findById(1L)).thenReturn(Optional.empty())
      assertThrows<EntityNotFoundException> {
        syncService.updatePrisonerContact(1L, updateRequest)
      }
      verify(prisonerContactRepository).findById(1L)
    }
  }

  private fun updatePrisonerContactRequest() =
    UpdatePrisonerContactRequest(
      contactId = 12345L,
      prisonerNumber = "A1234BC",
      relationshipType = "Family",
      nextOfKin = true,
      emergencyContact = false,
      comments = "Updated prison location",
      active = true,
      approvedVisitor = true,
      awareOfCharges = false,
      canBeContacted = true,
      approvedBy = "officer123",
      approvedTime = LocalDateTime.now(),
      expiryDate = LocalDate.of(2025, 12, 31),
      createdAtPrison = "HMP Wales",
      amendedBy = "adminUser",
      updatedTime = LocalDateTime.now(),
    )

  private fun createPrisonerContactRequest() =
    CreatePrisonerContactRequest(
      contactId = 12345L,
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
      createdBy = "adminUser",
      createdTime = LocalDateTime.now(),
    )

  private fun contactEntity() =
    PrisonerContactEntity(
      prisonerContactId = 1L,
      contactId = 12345L,
      prisonerNumber = "A1234BC",
      relationshipType = "Family",
      nextOfKin = true,
      emergencyContact = false,
      comments = "Updated relationship type to family",
      createdBy = "TEST",
      createdTime = LocalDateTime.now(),
    ).also {
      it.active = true
      it.approvedVisitor = true
      it.awareOfCharges = false
      it.canBeContacted = true
      it.approvedBy = "officer456"
      it.approvedTime = LocalDateTime.now()
      it.expiryDate = LocalDate.of(2025, 12, 31)
      it.createdAtPrison = "LONDN"
      it.amendedBy = "adminUser"
      it.amendedTime = LocalDateTime.now()
    }

  private fun UpdatePrisonerContactRequest.toEntity(): PrisonerContactEntity {
    val updatedBy = this.amendedBy
    val updatedTime = this.updatedTime

    return PrisonerContactEntity(
      prisonerContactId = 1L,
      contactId = 12345L,
      prisonerNumber = "A1234BC",
      relationshipType = "Social",
      nextOfKin = true,
      emergencyContact = false,
      comments = "Updated relationship type to social",
      createdBy = "TEST",
      createdTime = LocalDateTime.now(),
    ).also {
      it.active = false
      it.approvedVisitor = false
      it.awareOfCharges = false
      it.canBeContacted = false
      it.approvedBy = "ADMIN"
      it.approvedTime = LocalDateTime.now()
      it.expiryDate = LocalDate.of(2025, 12, 31)
      it.createdAtPrison = "HMP Wales"
      it.amendedBy = updatedBy
      it.amendedTime = updatedTime
    }
  }
}