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
import uk.gov.justice.digital.hmpps.hmppscontactsapi.model.response.CountryReference
import uk.gov.justice.digital.hmpps.hmppscontactsapi.service.CountryReferenceService
import uk.gov.justice.digital.hmpps.hmppscontactsapi.swagger.AuthApiResponses

@Tag(name = "country-reference")
@RestController
@RequestMapping(value = ["country-reference"], produces = [MediaType.APPLICATION_JSON_VALUE])
@AuthApiResponses
class CountryReferenceController(private val countryReferenceService: CountryReferenceService) {

  companion object {
    private val logger = LoggerFactory.getLogger(this::class.java)
  }

  @GetMapping("/{id}")
  @Operation(
    summary = "Get country reference",
    description = "Gets a country reference by their id",
  )
  @ApiResponses(
    value = [
      ApiResponse(
        responseCode = "200",
        description = "Found the country reference",
        content = [
          Content(
            mediaType = "application/json",
            schema = Schema(implementation = CountryReference::class),
          ),
        ],
      ),
      ApiResponse(
        responseCode = "404",
        description = "No country reference with that id could be found",
      ),
    ],
  )
  @PreAuthorize("hasAnyRole('ROLE_CONTACTS_ADMIN')")
  fun getCountryById(@PathVariable id: Long): ResponseEntity<CountryReference?> {
    val country = countryReferenceService.getCountryById(id)
    return if (country != null) {
      ResponseEntity.ok(country)
    } else {
      logger.info("Couldn't find country reference with id '{}'", id)
      ResponseEntity.notFound().build()
    }
  }

  @GetMapping
  @Operation(
    summary = "Get country reference",
    description = "Gets all country references",
  )
  @ApiResponses(
    value = [
      ApiResponse(
        responseCode = "200",
        description = "Found the country reference",
        content = [
          Content(
            mediaType = "application/json",
            schema = Schema(implementation = CountryReference::class),
          ),
        ],
      ),
    ],
  )
  @PreAuthorize("hasAnyRole('ROLE_CONTACTS_ADMIN')")
  fun getAllCountries(): ResponseEntity<List<CountryReference>> {
    val countries = countryReferenceService.getAllCountries()
    return ResponseEntity.ok(countries)
  }

  @GetMapping("/nomis-code/{code}")
  @Operation(
    summary = "Get country reference",
    description = "Gets a country reference by their nomis code",
  )
  @ApiResponses(
    value = [
      ApiResponse(
        responseCode = "200",
        description = "Found the country reference",
        content = [
          Content(
            mediaType = "application/json",
            schema = Schema(implementation = CountryReference::class),
          ),
        ],
      ),
      ApiResponse(
        responseCode = "404",
        description = "No country reference with that nomis code could be found",
      ),
    ],
  )
  @PreAuthorize("hasAnyRole('ROLE_CONTACTS_ADMIN')")
  fun getCountryByNomisCode(@PathVariable code: String): ResponseEntity<CountryReference?> {
    val country = countryReferenceService.getCountryByNomisCode(code)
    return if (country != null) {
      ResponseEntity.ok(country)
    } else {
      logger.info("Couldn't find country reference with nomis code '{}'", code)
      ResponseEntity.notFound().build()
    }
  }

  @GetMapping("/iso-alpha2/{code}")
  @Operation(
    summary = "Get country reference",
    description = "Gets a country reference by their ISO Alpha 2 code",
  )
  @ApiResponses(
    value = [
      ApiResponse(
        responseCode = "200",
        description = "Found the country reference",
        content = [
          Content(
            mediaType = "application/json",
            schema = Schema(implementation = CountryReference::class),
          ),
        ],
      ),
      ApiResponse(
        responseCode = "404",
        description = "No country reference with that ISO Alpha 2 code could be found",
      ),
    ],
  )
  @PreAuthorize("hasAnyRole('ROLE_CONTACTS_ADMIN')")
  fun getCountryByIsoAlpha2(@PathVariable code: String): ResponseEntity<CountryReference?> {
    val country = countryReferenceService.getCountryByIsoAlpha2(code)
    return if (country != null) {
      ResponseEntity.ok(country)
    } else {
      logger.info("Couldn't find country reference with alpha 2 code '{}'", code)
      ResponseEntity.notFound().build()
    }
  }

  @GetMapping("/iso-alpha3/{code}")
  @Operation(
    summary = "Get country reference",
    description = "Gets a country reference by their by ISO Alpha 3 code",
  )
  @ApiResponses(
    value = [
      ApiResponse(
        responseCode = "200",
        description = "Found the country reference",
        content = [
          Content(
            mediaType = "application/json",
            schema = Schema(implementation = CountryReference::class),
          ),
        ],
      ),
      ApiResponse(
        responseCode = "404",
        description = "No country reference with that ISO Alpha 3 code could be found",
      ),
    ],
  )
  @PreAuthorize("hasAnyRole('ROLE_CONTACTS_ADMIN')")
  fun getCountryByIsoAlpha3(@PathVariable code: String): ResponseEntity<CountryReference?> {
    val country = countryReferenceService.getCountryByIsoAlpha3(code)
    return if (country != null) {
      ResponseEntity.ok(country)
    } else {
      logger.info("Couldn't find country reference with alpha 3 code '{}'", code)
      ResponseEntity.notFound().build()
    }
  }
}
