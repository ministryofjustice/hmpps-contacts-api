package uk.gov.justice.digital.hmpps.hmppscontactsapi.service

import jakarta.persistence.EntityNotFoundException
import jakarta.transaction.Transactional
import org.slf4j.LoggerFactory
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import uk.gov.justice.digital.hmpps.hmppscontactsapi.mapping.ContactServiceMappers
import uk.gov.justice.digital.hmpps.hmppscontactsapi.model.request.ContactSearchRequest
import uk.gov.justice.digital.hmpps.hmppscontactsapi.model.request.CreateContactRequest
import uk.gov.justice.digital.hmpps.hmppscontactsapi.model.response.Contact
import uk.gov.justice.digital.hmpps.hmppscontactsapi.repository.ContactRepository
import uk.gov.justice.digital.hmpps.hmppscontactsapi.repository.ContactSearchRepository
import uk.gov.justice.digital.hmpps.hmppscontactsapi.repository.PrisonerContactRepository
import java.time.Clock
import kotlin.jvm.optionals.getOrNull

@Service
class ContactService(
  private val contactRepository: ContactRepository,
  private val prisonerContactRepository: PrisonerContactRepository,
  private val prisonerService: PrisonerService,
  clock: Clock,
  val contactSearchRepository: ContactSearchRepository,
) : ContactServiceMappers(clock) {
  companion object {
    private val logger = LoggerFactory.getLogger(this::class.java)
  }

  @Transactional
  fun createContact(request: CreateContactRequest): Contact {
    validate(request)
    val newContact = mapContact(request)
    val createdContact = mapEntityToContact(contactRepository.saveAndFlush(newContact))
    val newRelationship = request.relationship
      ?.let { mapRelationShip(createdContact, request.relationship, request) }
      ?.let { prisonerContactRepository.saveAndFlush(it) }

    logger.info("Created new contact {}", createdContact)
    newRelationship?.let { logger.info("Created new relationship {}", newRelationship) }
    return createdContact
  }

  fun getContact(id: Long): Contact? {
    return contactRepository.findById(id).getOrNull()
      ?.let { entity -> mapEntityToContact(entity) }
  }

  fun searchContacts(pageable: Pageable, request: ContactSearchRequest): Page<Contact> =
    contactSearchRepository.searchContacts(request, pageable)
      .map { entity ->
        mapEntityToContact(entity)
      }

  private fun validate(request: CreateContactRequest) {
    if (request.relationship != null) {
      prisonerService.getPrisoner(request.relationship.prisonerNumber)
        ?: throw EntityNotFoundException("Prisoner number ${request.relationship.prisonerNumber} - not found")
    }
  }
}
