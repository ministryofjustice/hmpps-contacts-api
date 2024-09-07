package uk.gov.justice.digital.hmpps.hmppscontactsapi.repository

import uk.gov.justice.digital.hmpps.hmppscontactsapi.entity.CityReferenceEntity
import java.util.Optional

@org.springframework.stereotype.Repository
interface CityReferenceRepository : org.springframework.data.repository.Repository<CityReferenceEntity, Long> {

  fun findByCityId(cityId: Long): Optional<CityReferenceEntity>

  fun findByNomisCode(nomisCode: String): Optional<CityReferenceEntity>

  fun findAll(): List<CityReferenceEntity>
}
