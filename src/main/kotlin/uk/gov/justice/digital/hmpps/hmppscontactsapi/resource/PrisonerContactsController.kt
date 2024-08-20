package uk.gov.justice.digital.hmpps.hmppscontactsapi.resource

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.MediaType
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import uk.gov.justice.digital.hmpps.hmppscontactsapi.config.ErrorResponse
import uk.gov.justice.digital.hmpps.hmppscontactsapi.model.PrisonerContactSummary
import uk.gov.justice.digital.hmpps.hmppscontactsapi.service.ContactService

@Tag(name = "Prisoner Contacts Controller")
@RestController
@RequestMapping("/prisoner-contacts", produces = [MediaType.APPLICATION_JSON_VALUE])
class PrisonerContactsController(
  private val contactService: ContactService,
) {

  @Operation(summary = "Endpoint to fetch all contacts for a specific prisoner by prisoner number and active status")
  @ApiResponses(
    value = [
      ApiResponse(
        responseCode = "200",
        description = "List of all contacts for the prisoner",
        content = [
          Content(
            mediaType = "application/json",
            schema = Schema(implementation = PrisonerContactSummary::class),
          ),
        ],
      ),
      ApiResponse(
        responseCode = "401",
        description = "Unauthorised, requires a valid Oauth2 token",
        content = [
          Content(
            mediaType = "application/json",
            schema = Schema(implementation = ErrorResponse::class),
          ),
        ],
      ),
      ApiResponse(
        responseCode = "403",
        description = "Forbidden, requires an appropriate role",
        content = [
          Content(
            mediaType = "application/json",
            schema = Schema(implementation = ErrorResponse::class),
          ),
        ],
      ),
      ApiResponse(
        responseCode = "404",
        description = "The Contact was not found.",
        content = [
          Content(
            mediaType = "application/json",
            schema = Schema(implementation = ErrorResponse::class),
          ),
        ],
      ),
    ],
  )
  @GetMapping(value = ["/prisoner/{prisonerNumber}"], produces = [MediaType.APPLICATION_JSON_VALUE])
  @PreAuthorize("hasAnyRole('ROLE_CONTACTS_ADMIN')")
  fun getAllContacts(
    @PathVariable prisonerNumber: String,
    @RequestParam(name = "active", defaultValue = "true") active: Boolean,
  ): List<PrisonerContactSummary> = contactService.getAllContacts(prisonerNumber, active)
}
