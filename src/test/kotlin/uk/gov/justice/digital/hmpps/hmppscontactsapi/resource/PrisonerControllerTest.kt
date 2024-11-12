package uk.gov.justice.digital.hmpps.hmppscontactsapi.resource

import jakarta.persistence.EntityNotFoundException
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito.mock
import org.mockito.Mockito.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import uk.gov.justice.digital.hmpps.hmppscontactsapi.model.request.EstimatedIsOverEighteen
import uk.gov.justice.digital.hmpps.hmppscontactsapi.model.response.PrisonerContactSummary
import uk.gov.justice.digital.hmpps.hmppscontactsapi.service.PrisonerContactService
import uk.gov.justice.digital.hmpps.hmppscontactsapi.service.PrisonerService
import java.time.LocalDate

class PrisonerControllerTest {

  private val prisonerService: PrisonerService = mock()
  private val prisonerContactService: PrisonerContactService = mock()

  private val prisonerController = PrisonerController(prisonerService, prisonerContactService)

  @Test
  fun `should return prisoner contact relationship when found`() {
    val prisonerContactId = 1L
    val mockPrisonerContactSummary = getMockPrisonerContactSummary()
    whenever(prisonerContactService.getById(prisonerContactId)).thenReturn(mockPrisonerContactSummary)

    val response: PrisonerContactSummary = prisonerController.getPrisonerContactById(prisonerContactId)

    assertThat(response).isEqualTo(mockPrisonerContactSummary)
    verify(prisonerContactService).getById(prisonerContactId)
  }

  @Test
  fun `should throw exception when relationship is not found`() {
    val prisonContactId = 999L
    whenever(prisonerContactService.getById(prisonContactId)).thenThrow(EntityNotFoundException::class.java)

    assertThrows<EntityNotFoundException> {
      prisonerController.getPrisonerContactById(prisonContactId)
    }
    verify(prisonerContactService, times(1)).getById(prisonContactId)
  }

  private fun getMockPrisonerContactSummary() = PrisonerContactSummary(
    prisonerContactId = 1L,
    contactId = 654321,
    prisonerNumber = "A1234BC",
    lastName = "Doe",
    firstName = "John",
    middleNames = "William",
    dateOfBirth = LocalDate.of(1980, 1, 1),
    estimatedIsOverEighteen = EstimatedIsOverEighteen.YES,
    relationshipCode = "FRI",
    relationshipDescription = "Friend",
    flat = "Flat 1",
    property = "123",
    street = "Baker Street",
    area = "Marylebone",
    cityCode = "25343",
    cityDescription = "Sheffield",
    countyCode = "S.YORKSHIRE",
    countyDescription = "South Yorkshire",
    postCode = "NW1 6XE",
    countryCode = "ENG",
    countryDescription = "England",
    phoneType = "MOB",
    phoneTypeDescription = "Mobile",
    phoneNumber = "+1234567890",
    extNumber = "123",
    approvedVisitor = true,
    nextOfKin = false,
    emergencyContact = true,
    isRelationshipActive = true,
    currentTerm = true,
    mailAddress = true,
    primaryAddress = true,
    comments = "Close family friend",
  )
}
