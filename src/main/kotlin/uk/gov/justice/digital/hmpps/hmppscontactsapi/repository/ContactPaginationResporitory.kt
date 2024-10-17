package uk.gov.justice.digital.hmpps.hmppscontactsapi.repository

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.PagingAndSortingRepository
import uk.gov.justice.digital.hmpps.hmppscontactsapi.entity.ContactEntity

interface ContactPaginationRepository : PagingAndSortingRepository<ContactEntity, Long> {

    @Query(
        """
        SELECT c FROM ContactEntity c 
        WHERE soundex(c.firstName) = soundex('jaik') 
        OR similarity(c.firstName, :forename) > :threshold
        ORDER BY similarity(c.firstName, :forename) DESC
    """
    )
    fun findByForenameFuzzy(
        forename: String,
        threshold: Double,
        pageable: Pageable
    ): Page<ContactEntity>
}