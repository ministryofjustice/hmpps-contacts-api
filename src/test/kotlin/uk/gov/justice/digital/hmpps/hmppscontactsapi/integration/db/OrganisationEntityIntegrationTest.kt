package uk.gov.justice.digital.hmpps.hmppscontactsapi.integration.db

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import uk.gov.justice.digital.hmpps.hmppscontactsapi.entity.EmploymentEntity
import uk.gov.justice.digital.hmpps.hmppscontactsapi.entity.OrganisationAddressEntity
import uk.gov.justice.digital.hmpps.hmppscontactsapi.entity.OrganisationAddressPhoneEntity
import uk.gov.justice.digital.hmpps.hmppscontactsapi.entity.OrganisationEmailEntity
import uk.gov.justice.digital.hmpps.hmppscontactsapi.entity.OrganisationEntity
import uk.gov.justice.digital.hmpps.hmppscontactsapi.entity.OrganisationPhoneEntity
import uk.gov.justice.digital.hmpps.hmppscontactsapi.entity.OrganisationTypeEntity
import uk.gov.justice.digital.hmpps.hmppscontactsapi.entity.OrganisationWebAddressEntity
import uk.gov.justice.digital.hmpps.hmppscontactsapi.entity.OrganisationWithFixedIdEntity
import uk.gov.justice.digital.hmpps.hmppscontactsapi.integration.PostgresIntegrationTestBase
import uk.gov.justice.digital.hmpps.hmppscontactsapi.repository.EmploymentRepository
import uk.gov.justice.digital.hmpps.hmppscontactsapi.repository.OrganisationAddressPhoneRepository
import uk.gov.justice.digital.hmpps.hmppscontactsapi.repository.OrganisationAddressRepository
import uk.gov.justice.digital.hmpps.hmppscontactsapi.repository.OrganisationEmailRepository
import uk.gov.justice.digital.hmpps.hmppscontactsapi.repository.OrganisationPhoneRepository
import uk.gov.justice.digital.hmpps.hmppscontactsapi.repository.OrganisationRepository
import uk.gov.justice.digital.hmpps.hmppscontactsapi.repository.OrganisationTypeRepository
import uk.gov.justice.digital.hmpps.hmppscontactsapi.repository.OrganisationWebAddressRepository
import uk.gov.justice.digital.hmpps.hmppscontactsapi.repository.OrganisationWithFixedIdRepository
import java.time.LocalDate
import java.time.LocalDateTime

/**
 * Temporary integration test until APIs are built. This is just to ratify the entity mapping.
 */
class OrganisationEntityIntegrationTest : PostgresIntegrationTestBase() {

  @Autowired
  private lateinit var organisationWithFixedIdRepository: OrganisationWithFixedIdRepository

  @Autowired
  private lateinit var organisationRepository: OrganisationRepository

  @Autowired
  private lateinit var organisationTypeRepository: OrganisationTypeRepository

  @Autowired
  private lateinit var organisationPhoneRepository: OrganisationPhoneRepository

  @Autowired
  private lateinit var organisationEmailRepository: OrganisationEmailRepository

  @Autowired
  private lateinit var organisationWebAddressRepository: OrganisationWebAddressRepository

  @Autowired
  private lateinit var organisationAddressRepository: OrganisationAddressRepository

  @Autowired
  private lateinit var organisationAddressPhoneRepository: OrganisationAddressPhoneRepository

  @Autowired
  private lateinit var employmentRepository: EmploymentRepository

  @Test
  fun `can create an organisation with a fixed id with minimal fields`() {
    val entity = OrganisationWithFixedIdEntity(
      12345,
      organisationName = "Name",
      programmeNumber = null,
      vatNumber = null,
      caseloadId = null,
      comments = null,
      active = true,
      deactivatedDate = null,
      createdBy = "Created by",
      createdTime = LocalDateTime.now(),
      updatedBy = null,
      updatedTime = null,
    )
    val created = organisationWithFixedIdRepository.saveAndFlush(entity)
    assertThat(created.organisationId).isEqualTo(12345)
  }

  @Test
  fun `can create an organisation with a fixed id with optional fields`() {
    val entity = OrganisationWithFixedIdEntity(
      654321,
      organisationName = "Name",
      programmeNumber = "P1",
      vatNumber = "V1",
      caseloadId = "C1",
      comments = "C2",
      active = false,
      deactivatedDate = LocalDate.now(),
      createdBy = "Created by",
      createdTime = LocalDateTime.now().minusMinutes(20),
      updatedBy = "U1",
      updatedTime = LocalDateTime.now().plusMinutes(20),
    )
    val created = organisationWithFixedIdRepository.saveAndFlush(entity)
    assertThat(created.organisationId).isEqualTo(654321)
  }

  @Test
  fun `can create an organisation with a generated id with minimal fields`() {
    val entity = OrganisationEntity(
      null,
      organisationName = "Name",
      programmeNumber = null,
      vatNumber = null,
      caseloadId = null,
      comments = null,
      active = true,
      deactivatedDate = null,
      createdBy = "Created by",
      createdTime = LocalDateTime.now(),
      updatedBy = null,
      updatedTime = null,
    )
    val created = organisationRepository.saveAndFlush(entity)
    assertThat(created.organisationId).isGreaterThanOrEqualTo(20000000)
  }

  @Test
  fun `can create an organisation with a generated id with optional fields`() {
    val entity = OrganisationEntity(
      null,
      organisationName = "Name",
      programmeNumber = "P1",
      vatNumber = "V1",
      caseloadId = "C1",
      comments = "C2",
      active = false,
      deactivatedDate = LocalDate.now(),
      createdBy = "Created by",
      createdTime = LocalDateTime.now().minusMinutes(20),
      updatedBy = "U1",
      updatedTime = LocalDateTime.now().plusMinutes(20),
    )
    val created = organisationRepository.saveAndFlush(entity)
    assertThat(created.organisationId).isGreaterThanOrEqualTo(20000000)
  }

  @Test
  fun `can create organisation types`() {
    val org = aNewOrganisation()
    val withMinimalFields = OrganisationTypeEntity(
      organisationTypeId = 0,
      organisationId = org.id(),
      organisationType = "SWO",
      createdBy = "CREATED",
      createdTime = LocalDateTime.now().minusMinutes(20),
      updatedBy = null,
      updatedTime = null,
    )
    val withAllFields = OrganisationTypeEntity(
      organisationTypeId = 0,
      organisationId = org.id(),
      organisationType = "SWO",
      createdBy = "CREATED",
      createdTime = LocalDateTime.now().minusMinutes(20),
      updatedBy = "UPDATED",
      updatedTime = LocalDateTime.now().plusMinutes(20),
    )
    val createdMin = organisationTypeRepository.saveAndFlush(withMinimalFields)
    assertThat(createdMin.organisationTypeId).isGreaterThan(0)
    val createdMax = organisationTypeRepository.saveAndFlush(withAllFields)
    assertThat(createdMax.organisationTypeId).isGreaterThan(0)
  }

  @Test
  fun `can create organisation phones`() {
    val org = aNewOrganisation()
    val withMinimalFields = OrganisationPhoneEntity(
      organisationPhoneId = 0,
      organisationId = org.id(),
      phoneType = "MOB",
      phoneNumber = "123",
      extNumber = null,
      createdBy = "CREATED",
      createdTime = LocalDateTime.now().minusMinutes(20),
      updatedBy = null,
      updatedTime = null,
    )
    val withAllFields = OrganisationPhoneEntity(
      organisationPhoneId = 0,
      organisationId = org.id(),
      phoneType = "HOME",
      phoneNumber = "321",
      extNumber = "987",
      createdBy = "CREATED",
      createdTime = LocalDateTime.now().minusMinutes(20),
      updatedBy = "UPDATED",
      updatedTime = LocalDateTime.now().plusMinutes(20),
    )
    val createdMin = organisationPhoneRepository.saveAndFlush(withMinimalFields)
    assertThat(createdMin.organisationPhoneId).isGreaterThan(0)
    val createdMax = organisationPhoneRepository.saveAndFlush(withAllFields)
    assertThat(createdMax.organisationPhoneId).isGreaterThan(0)
  }

  @Test
  fun `can create organisation emails`() {
    val org = aNewOrganisation()
    val withMinimalFields = OrganisationEmailEntity(
      organisationEmailId = 0,
      organisationId = org.id(),
      emailAddress = "test@example.com",
      createdBy = "CREATED",
      createdTime = LocalDateTime.now().minusMinutes(20),
      updatedBy = null,
      updatedTime = null,
    )
    val withAllFields = OrganisationEmailEntity(
      organisationEmailId = 0,
      organisationId = org.id(),
      emailAddress = "more@example.com",
      createdBy = "CREATED",
      createdTime = LocalDateTime.now().minusMinutes(20),
      updatedBy = "UPDATED",
      updatedTime = LocalDateTime.now().plusMinutes(20),
    )
    val createdMin = organisationEmailRepository.saveAndFlush(withMinimalFields)
    assertThat(createdMin.organisationEmailId).isGreaterThan(0)
    val createdMax = organisationEmailRepository.saveAndFlush(withAllFields)
    assertThat(createdMax.organisationEmailId).isGreaterThan(0)
  }

  @Test
  fun `can create organisation web addresses`() {
    val org = aNewOrganisation()
    val withMinimalFields = OrganisationWebAddressEntity(
      organisationWebAddressId = 0,
      organisationId = org.id(),
      webAddress = "test.example.com",
      createdBy = "CREATED",
      createdTime = LocalDateTime.now().minusMinutes(20),
      updatedBy = null,
      updatedTime = null,
    )
    val withAllFields = OrganisationWebAddressEntity(
      organisationWebAddressId = 0,
      organisationId = org.id(),
      webAddress = "more.example.com",
      createdBy = "CREATED",
      createdTime = LocalDateTime.now().minusMinutes(20),
      updatedBy = "UPDATED",
      updatedTime = LocalDateTime.now().plusMinutes(20),
    )
    val createdMin = organisationWebAddressRepository.saveAndFlush(withMinimalFields)
    assertThat(createdMin.organisationWebAddressId).isGreaterThan(0)
    val createdMax = organisationWebAddressRepository.saveAndFlush(withAllFields)
    assertThat(createdMax.organisationWebAddressId).isGreaterThan(0)
  }

  @Test
  fun `can create organisation addresses`() {
    val org = aNewOrganisation()
    val withMinimalFields = OrganisationAddressEntity(
      organisationAddressId = 0,
      organisationId = org.id(),
      addressType = null,
      primaryAddress = false,
      mailAddress = false,
      serviceAddress = false,
      noFixedAddress = false,
      flat = null,
      property = null,
      street = null,
      area = null,
      cityCode = null,
      countyCode = null,
      postCode = null,
      countryCode = null,
      specialNeedsCode = null,
      contactPersonName = null,
      businessHours = null,
      comments = null,
      startDate = null,
      endDate = null,
      createdBy = "CREATED",
      createdTime = LocalDateTime.now().minusMinutes(20),
      updatedBy = null,
      updatedTime = null,
    )
    val withAllFields = OrganisationAddressEntity(
      organisationAddressId = 0,
      organisationId = org.id(),
      addressType = "HOME",
      primaryAddress = true,
      mailAddress = true,
      serviceAddress = true,
      noFixedAddress = true,
      flat = "F1",
      property = "P1",
      street = "S1",
      area = "A1",
      cityCode = "25343",
      countyCode = "S.YORKSHIRE",
      postCode = "P1C1",
      countryCode = "ENG",
      specialNeedsCode = "DEAF",
      contactPersonName = "CP1",
      businessHours = "BH1",
      comments = "Comments",
      startDate = LocalDate.of(2000, 1, 1),
      endDate = LocalDate.of(2010, 10, 10),
      createdBy = "CREATED",
      createdTime = LocalDateTime.now().minusMinutes(20),
      updatedBy = "UPDATED",
      updatedTime = LocalDateTime.now().plusMinutes(20),
    )
    val createdMin = organisationAddressRepository.saveAndFlush(withMinimalFields)
    assertThat(createdMin.organisationAddressId).isGreaterThan(0)
    val createdMax = organisationAddressRepository.saveAndFlush(withAllFields)
    assertThat(createdMax.organisationAddressId).isGreaterThan(0)
  }

  @Test
  fun `can create organisation address phones`() {
    val org = aNewOrganisation()
    val phone = organisationPhoneRepository.saveAndFlush(
      OrganisationPhoneEntity(
        organisationPhoneId = 0,
        organisationId = org.id(),
        phoneType = "MOB",
        phoneNumber = "123",
        extNumber = null,
        createdBy = "CREATED",
        createdTime = LocalDateTime.now().minusMinutes(20),
        updatedBy = null,
        updatedTime = null,
      ),
    )
    val address = organisationAddressRepository.saveAndFlush(
      OrganisationAddressEntity(
        organisationAddressId = 0,
        organisationId = org.id(),
        addressType = null,
        primaryAddress = false,
        mailAddress = false,
        serviceAddress = false,
        noFixedAddress = false,
        flat = null,
        property = null,
        street = null,
        area = null,
        cityCode = null,
        countyCode = null,
        postCode = null,
        countryCode = null,
        specialNeedsCode = null,
        contactPersonName = null,
        businessHours = null,
        comments = null,
        startDate = null,
        endDate = null,
        createdBy = "CREATED",
        createdTime = LocalDateTime.now().minusMinutes(20),
        updatedBy = null,
        updatedTime = null,
      ),
    )
    val withMinimalFields = OrganisationAddressPhoneEntity(
      organisationAddressPhoneId = 0,
      organisationId = org.id(),
      organisationPhoneId = phone.organisationPhoneId,
      organisationAddressId = address.organisationAddressId,
      createdBy = "CREATED",
      createdTime = LocalDateTime.now().minusMinutes(20),
      updatedBy = null,
      updatedTime = null,
    )
    val withAllFields = OrganisationAddressPhoneEntity(
      organisationAddressPhoneId = 0,
      organisationId = org.id(),
      organisationPhoneId = phone.organisationPhoneId,
      organisationAddressId = address.organisationAddressId,
      createdBy = "CREATED",
      createdTime = LocalDateTime.now().minusMinutes(20),
      updatedBy = "UPDATED",
      updatedTime = LocalDateTime.now().plusMinutes(20),
    )
    val createdMin = organisationAddressPhoneRepository.saveAndFlush(withMinimalFields)
    assertThat(createdMin.organisationAddressPhoneId).isGreaterThan(0)
    val createdMax = organisationAddressPhoneRepository.saveAndFlush(withAllFields)
    assertThat(createdMax.organisationAddressPhoneId).isGreaterThan(0)
  }

  @Test
  fun `can create employments and no referential integrity is required`() {
    val withMinimalFields = EmploymentEntity(
      employmentId = 0,
      organisationId = 99,
      contactId = 77,
      active = false,
      createdBy = "CREATED",
      createdTime = LocalDateTime.now().minusMinutes(20),
      updatedBy = null,
      updatedTime = null,
    )
    val withAllFields = EmploymentEntity(
      employmentId = 0,
      organisationId = 456,
      contactId = 789,
      active = true,
      createdBy = "CREATED",
      createdTime = LocalDateTime.now().minusMinutes(20),
      updatedBy = "UPDATED",
      updatedTime = LocalDateTime.now().plusMinutes(20),
    )
    val createdMin = employmentRepository.saveAndFlush(withMinimalFields)
    assertThat(createdMin.employmentId).isGreaterThan(0)
    val createdMax = employmentRepository.saveAndFlush(withAllFields)
    assertThat(createdMax.employmentId).isGreaterThan(0)
  }

  private fun aNewOrganisation(): OrganisationEntity {
    val entity = OrganisationEntity(
      null,
      organisationName = "Name",
      programmeNumber = null,
      vatNumber = null,
      caseloadId = null,
      comments = null,
      active = true,
      deactivatedDate = null,
      createdBy = "Created by",
      createdTime = LocalDateTime.now(),
      updatedBy = null,
      updatedTime = null,
    )
    return organisationRepository.saveAndFlush(entity)
  }
}