package uk.gov.justice.digital.hmpps.hmppscontactsapi.resource

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import uk.gov.justice.digital.hmpps.hmppscontactsapi.model.response.CountyReference
import uk.gov.justice.digital.hmpps.hmppscontactsapi.service.CountyReferenceService

class CountyReferenceControllerTest {

  private val countyReferenceService: CountyReferenceService = mock()
  private val countyReferenceController = CountyReferenceController(countyReferenceService)

  @Test
  fun `getCountyById should return county when found`() {
    val countyId = 1L
    val mockCounty =
      CountyReference(countyId = countyId, nomisCode = "GB", nomisDescription = "United Kingdom")
    `when`(countyReferenceService.getCountyById(countyId)).thenReturn(mockCounty)

    val response: ResponseEntity<CountyReference?> = countyReferenceController.getCountyById(countyId)

    assertEquals(HttpStatus.OK, response.statusCode)
    assertEquals(mockCounty, response.body)
  }

  @Test
  fun `getCountyById should return 404 when county not found`() {
    val countyId = 1L
    `when`(countyReferenceService.getCountyById(countyId)).thenReturn(null)

    val response: ResponseEntity<CountyReference?> = countyReferenceController.getCountyById(countyId)

    assertEquals(HttpStatus.NOT_FOUND, response.statusCode)
    assertEquals(null, response.body)
  }

  @Test
  fun `getAllCountries should return list of countries`() {
    val mockCountries = listOf(
      CountyReference(countyId = 1L, nomisCode = "GB", nomisDescription = "United Kingdom"),
      CountyReference(countyId = 2L, nomisCode = "GB", nomisDescription = "United Kingdom"),
    )
    `when`(countyReferenceService.getAllCountries()).thenReturn(mockCountries)

    val response: ResponseEntity<List<CountyReference>> = countyReferenceController.getAllCountries()

    assertEquals(HttpStatus.OK, response.statusCode)
    assertEquals(mockCountries, response.body)
  }

  @Test
  fun `getCountyByNomisCode should return county when found`() {
    val nomisCode = "GB"
    val mockCounty =
      CountyReference(countyId = 1L, nomisCode = "GB", nomisDescription = "United Kingdom")
    `when`(countyReferenceService.getCountyByNomisCode(nomisCode)).thenReturn(mockCounty)

    val response: ResponseEntity<CountyReference?> = countyReferenceController.getCountyByNomisCode(nomisCode)

    assertEquals(HttpStatus.OK, response.statusCode)
    assertEquals(mockCounty, response.body)
  }
}
