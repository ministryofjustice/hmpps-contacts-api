package uk.gov.justice.digital.hmpps.hmppscontactsapi.service

import jakarta.persistence.EntityNotFoundException
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.kotlin.any
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.whenever
import uk.gov.justice.digital.hmpps.hmppscontactsapi.client.prisonersearch.Prisoner
import uk.gov.justice.digital.hmpps.hmppscontactsapi.entity.ContactEntity
import uk.gov.justice.digital.hmpps.hmppscontactsapi.entity.PrisonerContactEntity
import uk.gov.justice.digital.hmpps.hmppscontactsapi.model.request.AddContactRelationshipRequest
import uk.gov.justice.digital.hmpps.hmppscontactsapi.model.request.ContactRelationship
import uk.gov.justice.digital.hmpps.hmppscontactsapi.model.request.EstimatedIsOverEighteen
import uk.gov.justice.digital.hmpps.hmppscontactsapi.repository.ContactRepository
import uk.gov.justice.digital.hmpps.hmppscontactsapi.repository.PrisonerContactRepository
import java.time.LocalDateTime
import java.util.*

class RelationshipServiceTest {

  private val contactRepository: ContactRepository = mock()
  private val prisonerContactRepository: PrisonerContactRepository = mock()
  private val prisonerService: PrisonerService = mock()

  private val service = RelationshipService(
    contactRepository,
    prisonerContactRepository,
    prisonerService,
  )

  @Nested
  inner class AddContactRelationship {
    private val id = 123456L
    private val relationship = ContactRelationship(
      prisonerNumber = "A1234BC",
      relationshipCode = "MOT",
      isNextOfKin = true,
      isEmergencyContact = false,
      comments = "Foo",
    )
    private val request = AddContactRelationshipRequest(relationship, "RELATIONSHIP_USER")
    private val contact = ContactEntity(
      contactId = id,
      title = null,
      lastName = "last",
      middleNames = null,
      firstName = "first",
      dateOfBirth = null,
      estimatedIsOverEighteen = EstimatedIsOverEighteen.DO_NOT_KNOW,
      isDeceased = false,
      deceasedDate = null,
      createdBy = "user",
      createdTime = LocalDateTime.now(),
    )

    @Test
    fun `should save the contact relationship`() {
      whenever(prisonerService.getPrisoner(any())).thenReturn(
        Prisoner(
          request.relationship.prisonerNumber,
          prisonId = "MDI",
        ),
      )
      whenever(contactRepository.findById(id)).thenReturn(Optional.of(contact))
      whenever(prisonerContactRepository.saveAndFlush(any())).thenAnswer { i -> i.arguments[0] }

      service.create(id, request)

      val prisonerContactCaptor = argumentCaptor<PrisonerContactEntity>()
      verify(prisonerContactRepository).saveAndFlush(prisonerContactCaptor.capture())
      with(prisonerContactCaptor.firstValue) {
        assertThat(prisonerNumber).isEqualTo("A1234BC")
        assertThat(relationshipType).isEqualTo("MOT")
        assertThat(nextOfKin).isEqualTo(true)
        assertThat(emergencyContact).isEqualTo(false)
        assertThat(comments).isEqualTo("Foo")
        assertThat(createdBy).isEqualTo("RELATIONSHIP_USER")
      }
    }

    @Test
    fun `should blow up if prisoner not found`() {
      whenever(prisonerService.getPrisoner(any())).thenReturn(null)

      org.junit.jupiter.api.assertThrows<EntityNotFoundException>("Prisoner (A1234BC) could not be found") {
        service.create(id, request)
      }
    }

    @Test
    fun `should blow up if contact not found`() {
      whenever(contactRepository.findById(id)).thenReturn(Optional.empty())

      org.junit.jupiter.api.assertThrows<EntityNotFoundException>("Contact ($id) could not be found") {
        service.create(id, request)
      }
    }

    @Test
    fun `should propagate exceptions adding a relationship`() {
      whenever(prisonerService.getPrisoner(any())).thenReturn(
        Prisoner(
          request.relationship.prisonerNumber,
          prisonId = "MDI",
        ),
      )
      whenever(contactRepository.findById(id)).thenReturn(Optional.of(contact))
      whenever(prisonerContactRepository.saveAndFlush(any())).thenThrow(RuntimeException("Bang!"))

      org.junit.jupiter.api.assertThrows<RuntimeException>("Bang!") {
        service.create(id, request)
      }
    }
  }
}
