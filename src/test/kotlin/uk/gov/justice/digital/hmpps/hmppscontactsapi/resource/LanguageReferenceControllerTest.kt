package uk.gov.justice.digital.hmpps.hmppscontactsapi.resource

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import uk.gov.justice.digital.hmpps.hmppscontactsapi.model.response.LanguageReference
import uk.gov.justice.digital.hmpps.hmppscontactsapi.service.LanguageReferenceService

class LanguageReferenceControllerTest {

  private val languageReferenceService: LanguageReferenceService = mock()
  private val languageReferenceController = LanguageReferenceController(languageReferenceService)

  @Test
  fun `getLanguageById should return language when found`() {
    val languageId = 1L
    val mockLanguage =
      LanguageReference(
        languageId = languageId,
        isoAlpha2 = "GB",
        isoAlpha3 = "GBR",
        isoLanguageDesc = "United Kingdom",
      )
    `when`(languageReferenceService.getLanguageById(languageId)).thenReturn(mockLanguage)

    val response: ResponseEntity<LanguageReference?> = languageReferenceController.getLanguageById(languageId)

    assertEquals(HttpStatus.OK, response.statusCode)
    assertEquals(mockLanguage, response.body)
  }

  @Test
  fun `getLanguageById should return 404 when language not found`() {
    val languageId = 1L
    `when`(languageReferenceService.getLanguageById(languageId)).thenReturn(null)

    val response: ResponseEntity<LanguageReference?> = languageReferenceController.getLanguageById(languageId)

    assertEquals(HttpStatus.NOT_FOUND, response.statusCode)
    assertEquals(null, response.body)
  }

  @Test
  fun `getAllCountries should return list of countries`() {
    val mockCountries = listOf(
      LanguageReference(languageId = 1L, isoAlpha2 = "GB", isoAlpha3 = "GBR", isoLanguageDesc = "United Kingdom"),
      LanguageReference(languageId = 2L, isoAlpha2 = "US", isoAlpha3 = "USA", isoLanguageDesc = "United States"),
    )
    `when`(languageReferenceService.getAllCountries()).thenReturn(mockCountries)

    val response: ResponseEntity<List<LanguageReference>> = languageReferenceController.getAllCountries()

    assertEquals(HttpStatus.OK, response.statusCode)
    assertEquals(mockCountries, response.body)
  }

  @Test
  fun `getLanguageByNomisCode should return language when found`() {
    val nomisCode = "GB"
    val mockLanguage =
      LanguageReference(languageId = 1L, isoAlpha2 = "GB", isoAlpha3 = "GBR", isoLanguageDesc = "United Kingdom")
    `when`(languageReferenceService.getLanguageByNomisCode(nomisCode)).thenReturn(mockLanguage)

    val response: ResponseEntity<LanguageReference?> = languageReferenceController.getLanguageByNomisCode(nomisCode)

    assertEquals(HttpStatus.OK, response.statusCode)
    assertEquals(mockLanguage, response.body)
  }

  @Test
  fun `getLanguageByIsoAlpha2 should return language when found`() {
    val isoAlpha2 = "GB"
    val mockLanguage =
      LanguageReference(languageId = 1L, isoAlpha2 = "GB", isoAlpha3 = "GBR", isoLanguageDesc = "United Kingdom")
    `when`(languageReferenceService.getLanguageByIsoAlpha2(isoAlpha2)).thenReturn(mockLanguage)

    val response: ResponseEntity<LanguageReference?> = languageReferenceController.getLanguageByIsoAlpha2(isoAlpha2)

    assertEquals(HttpStatus.OK, response.statusCode)
    assertEquals(mockLanguage, response.body)
  }

  @Test
  fun `getLanguageByIsoAlpha3 should return language when found`() {
    val isoAlpha3 = "GBR"
    val mockLanguage =
      LanguageReference(languageId = 1L, isoAlpha2 = "GB", isoAlpha3 = "GBR", isoLanguageDesc = "United Kingdom")
    `when`(languageReferenceService.getLanguageByIsoAlpha3(isoAlpha3)).thenReturn(mockLanguage)

    val response: ResponseEntity<LanguageReference?> = languageReferenceController.getLanguageByIsoAlpha3(isoAlpha3)

    assertEquals(HttpStatus.OK, response.statusCode)
    assertEquals(mockLanguage, response.body)
  }
}
