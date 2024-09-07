package uk.gov.justice.digital.hmpps.hmppscontactsapi.resource

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import uk.gov.justice.digital.hmpps.hmppscontactsapi.model.response.CityReference
import uk.gov.justice.digital.hmpps.hmppscontactsapi.service.CityReferenceService

class CityReferenceControllerTest {

  private val cityReferenceService: CityReferenceService = mock()
  private val cityReferenceController = CityReferenceController(cityReferenceService)

  @Test
  fun `getCityById should return city when found`() {
    val cityId = 1L
    val mockCity =
      CityReference(cityId = 1L, nomisCode = "GB", nomisDescription = "United Kingdom")
    `when`(cityReferenceService.getCityById(cityId)).thenReturn(mockCity)

    val response: ResponseEntity<CityReference?> = cityReferenceController.getCityById(cityId)

    assertEquals(HttpStatus.OK, response.statusCode)
    assertEquals(mockCity, response.body)
  }

  @Test
  fun `getCityById should return 404 when city not found`() {
    val cityId = 1L
    `when`(cityReferenceService.getCityById(cityId)).thenReturn(null)

    val response: ResponseEntity<CityReference?> = cityReferenceController.getCityById(cityId)

    assertEquals(HttpStatus.NOT_FOUND, response.statusCode)
    assertEquals(null, response.body)
  }

  @Test
  fun `getAllCountries should return list of countries`() {
    val mockCountries = listOf(
      CityReference(cityId = 1L, nomisCode = "GB", nomisDescription = "United Kingdom"),
      CityReference(cityId = 2L, nomisCode = "US", nomisDescription = "United States"),
    )
    `when`(cityReferenceService.getAllCountries()).thenReturn(mockCountries)

    val response: ResponseEntity<List<CityReference>> = cityReferenceController.getAllCountries()

    assertEquals(HttpStatus.OK, response.statusCode)
    assertEquals(mockCountries, response.body)
  }

  @Test
  fun `getCityByNomisCode should return city when found`() {
    val nomisCode = "GB"
    val mockCity =
      CityReference(cityId = 1L, nomisCode = "GB", nomisDescription = "United Kingdom")
    `when`(cityReferenceService.getCityByNomisCode(nomisCode)).thenReturn(mockCity)

    val response: ResponseEntity<CityReference?> = cityReferenceController.getCityByNomisCode(nomisCode)

    assertEquals(HttpStatus.OK, response.statusCode)
    assertEquals(mockCity, response.body)
  }
}
