package uk.gov.justice.digital.hmpps.hmppscontactsapi.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import uk.gov.justice.digital.hmpps.hmppscontactsapi.config.ErrorResponse
import uk.gov.justice.digital.hmpps.hmppscontactsapi.mapping.toModel
import uk.gov.justice.digital.hmpps.hmppscontactsapi.model.PrisonerContact
import uk.gov.justice.digital.hmpps.hmppscontactsapi.service.ContactService

@Tag(name = "Prisoner Contacts Controller")
@RestController
@RequestMapping("/prisoner-contacts", produces = [MediaType.APPLICATION_JSON_VALUE])
class PrisonerContactsController @Autowired constructor(
  private val contactService: ContactService,
) {
  private val logger = LoggerFactory.getLogger(PrisonerContactsController::class.java)

  @Operation(summary = "Endpoint to fetch all contacts for a specific prisoner by prisoner number and active status")
  @ApiResponses(
    value = [
      ApiResponse(
        responseCode = "200",
        description = "List of all contacts for the prisoner",
        content = [
          Content(
            mediaType = "application/json",
            schema = Schema(implementation = PrisonerContact::class),
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
    ],
  )
  @GetMapping(value = ["/prisoner/{prisonerNumber}"], produces = [MediaType.APPLICATION_JSON_VALUE])
  @PreAuthorize("hasAnyRole('ROLE_CONTACTS_ADMIN')")
  fun getAllContacts(
    @PathVariable prisonerNumber: String,
    @RequestParam(name = "active", defaultValue = "true") active: Boolean,
  ): ResponseEntity<List<PrisonerContact>> {
    logger.info("Received request to fetch all contacts for prisoner number: $prisonerNumber with active status: $active")
    val contacts = contactService.getAllContacts(prisonerNumber, active).toModel()
    logger.info("Returning {} contacts", contacts.size)
    return ResponseEntity.ok(contacts)
  }
}
