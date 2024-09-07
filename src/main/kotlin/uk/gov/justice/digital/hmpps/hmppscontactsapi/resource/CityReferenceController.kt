package uk.gov.justice.digital.hmpps.hmppscontactsapi.resource

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import org.slf4j.LoggerFactory
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import uk.gov.justice.digital.hmpps.hmppscontactsapi.model.response.CityReference
import uk.gov.justice.digital.hmpps.hmppscontactsapi.service.CityReferenceService
import uk.gov.justice.digital.hmpps.hmppscontactsapi.swagger.AuthApiResponses

@Tag(name = "city-reference")
@RestController
@RequestMapping(value = ["city-reference"], produces = [MediaType.APPLICATION_JSON_VALUE])
@AuthApiResponses
class CityReferenceController(private val cityReferenceService: CityReferenceService) {

  companion object {
    private val logger = LoggerFactory.getLogger(this::class.java)
  }

  @GetMapping("/{id}")
  @Operation(
    summary = "Get city reference",
    description = "Gets a city reference by their id",
  )
  @ApiResponses(
    value = [
      ApiResponse(
        responseCode = "200",
        description = "Found the city reference",
        content = [
          Content(
            mediaType = "application/json",
            schema = Schema(implementation = CityReference::class),
          ),
        ],
      ),
      ApiResponse(
        responseCode = "404",
        description = "No city reference with that id could be found",
      ),
    ],
  )
  @PreAuthorize("hasAnyRole('ROLE_CONTACTS_ADMIN')")
  fun getCityById(@PathVariable id: Long): ResponseEntity<CityReference?> {
    val city = cityReferenceService.getCityById(id)
    return if (city != null) {
      ResponseEntity.ok(city)
    } else {
      logger.info("Couldn't find city reference with id '{}'", id)
      ResponseEntity.notFound().build()
    }
  }

  @GetMapping
  @Operation(
    summary = "Get city reference",
    description = "Gets all city references",
  )
  @ApiResponses(
    value = [
      ApiResponse(
        responseCode = "200",
        description = "Found the city reference",
        content = [
          Content(
            mediaType = "application/json",
            schema = Schema(implementation = CityReference::class),
          ),
        ],
      ),
    ],
  )
  @PreAuthorize("hasAnyRole('ROLE_CONTACTS_ADMIN')")
  fun getAllCountries(): ResponseEntity<List<CityReference>> {
    val countries = cityReferenceService.getAllCountries()
    return ResponseEntity.ok(countries)
  }

  @GetMapping("/nomis-code/{code}")
  @Operation(
    summary = "Get city reference",
    description = "Gets a city reference by their nomis code",
  )
  @ApiResponses(
    value = [
      ApiResponse(
        responseCode = "200",
        description = "Found the city reference",
        content = [
          Content(
            mediaType = "application/json",
            schema = Schema(implementation = CityReference::class),
          ),
        ],
      ),
      ApiResponse(
        responseCode = "404",
        description = "No city reference with that nomis code could be found",
      ),
    ],
  )
  @PreAuthorize("hasAnyRole('ROLE_CONTACTS_ADMIN')")
  fun getCityByNomisCode(@PathVariable code: String): ResponseEntity<CityReference?> {
    val city = cityReferenceService.getCityByNomisCode(code)
    return if (city != null) {
      ResponseEntity.ok(city)
    } else {
      logger.info("Couldn't find city reference with nomis code '{}'", code)
      ResponseEntity.notFound().build()
    }
  }
}
