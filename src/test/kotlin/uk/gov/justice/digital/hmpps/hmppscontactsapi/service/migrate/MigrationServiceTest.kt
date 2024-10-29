package uk.gov.justice.digital.hmpps.hmppscontactsapi.service.migrate

import org.assertj.core.api.AssertionsForClassTypes.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import org.mockito.MockitoAnnotations.openMocks
import org.mockito.kotlin.any
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import uk.gov.justice.digital.hmpps.hmppscontactsapi.entity.ContactAddressEntity
import uk.gov.justice.digital.hmpps.hmppscontactsapi.entity.ContactEmailEntity
import uk.gov.justice.digital.hmpps.hmppscontactsapi.entity.ContactEmploymentEntity
import uk.gov.justice.digital.hmpps.hmppscontactsapi.entity.ContactEntity
import uk.gov.justice.digital.hmpps.hmppscontactsapi.entity.ContactIdentityEntity
import uk.gov.justice.digital.hmpps.hmppscontactsapi.entity.ContactPhoneEntity
import uk.gov.justice.digital.hmpps.hmppscontactsapi.entity.ContactRestrictionEntity
import uk.gov.justice.digital.hmpps.hmppscontactsapi.entity.PrisonerContactEntity
import uk.gov.justice.digital.hmpps.hmppscontactsapi.entity.PrisonerContactRestrictionEntity
import uk.gov.justice.digital.hmpps.hmppscontactsapi.model.request.EstimatedIsOverEighteen
import uk.gov.justice.digital.hmpps.hmppscontactsapi.model.request.migrate.CodedValue
import uk.gov.justice.digital.hmpps.hmppscontactsapi.model.request.migrate.Corporate
import uk.gov.justice.digital.hmpps.hmppscontactsapi.model.request.migrate.MigrateAddress
import uk.gov.justice.digital.hmpps.hmppscontactsapi.model.request.migrate.MigrateAuditInfo
import uk.gov.justice.digital.hmpps.hmppscontactsapi.model.request.migrate.MigrateContactRequest
import uk.gov.justice.digital.hmpps.hmppscontactsapi.model.request.migrate.MigrateEmailAddress
import uk.gov.justice.digital.hmpps.hmppscontactsapi.model.request.migrate.MigrateEmployment
import uk.gov.justice.digital.hmpps.hmppscontactsapi.model.request.migrate.MigrateIdentifier
import uk.gov.justice.digital.hmpps.hmppscontactsapi.model.request.migrate.MigratePhoneNumber
import uk.gov.justice.digital.hmpps.hmppscontactsapi.model.request.migrate.MigratePrisonerContactRestriction
import uk.gov.justice.digital.hmpps.hmppscontactsapi.model.request.migrate.MigrateRelationship
import uk.gov.justice.digital.hmpps.hmppscontactsapi.model.request.migrate.MigrateRestriction
import uk.gov.justice.digital.hmpps.hmppscontactsapi.model.response.migrate.ElementType
import uk.gov.justice.digital.hmpps.hmppscontactsapi.repository.ContactAddressPhoneRepository
import uk.gov.justice.digital.hmpps.hmppscontactsapi.repository.ContactAddressRepository
import uk.gov.justice.digital.hmpps.hmppscontactsapi.repository.ContactEmailRepository
import uk.gov.justice.digital.hmpps.hmppscontactsapi.repository.ContactEmploymentRepository
import uk.gov.justice.digital.hmpps.hmppscontactsapi.repository.ContactIdentityRepository
import uk.gov.justice.digital.hmpps.hmppscontactsapi.repository.ContactPhoneRepository
import uk.gov.justice.digital.hmpps.hmppscontactsapi.repository.ContactRepository
import uk.gov.justice.digital.hmpps.hmppscontactsapi.repository.ContactRestrictionRepository
import uk.gov.justice.digital.hmpps.hmppscontactsapi.repository.PrisonerContactRepository
import uk.gov.justice.digital.hmpps.hmppscontactsapi.repository.PrisonerContactRestrictionRepository
import java.time.LocalDate
import java.time.LocalDateTime

class MigrationServiceTest {
  private val contactRepository: ContactRepository = mock()
  private val contactAddressRepository: ContactAddressRepository = mock()
  private val contactAddressPhoneRepository: ContactAddressPhoneRepository = mock()
  private val contactPhoneRepository: ContactPhoneRepository = mock()
  private val contactEmailRepository: ContactEmailRepository = mock()
  private val contactIdentityRepository: ContactIdentityRepository = mock()
  private val contactRestrictionRepository: ContactRestrictionRepository = mock()
  private val prisonerContactRepository: PrisonerContactRepository = mock()
  private val prisonerContactRestrictionRepository: PrisonerContactRestrictionRepository = mock()
  private val contactEmploymentRepository: ContactEmploymentRepository = mock()

  val migrationService = MigrationService(
    contactRepository,
    contactAddressRepository,
    contactAddressPhoneRepository,
    contactPhoneRepository,
    contactEmailRepository,
    contactIdentityRepository,
    contactRestrictionRepository,
    prisonerContactRepository,
    prisonerContactRestrictionRepository,
    contactEmploymentRepository,
  )

  @BeforeEach
  fun setUp() {
    openMocks(this)
  }

  @Nested
  inner class MigrationExtractFromRequest {

    @Test
    fun `should extract and save basic contact details`() {
      val auditInfo = migrateAuditInfo()
      val request = migrateRequest(auditInfo)
      val contact = aContactEntity(1L, auditInfo)

      whenever(contactRepository.save(any())).thenReturn(contact)

      val contactCaptor = argumentCaptor<ContactEntity>()

      val result = migrationService.extractAndSaveContact(request)

      assertThat(result.first).isEqualTo(request.personId)
      assertThat(result.second)
        .extracting("contactId", "lastName", "firstName")
        .contains(contact.contactId, contact.lastName, contact.firstName)

      verify(contactRepository).save(contactCaptor.capture())

      with(contactCaptor.firstValue) {
        assertThat(this)
          .extracting("contactId", "lastName", "firstName", "createdBy", "createdTime", "amendedBy", "amendedTime")
          .contains(
            0L,
            contact.lastName,
            contact.firstName,
            auditInfo.createUsername,
            auditInfo.createDateTime,
            auditInfo.modifyUserId,
            auditInfo.modifyDateTime,
          )
      }
    }

    @Test
    fun `should extract and save contact phone numbers`() {
      val auditInfo = migrateAuditInfo()
      val request = migrateRequest(auditInfo).copy(phoneNumbers = phoneNumbers())
      val responses = listOf(
        ContactPhoneEntity(
          contactId = 1L,
          contactPhoneId = 1L,
          phoneType = request.phoneNumbers[0].type.code,
          phoneNumber = request.phoneNumbers[0].number,
          createdBy = auditInfo.createUsername!!,
          createdTime = auditInfo.createDateTime!!,
        ),
        ContactPhoneEntity(
          contactId = 1L,
          contactPhoneId = 2L,
          phoneType = request.phoneNumbers[1].type.code,
          phoneNumber = request.phoneNumbers[1].number,
          createdBy = auditInfo.createUsername!!,
          createdTime = auditInfo.createDateTime!!,
        ),
      )

      whenever(contactPhoneRepository.save(any()))
        .thenReturn(responses[0])
        .thenReturn(responses[1])

      val contactPhoneCaptor = argumentCaptor<ContactPhoneEntity>()

      val result = migrationService.extractAndSavePhones(request, 1L)

      assertThat(result.size).isEqualTo(2)

      for (i in 0..1) {
        assertThat(result[i].first).isEqualTo(request.phoneNumbers[i].phoneId)
        assertThat(result[i].second)
          .extracting("contactId", "contactPhoneId", "phoneType", "phoneNumber")
          .contains(
            responses[i].contactId,
            responses[i].contactPhoneId,
            responses[i].phoneType,
            responses[i].phoneNumber,
          )
      }

      verify(contactPhoneRepository, times(2)).save(contactPhoneCaptor.capture())

      for (x in 0..1) {
        with(contactPhoneCaptor.allValues[x]) {
          assertThat(this)
            .extracting("contactId", "contactPhoneId", "createdBy", "createdTime", "amendedBy", "amendedTime")
            .contains(
              responses[x].contactId,
              // ID generated by DB so not set on call to save
              0L,
              auditInfo.createUsername,
              auditInfo.createDateTime,
              auditInfo.modifyUserId,
              auditInfo.modifyDateTime,
            )
        }
      }
    }

    @Test
    fun `should extract and save contact identifiers`() {
      val auditInfo = migrateAuditInfo()
      val request = migrateRequest(auditInfo).copy(identifiers = identifiers())
      val responses = listOf(
        ContactIdentityEntity(
          contactId = 1L,
          contactIdentityId = 1L,
          identityType = request.identifiers[0].type.code,
          identityValue = request.identifiers[0].identifier,
          issuingAuthority = request.identifiers[0].issuedAuthority,
          createdBy = auditInfo.createUsername!!,
          createdTime = auditInfo.createDateTime!!,
        ),
        ContactIdentityEntity(
          contactId = 1L,
          contactIdentityId = 2L,
          identityType = request.identifiers[1].type.code,
          identityValue = request.identifiers[1].identifier,
          issuingAuthority = request.identifiers[1].issuedAuthority,
          createdBy = auditInfo.createUsername!!,
          createdTime = auditInfo.createDateTime!!,
        ),
      )

      whenever(contactIdentityRepository.save(any()))
        .thenReturn(responses[0])
        .thenReturn(responses[1])

      val contactIdentityCaptor = argumentCaptor<ContactIdentityEntity>()

      val result = migrationService.extractAndSaveIdentities(request, 1L)

      assertThat(result.size).isEqualTo(2)

      for (i in 0..1) {
        assertThat(result[i].first).isEqualTo(request.identifiers[i].sequence)
        assertThat(result[i].second)
          .extracting("contactId", "contactIdentityId", "identityType", "identityValue", "issuingAuthority")
          .contains(
            responses[i].contactId,
            responses[i].contactIdentityId,
            responses[i].identityType,
            responses[i].identityValue,
            responses[i].issuingAuthority,
          )
      }

      verify(contactIdentityRepository, times(2)).save(contactIdentityCaptor.capture())

      for (x in 0..1) {
        with(contactIdentityCaptor.allValues[x]) {
          assertThat(this)
            .extracting("contactId", "contactIdentityId", "createdBy", "createdTime", "amendedBy", "amendedTime")
            .contains(
              responses[x].contactId,
              // ID generated by DB so not set on call to save
              0L,
              auditInfo.createUsername,
              auditInfo.createDateTime,
              auditInfo.modifyUserId,
              auditInfo.modifyDateTime,
            )
        }
      }
    }

    @Test
    fun `should extract and save contact addresses`() {
      val auditInfo = migrateAuditInfo()
      val request = migrateRequest(auditInfo).copy(addresses = addresses())
      val responses = listOf(
        ContactAddressEntity(
          contactAddressId = 1L,
          contactId = 1L,
          addressType = request.addresses[0].type.code,
          property = request.addresses[0].premise,
          street = request.addresses[0].street,
          postCode = request.addresses[0].postCode,
          createdBy = auditInfo.createUsername!!,
          createdTime = auditInfo.createDateTime!!,
        ),
        ContactAddressEntity(
          contactAddressId = 2L,
          contactId = 1L,
          addressType = request.addresses[1].type.code,
          property = request.addresses[1].premise,
          street = request.addresses[1].street,
          postCode = request.addresses[1].postCode,
          createdBy = auditInfo.createUsername!!,
          createdTime = auditInfo.createDateTime!!,
        ),
      )

      whenever(contactAddressRepository.save(any()))
        .thenReturn(responses[0])
        .thenReturn(responses[1])

      val contactAddressCaptor = argumentCaptor<ContactAddressEntity>()

      val result = migrationService.extractAndSaveAddresses(request, 1L)

      assertThat(result.size).isEqualTo(2)

      for (i in 0..1) {
        assertThat(result[i].first).isEqualTo(request.addresses[i].addressId)
        assertThat(result[i].second)
          .extracting("contactId", "contactAddressId", "addressType", "postCode")
          .contains(
            responses[i].contactId,
            responses[i].contactAddressId,
            responses[i].addressType,
            responses[i].postCode,
          )
      }

      verify(contactAddressRepository, times(2)).save(contactAddressCaptor.capture())

      for (x in 0..1) {
        with(contactAddressCaptor.allValues[x]) {
          assertThat(this)
            .extracting("contactId", "contactAddressId", "createdBy", "createdTime", "amendedBy", "amendedTime")
            .contains(
              responses[x].contactId,
              // ID generated by DB so not set on call to save
              0L,
              auditInfo.createUsername,
              auditInfo.createDateTime,
              auditInfo.modifyUserId,
              auditInfo.modifyDateTime,
            )
        }
      }
    }

    @Test
    fun `should extract and save contact emails`() {
      val auditInfo = migrateAuditInfo()
      val request = migrateRequest(auditInfo).copy(emailAddresses = emails())
      val responses = listOf(
        ContactEmailEntity(
          contactEmailId = 1L,
          contactId = 1L,
          emailType = "Unknown",
          emailAddress = request.emailAddresses[0].email,
          createdBy = auditInfo.createUsername!!,
          createdTime = auditInfo.createDateTime!!,
        ),
        ContactEmailEntity(
          contactEmailId = 2L,
          contactId = 1L,
          emailType = "Unknown",
          emailAddress = request.emailAddresses[1].email,
          createdBy = auditInfo.createUsername!!,
          createdTime = auditInfo.createDateTime!!,
        ),
      )

      whenever(contactEmailRepository.save(any()))
        .thenReturn(responses[0])
        .thenReturn(responses[1])

      val contactEmailCaptor = argumentCaptor<ContactEmailEntity>()

      val result = migrationService.extractAndSaveEmails(request, 1L)

      assertThat(result.size).isEqualTo(2)

      for (i in 0..1) {
        assertThat(result[i].first).isEqualTo(request.emailAddresses[i].emailAddressId)
        assertThat(result[i].second)
          .extracting("contactId", "contactEmailId", "emailType", "emailAddress")
          .contains(
            responses[i].contactId,
            responses[i].contactEmailId,
            responses[i].emailType,
            responses[i].emailAddress,
          )
      }

      verify(contactEmailRepository, times(2)).save(contactEmailCaptor.capture())

      for (x in 0..1) {
        with(contactEmailCaptor.allValues[x]) {
          assertThat(this)
            .extracting("contactId", "contactEmailId", "createdBy", "createdTime", "amendedBy", "amendedTime")
            .contains(
              responses[x].contactId,
              // ID generated by DB so not set on call to save
              0L,
              auditInfo.createUsername,
              auditInfo.createDateTime,
              auditInfo.modifyUserId,
              auditInfo.modifyDateTime,
            )
        }
      }
    }

    @Test
    fun `should extract and save contact (visitor) restrictions`() {
      val auditInfo = migrateAuditInfo()
      val request = migrateRequest(auditInfo).copy(restrictions = restrictions())
      val responses = listOf(
        ContactRestrictionEntity(
          contactRestrictionId = 1L,
          contactId = 1L,
          restrictionType = request.restrictions[0].type.code,
          startDate = request.restrictions[0].effectiveDate,
          expiryDate = request.restrictions[0].expiryDate,
          comments = request.restrictions[0].comment,
          createdBy = auditInfo.createUsername!!,
          createdTime = auditInfo.createDateTime!!,
        ),
        ContactRestrictionEntity(
          contactRestrictionId = 2L,
          contactId = 1L,
          restrictionType = request.restrictions[1].type.code,
          startDate = request.restrictions[1].effectiveDate,
          expiryDate = request.restrictions[1].expiryDate,
          comments = request.restrictions[1].comment,
          createdBy = auditInfo.createUsername!!,
          createdTime = auditInfo.createDateTime!!,
        ),
      )

      whenever(contactRestrictionRepository.save(any()))
        .thenReturn(responses[0])
        .thenReturn(responses[1])

      val contactRestrictionCaptor = argumentCaptor<ContactRestrictionEntity>()

      val result = migrationService.extractAndSaveRestrictions(request, 1L)

      assertThat(result.size).isEqualTo(2)

      for (i in 0..1) {
        assertThat(result[i].first).isEqualTo(request.restrictions[i].id)
        assertThat(result[i].second)
          .extracting("contactId", "contactRestrictionId", "restrictionType", "startDate", "expiryDate", "comments")
          .contains(
            responses[i].contactId,
            responses[i].contactRestrictionId,
            responses[i].restrictionType,
            responses[i].startDate,
            responses[i].expiryDate,
            responses[i].comments,
          )
      }

      verify(contactRestrictionRepository, times(2)).save(contactRestrictionCaptor.capture())

      for (x in 0..1) {
        with(contactRestrictionCaptor.allValues[x]) {
          assertThat(this)
            .extracting("contactId", "contactRestrictionId", "createdBy", "createdTime", "amendedBy", "amendedTime")
            .contains(
              responses[x].contactId,
              // ID generated by DB so not set on call to save
              0L,
              auditInfo.createUsername,
              auditInfo.createDateTime,
              auditInfo.modifyUserId,
              auditInfo.modifyDateTime,
            )
        }
      }
    }

    @Test
    fun `should extract and save contact employments`() {
      val auditInfo = migrateAuditInfo()
      val request = migrateRequest(auditInfo).copy(employments = employments())
      val responses = listOf(
        ContactEmploymentEntity(
          contactEmploymentId = 1L,
          contactId = 1L,
          corporateId = request.employments[0].corporate.id,
          corporateName = request.employments[0].corporate.name,
          active = request.employments[0].active,
          createdBy = auditInfo.createUsername!!,
          createdTime = auditInfo.createDateTime!!,
        ),
        ContactEmploymentEntity(
          contactEmploymentId = 2L,
          contactId = 1L,
          corporateId = request.employments[1].corporate.id,
          corporateName = request.employments[1].corporate.name,
          active = request.employments[1].active,
          createdBy = auditInfo.createUsername!!,
          createdTime = auditInfo.createDateTime!!,
        ),
      )

      whenever(contactEmploymentRepository.save(any()))
        .thenReturn(responses[0])
        .thenReturn(responses[1])

      val contactEmploymentCaptor = argumentCaptor<ContactEmploymentEntity>()

      val result = migrationService.extractAndSaveEmployments(request, 1L)

      assertThat(result.size).isEqualTo(2)

      for (i in 0..1) {
        assertThat(result[i].first).isEqualTo(request.employments[i].sequence)
        assertThat(result[i].second)
          .extracting("contactId", "contactEmploymentId", "corporateId", "corporateName", "active")
          .contains(
            responses[i].contactId,
            responses[i].contactEmploymentId,
            responses[i].corporateId,
            responses[i].corporateName,
            responses[i].active,
          )
      }

      verify(contactEmploymentRepository, times(2)).save(contactEmploymentCaptor.capture())

      for (x in 0..1) {
        with(contactEmploymentCaptor.allValues[x]) {
          assertThat(this)
            .extracting("contactId", "contactEmploymentId", "createdBy", "createdTime", "amendedBy", "amendedTime")
            .contains(
              responses[x].contactId,
              // ID generated by DB so not set on call to save
              0L,
              auditInfo.createUsername,
              auditInfo.createDateTime,
              auditInfo.modifyUserId,
              auditInfo.modifyDateTime,
            )
        }
      }
    }

    @Test
    fun `should extract and save prisoner contact relationships`() {
      val auditInfo = migrateAuditInfo()
      val request = migrateRequest(auditInfo).copy(contacts = relationships())
      val responses = listOf(
        PrisonerContactEntity(
          prisonerContactId = 1L,
          contactId = 1L,
          contactType = request.contacts[0].contactType.code,
          relationshipType = request.contacts[0].relationshipType.code,
          prisonerNumber = request.contacts[0].prisonerNumber,
          emergencyContact = request.contacts[0].emergencyContact,
          nextOfKin = request.contacts[0].nextOfKin,
          comments = request.contacts[0].comment,
          createdBy = auditInfo.createUsername!!,
          createdTime = auditInfo.createDateTime!!,
        ).also {
          it.approvedVisitor = request.contacts[0].approvedVisitor
        },
        PrisonerContactEntity(
          prisonerContactId = 2L,
          contactId = 1L,
          contactType = request.contacts[1].contactType.code,
          relationshipType = request.contacts[1].relationshipType.code,
          prisonerNumber = request.contacts[1].prisonerNumber,
          emergencyContact = request.contacts[1].emergencyContact,
          nextOfKin = request.contacts[1].nextOfKin,
          comments = request.contacts[1].comment,
          createdBy = auditInfo.createUsername!!,
          createdTime = auditInfo.createDateTime!!,
        ).also {
          it.approvedVisitor = request.contacts[1].approvedVisitor
        },
      )

      whenever(prisonerContactRepository.save(any()))
        .thenReturn(responses[0])
        .thenReturn(responses[1])

      val prisonerContactCaptor = argumentCaptor<PrisonerContactEntity>()

      val result = migrationService.extractAndSavePrisonerContacts(request, 1L)

      assertThat(result.size).isEqualTo(2)

      for (i in 0..1) {
        assertThat(result[i].first).isEqualTo(request.contacts[i].id)
        assertThat(result[i].second)
          .extracting("contactId", "prisonerContactId", "contactType", "relationshipType", "prisonerNumber", "nextOfKin", "emergencyContact", "comments", "approvedVisitor")
          .contains(
            responses[i].contactId,
            responses[i].prisonerContactId,
            responses[i].contactType,
            responses[i].relationshipType,
            responses[i].prisonerNumber,
            responses[i].nextOfKin,
            responses[i].emergencyContact,
            responses[i].comments,
            responses[i].approvedVisitor,
          )
      }

      verify(prisonerContactRepository, times(2)).save(prisonerContactCaptor.capture())

      for (x in 0..1) {
        with(prisonerContactCaptor.allValues[x]) {
          assertThat(this)
            .extracting("contactId", "prisonerContactId", "createdBy", "createdTime", "amendedBy", "amendedTime")
            .contains(
              responses[x].contactId,
              // ID generated by DB so not set on call to save
              0L,
              auditInfo.createUsername,
              auditInfo.createDateTime,
              auditInfo.modifyUserId,
              auditInfo.modifyDateTime,
            )
        }
      }
    }

    @Test
    fun `should extract and save contact address phone numbers`() {
    }

    @Test
    fun `should extract and save prisoner contact restrictions`() {
      val auditInfo = migrateAuditInfo()
      val request = migrateRequest(auditInfo).copy(contacts = relationshipsWithRestrictions())
      val relationshipResponse = PrisonerContactEntity(
        prisonerContactId = 1L,
        contactId = 1L,
        contactType = request.contacts[0].contactType.code,
        relationshipType = request.contacts[0].relationshipType.code,
        prisonerNumber = request.contacts[0].prisonerNumber,
        emergencyContact = request.contacts[0].emergencyContact,
        nextOfKin = request.contacts[0].nextOfKin,
        comments = request.contacts[0].comment,
        createdBy = auditInfo.createUsername!!,
        createdTime = auditInfo.createDateTime!!,
      ).also {
        it.approvedVisitor = request.contacts[0].approvedVisitor
      }

      val restrictionResponses = listOf(
        PrisonerContactRestrictionEntity(
          prisonerContactRestrictionId = 1L,
          prisonerContactId = 1L,
          restrictionType = request.contacts[0].restrictions[0].restrictionType.code,
          startDate = request.contacts[0].restrictions[0].startDate,
          expiryDate = request.contacts[0].restrictions[0].expiryDate,
          comments = request.contacts[0].restrictions[0].comment,
          createdBy = request.contacts[0].restrictions[0].createdByUsername,
          createdTime = LocalDateTime.now(),
        ),
        PrisonerContactRestrictionEntity(
          prisonerContactRestrictionId = 2L,
          prisonerContactId = 1L,
          restrictionType = request.contacts[0].restrictions[1].restrictionType.code,
          startDate = request.contacts[0].restrictions[1].startDate,
          expiryDate = request.contacts[0].restrictions[1].expiryDate,
          comments = request.contacts[0].restrictions[1].comment,
          createdBy = request.contacts[0].restrictions[1].createdByUsername,
          createdTime = LocalDateTime.now(),
        ),
      )

      whenever(prisonerContactRepository.save(any())).thenReturn(relationshipResponse)

      whenever(prisonerContactRestrictionRepository.save(any()))
        .thenReturn(restrictionResponses[0])
        .thenReturn(restrictionResponses[1])

      val prisonerContactRestrictionCaptor = argumentCaptor<PrisonerContactRestrictionEntity>()

      val resultContacts = migrationService.extractAndSavePrisonerContacts(request, 1L)
      val resultRestrictions = migrationService.extractAndSavePrisonerContactRestrictions(request, 1L, resultContacts)

      assertThat(resultRestrictions[0].second.size).isEqualTo(2)
      assertThat(resultRestrictions[0].second[0].first).isEqualTo(20L)
      assertThat(resultRestrictions[0].second[0].second.restrictionType).isEqualTo("FORBID")
      assertThat(resultRestrictions[0].second[1].first).isEqualTo(21L)
      assertThat(resultRestrictions[0].second[1].second.restrictionType).isEqualTo("ACCOMP")

      verify(prisonerContactRestrictionRepository, times(2)).save(prisonerContactRestrictionCaptor.capture())

      for (x in 0..1) {
        with(prisonerContactRestrictionCaptor.allValues[x]) {
          assertThat(this)
            .extracting("prisonerContactId", "prisonerContactRestrictionId", "restrictionType", "comments")
            .contains(
              relationshipResponse.prisonerContactId,
              // ID generated by DB so not set on call to save
              0L,
              restrictionResponses[x].restrictionType,
              restrictionResponses[x].comments,
            )
        }
      }
    }

    @Test
    fun `should build the contact restrictions response object`() {
      val contacts = listOf(
        Pair(
          first = 2L,
          second = PrisonerContactEntity(
            prisonerContactId = 1L,
            contactId = 1L,
            contactType = "SOCIAL",
            relationshipType = "BRO",
            prisonerNumber = "A1234AA",
            createdBy = "TEST",
            createdTime = LocalDateTime.now(),
            comments = "",
            emergencyContact = false,
            nextOfKin = true,
          ),
        ),
      )

      val restrictions = listOf(
        Pair(
          first = 2L,
          second = listOf(
            Pair(
              first = 20L,
              second = PrisonerContactRestrictionEntity(
                prisonerContactRestrictionId = 1L,
                prisonerContactId = 1L,
                restrictionType = "FORBID",
                startDate = LocalDate.now(),
                expiryDate = LocalDate.now().plusDays(10),
                comments = "Forbid",
                createdBy = "TEST",
                createdTime = LocalDateTime.now(),
              ),
            ),
            Pair(
              first = 21L,
              second = PrisonerContactRestrictionEntity(
                prisonerContactRestrictionId = 2L,
                prisonerContactId = 1L,
                restrictionType = "ACCOMP",
                startDate = LocalDate.now(),
                expiryDate = LocalDate.now().plusDays(10),
                comments = "Accompany",
                createdBy = "TEST",
                createdTime = LocalDateTime.now(),
              ),
            ),
          ),
        ),
      )

      val result = migrationService.buildContactsAndRestrictionsResponse(contacts, restrictions)

      assertThat(result[0].restrictions[0])
        .extracting("elementType", "nomisId", "dpsId")
        .contains(ElementType.PRISONER_CONTACT_RESTRICTION, 20L, 1L)

      assertThat(result[0].restrictions[1])
        .extracting("elementType", "nomisId", "dpsId")
        .contains(ElementType.PRISONER_CONTACT_RESTRICTION, 21L, 2L)
    }
  }

  private fun migrateAuditInfo() =
    MigrateAuditInfo(
      createUsername = "J999J",
      createDateTime = LocalDateTime.of(2024, 1, 1, 13, 47),
      createDisplayName = "Jay Jaysen",
      modifyUserId = "K999K",
      modifyDisplayName = "Kay Kaysen",
      modifyDateTime = LocalDateTime.of(2024, 2, 2, 14, 48),
    )

  private fun migrateRequest(audit: MigrateAuditInfo): MigrateContactRequest =
    MigrateContactRequest(
      personId = 1,
      title = CodedValue("MR", "Mr"),
      lastName = "Smith",
      firstName = "John",
      gender = CodedValue("Male", "Male"),
      keepBiometrics = false,
      audit = audit,
    )

  private fun aContactEntity(contactId: Long = 1L, audit: MigrateAuditInfo) =
    ContactEntity(
      contactId = contactId,
      title = "Mr",
      firstName = "John",
      middleNames = null,
      lastName = "Smith",
      dateOfBirth = null,
      estimatedIsOverEighteen = EstimatedIsOverEighteen.NO,
      isDeceased = false,
      deceasedDate = null,
      createdBy = audit.createUsername!!,
      createdTime = audit.createDateTime!!,
    )

  private fun phoneNumbers() =
    listOf(
      MigratePhoneNumber(phoneId = 1L, number = "11111", extension = "1", type = CodedValue("HOME", "Home")),
      MigratePhoneNumber(phoneId = 2L, number = "22222", extension = "2", type = CodedValue("WORK", "Work")),
    )

  private fun addresses() =
    listOf(
      MigrateAddress(addressId = 1L, type = CodedValue("HOME", "Home"), premise = "10", street = "Dublin Road", postCode = "D1 1DN", primaryAddress = true),
      MigrateAddress(addressId = 2L, type = CodedValue("WORK", "Work"), premise = "11", street = "Dublin Road", postCode = "D1 2DN"),
    )

  private fun emails() =
    listOf(
      MigrateEmailAddress(emailAddressId = 1L, email = "a@.com"),
      MigrateEmailAddress(emailAddressId = 2L, email = "b@b.com"),
    )

  private fun restrictions() =
    listOf(
      MigrateRestriction(id = 1L, type = CodedValue("ESCORTED", "Desc"), comment = "Active", effectiveDate = LocalDate.now(), expiryDate = LocalDate.now().plusDays(30)),
      MigrateRestriction(id = 2L, type = CodedValue("CHILDREN", "Desc"), comment = "Expired", effectiveDate = LocalDate.now().minusDays(30), expiryDate = LocalDate.now().minusDays(1)),
    )

  private fun employments() =
    listOf(
      MigrateEmployment(sequence = 1L, corporate = Corporate(id = 123L, name = "Big Blue"), active = true),
      MigrateEmployment(sequence = 2L, corporate = Corporate(id = 321L, name = "Big Yellow"), active = false),
    )

  private fun relationships() =
    listOf(
      MigrateRelationship(
        id = 1L,
        contactType = CodedValue("SOCIAL", "Social contact"),
        relationshipType = CodedValue("BRO", "Brother"),
        active = true,
        expiryDate = null,
        approvedVisitor = true,
        nextOfKin = true,
        emergencyContact = true,
        comment = "Visits regularly",
        prisonerNumber = "A1234AA",
        restrictions = emptyList(),
      ),
      MigrateRelationship(
        id = 2L,
        contactType = CodedValue("OFFICIAL", "Official contact"),
        relationshipType = CodedValue("ILP", "In Loco Parentis"),
        active = false,
        expiryDate = LocalDate.now().minusDays(10),
        approvedVisitor = false,
        nextOfKin = false,
        emergencyContact = false,
        comment = "Used to visit but no more",
        prisonerNumber = "A1234AA",
        restrictions = emptyList(),
      ),
    )

  private fun relationshipsWithRestrictions() =
    listOf(
      MigrateRelationship(
        id = 11L,
        contactType = CodedValue("SOCIAL", "Social contact"),
        relationshipType = CodedValue("BRO", "Brother"),
        active = true,
        expiryDate = null,
        approvedVisitor = true,
        nextOfKin = true,
        emergencyContact = true,
        comment = "Visits regularly",
        prisonerNumber = "A1234AA",
        restrictions = listOf(
          MigratePrisonerContactRestriction(
            id = 20L,
            restrictionType = CodedValue("FORBID", "Forbidden"),
            comment = "This person is not allowed to visit",
            startDate = LocalDate.now().minusDays(30),
            expiryDate = LocalDate.now().plusDays(10),
            createdByUsername = "X",
          ),
          MigratePrisonerContactRestriction(
            id = 21L,
            restrictionType = CodedValue("ACCOMP", "Accompanied"),
            comment = "This person must be accompanied during visits",
            startDate = LocalDate.now().minusDays(30),
            expiryDate = LocalDate.now().plusDays(10),
            createdByUsername = "Y",
          ),
        ),
      ),
    )

  private fun identifiers() =
    listOf(
      MigrateIdentifier(
        sequence = 1L,
        type = CodedValue("DRIVING_LICENCE", "Driving Licence"),
        identifier = "DL1",
        issuedAuthority = "DVLA",
      ),
      MigrateIdentifier(
        sequence = 2L,
        type = CodedValue("PASSPORT", "Passport"),
        identifier = "PASS1",
        issuedAuthority = "UKBORDER",
      ),
    )
}
