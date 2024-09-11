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
import uk.gov.justice.digital.hmpps.hmppscontactsapi.entity.CityEntity
import uk.gov.justice.digital.hmpps.hmppscontactsapi.integration.helper.isEqualTo
import uk.gov.justice.digital.hmpps.hmppscontactsapi.repository.CityRepository
import java.util.*

class CityServiceTest {

  private lateinit var cityService: CityService

  @Mock
  private lateinit var cityRepository: CityRepository

  @BeforeEach
  fun setUp() {
    MockitoAnnotations.openMocks(this)
    cityService = CityService(cityRepository)
  }

  @Nested
  inner class GetCityByCityId {

    @Test
    fun `should return a city when valid id is provided`() {
      // Given
      val cityId = 1L
      val city = CityEntity(
        cityId = cityId,
        nomisCode = "ABC",
        nomisDescription = "Test City",
        displaySequence = 123,
      )
      `when`(cityRepository.findByCityId(cityId)).thenReturn(Optional.of(city))

      // When
      val result = cityService.getCityById(cityId)

      // Then
      assertNotNull(result)
      assertEquals("ABC", result.nomisCode)
      assertEquals("Test City", result.nomisDescription)
    }

    @Test
    fun `should return null when city id does not exist`() {
      // Given
      val cityId = 1009L
      `when`(cityRepository.findByCityId(cityId)).thenReturn(Optional.empty())

      // When
      val exception = assertThrows<EntityNotFoundException> {
        cityService.getCityById(cityId)
      }

      // Then
      exception.message isEqualTo "City with id 1009 not found"
    }
  }

  @Nested
  inner class GetAllCountries {

    @Test
    fun `should return a list of all countries`() {
      // Given
      val countries = listOf(
        CityEntity(
          cityId = 1L,
          nomisCode = "USA",
          nomisDescription = "Test City",
          displaySequence = 123,
        ),
        CityEntity(
          cityId = 2L,
          nomisCode = "CAN",
          nomisDescription = "Test City",
          displaySequence = 123,
        ),
      )
      `when`(cityRepository.findAll()).thenReturn(countries)

      // When
      val result = cityService.getAllCountries()

      // Then
      assertEquals(2, result.size)
      assertEquals("USA", result[0].nomisCode)
      assertEquals("CAN", result[1].nomisCode)
    }
  }

  @Nested
  inner class GetCityByNomisCode {
    @Test
    fun `should return a city by nomis code`() {
      // Given
      val nomisCode = "USA"
      val city = CityEntity(
        cityId = 1L,
        nomisCode = nomisCode,
        displaySequence = 123,
        nomisDescription = "Test City",
      )
      `when`(cityRepository.findByNomisCode(nomisCode)).thenReturn(Optional.of(city))

      // When
      val result = cityService.getCityByNomisCode(nomisCode)

      // Then
      assertNotNull(result)
      assertEquals("USA", result.nomisCode)
    }
  }
}
