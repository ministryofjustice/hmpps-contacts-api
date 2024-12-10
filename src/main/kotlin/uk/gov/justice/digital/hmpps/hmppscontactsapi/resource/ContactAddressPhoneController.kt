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
import uk.gov.justice.digital.hmpps.hmppscontactsapi.facade.ContactAddressPhoneFacade
import uk.gov.justice.digital.hmpps.hmppscontactsapi.model.request.CreateContactAddressPhoneRequest
import uk.gov.justice.digital.hmpps.hmppscontactsapi.model.request.UpdateContactAddressPhoneRequest
import uk.gov.justice.digital.hmpps.hmppscontactsapi.model.response.ContactAddressPhoneResponse
import uk.gov.justice.digital.hmpps.hmppscontactsapi.swagger.AuthApiResponses
import uk.gov.justice.hmpps.kotlin.common.ErrorResponse

@Tag(name = "Contact Address Phone - address-specific phone numbers")
@RestController
@RequestMapping(
  value = ["/contact/{contactId}/address/{contactAddressId}/phone"],
  produces = [MediaType.APPLICATION_JSON_VALUE],
)
@AuthApiResponses
class ContactAddressPhoneController(
  private val contactAddressPhoneFacade: ContactAddressPhoneFacade,
) {

  @PostMapping(consumes = [MediaType.APPLICATION_JSON_VALUE])
  @Operation(summary = "Create new address-specific phone number", description = "Creates a new address-specific phone number")
  @ApiResponses(
    value = [
      ApiResponse(
        responseCode = "201",
        description = "Created the address-specific phone number successfully",
        content = [
          Content(
            mediaType = "application/json",
            schema = Schema(implementation = ContactAddressPhoneResponse::class),
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
        description = "Could not find the the contact or address provided",
        content = [Content(schema = Schema(implementation = ErrorResponse::class))],
      ),
    ],
  )
  @PreAuthorize("hasAnyRole('ROLE_CONTACTS_ADMIN')")
  fun createContactAddressPhone(
    @PathVariable("contactId")
    @Parameter(name = "contactId", description = "The id of the contact", example = "111")
    contactId: Long,
    @PathVariable("contactAddressId")
    @Parameter(name = "contactAddressId", description = "The id of the address", example = "222")
    contactAddressId: Long,
    @Valid @RequestBody
    request: CreateContactAddressPhoneRequest,
  ): ResponseEntity<Any> {
    val created = contactAddressPhoneFacade.create(contactId, contactAddressId, request)
    return ResponseEntity
      .status(HttpStatus.CREATED)
      .body(created)
  }

  @PutMapping("/{contactAddressPhoneId}", consumes = [MediaType.APPLICATION_JSON_VALUE])
  @Operation(summary = "Update an address-specific phone number", description = "Updates an address-specific phone number by its ID")
  @ApiResponses(
    value = [
      ApiResponse(
        responseCode = "200",
        description = "Updated the address-specific phone number successfully",
        content = [
          Content(
            mediaType = "application/json",
            schema = Schema(implementation = ContactAddressPhoneResponse::class),
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
        description = "Could not find the the contact, address or phone number by ID",
        content = [Content(schema = Schema(implementation = ErrorResponse::class))],
      ),
    ],
  )
  @PreAuthorize("hasAnyRole('ROLE_CONTACTS_ADMIN')")
  fun updateContactAddressPhone(
    @PathVariable("contactId")
    @Parameter(name = "contactId", description = "The contact ID", example = "123")
    contactId: Long,
    @PathVariable("contactAddressId")
    @Parameter(name = "contactAddressId", description = "The contact address ID", example = "878")
    contactAddressId: Long,
    @PathVariable("contactAddressPhoneId")
    @Parameter(name = "contactAddressPhoneId", description = "The address-specific phone ID", example = "979")
    contactAddressPhoneId: Long,
    @Valid @RequestBody
    request: UpdateContactAddressPhoneRequest,
  ) = contactAddressPhoneFacade.update(contactId, contactAddressPhoneId, request)

  @GetMapping("/{contactAddressPhoneId}")
  @Operation(summary = "Get an address-specific phone number", description = "Get an address-specific phone number by its ID")
  @ApiResponses(
    value = [
      ApiResponse(
        responseCode = "200",
        description = "Found the address-specific phone number successfully",
        content = [
          Content(
            mediaType = "application/json",
            schema = Schema(implementation = ContactAddressPhoneResponse::class),
          ),
        ],
      ),
      ApiResponse(
        responseCode = "404",
        description = "Could not find the the contact, address or phone number by ID",
        content = [Content(schema = Schema(implementation = ErrorResponse::class))],
      ),
    ],
  )
  @PreAuthorize("hasAnyRole('ROLE_CONTACTS_ADMIN')")
  fun getContactAddressPhone(
    @PathVariable("contactId")
    @Parameter(name = "contactId", description = "The contact ID", example = "123456")
    contactId: Long,
    @PathVariable("contactAddressId")
    @Parameter(name = "contactAddressId", description = "The contact address ID", example = "122")
    contactAddressId: Long,
    @PathVariable("contactAddressPhoneId")
    @Parameter(name = "contactAddressPhoneId", description = "The address-specific phone ID", example = "979")
    contactAddressPhoneId: Long,
  ) = contactAddressPhoneFacade.get(contactId, contactAddressPhoneId)

  @DeleteMapping("/{contactAddressPhoneId}")
  @Operation(summary = "Delete an address-specific phone number", description = "Deletes an address-specific phone number by its ID")
  @ApiResponses(
    value = [
      ApiResponse(
        responseCode = "204",
        description = "Deleted the address-specific phone number",
        content = [
          Content(
            mediaType = "application/json",
            schema = Schema(implementation = ContactAddressPhoneResponse::class),
          ),
        ],
      ),
      ApiResponse(
        responseCode = "404",
        description = "Could not find the the contact, address or address specific phone number by ID",
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
    @PathVariable("contactAddressPhoneId")
    @Parameter(name = "contactAddressPhoneId", description = "The address-specific phone ID", example = "979")
    contactAddressPhoneId: Long,
  ): ResponseEntity<Any> {
    contactAddressPhoneFacade.delete(contactId, contactAddressPhoneId)
    return ResponseEntity.noContent().build()
  }
}