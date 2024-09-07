package uk.gov.justice.digital.hmpps.hmppscontactsapi.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import uk.gov.justice.digital.hmpps.hmppscontactsapi.mapping.toModel
import uk.gov.justice.digital.hmpps.hmppscontactsapi.model.response.LanguageReference
import uk.gov.justice.digital.hmpps.hmppscontactsapi.repository.LanguageReferenceRepository

@Service
class LanguageReferenceService(private val languageReferenceRepository: LanguageReferenceRepository) {

  @Transactional(readOnly = true)
  fun getLanguageById(id: Long): LanguageReference? =
    languageReferenceRepository.findByLanguageId(id)
      .map { it.toModel() }
      .orElse(null)

  @Transactional(readOnly = true)
  fun getLanguageByNomisCode(code: String): LanguageReference? =
    languageReferenceRepository.findByNomisCode(code)
      .map { it.toModel() }
      .orElse(null)

  @Transactional(readOnly = true)
  fun getLanguageByIsoAlpha2(code: String): LanguageReference? =
    languageReferenceRepository.findByIsoAlpha2(code)
      .map { it.toModel() }
      .orElse(null)

  @Transactional(readOnly = true)
  fun getLanguageByIsoAlpha3(code: String): LanguageReference? =
    languageReferenceRepository.findByIsoAlpha3(code)
      .map { it.toModel() }
      .orElse(null)

  @Transactional(readOnly = true)
  fun getAllCountries(): List<LanguageReference> {
    return languageReferenceRepository.findAll().toModel()
  }
}
