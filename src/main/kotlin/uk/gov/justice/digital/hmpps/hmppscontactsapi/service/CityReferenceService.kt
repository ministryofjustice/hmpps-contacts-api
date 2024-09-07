package uk.gov.justice.digital.hmpps.hmppscontactsapi.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import uk.gov.justice.digital.hmpps.hmppscontactsapi.mapping.toModel
import uk.gov.justice.digital.hmpps.hmppscontactsapi.model.response.CityReference
import uk.gov.justice.digital.hmpps.hmppscontactsapi.repository.CityReferenceRepository

@Service
class CityReferenceService(private val cityReferenceRepository: CityReferenceRepository) {

  @Transactional(readOnly = true)
  fun getCityById(id: Long): CityReference? =
    cityReferenceRepository.findByCityId(id)
      .map { it.toModel() }
      .orElse(null)

  @Transactional(readOnly = true)
  fun getCityByNomisCode(code: String): CityReference? =
    cityReferenceRepository.findByNomisCode(code)
      .map { it.toModel() }
      .orElse(null)

  @Transactional(readOnly = true)
  fun getAllCountries(): List<CityReference> {
    return cityReferenceRepository.findAll().toModel()
  }
}
