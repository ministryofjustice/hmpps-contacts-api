package uk.gov.justice.digital.hmpps.hmppscontactsapi.mapping

import uk.gov.justice.digital.hmpps.hmppscontactsapi.entity.CityReferenceEntity
import uk.gov.justice.digital.hmpps.hmppscontactsapi.model.response.CityReference

fun CityReferenceEntity.toModel(): CityReference {
  return CityReference(
    cityId = this.cityId,
    nomisCode = this.nomisCode,
    nomisDescription = this.nomisDescription,
    displaySequence = this.displaySequence,
  )
}

fun List<CityReferenceEntity>.toModel() = map { it.toModel() }
