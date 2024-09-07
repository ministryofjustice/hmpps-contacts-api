package uk.gov.justice.digital.hmpps.hmppscontactsapi.mapping

import uk.gov.justice.digital.hmpps.hmppscontactsapi.entity.LanguageReferenceEntity
import uk.gov.justice.digital.hmpps.hmppscontactsapi.model.response.LanguageReference

fun LanguageReferenceEntity.toModel(): LanguageReference {
  return LanguageReference(
    languageId = this.languageId,
    nomisCode = this.nomisCode,
    nomisDescription = this.nomisDescription,
    isoAlpha2 = this.isoAlpha2,
    isoAlpha3 = this.isoAlpha3,
    displaySequence = this.displaySequence,
  )
}

fun List<LanguageReferenceEntity>.toModel() = map { it.toModel() }
