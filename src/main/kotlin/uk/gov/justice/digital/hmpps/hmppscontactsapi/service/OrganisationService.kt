package uk.gov.justice.digital.hmpps.hmppscontactsapi.service

import jakarta.persistence.EntityNotFoundException
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service
import uk.gov.justice.digital.hmpps.hmppscontactsapi.entity.OrganisationEntity
import uk.gov.justice.digital.hmpps.hmppscontactsapi.mapping.toModel
import uk.gov.justice.digital.hmpps.hmppscontactsapi.model.request.CreateOrganisationRequest
import uk.gov.justice.digital.hmpps.hmppscontactsapi.model.response.Organisation
import uk.gov.justice.digital.hmpps.hmppscontactsapi.repository.OrganisationRepository

@Service
class OrganisationService(private val organisationRepository: OrganisationRepository) {

  fun getOrganisationById(id: Long): Organisation = organisationRepository.findById(id)
    .orElseThrow { EntityNotFoundException("Organisation with id $id not found") }.toModel()

  @Transactional
  fun create(request: CreateOrganisationRequest): Organisation {
    val created = organisationRepository.saveAndFlush(
      OrganisationEntity(
        organisationName = request.organisationName,
        programmeNumber = request.programmeNumber,
        vatNumber = request.vatNumber,
        caseloadId = request.caseloadId,
        comments = request.comments,
        active = request.active,
        deactivatedDate = request.deactivatedDate,
        createdBy = request.createdBy,
        createdTime = request.createdTime,
        updatedBy = request.updatedBy,
        updatedTime = request.updatedTime,
      ),
    )
    return created.toModel()
  }
}
