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
import uk.gov.justice.digital.hmpps.hmppscontactsapi.model.request.sync.CreateContactRequest
import uk.gov.justice.digital.hmpps.hmppscontactsapi.model.request.sync.UpdateContactRequest
import uk.gov.justice.digital.hmpps.hmppscontactsapi.service.SyncContactService

@Tag(name = "Sync endpoints")
@RestController
@RequestMapping(value = ["/sync"], produces = [MediaType.APPLICATION_JSON_VALUE])
class ContactSyncController(
  val syncService: SyncContactService,
) {
  @GetMapping(path = ["/contact-restriction/{contactId}"], produces = [MediaType.APPLICATION_JSON_VALUE])
  @ResponseStatus(HttpStatus.OK)
  @Operation(
    summary = "Returns the data for a contact by contactId",
    description = """
      Requires role: ROLE_CONTACTS_MIGRATION.
      Used to get the details for one contact.
      """,
  )
  @PreAuthorize("hasAnyRole('CONTACTS_MIGRATION')")
  fun getContactById(
    @Parameter(description = "The internal ID for a contact.", required = true)
    @PathVariable contactId: Long,
  ) = syncService.getContactById(contactId)

  @DeleteMapping(path = ["/contact-restriction/{contactId}"], produces = [MediaType.APPLICATION_JSON_VALUE])
  @ResponseStatus(HttpStatus.OK)
  @Operation(
    summary = "Deletes one contact by internal ID",
    description = """
      Requires role: ROLE_CONTACTS_MIGRATION.
      Used to delete a contact.
      """,
  )
  @PreAuthorize("hasAnyRole('CONTACTS_MIGRATION')")
  fun deleteContactById(
    @Parameter(description = "The internal ID for the contact.", required = true)
    @PathVariable contactId: Long,
  ) = syncService.deleteContact(contactId)

  @PutMapping(path = ["/contact-restriction"], produces = [MediaType.APPLICATION_JSON_VALUE])
  @ResponseStatus(HttpStatus.OK)
  @ResponseBody
  @Operation(
    summary = "Creates a new contact",
    description = """
      Requires role: ROLE_CONTACTS_MIGRATION.
      Used to create a contact and associate it with a contact.
      """,
  )
  @PreAuthorize("hasAnyRole('CONTACTS_MIGRATION')")
  fun createContact(
    @Valid @RequestBody createContactRequest: CreateContactRequest,
  ) = syncService.createContact(createContactRequest)

  @PostMapping(path = ["/contact-restriction/{contactId}"], produces = [MediaType.APPLICATION_JSON_VALUE])
  @ResponseStatus(HttpStatus.OK)
  @ResponseBody
  @Operation(
    summary = "Updates a contact with new or extra detail",
    description = """
      Requires role: ROLE_CONTACTS_MIGRATION.
      Used to update a contact.
      """,
  )
  @PreAuthorize("hasAnyRole('CONTACTS_MIGRATION')")
  fun updateContact(
    @Parameter(description = "The internal ID for the contact.", required = true)
    @PathVariable contactId: Long,
    @Valid @RequestBody updateContactRequest: UpdateContactRequest,
  ) = syncService.updateContact(contactId, updateContactRequest)
}
