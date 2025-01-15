package uk.gov.justice.digital.hmpps.hmppscontactsapi.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import uk.gov.justice.digital.hmpps.hmppscontactsapi.entity.OrganisationWebAddressEntity

@Repository
interface OrganisationWebAddressRepository : JpaRepository<OrganisationWebAddressEntity, Long> {
  @Modifying
  @Query("delete from OrganisationWebAddressEntity c where c.organisationId = :organisationId")
  fun deleteAllByOrganisationId(organisationId: Long): Int
}
