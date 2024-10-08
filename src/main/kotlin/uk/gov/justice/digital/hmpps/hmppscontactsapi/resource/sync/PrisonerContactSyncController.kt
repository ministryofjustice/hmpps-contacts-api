package uk.gov.justice.digital.hmpps.hmppscontactsapi.resource.sync

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.MediaType
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.RestController
import uk.gov.justice.digital.hmpps.hmppscontactsapi.model.request.sync.CreatePrisonerContactRequest
import uk.gov.justice.digital.hmpps.hmppscontactsapi.model.request.sync.UpdatePrisonerContactRequest
import uk.gov.justice.digital.hmpps.hmppscontactsapi.model.response.sync.PrisonerContact
import uk.gov.justice.digital.hmpps.hmppscontactsapi.service.sync.SyncPrisonerContactService
import uk.gov.justice.digital.hmpps.hmppscontactsapi.swagger.AuthApiResponses
import uk.gov.justice.hmpps.kotlin.common.ErrorResponse

@Tag(name = "Sync endpoints - prisoner contact")
@RestController
@RequestMapping(value = ["/sync"], produces = [MediaType.APPLICATION_JSON_VALUE])
@AuthApiResponses
class PrisonerContactSyncController(
  val syncService: SyncPrisonerContactService,
) {
  @GetMapping(path = ["/prisoner-contact/{prisonerContactId}"], produces = [MediaType.APPLICATION_JSON_VALUE])
  @Operation(
    summary = "Returns the data for a prisoner contact by prisonerContactId",
    description = """
      Requires role: ROLE_CONTACTS_MIGRATION.
      Used to get the details for one prisoner contact.
      """,
  )
  @ApiResponses(
    value = [
      ApiResponse(
        responseCode = "200",
        description = "Found the prisoner contact",
        content = [
          Content(
            mediaType = "application/json",
            schema = Schema(implementation = PrisonerContact::class),
          ),
        ],
      ),
      ApiResponse(
        responseCode = "404",
        description = "No prisoner contact reference with that id could be found",
      ),
    ],
  )
  @PreAuthorize("hasAnyRole('CONTACTS_MIGRATION')")
  fun getPrisonerContactById(
    @Parameter(description = "The internal ID for a prisoner contact.", required = true)
    @PathVariable prisonerContactId: Long,
  ) = syncService.getPrisonerContactById(prisonerContactId)

  @DeleteMapping(path = ["/prisoner-contact/{prisonerContactId}"], produces = [MediaType.APPLICATION_JSON_VALUE])
  @Operation(
    summary = "Deletes one prisoner contact by internal ID",
    description = """
      Requires role: ROLE_CONTACTS_MIGRATION.
      Used to delete a prisoner contact.
      """,
  )
  @ApiResponses(
    value = [
      ApiResponse(
        responseCode = "204",
        description = "Successfully deleted prisoner contact",
      ),
      ApiResponse(
        responseCode = "404",
        description = "No prisoner contact reference with that id could be found",
      ),
    ],
  )
  @PreAuthorize("hasAnyRole('CONTACTS_MIGRATION')")
  fun deletePrisonerContactById(
    @Parameter(description = "The internal ID for the prisoner contact.", required = true)
    @PathVariable prisonerContactId: Long,
  ) = syncService.deletePrisonerContact(prisonerContactId)

  @PostMapping(path = ["/prisoner-contact"], produces = [MediaType.APPLICATION_JSON_VALUE])
  @ResponseBody
  @Operation(
    summary = "Creates a new prisoner contact",
    description = """
      Requires role: ROLE_CONTACTS_MIGRATION.
      Used to create a prisoner contact.
      """,
  )
  @ApiResponses(
    value = [
      ApiResponse(
        responseCode = "201",
        description = "Successfully created prisoner contact",
        content = [
          Content(
            mediaType = "application/json",
            schema = Schema(implementation = PrisonerContact::class),
          ),
        ],
      ),
      ApiResponse(
        responseCode = "400",
        description = "The request has invalid or missing fields",
        content = [Content(schema = Schema(implementation = ErrorResponse::class))],
      ),
    ],
  )
  @PreAuthorize("hasAnyRole('CONTACTS_MIGRATION')")
  fun createPrisonerContact(
    @Valid @RequestBody createPrisonerContactRequest: CreatePrisonerContactRequest,
  ) = syncService.createPrisonerContact(createPrisonerContactRequest)

  @PutMapping(path = ["/prisoner-contact/{prisonerContactId}"], produces = [MediaType.APPLICATION_JSON_VALUE])
  @ResponseBody
  @Operation(
    summary = "Updates a prisoner contact with new or extra detail",
    description = """
      Requires role: ROLE_CONTACTS_MIGRATION.
      Used to update a prisoner contact.
      """,
  )
  @ApiResponses(
    value = [
      ApiResponse(
        responseCode = "200",
        description = "Successfully updated prisoner contact",
        content = [
          Content(
            mediaType = "application/json",
            schema = Schema(implementation = PrisonerContact::class),
          ),
        ],
      ),
      ApiResponse(
        responseCode = "404",
        description = "PrisonerContact not found",
      ),
      ApiResponse(
        responseCode = "400",
        description = "Invalid input data",
      ),
    ],
  )
  @PreAuthorize("hasAnyRole('CONTACTS_MIGRATION')")
  fun updatePrisonerContact(
    @Parameter(description = "The internal ID for the prisoner contact.", required = true)
    @PathVariable prisonerContactId: Long,
    @Valid @RequestBody updatePrisonerContactRequest: UpdatePrisonerContactRequest,
  ) = syncService.updatePrisonerContact(prisonerContactId, updatePrisonerContactRequest)
}