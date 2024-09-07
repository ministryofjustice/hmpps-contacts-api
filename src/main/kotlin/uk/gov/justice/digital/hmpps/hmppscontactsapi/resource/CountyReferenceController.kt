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
import uk.gov.justice.digital.hmpps.hmppscontactsapi.model.response.CountyReference
import uk.gov.justice.digital.hmpps.hmppscontactsapi.service.CountyReferenceService
import uk.gov.justice.digital.hmpps.hmppscontactsapi.swagger.AuthApiResponses

@Tag(name = "county-reference")
@RestController
@RequestMapping(value = ["county-reference"], produces = [MediaType.APPLICATION_JSON_VALUE])
@AuthApiResponses
class CountyReferenceController(private val countyReferenceService: CountyReferenceService) {

  companion object {
    private val logger = LoggerFactory.getLogger(this::class.java)
  }

  @GetMapping("/{id}")
  @Operation(
    summary = "Get county reference",
    description = "Gets a county reference by their id",
  )
  @ApiResponses(
    value = [
      ApiResponse(
        responseCode = "200",
        description = "Found the county reference",
        content = [
          Content(
            mediaType = "application/json",
            schema = Schema(implementation = CountyReference::class),
          ),
        ],
      ),
      ApiResponse(
        responseCode = "404",
        description = "No county reference with that id could be found",
      ),
    ],
  )
  @PreAuthorize("hasAnyRole('ROLE_CONTACTS_ADMIN')")
  fun getCountyById(@PathVariable id: Long): ResponseEntity<CountyReference?> {
    val county = countyReferenceService.getCountyById(id)
    return if (county != null) {
      ResponseEntity.ok(county)
    } else {
      logger.info("Couldn't find county reference with id '{}'", id)
      ResponseEntity.notFound().build()
    }
  }

  @GetMapping
  @Operation(
    summary = "Get county reference",
    description = "Gets all county references",
  )
  @ApiResponses(
    value = [
      ApiResponse(
        responseCode = "200",
        description = "Found the county reference",
        content = [
          Content(
            mediaType = "application/json",
            schema = Schema(implementation = CountyReference::class),
          ),
        ],
      ),
    ],
  )
  @PreAuthorize("hasAnyRole('ROLE_CONTACTS_ADMIN')")
  fun getAllCountries(): ResponseEntity<List<CountyReference>> {
    val countries = countyReferenceService.getAllCountries()
    return ResponseEntity.ok(countries)
  }

  @GetMapping("/nomis-code/{code}")
  @Operation(
    summary = "Get county reference",
    description = "Gets a county reference by their nomis code",
  )
  @ApiResponses(
    value = [
      ApiResponse(
        responseCode = "200",
        description = "Found the county reference",
        content = [
          Content(
            mediaType = "application/json",
            schema = Schema(implementation = CountyReference::class),
          ),
        ],
      ),
      ApiResponse(
        responseCode = "404",
        description = "No county reference with that nomis code could be found",
      ),
    ],
  )
  @PreAuthorize("hasAnyRole('ROLE_CONTACTS_ADMIN')")
  fun getCountyByNomisCode(@PathVariable code: String): ResponseEntity<CountyReference?> {
    val county = countyReferenceService.getCountyByNomisCode(code)
    return if (county != null) {
      ResponseEntity.ok(county)
    } else {
      logger.info("Couldn't find county reference with nomis code '{}'", code)
      ResponseEntity.notFound().build()
    }
  }
}
