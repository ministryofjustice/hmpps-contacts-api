package uk.gov.justice.digital.hmpps.hmppscontactsapi.service

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import uk.gov.justice.digital.hmpps.hmppscontactsapi.entity.PrisonerContactViewEntity
import uk.gov.justice.digital.hmpps.hmppscontactsapi.repository.PrisonerContactViewRepository

@Service
class ContactService @Autowired constructor(
    private val prisonerContactViewRepository: PrisonerContactViewRepository,
) {
    private val logger = LoggerFactory.getLogger(ContactService::class.java)

    fun getAllContacts(prisonerNumber: String, active: Boolean): List<PrisonerContactViewEntity> {
        logger.info("Fetching all contacts")
        val contacts = prisonerContactViewRepository.findAll()
        logger.info("Found {} contacts", contacts.size)
        return contacts
    }
}
