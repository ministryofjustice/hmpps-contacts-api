package uk.gov.justice.digital.hmpps.hmppscontactsapi.service

import jakarta.persistence.EntityNotFoundException
import jakarta.transaction.Transactional
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import uk.gov.justice.digital.hmpps.hmppscontactsapi.entity.ContactEntity
import uk.gov.justice.digital.hmpps.hmppscontactsapi.mapping.toEntity
import uk.gov.justice.digital.hmpps.hmppscontactsapi.model.request.AddContactRelationshipRequest
import uk.gov.justice.digital.hmpps.hmppscontactsapi.model.request.ContactRelationship
import uk.gov.justice.digital.hmpps.hmppscontactsapi.repository.ContactRepository
import uk.gov.justice.digital.hmpps.hmppscontactsapi.repository.PrisonerContactRepository
import java.util.*

@Service
class RelationshipService(
  private val contactRepository: ContactRepository,
  private val prisonerContactRepository: PrisonerContactRepository,
  private val prisonerService: PrisonerService,
) {
  companion object {
    private val logger = LoggerFactory.getLogger(this::class.java)
  }

  @Transactional
  fun create(contactId: Long, request: AddContactRelationshipRequest) {
    validateRelationship(request.relationship)
    getContact(contactId).orElseThrow { EntityNotFoundException("Contact ($contactId) could not be found") }
    prisonerContactRepository.saveAndFlush(request.relationship.toEntity(contactId, request.createdBy))
  }

  private fun validateRelationship(relationship: ContactRelationship) {
    prisonerService.getPrisoner(relationship.prisonerNumber)
      ?: throw EntityNotFoundException("Prisoner (${relationship.prisonerNumber}) could not be found")
  }

  private fun getContact(id: Long): Optional<ContactEntity> {
    return contactRepository.findById(id)
  }
}
