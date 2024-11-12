package uk.gov.justice.digital.hmpps.hmppscontactsapi.service

import jakarta.persistence.EntityNotFoundException
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import uk.gov.justice.digital.hmpps.hmppscontactsapi.client.prisonersearch.Prisoner
import uk.gov.justice.digital.hmpps.hmppscontactsapi.entity.PrisonerContactSummaryEntity
import uk.gov.justice.digital.hmpps.hmppscontactsapi.integration.helper.hasSize
import uk.gov.justice.digital.hmpps.hmppscontactsapi.integration.helper.isEqualTo
import uk.gov.justice.digital.hmpps.hmppscontactsapi.mapping.toModel
import uk.gov.justice.digital.hmpps.hmppscontactsapi.model.request.EstimatedIsOverEighteen
import uk.gov.justice.digital.hmpps.hmppscontactsapi.model.response.PrisonerContactSummary
import uk.gov.justice.digital.hmpps.hmppscontactsapi.repository.PrisonerContactSummaryRepository
import java.time.LocalDate
import java.util.Optional

@ExtendWith(MockitoExtension::class)
class PrisonerContactServiceTest {

  @Mock
  private lateinit var prisonerContactSummaryRepository: PrisonerContactSummaryRepository

  @Mock
  private lateinit var prisonerService: PrisonerService

  @InjectMocks
  private lateinit var prisonerContactService: PrisonerContactService

  private lateinit var prisoner: Prisoner

  private val prisonerNumber = "A1111AA"

  @BeforeEach
  fun before() {
    prisoner = Prisoner(prisonerNumber, prisonId = "MDI")
  }

  private val pageable = Pageable.ofSize(10)

  @Test
  fun `should fetch all contacts for a prisoner`() {
    val dateOfBirth = LocalDate.of(1980, 5, 10)
    val c1 = makePrisonerContact(
      prisonerContactId = 1L,
      contactId = 2L,
      dateOfBirth,
      firstName = "John",
      lastName = "Doe",
      EstimatedIsOverEighteen.DO_NOT_KNOW,
    )
    val c2 = makePrisonerContact(
      prisonerContactId = 2L,
      contactId = 2L,
      dateOfBirth,
      firstName = "David",
      lastName = "Doe",
      EstimatedIsOverEighteen.YES,
    )
    val contacts = listOf(c1, c2)
    val page = PageImpl(contacts, pageable, contacts.size.toLong())

    whenever(prisonerService.getPrisoner(prisonerNumber)).thenReturn(prisoner)
    whenever(
      prisonerContactSummaryRepository.findByPrisonerNumberAndActive(
        prisonerNumber,
        true,
        pageable,
      ),
    ).thenReturn(page)

    val result = prisonerContactService.getAllContacts(prisonerNumber, true, pageable)

    result.content hasSize 2
    assertThat(result).containsAll(listOf(c1.toModel(), c2.toModel()))

    verify(prisonerContactSummaryRepository).findByPrisonerNumberAndActive(prisonerNumber, true, pageable)
  }

  @Test
  fun `should throw exception`() {
    whenever(prisonerService.getPrisoner(prisonerNumber)).thenReturn(null)
    val exception = assertThrows<EntityNotFoundException> {
      prisonerContactService.getAllContacts(prisonerNumber, true, pageable)
    }
    exception.message isEqualTo "Prisoner number $prisonerNumber - not found"
  }

  @Test
  fun `should return when prisoner contact relationship exists`() {
    val prisonerContactId = 1L
    val expectedPrisonerContactSummary = PrisonerContactSummary(
      prisonerContactId,
      contactId = 2L,
      prisonerNumber = "A1234BC",
      lastName = "Doe",
      firstName = "Jack",
      middleNames = "Any",
      dateOfBirth = LocalDate.of(2000, 11, 21),
      estimatedIsOverEighteen = EstimatedIsOverEighteen.DO_NOT_KNOW,
      relationshipCode = "FRIEND",
      relationshipDescription = "Friend",
      flat = "2B",
      property = "123",
      street = "Baker Street",
      area = "Westminster",
      cityCode = "SHEF",
      cityDescription = "Sheffield",
      countyCode = "SYORKS",
      countyDescription = "South Yorkshire",
      postCode = "NW1 6XE",
      countryCode = "UK",
      countryDescription = "United Kingdom",
      phoneType = "Mobile",
      phoneTypeDescription = "Mobile Phone",
      phoneNumber = "07123456789",
      extNumber = "0123",
      approvedVisitor = true,
      nextOfKin = false,
      emergencyContact = false,
      isRelationshipActive = true,
      currentTerm = true,
      mailAddress = false,
      primaryAddress = false,
      comments = "No comments",
    )

    val prisonerContactRelationship = makePrisonerContact(
      prisonerContactId = 1L,
      contactId = 2L,
      dateOfBirth = LocalDate.of(2000, 11, 21),
      firstName = "Jack",
      lastName = "Doe",
      EstimatedIsOverEighteen.DO_NOT_KNOW,
    )

    whenever(prisonerContactSummaryRepository.findById(prisonerContactId)).thenReturn(Optional.of(prisonerContactRelationship))

    val actualPrisonerContactSummary = prisonerContactService.getById(prisonerContactId)

    assertThat(actualPrisonerContactSummary).isEqualTo(expectedPrisonerContactSummary)
    verify(prisonerContactSummaryRepository).findById(prisonerContactId)
  }

  @Test
  fun `should throw EntityNotFoundException when prisoner contact relationship does not exist`() {
    val prisonerContactId = 1L
    whenever(prisonerContactSummaryRepository.findById(prisonerContactId)).thenReturn(Optional.empty())

    val exception = assertThrows<EntityNotFoundException> {
      prisonerContactService.getById(prisonerContactId)
    }

    assertThat(exception.message).isEqualTo("prisoner contact relationship with id $prisonerContactId not found")
    verify(prisonerContactSummaryRepository).findById(prisonerContactId)
  }

  private fun makePrisonerContact(
    prisonerContactId: Long,
    contactId: Long,
    dateOfBirth: LocalDate?,
    firstName: String,
    lastName: String,
    estimatedIsOverEighteen: EstimatedIsOverEighteen,
    active: Boolean = true,
  ): PrisonerContactSummaryEntity =
    PrisonerContactSummaryEntity(
      prisonerContactId,
      contactId = contactId,
      title = "Mr.",
      firstName = firstName,
      middleNames = "Any",
      lastName = lastName,
      dateOfBirth = dateOfBirth,
      estimatedIsOverEighteen = estimatedIsOverEighteen,
      contactAddressId = 3L,
      flat = "2B",
      property = "123",
      street = "Baker Street",
      area = "Westminster",
      cityCode = "SHEF",
      cityDescription = "Sheffield",
      countyCode = "SYORKS",
      countyDescription = "South Yorkshire",
      postCode = "NW1 6XE",
      countryCode = "UK",
      countryDescription = "United Kingdom",
      primaryAddress = false,
      mailFlag = false,
      contactPhoneId = 4L,
      phoneType = "Mobile",
      phoneTypeDescription = "Mobile Phone",
      phoneNumber = "07123456789",
      extNumber = "0123",
      contactEmailId = 5L,
      emailAddress = "john.doe@example.com",
      prisonerNumber = "A1234BC",
      relationshipType = "FRIEND",
      relationshipDescription = "Friend",
      active = active,
      approvedVisitor = true,
      nextOfKin = false,
      emergencyContact = false,
      currentTerm = true,
      comments = "No comments",
    )
}
