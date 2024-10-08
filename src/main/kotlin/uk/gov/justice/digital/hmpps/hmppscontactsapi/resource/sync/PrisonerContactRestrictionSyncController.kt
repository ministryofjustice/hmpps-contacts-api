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
import uk.gov.justice.digital.hmpps.hmppscontactsapi.model.request.sync.CreatePrisonerContactRestrictionRequest
import uk.gov.justice.digital.hmpps.hmppscontactsapi.model.request.sync.UpdatePrisonerContactRestrictionRequest
import uk.gov.justice.digital.hmpps.hmppscontactsapi.model.response.sync.PrisonerContactRestriction
import uk.gov.justice.digital.hmpps.hmppscontactsapi.service.sync.SyncPrisonerContactRestrictionService
import uk.gov.justice.digital.hmpps.hmppscontactsapi.swagger.AuthApiResponses
import uk.gov.justice.hmpps.kotlin.common.ErrorResponse

@Tag(name = "Sync endpoints - prisoner contact restriction")
@RestController
@RequestMapping(value = ["/sync"], produces = [MediaType.APPLICATION_JSON_VALUE])
@AuthApiResponses
class PrisonerContactRestrictionSyncController(
  val syncService: SyncPrisonerContactRestrictionService,
) {
  @GetMapping(path = ["/prisoner-contact-restriction/{id}"], produces = [MediaType.APPLICATION_JSON_VALUE])
  @Operation(
    summary = "Returns the data for a prisoner contact restriction by id",
    description = """
      Requires role: ROLE_CONTACTS_MIGRATION.
      Used to get the details for one prisoner contact restriction.
      """,
  )
  @ApiResponses(
    value = [
      ApiResponse(
        responseCode = "200",
        description = "Found the prisoner contact restriction",
        content = [
          Content(
            mediaType = "application/json",
            schema = Schema(implementation = PrisonerContactRestriction::class),
          ),
        ],
      ),
      ApiResponse(
        responseCode = "404",
        description = "No prisoner contact restriction reference with that id could be found",
      ),
    ],
  )
  @PreAuthorize("hasAnyRole('CONTACTS_MIGRATION')")
  fun getPrisonerContactRestrictionById(
    @Parameter(description = "The internal ID for a prisoner contact restriction.", required = true)
    @PathVariable id: Long,
  ) = syncService.getPrisonerContactRestrictionById(id)

  @DeleteMapping(path = ["/prisoner-contact-restriction/{id}"], produces = [MediaType.APPLICATION_JSON_VALUE])
  @Operation(
    summary = "Deletes one prisoner contact restriction by internal ID",
    description = """
      Requires role: ROLE_CONTACTS_MIGRATION.
      Used to delete a prisoner contact restriction.
      """,
  )
  @ApiResponses(
    value = [
      ApiResponse(
        responseCode = "204",
        description = "Successfully deleted prisoner contact restriction",
      ),
      ApiResponse(
        responseCode = "404",
        description = "No prisoner contact restriction reference with that id could be found",
      ),
    ],
  )
  @PreAuthorize("hasAnyRole('CONTACTS_MIGRATION')")
  fun deletePrisonerContactRestrictionById(
    @Parameter(description = "The internal ID for the prisoner contact restriction.", required = true)
    @PathVariable id: Long,
  ) = syncService.deletePrisonerContactRestriction(id)

  @PostMapping(path = ["/prisoner-contact-restriction"], produces = [MediaType.APPLICATION_JSON_VALUE])
  @ResponseBody
  @Operation(
    summary = "Creates a new prisoner contact restriction",
    description = """
      Requires role: ROLE_CONTACTS_MIGRATION.
      Used to create a prisoner contact restriction.
      """,
  )
  @ApiResponses(
    value = [
      ApiResponse(
        responseCode = "201",
        description = "Successfully created prisoner contact restriction",
        content = [
          Content(
            mediaType = "application/json",
            schema = Schema(implementation = PrisonerContactRestriction::class),
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
  fun createPrisonerContactRestriction(
    @Valid @RequestBody createPrisonerContactRestrictionRequest: CreatePrisonerContactRestrictionRequest,
  ) = syncService.createPrisonerContactRestriction(createPrisonerContactRestrictionRequest)

  @PutMapping(path = ["/prisoner-contact-restriction/{id}"], produces = [MediaType.APPLICATION_JSON_VALUE])
  @ResponseBody
  @Operation(
    summary = "Updates a prisoner contact restriction with new or extra detail",
    description = """
      Requires role: ROLE_CONTACTS_MIGRATION.
      Used to update a prisoner contact restriction.
      """,
  )
  @ApiResponses(
    value = [
      ApiResponse(
        responseCode = "200",
        description = "Successfully updated prisoner contact restriction",
        content = [
          Content(
            mediaType = "application/json",
            schema = Schema(implementation = PrisonerContactRestriction::class),
          ),
        ],
      ),
      ApiResponse(
        responseCode = "404",
        description = "Prisoner contact restriction not found",
      ),
      ApiResponse(
        responseCode = "400",
        description = "Invalid input data",
      ),
    ],
  )
  @PreAuthorize("hasAnyRole('CONTACTS_MIGRATION')")
  fun updatePrisonerContactRestriction(
    @Parameter(description = "The internal ID for the prisoner contact restriction.", required = true)
    @PathVariable id: Long,
    @Valid @RequestBody updatePrisonerContactRestrictionRequest: UpdatePrisonerContactRestrictionRequest,
  ) = syncService.updatePrisonerContactRestriction(id, updatePrisonerContactRestrictionRequest)
}