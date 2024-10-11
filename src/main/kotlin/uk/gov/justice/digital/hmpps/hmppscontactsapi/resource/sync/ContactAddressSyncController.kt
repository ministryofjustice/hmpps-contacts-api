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
import uk.gov.justice.digital.hmpps.hmppscontactsapi.model.request.sync.CreateContactAddressRequest
import uk.gov.justice.digital.hmpps.hmppscontactsapi.model.request.sync.UpdateContactAddressRequest
import uk.gov.justice.digital.hmpps.hmppscontactsapi.model.response.sync.ContactAddress
import uk.gov.justice.digital.hmpps.hmppscontactsapi.service.sync.SyncContactAddressService
import uk.gov.justice.hmpps.kotlin.common.ErrorResponse

@Tag(name = "Sync endpoints - contact address")
@RestController
@RequestMapping(value = ["/sync"], produces = [MediaType.APPLICATION_JSON_VALUE])
class ContactAddressSyncController(
  val syncContactAddressService: SyncContactAddressService,
) {
  @GetMapping(path = ["/contact-address/{contactAddressId}"], produces = [MediaType.APPLICATION_JSON_VALUE])
  @Operation(
    summary = "Returns the data for a contact address by contactAddressId",
    description = """
      Requires role: ROLE_CONTACTS_MIGRATION.
      Used to get the details for one contact address.
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
            schema = Schema(implementation = ContactAddress::class),
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
  fun getContactAddressById(
    @Parameter(description = "The internal ID for a contact address.", required = true)
    @PathVariable contactAddressId: Long,
  ) = syncContactAddressService.getContactAddressById(contactAddressId)

  @DeleteMapping(path = ["/contact-address/{contactAddressId}"], produces = [MediaType.APPLICATION_JSON_VALUE])
  @Operation(
    summary = "Deletes one contact address by internal ID",
    description = """
      Requires role: ROLE_CONTACTS_MIGRATION.
      Used to delete a contact address.
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
  fun deleteContactAddressById(
    @Parameter(description = "The internal ID for the contact address.", required = true)
    @PathVariable contactAddressId: Long,
  ) = syncContactAddressService.deleteContactAddressById(contactAddressId)

  @PostMapping(path = ["/contact-address"], produces = [MediaType.APPLICATION_JSON_VALUE])
  @ResponseBody
  @Operation(
    summary = "Creates a new contact address",
    description = """
      Requires role: ROLE_CONTACTS_MIGRATION.
      Used to create a contact address and associate it with a contact.
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
            schema = Schema(implementation = ContactAddress::class),
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
  fun createContactAddress(
    @Valid @RequestBody createContactAddressRequest: CreateContactAddressRequest,
  ) = syncContactAddressService.createContactAddress(createContactAddressRequest)

  @PutMapping(path = ["/contact-address/{contactAddressId}"], produces = [MediaType.APPLICATION_JSON_VALUE])
  @ResponseBody
  @Operation(
    summary = "Updates a contact address with new or extra detail",
    description = """
      Requires role: ROLE_CONTACTS_MIGRATION.
      Used to update a contact address.
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
            schema = Schema(implementation = ContactAddress::class),
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
  fun updateContactAddress(
    @Parameter(description = "The internal ID for the contact address.", required = true)
    @PathVariable contactAddressId: Long,
    @Valid @RequestBody updateContactAddressRequest: UpdateContactAddressRequest,
  ) = syncContactAddressService.updateContactAddress(contactAddressId, updateContactAddressRequest)
}