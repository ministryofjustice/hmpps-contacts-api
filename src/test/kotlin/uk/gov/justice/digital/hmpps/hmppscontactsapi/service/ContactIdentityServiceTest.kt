package uk.gov.justice.digital.hmpps.hmppscontactsapi.service

import jakarta.persistence.EntityNotFoundException
import jakarta.validation.ValidationException
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.kotlin.any
import org.mockito.kotlin.whenever
import uk.gov.justice.digital.hmpps.hmppscontactsapi.entity.ContactEntity
import uk.gov.justice.digital.hmpps.hmppscontactsapi.entity.ContactIdentityDetailsEntity
import uk.gov.justice.digital.hmpps.hmppscontactsapi.entity.ContactIdentityEntity
import uk.gov.justice.digital.hmpps.hmppscontactsapi.model.request.CreateIdentityRequest
import uk.gov.justice.digital.hmpps.hmppscontactsapi.model.request.EstimatedIsOverEighteen
import uk.gov.justice.digital.hmpps.hmppscontactsapi.model.request.UpdateIdentityRequest
import uk.gov.justice.digital.hmpps.hmppscontactsapi.model.response.ContactIdentityDetails
import uk.gov.justice.digital.hmpps.hmppscontactsapi.model.response.ReferenceCode
import uk.gov.justice.digital.hmpps.hmppscontactsapi.repository.ContactIdentityDetailsRepository
import uk.gov.justice.digital.hmpps.hmppscontactsapi.repository.ContactIdentityRepository
import uk.gov.justice.digital.hmpps.hmppscontactsapi.repository.ContactRepository
import java.time.LocalDateTime.now
import java.util.*

class ContactIdentityServiceTest {

  private val contactRepository: ContactRepository = mock()
  private val contactIdentityRepository: ContactIdentityRepository = mock()
  private val contactIdentityDetailsRepository: ContactIdentityDetailsRepository = mock()
  private val referenceCodeService: ReferenceCodeService = mock()
  private val service =
    ContactIdentityService(contactRepository, contactIdentityRepository, contactIdentityDetailsRepository, referenceCodeService)

  private val contactId = 99L
  private val aContact = ContactEntity(
    contactId = contactId,
    title = "Mr",
    lastName = "last",
    middleNames = "middle",
    firstName = "first",
    dateOfBirth = null,
    estimatedIsOverEighteen = EstimatedIsOverEighteen.YES,
    isDeceased = false,
    deceasedDate = null,
    createdBy = "user",
    createdTime = now(),
  )

  @Nested
  inner class CreateIdentity {
    private val request = CreateIdentityRequest(
      identityType = "DRIVING_LIC",
      identityValue = "DL123456789",
      issuingAuthority = "DVLA",
      createdBy = "created",
    )

    @Test
    fun `should throw EntityNotFoundException creating identity if contact doesn't exist`() {
      whenever(contactRepository.findById(contactId)).thenReturn(Optional.empty())

      val exception = assertThrows<EntityNotFoundException> {
        service.create(contactId, request)
      }
      assertThat(exception.message).isEqualTo("Contact (99) not found")
    }

    @Test
    fun `should throw ValidationException creating identity if identity type doesn't exist`() {
      whenever(contactRepository.findById(contactId)).thenReturn(Optional.of(aContact))
      whenever(referenceCodeService.getReferenceDataByGroupAndCode("ID_TYPE", "FOO")).thenReturn(null)

      val exception = assertThrows<ValidationException> {
        service.create(contactId, request.copy(identityType = "FOO"))
      }
      assertThat(exception.message).isEqualTo("Unsupported identity type (FOO)")
    }

    @Test
    fun `should return identity details including the reference data after creating successfully`() {
      whenever(contactRepository.findById(contactId)).thenReturn(Optional.of(aContact))
      whenever(referenceCodeService.getReferenceDataByGroupAndCode("ID_TYPE", "DRIVING_LIC")).thenReturn(
        ReferenceCode(
          0,
          "ID_TYPE",
          "DRIVING_LIC",
          "Driving licence",
          90,
        ),
      )
      whenever(contactIdentityRepository.saveAndFlush(any())).thenAnswer { i ->
        (i.arguments[0] as ContactIdentityEntity).copy(
          contactIdentityId = 9999,
        )
      }

      val created = service.create(contactId, request)
      assertThat(created.createdTime).isNotNull()
      assertThat(created).isEqualTo(
        ContactIdentityDetails(
          contactIdentityId = 9999,
          contactId = contactId,
          identityType = "DRIVING_LIC",
          identityTypeDescription = "Driving licence",
          identityValue = "DL123456789",
          issuingAuthority = "DVLA",
          createdBy = "created",
          createdTime = created.createdTime,
          amendedBy = null,
          amendedTime = null,
        ),
      )
    }
  }

  @Nested
  inner class UpdateIdentity {
    private val request = UpdateIdentityRequest(
      "PASSPORT",
      "P987654321",
      "Passport office",
      "amended",
    )
    private val contactIdentityId = 1234L
    private val existingIdentity = ContactIdentityEntity(
      contactIdentityId = contactIdentityId,
      contactId = contactId,
      identityType = "DRIVING_LIC",
      identityValue = "DL123456789",
      issuingAuthority = null,
      createdBy = "USER99",
      createdTime = now().minusDays(2),
      amendedBy = null,
      amendedTime = null,
    )

    @Test
    fun `should throw EntityNotFoundException updating identity if contact doesn't exist`() {
      whenever(contactRepository.findById(contactId)).thenReturn(Optional.empty())

      val exception = assertThrows<EntityNotFoundException> {
        service.update(contactId, contactIdentityId, request)
      }
      assertThat(exception.message).isEqualTo("Contact (99) not found")
    }

    @Test
    fun `should throw EntityNotFoundException updating identity if identity doesn't exist`() {
      whenever(contactRepository.findById(contactId)).thenReturn(Optional.of(aContact))
      whenever(contactIdentityRepository.findById(contactIdentityId)).thenReturn(Optional.empty())

      val exception = assertThrows<EntityNotFoundException> {
        service.update(contactId, contactIdentityId, request)
      }
      assertThat(exception.message).isEqualTo("Contact identity (1234) not found")
    }

    @Test
    fun `should throw ValidationException updating identity if identity type doesn't exist`() {
      whenever(contactRepository.findById(contactId)).thenReturn(Optional.of(aContact))
      whenever(contactIdentityRepository.findById(contactIdentityId)).thenReturn(Optional.of(existingIdentity))
      whenever(referenceCodeService.getReferenceDataByGroupAndCode("ID_TYPE", "FOO")).thenReturn(null)

      val exception = assertThrows<ValidationException> {
        service.update(contactId, contactIdentityId, request.copy(identityType = "FOO"))
      }
      assertThat(exception.message).isEqualTo("Unsupported identity type (FOO)")
    }

    @Test
    fun `should return a identity details including the reference data after updating a identity successfully`() {
      whenever(contactRepository.findById(contactId)).thenReturn(Optional.of(aContact))
      whenever(contactIdentityRepository.findById(contactIdentityId)).thenReturn(Optional.of(existingIdentity))
      whenever(referenceCodeService.getReferenceDataByGroupAndCode("ID_TYPE", "PASSPORT")).thenReturn(
        ReferenceCode(
          0,
          "ID_TYPE",
          "PASSPORT",
          "Passport",
          90,
        ),
      )
      whenever(contactIdentityRepository.saveAndFlush(any())).thenAnswer { i ->
        (i.arguments[0] as ContactIdentityEntity).copy(
          contactIdentityId = 9999,
        )
      }

      val updated = service.update(contactId, contactIdentityId, request)
      assertThat(updated.amendedTime).isNotNull()
      assertThat(updated).isEqualTo(
        ContactIdentityDetails(
          contactIdentityId = 9999,
          contactId = contactId,
          identityType = "PASSPORT",
          identityTypeDescription = "Passport",
          identityValue = "P987654321",
          issuingAuthority = "Passport office",
          createdBy = "USER99",
          createdTime = existingIdentity.createdTime,
          amendedBy = "amended",
          amendedTime = updated.amendedTime,
        ),
      )
    }
  }

  @Nested
  inner class GetIdentity {
    private val createdTime = now()
    private val entity = ContactIdentityDetailsEntity(
      contactIdentityId = 99,
      contactId = contactId,
      identityType = "DRIVING_LIC",
      identityTypeDescription = "Driving licence",
      identityValue = "DL123456789",
      issuingAuthority = "DVLA",
      createdBy = "USER1",
      createdTime = createdTime,
      amendedBy = null,
      amendedTime = null,
    )

    @Test
    fun `get identity if found by ids`() {
      whenever(contactIdentityDetailsRepository.findByContactIdAndContactIdentityId(contactId, 99)).thenReturn(entity)

      val returned = service.get(contactId, 99)

      assertThat(returned).isEqualTo(
        ContactIdentityDetails(
          contactIdentityId = 99,
          contactId = contactId,
          identityType = "DRIVING_LIC",
          identityTypeDescription = "Driving licence",
          identityValue = "DL123456789",
          issuingAuthority = "DVLA",
          createdBy = "USER1",
          createdTime = createdTime,
          amendedBy = null,
          amendedTime = null,
        ),
      )
    }

    @Test
    fun `return null if not found`() {
      whenever(contactIdentityDetailsRepository.findByContactIdAndContactIdentityId(contactId, 99)).thenReturn(null)

      assertThat(service.get(contactId, 99)).isNull()
    }
  }

  @Nested
  inner class DeleteIdentity {
    private val contactIdentityId = 1234L
    private val existingIdentity = ContactIdentityEntity(
      contactIdentityId = contactIdentityId,
      contactId = contactId,
      identityType = "DRIVING_LIC",
      identityValue = "DL123456789",
      createdBy = "USER99",
      createdTime = now().minusDays(2),
      amendedBy = null,
      amendedTime = null,
    )

    @Test
    fun `should throw EntityNotFoundException deleting identity if contact doesn't exist`() {
      whenever(contactRepository.findById(contactId)).thenReturn(Optional.empty())

      val exception = assertThrows<EntityNotFoundException> {
        service.delete(contactId, contactIdentityId)
      }
      assertThat(exception.message).isEqualTo("Contact (99) not found")
    }

    @Test
    fun `should throw EntityNotFoundException deleting identity if identity doesn't exist`() {
      whenever(contactRepository.findById(contactId)).thenReturn(Optional.of(aContact))
      whenever(contactIdentityRepository.findById(contactIdentityId)).thenReturn(Optional.empty())

      val exception = assertThrows<EntityNotFoundException> {
        service.delete(contactId, contactIdentityId)
      }
      assertThat(exception.message).isEqualTo("Contact identity (1234) not found")
    }

    @Test
    fun `should just delete the identity and any address links if it exists`() {
      whenever(contactRepository.findById(contactId)).thenReturn(Optional.of(aContact))
      whenever(contactIdentityRepository.findById(contactIdentityId)).thenReturn(Optional.of(existingIdentity))
      whenever(contactIdentityRepository.delete(any())).then {}

      service.delete(contactId, contactIdentityId)

      verify(contactIdentityRepository).delete(existingIdentity)
    }
  }
}