package uk.gov.justice.digital.hmpps.hmppscontactsapi.repository

import uk.gov.justice.digital.hmpps.hmppscontactsapi.entity.CountryReferenceEntity
import java.util.Optional

@org.springframework.stereotype.Repository
interface CountryReferenceRepository : org.springframework.data.repository.Repository<CountryReferenceEntity, Long> {

  fun findByCountryId(countryId: Long): Optional<CountryReferenceEntity>

  fun findByNomisCode(nomisCode: String): Optional<CountryReferenceEntity>

  fun findByIsoAlpha2(isoAlpha2: String): Optional<CountryReferenceEntity>

  fun findByIsoAlpha3(isoAlpha3: String): Optional<CountryReferenceEntity>

  fun findAll(): List<CountryReferenceEntity>
}
