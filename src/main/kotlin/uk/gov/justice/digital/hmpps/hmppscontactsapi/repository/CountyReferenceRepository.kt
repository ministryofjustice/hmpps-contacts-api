package uk.gov.justice.digital.hmpps.hmppscontactsapi.repository

import uk.gov.justice.digital.hmpps.hmppscontactsapi.entity.CountyReferenceEntity
import java.util.*

@org.springframework.stereotype.Repository
interface CountyReferenceRepository : org.springframework.data.repository.Repository<CountyReferenceEntity, Long> {

  fun findByCountyId(countyId: Long): Optional<CountyReferenceEntity>

  fun findByNomisCode(nomisCode: String): Optional<CountyReferenceEntity>

  fun findAll(): List<CountyReferenceEntity>
}
