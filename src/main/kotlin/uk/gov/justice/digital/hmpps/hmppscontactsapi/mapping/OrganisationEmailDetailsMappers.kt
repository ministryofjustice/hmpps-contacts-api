package uk.gov.justice.digital.hmpps.hmppscontactsapi.mapping

import uk.gov.justice.digital.hmpps.hmppscontactsapi.entity.OrganisationEmailEntity
import uk.gov.justice.digital.hmpps.hmppscontactsapi.model.response.OrganisationEmailDetails

fun OrganisationEmailEntity.toModel(): OrganisationEmailDetails = OrganisationEmailDetails(
  organisationEmailId = this.organisationEmailId,
  organisationId = this.organisationId,
  emailAddress = this.emailAddress,
  createdBy = this.createdBy,
  createdTime = this.createdTime,
  updatedBy = this.updatedBy,
  updatedTime = this.updatedTime,
)

fun List<OrganisationEmailEntity>.toModel() = map { it.toModel() }