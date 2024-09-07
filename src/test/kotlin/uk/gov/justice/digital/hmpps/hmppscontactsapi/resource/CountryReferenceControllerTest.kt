package uk.gov.justice.digital.hmpps.hmppscontactsapi.resource

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import uk.gov.justice.digital.hmpps.hmppscontactsapi.model.response.CountryReference
import uk.gov.justice.digital.hmpps.hmppscontactsapi.service.CountryReferenceService

class CountryReferenceControllerTest {

  private val countryReferenceService: CountryReferenceService = mock()
  private val countryReferenceController = CountryReferenceController(countryReferenceService)

  @Test
  fun `getCountryById should return country when found`() {
    val countryId = 1L
    val mockCountry =
      CountryReference(
        countryId = countryId,
        isoAlpha2 = "GB",
        isoAlpha3 = "GBR",
        isoCountryDesc = "United Kingdom",
      )
    `when`(countryReferenceService.getCountryById(countryId)).thenReturn(mockCountry)

    val response: ResponseEntity<CountryReference?> = countryReferenceController.getCountryById(countryId)

    assertEquals(HttpStatus.OK, response.statusCode)
    assertEquals(mockCountry, response.body)
  }

  @Test
  fun `getCountryById should return 404 when country not found`() {
    val countryId = 1L
    `when`(countryReferenceService.getCountryById(countryId)).thenReturn(null)

    val response: ResponseEntity<CountryReference?> = countryReferenceController.getCountryById(countryId)

    assertEquals(HttpStatus.NOT_FOUND, response.statusCode)
    assertEquals(null, response.body)
  }

  @Test
  fun `getAllCountries should return list of countries`() {
    val mockCountries = listOf(
      CountryReference(countryId = 1L, isoAlpha2 = "GB", isoAlpha3 = "GBR", isoCountryDesc = "United Kingdom"),
      CountryReference(countryId = 2L, isoAlpha2 = "US", isoAlpha3 = "USA", isoCountryDesc = "United States"),
    )
    `when`(countryReferenceService.getAllCountries()).thenReturn(mockCountries)

    val response: ResponseEntity<List<CountryReference>> = countryReferenceController.getAllCountries()

    assertEquals(HttpStatus.OK, response.statusCode)
    assertEquals(mockCountries, response.body)
  }

  @Test
  fun `getCountryByNomisCode should return country when found`() {
    val nomisCode = "GB"
    val mockCountry =
      CountryReference(countryId = 1L, isoAlpha2 = "GB", isoAlpha3 = "GBR", isoCountryDesc = "United Kingdom")
    `when`(countryReferenceService.getCountryByNomisCode(nomisCode)).thenReturn(mockCountry)

    val response: ResponseEntity<CountryReference?> = countryReferenceController.getCountryByNomisCode(nomisCode)

    assertEquals(HttpStatus.OK, response.statusCode)
    assertEquals(mockCountry, response.body)
  }

  @Test
  fun `getCountryByIsoAlpha2 should return country when found`() {
    val isoAlpha2 = "GB"
    val mockCountry =
      CountryReference(countryId = 1L, isoAlpha2 = "GB", isoAlpha3 = "GBR", isoCountryDesc = "United Kingdom")
    `when`(countryReferenceService.getCountryByIsoAlpha2(isoAlpha2)).thenReturn(mockCountry)

    val response: ResponseEntity<CountryReference?> = countryReferenceController.getCountryByIsoAlpha2(isoAlpha2)

    assertEquals(HttpStatus.OK, response.statusCode)
    assertEquals(mockCountry, response.body)
  }

  @Test
  fun `getCountryByIsoAlpha3 should return country when found`() {
    val isoAlpha3 = "GBR"
    val mockCountry =
      CountryReference(countryId = 1L, isoAlpha2 = "GB", isoAlpha3 = "GBR", isoCountryDesc = "United Kingdom")
    `when`(countryReferenceService.getCountryByIsoAlpha3(isoAlpha3)).thenReturn(mockCountry)

    val response: ResponseEntity<CountryReference?> = countryReferenceController.getCountryByIsoAlpha3(isoAlpha3)

    assertEquals(HttpStatus.OK, response.statusCode)
    assertEquals(mockCountry, response.body)
  }
}
