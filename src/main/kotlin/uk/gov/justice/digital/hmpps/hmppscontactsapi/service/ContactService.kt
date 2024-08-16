package uk.gov.justice.digital.hmpps.hmppscontactsapi.service

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import uk.gov.justice.digital.hmpps.hmppscontactsapi.entity.Contact
import uk.gov.justice.digital.hmpps.hmppscontactsapi.repository.ContactRepository
import java.time.Instant

@Service
class ContactService @Autowired constructor(
  private val contactRepository: ContactRepository,
) {
  private val logger = LoggerFactory.getLogger(ContactService::class.java)

  fun createContact(contact: Contact): Contact {
    logger.info("Creating new contact: {}", contact)
    contact.createdTime = Instant.now()
    val savedContact = contactRepository.save(contact)
    logger.info("Contact created with ID: {}", savedContact.id)
    return savedContact
  }

  fun getContact(id: Long): Contact? {
    logger.info("Fetching contact with ID: {}", id)
    val contact = contactRepository.findById(id).orElse(null)
    if (contact != null) {
      logger.info("Contact found: {}", contact)
    } else {
      logger.warn("Contact with ID: {} not found", id)
    }
    return contact
  }

  fun updateContact(id: Long, contactDetails: Contact): Contact? {
    logger.info("Updating contact with ID: {}", id)
    return contactRepository.findById(id).map { existingContact ->
      existingContact.apply {
        logger.info("Current contact details: {}", this)
        firstName = contactDetails.firstName
        lastName = contactDetails.lastName
        middleName = contactDetails.middleName
        dateOfBirth = contactDetails.dateOfBirth
        gender = contactDetails.gender
        active = contactDetails.active
        amendedBy = contactDetails.amendedBy
        amendedTime = Instant.now()
        logger.info("Updated contact details: {}", this)
      }.let { updatedContact ->
        val savedContact = contactRepository.save(updatedContact)
        logger.info("Contact updated successfully with ID: {}", savedContact.id)
        savedContact
      }
    }.orElseGet {
      logger.warn("Contact with ID: {} not found, update operation aborted", id)
      null
    }
  }

  fun deleteContact(id: Long) {
    logger.info("Deleting contact with ID: {}", id)
    if (contactRepository.existsById(id)) {
      contactRepository.deleteById(id)
      logger.info("Contact with ID: {} deleted successfully", id)
    } else {
      logger.warn("Contact with ID: {} not found, delete operation aborted", id)
    }
  }

  fun getAllContacts(): List<Contact> {
    logger.info("Fetching all contacts")
    val contacts = contactRepository.findAll()
    logger.info("Found {} contacts", contacts.size)
    return contacts
  }
}
