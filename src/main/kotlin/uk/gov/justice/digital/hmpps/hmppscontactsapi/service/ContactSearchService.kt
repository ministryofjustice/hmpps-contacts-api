package uk.gov.justice.digital.hmpps.hmppscontactsapi.service

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import uk.gov.justice.digital.hmpps.hmppscontactsapi.entity.ContactEntity
import uk.gov.justice.digital.hmpps.hmppscontactsapi.repository.ContactPaginationRepository

@Service
class ContactSearchService(
    private val contactPaginationRepository: ContactPaginationRepository
) {
    fun getContactsByForenameFuzzy(forename: String, threshold: Double, pageable: Pageable): Page<ContactEntity> {
        return contactPaginationRepository.findByForenameFuzzy(forename, threshold, pageable)
    }
}
