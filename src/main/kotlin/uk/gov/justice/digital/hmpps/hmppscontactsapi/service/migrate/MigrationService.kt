package uk.gov.justice.digital.hmpps.hmppscontactsapi.service.migrate

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import uk.gov.justice.digital.hmpps.hmppscontactsapi.entity.ContactAddressEntity
import uk.gov.justice.digital.hmpps.hmppscontactsapi.entity.ContactEmailEntity
import uk.gov.justice.digital.hmpps.hmppscontactsapi.entity.ContactEntity
import uk.gov.justice.digital.hmpps.hmppscontactsapi.entity.ContactIdentityEntity
import uk.gov.justice.digital.hmpps.hmppscontactsapi.entity.ContactPhoneEntity
import uk.gov.justice.digital.hmpps.hmppscontactsapi.model.request.EstimatedIsOverEighteen
import uk.gov.justice.digital.hmpps.hmppscontactsapi.model.request.migrate.MigrateContactRequest
import uk.gov.justice.digital.hmpps.hmppscontactsapi.model.response.migrate.ElementType
import uk.gov.justice.digital.hmpps.hmppscontactsapi.model.response.migrate.IdPair
import uk.gov.justice.digital.hmpps.hmppscontactsapi.model.response.migrate.MigrateContactResponse
import uk.gov.justice.digital.hmpps.hmppscontactsapi.repository.ContactAddressRepository
import uk.gov.justice.digital.hmpps.hmppscontactsapi.repository.ContactEmailRepository
import uk.gov.justice.digital.hmpps.hmppscontactsapi.repository.ContactIdentityRepository
import uk.gov.justice.digital.hmpps.hmppscontactsapi.repository.ContactPhoneRepository
import uk.gov.justice.digital.hmpps.hmppscontactsapi.repository.ContactRepository
import uk.gov.justice.digital.hmpps.hmppscontactsapi.repository.ContactRestrictionRepository
import uk.gov.justice.digital.hmpps.hmppscontactsapi.repository.PrisonerContactRepository
import java.time.LocalDateTime

@Service
@Transactional
class MigrationService(
  private val contactRepository: ContactRepository,
  private val contactAddressRepository: ContactAddressRepository,
  private val contactPhoneRepository: ContactPhoneRepository,
  private val contactEmailRepository: ContactEmailRepository,
  private val contactIdentityRepository: ContactIdentityRepository,
  private val contactRestrictionRepository: ContactRestrictionRepository,
  private val prisonerContactRepository: PrisonerContactRepository,
  private val prisonerContactRestrictionRepository: ContactRestrictionRepository,
) {

  /**
   * Accept a full contact migration object, validate and create the equivalent objects
   * in the contacts database locally. Do NOT emit sync events for objects created as these
   * will already exist in NOMIS and the mapping tables - we're not generating new copies of
   * this data, as we did for A&A 1-way sync.
   *
   * Assemble a response object to contain both the NOMIS and DPS IDs for each
   * entity and sub-entity saved to allow the mapping service to link them.
   */
  fun migrateContact(request: MigrateContactRequest): MigrateContactResponse {
    val contactPair = extractAndSaveContact(request)
    val phoneNumberPairs = extractAndSavePhones(request, contactPair.second.contactId)
    val addressPairs = extractAndSaveAddresses(request, contactPair.second.contactId)
    val emailPairs = extractAndSaveEmails(request, contactPair.second.contactId)
    val identityPairs = extractAndSaveIdentities(request, contactPair.second.contactId)

    /*
    employmentPairs - official contacts
    restrictionPairs
    prisonerContactPairs
    prisonerContactRestrictionPairs
     */

    return MigrateContactResponse(
      nomisPersonId = contactPair.first,
      dpsContactId = contactPair.second.contactId,
      lastName = contactPair.second.lastName,
      contactTypeCode = contactPair.second.contactTypeCode ?: "SOCIAL",
      phoneNumbers = phoneNumberPairs.map { IdPair(ElementType.PHONE, it.first, it.second.contactPhoneId) },
      addresses = addressPairs.map { IdPair(ElementType.ADDRESS, it.first, it.second.contactAddressId) },
      emailAddresses = emailPairs.map { IdPair(ElementType.EMAIL, it.first, it.second.contactEmailId) },
      identities = identityPairs.map { IdPair(ElementType.IDENTITY, it.first, it.second.contactIdentityId) },
      restrictions = emptyList(),
      prisonerContacts = emptyList(),
      prisonerContactRestrictions = emptyList(),
    )
  }

  fun extractAndSaveContact(req: MigrateContactRequest): Pair<Long, ContactEntity> =
    Pair(
      req.personId,
      contactRepository.save(
        ContactEntity(
          contactId = 0L,
          title = req.title?.code,
          lastName = req.lastName,
          middleName = req.middleName,
          firstName = req.firstName,
          dateOfBirth = req.dateOfBirth,
          deceasedDate = req.deceasedDate,
          isDeceased = req.deceasedDate != null,
          estimatedIsOverEighteen = EstimatedIsOverEighteen.DO_NOT_KNOW,
          createdBy = req.audit?.createUsername ?: "MIGRATION",
          createdTime = req.audit?.createDateTime ?: LocalDateTime.now(),
        ).also {
          it.contactTypeCode = "SOCIAL"
          it.staffFlag = req.staff
          it.gender = req.gender?.code
          it.languageCode = req.language?.code
          it.maritalStatus = req.domesticStatus?.code
          // it.active
          // it.placeOfBirth
          it.interpreterRequired = req.interpreterRequired
          // it.comments
          it.amendedBy = req.audit?.modifyUserId
          it.amendedTime = req.audit?.modifyDateTime
        },
      ),
    )

  fun extractAndSavePhones(req: MigrateContactRequest, contactId: Long): List<Pair<Long, ContactPhoneEntity>> =
    req.phoneNumbers.map { requestPhone ->
      Pair(
        requestPhone.phoneId,
        contactPhoneRepository.save(
          ContactPhoneEntity(
            contactPhoneId = 0L,
            contactId = contactId,
            phoneType = requestPhone.type.code,
            phoneNumber = requestPhone.number,
            extNumber = requestPhone.extension,
            createdBy = req.audit?.createUsername ?: "MIGRATION",
            createdTime = req.audit?.createDateTime ?: LocalDateTime.now(),
          ).also {
            it.amendedBy = req.audit?.modifyUserId
            it.amendedTime = req.audit?.modifyDateTime
          },
        ),
      )
    }

  fun extractAndSaveAddresses(req: MigrateContactRequest, contactId: Long): List<Pair<Long, ContactAddressEntity>> =
    req.addresses.map { addr ->
      Pair(
        addr.addressId,
        contactAddressRepository.save(
          ContactAddressEntity(
            contactAddressId = 0L,
            contactId = contactId,
            addressType = "HOME",
            primaryAddress = addr.primaryAddress,
            flat = addr.flat,
            property = addr.premise,
            street = addr.street,
            area = addr.locality,
            cityCode = addr.city?.code,
            countyCode = addr.county?.code,
            postCode = addr.postCode,
            countryCode = addr.country?.code,
            verified = addr.validatedPAF,
            mailFlag = addr.mailAddress,
            startDate = addr.startDate,
            endDate = addr.endDate,
            noFixedAddress = addr.noFixedAddress,
            // Check any linked phone numbers are also supplied as phone numbers!
            createdBy = req.audit?.createUsername ?: "MIGRATION",
            createdTime = req.audit?.createDateTime ?: LocalDateTime.now(),
          ).also {
            it.amendedBy = req.audit?.modifyUserId
            it.amendedTime = req.audit?.modifyDateTime
          },
        ),
      )
    }

  fun extractAndSaveEmails(req: MigrateContactRequest, contactId: Long): List<Pair<Long, ContactEmailEntity>> =
    req.emailAddresses.map { requestEmail ->
      Pair(
        requestEmail.emailAddressId,
        contactEmailRepository.save(
          ContactEmailEntity(
            contactEmailId = 0L,
            contactId = contactId,
            emailType = "PERSONAL",
            emailAddress = requestEmail.email,
            createdBy = req.audit?.createUsername ?: "MIGRATION",
            createdTime = req.audit?.createDateTime ?: LocalDateTime.now(),
          ).also {
            it.amendedBy = req.audit?.modifyUserId
            it.amendedTime = req.audit?.modifyDateTime
          },
        ),
      )
    }

  fun extractAndSaveIdentities(req: MigrateContactRequest, contactId: Long): List<Pair<Long, ContactIdentityEntity>> =
    req.identifiers.map { requestIdentifier ->
      Pair(
        requestIdentifier.sequence,
        contactIdentityRepository.save(
          ContactIdentityEntity(
            contactIdentityId = 0L,
            contactId = contactId,
            identityType = requestIdentifier.type.code,
            identityValue = requestIdentifier.identifier,
            issuingAuthority = requestIdentifier.issuedAuthority,
            createdBy = req.audit?.createUsername ?: "MIGRATION",
            createdTime = req.audit?.createDateTime ?: LocalDateTime.now(),
          ).also {
            it.amendedBy = req.audit?.modifyUserId
            it.amendedTime = req.audit?.modifyDateTime
          },
        ),
      )
    }

//  private fun List<MigrateAlert>.alertCodes() =
//    map { it.alertCode }.distinct().let { requestAlertCodes ->
//      alertCodeRepository.findByCodeIn(requestAlertCodes).associateBy { it.code }
//    }
//
//  private fun List<MigrateAlert>.checkForNotFoundAlertCodes(alertCodes: Map<String, AlertCode>) =
//    with(map { it.alertCode }.distinct().filterNot { alertCodes.containsKey(it) }.sorted()) {
//      require(isEmpty()) {
//        joinToString(prefix = "Alert code(s) '", separator = "', '", postfix = "' not found")
//      }
//    }
//
//  private fun List<MigrateAlert>.logActiveToBeforeActiveFrom(prisonNumber: String) {
//    this.filter { it.activeTo?.isBefore(it.activeFrom) == true }.forEach {
//      log.warn("Alert with sequence '${it.alertSeq}' for person with prison number '$prisonNumber' from booking with id '${it.offenderBookId}' and sequence '${it.bookingSeq}' has an active to date '${it.activeTo}' that is before the active from date '${it.activeFrom}'")
//    }
//  }
}
