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
import uk.gov.justice.digital.hmpps.hmppscontactsapi.model.request.sync.CreateContactRestrictionRequest
import uk.gov.justice.digital.hmpps.hmppscontactsapi.model.request.sync.UpdateContactRestrictionRequest
import uk.gov.justice.digital.hmpps.hmppscontactsapi.model.response.ContactRestriction
import uk.gov.justice.digital.hmpps.hmppscontactsapi.service.sync.SyncContactRestrictionService
import uk.gov.justice.hmpps.kotlin.common.ErrorResponse

@Tag(name = "Sync endpoints - contact restriction")
@RestController
@RequestMapping(value = ["/sync"], produces = [MediaType.APPLICATION_JSON_VALUE])
class ContactRestrictionSyncController(
  val syncService: SyncContactRestrictionService,
) {
  @GetMapping(path = ["/contact-restriction/{contactRestrictionId}"], produces = [MediaType.APPLICATION_JSON_VALUE])
  @Operation(
    summary = "Returns the data for a contact restriction by contactRestrictionId",
    description = """
      Requires role: ROLE_CONTACTS_MIGRATION.
      Used to get the details for one contact restriction.
      """,
  )
  @ApiResponses(
    value = [
      ApiResponse(
        responseCode = "200",
        description = "Found the contact address",
        content = [
          Content(
            mediaType = "application/json",
            schema = Schema(implementation = ContactRestriction::class),
          ),
        ],
      ),
      ApiResponse(
        responseCode = "404",
        description = "No contact address reference with that id could be found",
      ),
    ],
  )
  @PreAuthorize("hasAnyRole('CONTACTS_MIGRATION')")
  fun getContactRestrictionById(
    @Parameter(description = "The internal ID for a contact restriction.", required = true)
    @PathVariable contactRestrictionId: Long,
  ) = syncService.getContactRestrictionById(contactRestrictionId)

  @DeleteMapping(path = ["/contact-restriction/{contactRestrictionId}"], produces = [MediaType.APPLICATION_JSON_VALUE])
  @Operation(
    summary = "Deletes one contact restriction by internal ID",
    description = """
      Requires role: ROLE_CONTACTS_MIGRATION.
      Used to delete a contact restriction.
      """,
  )
  @ApiResponses(
    value = [
      ApiResponse(
        responseCode = "204",
        description = "Successfully deleted contact address",
      ),
      ApiResponse(
        responseCode = "404",
        description = "No contact address reference with that id could be found",
      ),
    ],
  )
  @PreAuthorize("hasAnyRole('CONTACTS_MIGRATION')")
  fun deleteContactRestrictionById(
    @Parameter(description = "The internal ID for the contact restriction.", required = true)
    @PathVariable contactRestrictionId: Long,
  ) = syncService.deleteContactRestriction(contactRestrictionId)

  @PostMapping(path = ["/contact-restriction"], produces = [MediaType.APPLICATION_JSON_VALUE])
  @ResponseBody
  @Operation(
    summary = "Creates a new contact restriction",
    description = """
      Requires role: ROLE_CONTACTS_MIGRATION.
      Used to create a contact restriction and associate it with a contact.
      """,
  )
  @ApiResponses(
    value = [
      ApiResponse(
        responseCode = "201",
        description = "Successfully created contact address",
        content = [
          Content(
            mediaType = "application/json",
            schema = Schema(implementation = ContactRestriction::class),
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
  fun createContactRestriction(
    @Valid @RequestBody createContactRestrictionRequest: CreateContactRestrictionRequest,
  ) = syncService.createContactRestriction(createContactRestrictionRequest)

  @PutMapping(path = ["/contact-restriction/{contactRestrictionId}"], produces = [MediaType.APPLICATION_JSON_VALUE])
  @ResponseBody
  @Operation(
    summary = "Updates a contact restriction with new or extra detail",
    description = """
      Requires role: ROLE_CONTACTS_MIGRATION.
      Used to update a contact restriction.
      """,
  )
  @ApiResponses(
    value = [
      ApiResponse(
        responseCode = "200",
        description = "Successfully updated contact address",
        content = [
          Content(
            mediaType = "application/json",
            schema = Schema(implementation = ContactRestriction::class),
          ),
        ],
      ),
      ApiResponse(
        responseCode = "404",
        description = "Contact address not found",
      ),
      ApiResponse(
        responseCode = "400",
        description = "Invalid input data",
      ),
    ],
  )
  @PreAuthorize("hasAnyRole('CONTACTS_MIGRATION')")
  fun updateContactRestriction(
    @Parameter(description = "The internal ID for the contact restriction.", required = true)
    @PathVariable contactRestrictionId: Long,
    @Valid @RequestBody updateContactRestrictionRequest: UpdateContactRestrictionRequest,
  ) = syncService.updateContactRestriction(contactRestrictionId, updateContactRestrictionRequest)
}
