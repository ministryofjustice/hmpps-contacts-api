package uk.gov.justice.digital.hmpps.hmppscontactsapi.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import uk.gov.justice.digital.hmpps.hmppscontactsapi.mapping.toModel
import uk.gov.justice.digital.hmpps.hmppscontactsapi.model.response.CountyReference
import uk.gov.justice.digital.hmpps.hmppscontactsapi.repository.CountyReferenceRepository

@Service
class CountyReferenceService(private val countyReferenceRepository: CountyReferenceRepository) {

  @Transactional(readOnly = true)
  fun getCountyById(id: Long): CountyReference? =
    countyReferenceRepository.findByCountyId(id)
      .map { it.toModel() }
      .orElse(null)

  @Transactional(readOnly = true)
  fun getCountyByNomisCode(code: String): CountyReference? =
    countyReferenceRepository.findByNomisCode(code)
      .map { it.toModel() }
      .orElse(null)

  @Transactional(readOnly = true)
  fun getAllCountries(): List<CountyReference> {
    return countyReferenceRepository.findAll().toModel()
  }
}
