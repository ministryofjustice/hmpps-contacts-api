package uk.gov.justice.digital.hmpps.hmppscontactsapi.mapping

import uk.gov.justice.digital.hmpps.hmppscontactsapi.entity.CountryReferenceEntity
import uk.gov.justice.digital.hmpps.hmppscontactsapi.model.response.CountryReference

fun CountryReferenceEntity.toModel(): CountryReference {
  return CountryReference(
    countryId = this.countryId,
    nomisCode = this.nomisCode,
    nomisDescription = this.nomisDescription,
    isoNumeric = this.isoNumeric,
    isoAlpha2 = this.isoAlpha2,
    isoAlpha3 = this.isoAlpha3,
    isoCountryDesc = this.isoCountryDesc,
    displaySequence = this.displaySequence,
  )
}

fun List<CountryReferenceEntity>.toModel() = map { it.toModel() }
