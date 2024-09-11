package uk.gov.justice.digital.hmpps.hmppscontactsapi.service

import jakarta.persistence.EntityNotFoundException
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import uk.gov.justice.digital.hmpps.hmppscontactsapi.entity.CountyEntity
import uk.gov.justice.digital.hmpps.hmppscontactsapi.integration.helper.isEqualTo
import uk.gov.justice.digital.hmpps.hmppscontactsapi.repository.CountyRepository
import java.util.*

class CountyServiceTest {

  private lateinit var countyService: CountyService

  @Mock
  private lateinit var countyRepository: CountyRepository

  @BeforeEach
  fun setUp() {
    MockitoAnnotations.openMocks(this)
    countyService = CountyService(countyRepository)
  }

  @Nested
  inner class GetCountyByCountyId {

    @Test
    fun `should return a county when valid id is provided`() {
      // Given
      val countyId = 1L
      val county = CountyEntity(
        countyId = countyId,
        nomisCode = "ABC",
        nomisDescription = "Test County",
        displaySequence = 123,
      )
      `when`(countyRepository.findByCountyId(countyId)).thenReturn(Optional.of(county))

      // When
      val result = countyService.getCountyById(countyId)

      // Then
      assertNotNull(result)
      assertEquals("ABC", result.nomisCode)
      assertEquals("Test County", result.nomisDescription)
    }

    @Test
    fun `should return not found exception when county id does not exist`() {
      // Given
      val countyId = 1L
      `when`(countyRepository.findByCountyId(countyId)).thenReturn(Optional.empty())

      // When

      val exception = assertThrows<EntityNotFoundException> {
        countyService.getCountyById(countyId)
      }

      // Then
      exception.message isEqualTo "County with id 1 not found"
    }
  }

  @Nested
  inner class GetAllCountries {

    @Test
    fun `should return a list of all countries`() {
      // Given
      val countries = listOf(
        CountyEntity(
          countyId = 1L,
          nomisCode = "USA",
          nomisDescription = "Test County",
          displaySequence = 123,
        ),
        CountyEntity(
          countyId = 2L,
          nomisCode = "CAN",
          nomisDescription = "Test County",
          displaySequence = 123,
        ),
      )
      `when`(countyRepository.findAll()).thenReturn(countries)

      // When
      val result = countyService.getAllCountries()

      // Then
      assertEquals(2, result.size)
      assertEquals("USA", result[0].nomisCode)
      assertEquals("CAN", result[1].nomisCode)
    }
  }

  @Nested
  inner class GetCountyByNomisCode {
    @Test
    fun `should return a county by nomis code`() {
      // Given
      val nomisCode = "USA"
      val county = CountyEntity(
        countyId = 1L,
        nomisCode = nomisCode,
        displaySequence = 123,
        nomisDescription = "Test County",
      )
      `when`(countyRepository.findByNomisCode(nomisCode)).thenReturn(Optional.of(county))

      // When
      val result = countyService.getCountyByNomisCode(nomisCode)

      // Then
      assertNotNull(result)
      assertEquals("USA", result.nomisCode)
    }

    @Test
    fun `should return not found exception when nomis code does not exist`() {
      // Given
      val nomisCode = "XXX"
      `when`(countyRepository.findByNomisCode(nomisCode)).thenReturn(Optional.empty())

      // When

      val exception = assertThrows<EntityNotFoundException> {
        countyService.getCountyByNomisCode(nomisCode)
      }

      // Then
      exception.message isEqualTo "County with nomis code XXX not found"
    }
  }
}
