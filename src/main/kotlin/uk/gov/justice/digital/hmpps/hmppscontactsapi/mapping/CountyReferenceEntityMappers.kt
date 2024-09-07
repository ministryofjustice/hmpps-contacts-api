package uk.gov.justice.digital.hmpps.hmppscontactsapi.mapping

import uk.gov.justice.digital.hmpps.hmppscontactsapi.entity.CountyReferenceEntity
import uk.gov.justice.digital.hmpps.hmppscontactsapi.model.response.CountyReference

fun CountyReferenceEntity.toModel(): CountyReference {
  return CountyReference(
    countyId = this.countyId,
    nomisCode = this.nomisCode,
    nomisDescription = this.nomisDescription,
    displaySequence = this.displaySequence,
  )
}

fun List<CountyReferenceEntity>.toModel() = map { it.toModel() }
