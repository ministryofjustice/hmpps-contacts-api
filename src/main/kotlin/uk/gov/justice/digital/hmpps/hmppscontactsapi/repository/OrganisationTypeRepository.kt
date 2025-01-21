package uk.gov.justice.digital.hmpps.hmppscontactsapi.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import uk.gov.justice.digital.hmpps.hmppscontactsapi.entity.OrganisationTypeEntity
import uk.gov.justice.digital.hmpps.hmppscontactsapi.entity.OrganisationTypeId

@Repository
interface OrganisationTypeRepository : JpaRepository<OrganisationTypeEntity, OrganisationTypeId> {
  @Modifying
  @Query("delete from OrganisationTypeEntity o where o.id.organisationId = :organisationId")
  fun deleteAllByOrganisationId(organisationId: Long): Int

  fun getByIdOrganisationId(organisationId: Long): List<OrganisationTypeEntity>
}
