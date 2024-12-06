package uk.gov.justice.digital.hmpps.hmppscontactsapi.resource

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import uk.gov.justice.digital.hmpps.hmppscontactsapi.facade.ContactAddressFacade
import uk.gov.justice.digital.hmpps.hmppscontactsapi.model.request.CreateContactAddressRequest
import uk.gov.justice.digital.hmpps.hmppscontactsapi.model.request.UpdateContactAddressRequest
import uk.gov.justice.digital.hmpps.hmppscontactsapi.model.response.ContactAddressResponse
import uk.gov.justice.digital.hmpps.hmppscontactsapi.swagger.AuthApiResponses
import uk.gov.justice.hmpps.kotlin.common.ErrorResponse

@Tag(name = "Contact Address")
@RestController
@RequestMapping(value = ["contact/{contactId}/address"], produces = [MediaType.APPLICATION_JSON_VALUE])
@AuthApiResponses
class ContactAddressController(private val contactAddressFacade: ContactAddressFacade) {

  @PostMapping(consumes = [MediaType.APPLICATION_JSON_VALUE])
  @Operation(summary = "Create new contact address", description = "Creates a new address for the specified contact")
  @ApiResponses(
    value = [
      ApiResponse(
        responseCode = "201",
        description = "Created the contact address successfully",
        content = [
          Content(
            mediaType = "application/json",
            schema = Schema(implementation = ContactAddressResponse::class),
          ),
        ],
      ),
      ApiResponse(
        responseCode = "400",
        description = "The request has invalid or missing fields",
        content = [Content(schema = Schema(implementation = ErrorResponse::class))],
      ),
      ApiResponse(
        responseCode = "404",
        description = "Could not find the the email address",
        content = [Content(schema = Schema(implementation = ErrorResponse::class))],
      ),
    ],
  )
  @PreAuthorize("hasAnyRole('ROLE_CONTACTS_ADMIN')")
  fun createContactAddress(
    @PathVariable("contactId")
    @Parameter(name = "contactId", description = "The id of the contact", example = "123456")
    contactId: Long,
    @Valid @RequestBody
    request: CreateContactAddressRequest,
  ): ResponseEntity<Any> {
    val created = contactAddressFacade.create(contactId, request)
    return ResponseEntity
      .status(HttpStatus.CREATED)
      .body(created)
  }

  @PutMapping("/{contactAddressId}", consumes = [MediaType.APPLICATION_JSON_VALUE])
  @Operation(summary = "Update a contact address", description = "Updates an existing contact address by its ID")
  @ApiResponses(
    value = [
      ApiResponse(
        responseCode = "200",
        description = "Updated the contact address successfully",
        content = [
          Content(
            mediaType = "application/json",
            schema = Schema(implementation = ContactAddressResponse::class),
          ),
        ],
      ),
      ApiResponse(
        responseCode = "400",
        description = "The request has invalid or missing fields",
        content = [Content(schema = Schema(implementation = ErrorResponse::class))],
      ),
      ApiResponse(
        responseCode = "404",
        description = "Could not find the the contact or address by ID",
        content = [Content(schema = Schema(implementation = ErrorResponse::class))],
      ),
    ],
  )
  @PreAuthorize("hasAnyRole('ROLE_CONTACTS_ADMIN')")
  fun updateContactAddress(
    @PathVariable("contactId")
    @Parameter(name = "contactId", description = "The contact ID", example = "123456")
    contactId: Long,
    @PathVariable("contactAddressId")
    @Parameter(name = "contactAddressId", description = "The contact address ID", example = "1233")
    contactAddressId: Long,
    @Valid @RequestBody
    request: UpdateContactAddressRequest,
  ) = contactAddressFacade.update(contactId, contactAddressId, request)

  @GetMapping("/{contactAddressId}")
  @Operation(summary = "Get a contact address", description = "Get a contact address by its ID")
  @ApiResponses(
    value = [
      ApiResponse(
        responseCode = "200",
        description = "Found the address successfully",
        content = [
          Content(
            mediaType = "application/json",
            schema = Schema(implementation = ContactAddressResponse::class),
          ),
        ],
      ),
      ApiResponse(
        responseCode = "404",
        description = "Could not find the the contact or address by their IDs",
        content = [Content(schema = Schema(implementation = ErrorResponse::class))],
      ),
    ],
  )
  @PreAuthorize("hasAnyRole('ROLE_CONTACTS_ADMIN')")
  fun getContactAddress(
    @PathVariable("contactId")
    @Parameter(name = "contactId", description = "The contact ID", example = "123456")
    contactId: Long,
    @PathVariable("contactAddressId")
    @Parameter(name = "contactAddressId", description = "The contact address ID", example = "122")
    contactAddressId: Long,
  ) = contactAddressFacade.get(contactId, contactAddressId)

  @DeleteMapping("/{contactAddressId}")
  @Operation(summary = "Delete contact address", description = "Deletes a contact address by its ID")
  @ApiResponses(
    value = [
      ApiResponse(
        responseCode = "204",
        description = "Deleted the contact address successfully",
        content = [
          Content(
            mediaType = "application/json",
            schema = Schema(implementation = ContactAddressResponse::class),
          ),
        ],
      ),
      ApiResponse(
        responseCode = "404",
        description = "Could not find the the contact or address by the provided IDs",
        content = [Content(schema = Schema(implementation = ErrorResponse::class))],
      ),
    ],
  )
  @PreAuthorize("hasAnyRole('ROLE_CONTACTS_ADMIN')")
  fun deleteContactAddress(
    @PathVariable("contactId")
    @Parameter(name = "contactId", description = "The contact ID", example = "123")
    contactId: Long,
    @PathVariable("contactAddressId")
    @Parameter(name = "contactAddressId", description = "The contact address ID", example = "456")
    contactAddressId: Long,
  ): ResponseEntity<Any> {
    contactAddressFacade.delete(contactId, contactAddressId)
    return ResponseEntity.noContent().build()
  }
}