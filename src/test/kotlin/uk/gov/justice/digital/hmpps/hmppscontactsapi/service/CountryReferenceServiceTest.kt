package uk.gov.justice.digital.hmpps.hmppscontactsapi.service

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import uk.gov.justice.digital.hmpps.hmppscontactsapi.entity.CountryReferenceEntity
import uk.gov.justice.digital.hmpps.hmppscontactsapi.repository.CountryReferenceRepository
import java.util.*

class CountryReferenceServiceTest {

  private lateinit var countryReferenceService: CountryReferenceService

  @Mock
  private lateinit var countryReferenceRepository: CountryReferenceRepository

  @BeforeEach
  fun setUp() {
    MockitoAnnotations.openMocks(this)
    countryReferenceService = CountryReferenceService(countryReferenceRepository)
  }

  @Nested
  inner class GetCountryByCountryId {

    @Test
    fun `should return a country when valid id is provided`() {
      // Given
      val countryId = 1L
      val country = CountryReferenceEntity(
        countryId = countryId,
        nomisCode = "ABC",
        nomisDescription = "Test Country",
        isoAlpha2 = "TC",
        isoAlpha3 = "TCO",
        isoCountryDesc = "Test Country",
        displaySequence = 123,
        isoNumeric = 111,
      )
      `when`(countryReferenceRepository.findByCountryId(countryId)).thenReturn(Optional.of(country))

      // When
      val result = countryReferenceService.getCountryById(countryId)

      // Then
      assertNotNull(result)
      assertEquals("ABC", result?.nomisCode)
      assertEquals("Test Country", result?.nomisDescription)
    }

    @Test
    fun `should return null when country id does not exist`() {
      // Given
      val countryId = 1L
      `when`(countryReferenceRepository.findByCountryId(countryId)).thenReturn(Optional.empty())

      // When
      val result = countryReferenceService.getCountryById(countryId)

      // Then
      assertNull(result)
    }
  }

  @Nested
  inner class GetAllCountries {

    @Test
    fun `should return a list of all countries`() {
      // Given
      val countries = listOf(
        CountryReferenceEntity(
          countryId = 1L,
          nomisCode = "USA",
          isoAlpha2 = "US",
          nomisDescription = "Test Country",
          isoAlpha3 = "TCO",
          isoCountryDesc = "Test Country",
          displaySequence = 123,
          isoNumeric = 111,
        ),
        CountryReferenceEntity(
          countryId = 2L,
          nomisCode = "CAN",
          isoAlpha2 = "CA",
          nomisDescription = "Test Country",
          isoAlpha3 = "TCO",
          isoCountryDesc = "Test Country",
          displaySequence = 123,
          isoNumeric = 111,
        ),
      )
      `when`(countryReferenceRepository.findAll()).thenReturn(countries)

      // When
      val result = countryReferenceService.getAllCountries()

      // Then
      assertEquals(2, result.size)
      assertEquals("USA", result[0].nomisCode)
      assertEquals("CAN", result[1].nomisCode)
    }
  }

  @Nested
  inner class GetCountrByISOAlpha2Code {

    @Test
    fun `should return a country by ISO Alpha2 code`() {
      // Given
      val isoAlpha2 = "US"
      val country = CountryReferenceEntity(
        countryId = 1L,
        nomisCode = "USA",
        isoAlpha2 = isoAlpha2,
        isoAlpha3 = "USA",
        displaySequence = 123,
        isoNumeric = 111,
        nomisDescription = "Test Country",
        isoCountryDesc = "Test Country",
      )
      `when`(countryReferenceRepository.findByIsoAlpha2(isoAlpha2)).thenReturn(Optional.of(country))

      // When
      val result = countryReferenceService.getCountryByIsoAlpha2(isoAlpha2)

      // Then
      assertNotNull(result)
      assertEquals("US", result?.isoAlpha2)
      assertEquals("USA", result?.nomisCode)
    }

    @Test
    fun `should return null when ISO Alpha2 code does not exist`() {
      // Given
      val isoAlpha2 = "XX"
      `when`(countryReferenceRepository.findByIsoAlpha2(isoAlpha2)).thenReturn(Optional.empty())

      // When
      val result = countryReferenceService.getCountryByIsoAlpha2(isoAlpha2)

      // Then
      assertNull(result)
    }
  }

  @Nested
  inner class GetCountrByISOAlpha3Code {

    @Test
    fun `should return a country by ISO Alpha3 code`() {
      // Given
      val isoAlpha3 = "US"
      val country = CountryReferenceEntity(
        countryId = 1L,
        nomisCode = "USA",
        isoAlpha2 = "US",
        isoAlpha3 = isoAlpha3,
        displaySequence = 123,
        isoNumeric = 111,
        nomisDescription = "Test Country",
        isoCountryDesc = "Test Country",
      )
      `when`(countryReferenceRepository.findByIsoAlpha3(isoAlpha3)).thenReturn(Optional.of(country))

      // When
      val result = countryReferenceService.getCountryByIsoAlpha3(isoAlpha3)

      // Then
      assertNotNull(result)
      assertEquals("US", result?.isoAlpha3)
      assertEquals("USA", result?.nomisCode)
    }

    @Test
    fun `should return null when ISO Alpha3 code does not exist`() {
      // Given
      val isoAlpha3 = "XXX"
      `when`(countryReferenceRepository.findByIsoAlpha3(isoAlpha3)).thenReturn(Optional.empty())

      // When
      val result = countryReferenceService.getCountryByIsoAlpha2(isoAlpha3)

      // Then
      assertNull(result)
    }
  }

  @Nested
  inner class GetCountryByNomisCode {
    @Test
    fun `should return a country by nomis code`() {
      // Given
      val nomisCode = "USA"
      val country = CountryReferenceEntity(
        countryId = 1L,
        nomisCode = nomisCode,
        isoAlpha2 = "US",
        isoAlpha3 = "USA",
        displaySequence = 123,
        isoNumeric = 111,
        nomisDescription = "Test Country",
        isoCountryDesc = "Test Country",
      )
      `when`(countryReferenceRepository.findByNomisCode(nomisCode)).thenReturn(Optional.of(country))

      // When
      val result = countryReferenceService.getCountryByNomisCode(nomisCode)

      // Then
      assertNotNull(result)
      assertEquals("USA", result?.nomisCode)
      assertEquals("US", result?.isoAlpha2)
      assertEquals("USA", result?.isoAlpha3)
    }

    @Test
    fun `should return null when nomis code does not exist`() {
      // Given
      val nomisCode = "XXX"
      `when`(countryReferenceRepository.findByNomisCode(nomisCode)).thenReturn(Optional.empty())

      // When
      val result = countryReferenceService.getCountryByIsoAlpha2(nomisCode)

      // Then
      assertNull(result)
    }
  }
}
