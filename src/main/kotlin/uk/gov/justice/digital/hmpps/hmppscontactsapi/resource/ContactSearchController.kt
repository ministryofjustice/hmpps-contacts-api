package uk.gov.justice.digital.hmpps.hmppscontactsapi.resource

import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import uk.gov.justice.digital.hmpps.hmppscontactsapi.entity.ContactEntity
import uk.gov.justice.digital.hmpps.hmppscontactsapi.service.ContactSearchService

@RestController
class ContactSearchController(
    private val contactService: ContactSearchService
) {
    @GetMapping("/contacts/search")
    fun searchContacts(
        @RequestParam forename: String,
        @RequestParam(required = false, defaultValue = "0.3") threshold: Double,
        @RequestParam(required = false, defaultValue = "0") page: Int,
        @RequestParam(required = false, defaultValue = "10") size: Int
    ): Page<ContactEntity> {
        val pageable = PageRequest.of(page, size)
        return contactService.getContactsByForenameFuzzy(forename, threshold, pageable)
    }
}
