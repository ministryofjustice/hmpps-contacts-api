package uk.gov.justice.digital.hmpps.hmppscontactsapi.repository

import org.springframework.data.domain.Sort
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import uk.gov.justice.digital.hmpps.hmppscontactsapi.entity.ReferenceCodeEntity

@Repository
interface ReferenceCodeRepository : JpaRepository<ReferenceCodeEntity, Long> {
  fun findAllByGroupCodeEquals(groupCode: String, sort: Sort): List<ReferenceCodeEntity>
  fun findAllByGroupCodeAndIsActiveEquals(groupCode: String, isActive: Boolean, sort: Sort): List<ReferenceCodeEntity>
  fun findByGroupCodeAndCode(groupCode: String, code: String): ReferenceCodeEntity?
}
