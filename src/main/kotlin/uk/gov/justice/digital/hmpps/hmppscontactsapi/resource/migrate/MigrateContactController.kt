package uk.gov.justice.digital.hmpps.hmppscontactsapi.resource.migrate

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.slf4j.LoggerFactory
import org.springframework.http.MediaType
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import uk.gov.justice.digital.hmpps.hmppscontactsapi.model.request.migrate.MigrateContactRequest
import uk.gov.justice.digital.hmpps.hmppscontactsapi.model.response.migrate.MigrateContactResponse
import uk.gov.justice.digital.hmpps.hmppscontactsapi.service.migrate.MigrationService
import uk.gov.justice.digital.hmpps.hmppscontactsapi.swagger.AuthApiResponses
import uk.gov.justice.hmpps.kotlin.common.ErrorResponse

@Tag(name = "Migration")
@RestController
@RequestMapping(value = ["migrate/contact"], produces = [MediaType.APPLICATION_JSON_VALUE])
@AuthApiResponses
class MigrateContactController(val migrationService: MigrationService) {

  companion object {
    private val logger = LoggerFactory.getLogger(this::class.java)
  }

  @PostMapping(consumes = [MediaType.APPLICATION_JSON_VALUE])
  @Operation(
    summary = "Migrate a contact",
    description = "Migrate a contact from NOMIS with all of its associated data.",
  )
  @ApiResponses(
    value = [
      ApiResponse(
        responseCode = "200",
        description = "The contact and associated data was created successfully",
        content = [
          Content(
            mediaType = "application/json",
            schema = Schema(implementation = MigrateContactResponse::class),
          ),
        ],
      ),
      ApiResponse(
        responseCode = "400",
        description = "The request failed validation with invalid or missing data supplied",
        content = [Content(schema = Schema(implementation = ErrorResponse::class))],
      ),
    ],
  )
  @PreAuthorize("hasAnyRole('ROLE_PRISONER_CONTACTS__RW')")
  fun migrateContact(
    @Valid @RequestBody migrateContactRequest: MigrateContactRequest,
  ) = migrationService.migrateContact(migrateContactRequest)
}