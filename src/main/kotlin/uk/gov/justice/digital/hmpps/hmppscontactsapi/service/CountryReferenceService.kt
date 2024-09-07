package uk.gov.justice.digital.hmpps.hmppscontactsapi.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import uk.gov.justice.digital.hmpps.hmppscontactsapi.mapping.toModel
import uk.gov.justice.digital.hmpps.hmppscontactsapi.model.response.CountryReference
import uk.gov.justice.digital.hmpps.hmppscontactsapi.repository.CountryReferenceRepository

@Service
class CountryReferenceService(private val countryReferenceRepository: CountryReferenceRepository) {

  @Transactional(readOnly = true)
  fun getCountryById(id: Long): CountryReference? =
    countryReferenceRepository.findByCountryId(id)
      .map { it.toModel() }
      .orElse(null)

  @Transactional(readOnly = true)
  fun getCountryByNomisCode(code: String): CountryReference? =
    countryReferenceRepository.findByNomisCode(code)
      .map { it.toModel() }
      .orElse(null)

  @Transactional(readOnly = true)
  fun getCountryByIsoAlpha2(code: String): CountryReference? =
    countryReferenceRepository.findByIsoAlpha2(code)
      .map { it.toModel() }
      .orElse(null)

  @Transactional(readOnly = true)
  fun getCountryByIsoAlpha3(code: String): CountryReference? =
    countryReferenceRepository.findByIsoAlpha3(code)
      .map { it.toModel() }
      .orElse(null)

  @Transactional(readOnly = true)
  fun getAllCountries(): List<CountryReference> {
    return countryReferenceRepository.findAll().toModel()
  }
}
