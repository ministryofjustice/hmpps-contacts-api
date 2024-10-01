package uk.gov.justice.digital.hmpps.hmppscontactsapi.resource

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
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
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import uk.gov.justice.digital.hmpps.hmppscontactsapi.model.request.CreateContactIdentityRequest
import uk.gov.justice.digital.hmpps.hmppscontactsapi.model.request.UpdateContactIdentityRequest
import uk.gov.justice.digital.hmpps.hmppscontactsapi.service.SyncContactIdentityService

@Tag(name = "Sync endpoints")
@RestController
@RequestMapping(value = ["/sync"], produces = [MediaType.APPLICATION_JSON_VALUE])
class ContactIdentitySyncController(
  val syncService: SyncContactIdentityService,
) {
  @GetMapping(path = ["/contact-identity/{contactIdentityId}"], produces = [MediaType.APPLICATION_JSON_VALUE])
  @ResponseStatus(HttpStatus.OK)
  @Operation(
    summary = "Returns the data for a contact identity by contactIdentityId",
    description = """
      Requires role: ROLE_CONTACTS_MIGRATION.
      Used to get the details for one contact identity.
      """,
  )
  @PreAuthorize("hasAnyRole('CONTACTS_MIGRATION')")
  fun getContactIdentityById(
    @Parameter(description = "The internal ID for a contact identity.", required = true)
    @PathVariable contactIdentityId: Long,
  ) = syncService.getContactIdentityById(contactIdentityId)

  @DeleteMapping(path = ["/contact-identity/{contactIdentityId}"], produces = [MediaType.APPLICATION_JSON_VALUE])
  @ResponseStatus(HttpStatus.OK)
  @Operation(
    summary = "Deletes one contact identity by internal ID",
    description = """
      Requires role: ROLE_CONTACTS_MIGRATION.
      Used to delete a contact identity.
      """,
  )
  @PreAuthorize("hasAnyRole('CONTACTS_MIGRATION')")
  fun deleteContactIdentityById(
    @Parameter(description = "The internal ID for the contact identity.", required = true)
    @PathVariable contactIdentityId: Long,
  ) = syncService.deleteContactIdentity(contactIdentityId)

  @PutMapping(path = ["/contact-identity"], produces = [MediaType.APPLICATION_JSON_VALUE])
  @ResponseStatus(HttpStatus.OK)
  @ResponseBody
  @Operation(
    summary = "Creates a new contact identity",
    description = """
      Requires role: ROLE_CONTACTS_MIGRATION.
      Used to create a contact identity and associate it with a contact.
      """,
  )
  @PreAuthorize("hasAnyRole('CONTACTS_MIGRATION')")
  fun createContactIdentity(
    @Valid @RequestBody createContactIdentityRequest: CreateContactIdentityRequest,
  ) = syncService.createContactIdentity(createContactIdentityRequest)

  @PostMapping(path = ["/contact-identity/{contactIdentityId}"], produces = [MediaType.APPLICATION_JSON_VALUE])
  @ResponseStatus(HttpStatus.OK)
  @ResponseBody
  @Operation(
    summary = "Updates a contact identity with new or extra detail",
    description = """
      Requires role: ROLE_CONTACTS_MIGRATION.
      Used to update a contact identity.
      """,
  )
  @PreAuthorize("hasAnyRole('CONTACTS_MIGRATION')")
  fun updateContactIdentity(
    @Parameter(description = "The internal ID for the contact identity.", required = true)
    @PathVariable contactIdentityId: Long,
    @Valid @RequestBody updateContactIdentityRequest: UpdateContactIdentityRequest,
  ) = syncService.updateContactIdentity(contactIdentityId, updateContactIdentityRequest)
}