package uk.gov.justice.digital.hmpps.hmppscontactsapi.resource

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.springframework.http.HttpStatus
import uk.gov.justice.digital.hmpps.hmppscontactsapi.facade.OrganisationFacade
import uk.gov.justice.digital.hmpps.hmppscontactsapi.model.request.CreateOrganisationRequest
import uk.gov.justice.digital.hmpps.hmppscontactsapi.model.response.Organisation
import java.time.LocalDate
import java.time.LocalDateTime

class OrganisationControllerTest {

  private val organisationFacade: OrganisationFacade = mock()
  private val organisationController = OrganisationController(organisationFacade)

  @Nested
  inner class GetOrganisation {
    @Test
    fun `should return organisation when found`() {
      // Given
      val updatedTime = LocalDateTime.now().plusMinutes(20)
      val organisation = createTestOrganisation(updatedTime = updatedTime)
      whenever(organisationFacade.getOrganisationById(ORGANISATION_ID)).thenReturn(organisation)

      // When
      val response = organisationController.getOrganisationById(ORGANISATION_ID)

      // Then
      assertThat(response).isEqualTo(organisation)
      verify(organisationFacade).getOrganisationById(ORGANISATION_ID)
    }

    @Test
    fun `should return null when organisation not found`() {
      // Given
      whenever(organisationFacade.getOrganisationById(ORGANISATION_ID)).thenReturn(null)

      // When
      val response = organisationController.getOrganisationById(ORGANISATION_ID)

      // Then
      assertThat(response).isNull()
      verify(organisationFacade).getOrganisationById(ORGANISATION_ID)
    }
  }

  @Nested
  inner class CreateOrganisation {
    @Test
    fun `should create organisation successfully`() {
      // Given
      val request = createTestOrganisationRequest()
      val deactivatedDate = LocalDate.now()
      val createdTime = LocalDateTime.now().minusMinutes(20)
      val updatedTime = LocalDateTime.now().plusMinutes(20)
      val expectedOrganisation = createTestOrganisation(
        deactivatedDate = deactivatedDate,
        createdTime = createdTime,
        updatedTime = updatedTime,
      )

      whenever(organisationFacade.create(request)).thenReturn(expectedOrganisation)

      // When
      val response = organisationController.createOrganisation(request)

      // Then
      assertThat(response.statusCode).isEqualTo(HttpStatus.CREATED)
      assertThat(response.body).isEqualTo(expectedOrganisation)
      verify(organisationFacade).create(request)
    }

    @Test
    fun `should throw exception when request is invalid`() {
      // Given
      val invalidRequest = createTestOrganisationRequest()
      whenever(organisationFacade.create(invalidRequest)).thenThrow(RuntimeException("Invalid request"))

      // When/Then
      assertThrows<RuntimeException> {
        organisationController.createOrganisation(invalidRequest)
      }
    }
  }

  companion object TestData {
    private const val ORGANISATION_ID = 1L
    private const val ORGANISATION_NAME = "Name"
    private const val PROGRAMME_NUMBER = "P1"
    private const val VAT_NUMBER = "V1"
    private const val CASELOAD_ID = "C1"
    private const val COMMENTS = "C2"
    private const val CREATED_BY = "Created by"
    private const val UPDATED_BY = "U1"

    private val DEFAULT_CREATED_TIME: LocalDateTime = LocalDateTime.now().minusMinutes(20)
    private val DEFAULT_DEACTIVATED_DATE: LocalDate = LocalDate.now()

    fun createTestOrganisationRequest(
      organisationName: String = ORGANISATION_NAME,
      programmeNumber: String = PROGRAMME_NUMBER,
      vatNumber: String = VAT_NUMBER,
      caseloadId: String = CASELOAD_ID,
      comments: String? = COMMENTS,
      active: Boolean = true,
      deactivatedDate: LocalDate? = DEFAULT_DEACTIVATED_DATE,
      createdTime: LocalDateTime = DEFAULT_CREATED_TIME,
      updatedBy: String? = UPDATED_BY,
      createdBy: String = CREATED_BY,
      updatedTime: LocalDateTime? = DEFAULT_CREATED_TIME,
    ) = CreateOrganisationRequest(
      organisationName = organisationName,
      programmeNumber = programmeNumber,
      vatNumber = vatNumber,
      caseloadId = caseloadId,
      comments = comments,
      active = active,
      deactivatedDate = deactivatedDate,
      createdBy = createdBy,
      createdTime = createdTime,
      updatedBy = updatedBy,
      updatedTime = updatedTime,
    )

    fun createTestOrganisation(
      deactivatedDate: LocalDate? = DEFAULT_DEACTIVATED_DATE,
      createdTime: LocalDateTime = DEFAULT_CREATED_TIME,
      updatedBy: String? = UPDATED_BY,
      createdBy: String = CREATED_BY,
      updatedTime: LocalDateTime? = DEFAULT_CREATED_TIME,
      active: Boolean = true,
    ) = Organisation(
      organisationId = ORGANISATION_ID,
      organisationName = ORGANISATION_NAME,
      programmeNumber = PROGRAMME_NUMBER,
      vatNumber = VAT_NUMBER,
      caseloadId = CASELOAD_ID,
      comments = COMMENTS,
      active = active,
      deactivatedDate = deactivatedDate,
      createdBy = createdBy,
      createdTime = createdTime,
      updatedBy = updatedBy,
      updatedTime = updatedTime,
    )
  }
}