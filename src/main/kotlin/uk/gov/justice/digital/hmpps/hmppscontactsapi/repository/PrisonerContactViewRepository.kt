package uk.gov.justice.digital.hmpps.hmppscontactsapi.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import uk.gov.justice.digital.hmpps.hmppscontactsapi.entity.PrisonerContactViewEntity

@Repository
interface PrisonerContactViewRepository : JpaRepository<PrisonerContactViewEntity, Long> {

    // Custom query method to find by contact_id
    fun findByContactId(contactId: Long): List<PrisonerContactViewEntity>
}

