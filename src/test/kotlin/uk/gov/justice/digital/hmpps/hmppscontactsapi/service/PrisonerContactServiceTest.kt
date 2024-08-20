package uk.gov.justice.digital.hmpps.hmppscontactsapi.service

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.jupiter.MockitoExtension
import uk.gov.justice.digital.hmpps.hmppscontactsapi.entity.PrisonerContactViewEntity
import uk.gov.justice.digital.hmpps.hmppscontactsapi.integration.helper.hasSize
import uk.gov.justice.digital.hmpps.hmppscontactsapi.integration.helper.isBool
import uk.gov.justice.digital.hmpps.hmppscontactsapi.repository.PrisonerContactViewRepository
import java.time.LocalDate

@ExtendWith(MockitoExtension::class)
class PrisonerContactServiceTest {

  @Mock
  private lateinit var contactRepository: PrisonerContactViewRepository

  @InjectMocks
  private lateinit var contactService: ContactService

  @Test
  fun `should fetch all contacts`() {
    // Given

    val prisonerContact1 = PrisonerContactViewEntity(
      prisonerContactId = 1L,
      contactId = 101L,
      surname = "Doe",
      forename = "John",
      middleName = "Edward",
      dateOfBirth = LocalDate.of(1980, 5, 10),
      relationshipCode = "FRIEND",
      relationshipDescription = "Friend",
      addresses = "Address: 24 Acacia Avenue, SHEF, S2 3LK, UK",
      approvedVisitor = true,
    )

    val prisonerContact2 = PrisonerContactViewEntity(
      prisonerContactId = 2L,
      contactId = 101L,
      surname = "Mile",
      forename = "John",
      middleName = "Edward",
      dateOfBirth = LocalDate.of(1980, 5, 10),
      relationshipCode = "FRIEND",
      relationshipDescription = "Friend",
      addresses = "Address: 24 Acacia Avenue, SHEF, S2 3LK, UK",
      approvedVisitor = true,
    )

    val contacts = listOf(prisonerContact1, prisonerContact2)

    Mockito.`when`(contactRepository.findByContactId(1L)).thenReturn(contacts)

    // When
    val result = contactService.getAllContacts("somePrisonerNumber", true)

    // Then
    result hasSize 2
    result.containsAll(listOf(prisonerContact1, prisonerContact2)) isBool true
    Mockito.verify(contactRepository).findByContactId(1L)
  }
}
