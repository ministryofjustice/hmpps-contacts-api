package uk.gov.justice.digital.hmpps.hmppscontactsapi.repository

import uk.gov.justice.digital.hmpps.hmppscontactsapi.entity.LanguageReferenceEntity
import java.util.*

@org.springframework.stereotype.Repository
interface LanguageReferenceRepository : org.springframework.data.repository.Repository<LanguageReferenceEntity, Long> {

  fun findByLanguageId(languageId: Long): Optional<LanguageReferenceEntity>

  fun findByNomisCode(nomisCode: String): Optional<LanguageReferenceEntity>

  fun findByIsoAlpha2(isoAlpha2: String): Optional<LanguageReferenceEntity>

  fun findByIsoAlpha3(isoAlpha3: String): Optional<LanguageReferenceEntity>

  fun findAll(): List<LanguageReferenceEntity>
}
