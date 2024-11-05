package uk.gov.justice.digital.hmpps.hmppscontactsapi.integration.resource.migrate

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.groups.Tuple
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import uk.gov.justice.digital.hmpps.hmppscontactsapi.integration.H2IntegrationTestBase
import uk.gov.justice.digital.hmpps.hmppscontactsapi.model.request.migrate.CodedValue
import uk.gov.justice.digital.hmpps.hmppscontactsapi.model.request.migrate.Corporate
import uk.gov.justice.digital.hmpps.hmppscontactsapi.model.request.migrate.MigrateAddress
import uk.gov.justice.digital.hmpps.hmppscontactsapi.model.request.migrate.MigrateContactRequest
import uk.gov.justice.digital.hmpps.hmppscontactsapi.model.request.migrate.MigrateEmailAddress
import uk.gov.justice.digital.hmpps.hmppscontactsapi.model.request.migrate.MigrateEmployment
import uk.gov.justice.digital.hmpps.hmppscontactsapi.model.request.migrate.MigrateIdentifier
import uk.gov.justice.digital.hmpps.hmppscontactsapi.model.request.migrate.MigratePhoneNumber
import uk.gov.justice.digital.hmpps.hmppscontactsapi.model.request.migrate.MigrateRestriction
import uk.gov.justice.digital.hmpps.hmppscontactsapi.model.response.migrate.ElementType
import uk.gov.justice.digital.hmpps.hmppscontactsapi.repository.ContactPhoneRepository
import uk.gov.justice.digital.hmpps.hmppscontactsapi.repository.ContactRepository
import java.time.LocalDate
import java.time.LocalDateTime

class MigrateContactIntegrationTest : H2IntegrationTestBase() {
  @Autowired
  private lateinit var contactRepository: ContactRepository

  @Autowired
  private lateinit var contactPhoneRepository: ContactPhoneRepository

  private val aUsername = "XXX"
  private val aDateTime = LocalDateTime.of(2024, 1, 1, 13, 0)

  @Nested
  inner class MigrateContactTests {

    @BeforeEach
    fun initialiseData() {
    }

    @Test
    fun `should return unauthorized if no token provided`() {
      webTestClient.post()
        .uri("/migrate/contact")
        .accept(MediaType.APPLICATION_JSON)
        .contentType(MediaType.APPLICATION_JSON)
        .bodyValue(basicMigrationRequest())
        .exchange()
        .expectStatus()
        .isUnauthorized
    }

    @Test
    fun `should return forbidden without an authorised role on the token`() {
      webTestClient.post()
        .uri("/migrate/contact")
        .accept(MediaType.APPLICATION_JSON)
        .contentType(MediaType.APPLICATION_JSON)
        .bodyValue(basicMigrationRequest())
        .headers(setAuthorisation(roles = listOf("ROLE_WRONG")))
        .exchange()
        .expectStatus()
        .isForbidden
    }

    @Test
    fun `should migrate a basic contact`() {
      val request = basicMigrationRequest()
      val countContactsBefore = contactRepository.count()

      val result = testAPIClient.migrateAContact(request)

      with(result) {
        with(contact) {
          assertThat(elementType).isEqualTo(ElementType.CONTACT)
          assertThat(nomisId).isEqualTo(request.personId)
          assertThat(dpsId).isGreaterThan(0L)
        }
        assertThat(lastName).isEqualTo(request.lastName)
        assertThat(dateOfBirth).isEqualTo(request.dateOfBirth)
      }

      assertThat(contactRepository.count()).isEqualTo(countContactsBefore + 1)
    }

    @Test
    fun `should migrate a contact with addresses, phones, emails, restrictions and identifiers`() {
      val request = basicMigrationRequest().copy(
        addresses = addresses(),
        phoneNumbers = phoneNumbers(),
        emailAddresses = emails(),
        identifiers = identifiers(),
        restrictions = restrictions(),
      )

      val result = testAPIClient.migrateAContact(request)

      with(result) {
        assertThat(contact.elementType).isEqualTo(ElementType.CONTACT)
        assertThat(contact.nomisId).isEqualTo(request.personId)
        assertThat(contact.dpsId).isGreaterThan(0L)

        assertThat(phoneNumbers).hasSize(2)
        assertThat(phoneNumbers).extracting("elementType", "nomisId")
          .containsAll(
            listOf(
              Tuple(ElementType.PHONE, 101L),
              Tuple(ElementType.PHONE, 102L),
            ),
          )

        assertThat(addresses).hasSize(2)
        assertThat(addresses[0].address).extracting("elementType", "nomisId").contains(ElementType.ADDRESS, 201L)
        assertThat(addresses[1].address).extracting("elementType", "nomisId").contains(ElementType.ADDRESS, 202L)

        assertThat(emailAddresses).hasSize(2)
        assertThat(emailAddresses).extracting("elementType", "nomisId")
          .containsAll(listOf(Tuple(ElementType.EMAIL, 301L), Tuple(ElementType.EMAIL, 302L)))

        assertThat(restrictions).hasSize(2)
        assertThat(restrictions).extracting("elementType", "nomisId")
          .containsAll(listOf(Tuple(ElementType.RESTRICTION, 401L), Tuple(ElementType.RESTRICTION, 402L)))

        assertThat(identities).hasSize(2)
        assertThat(identities).extracting("elementType", "nomisId")
          .containsAll(listOf(Tuple(ElementType.IDENTITY, 601L), Tuple(ElementType.IDENTITY, 602L)))
      }
    }

    @Test
    fun `should migrate a contact with addresses with linked phone numbers`() {
      val phoneCount = contactPhoneRepository.count()

      val request = basicMigrationRequest().copy(
        addresses = addressesWithPhones(),
      )

      val result = testAPIClient.migrateAContact(request)

      with(result) {
        assertThat(phoneNumbers).hasSize(0)
        assertThat(addresses).hasSize(1)
        assertThat(addresses[0].address).extracting("elementType", "nomisId").contains(ElementType.ADDRESS, 201L)
        assertThat(addresses[0].phones).hasSize(2)
        assertThat(addresses[0].phones).extracting("elementType", "nomisId")
          .containsAll(
            listOf(
              Tuple(ElementType.ADDRESS_PHONE, 101L),
              Tuple(ElementType.ADDRESS_PHONE, 102L),
            ),
          )
      }

      assertThat(contactPhoneRepository.count()).isEqualTo(phoneCount + 2)
    }

    @Test
    fun `should migrate a contact with relationships and prisoner restrictions`() {
      // TODO: next PR
    }

    @Test
    fun `should fail due to missing reference data`() {
      // TODO: next PR
    }

    @Test
    fun `should fail with request validation errors`() {
      // TODO: next PR
    }
  }

  private fun basicMigrationRequest(
    personId: Long = 1L,
    lastName: String = "Smith",
    firstName: String = "John",
    dateOfBirth: LocalDate = LocalDate.of(2001, 1, 1),
  ) = MigrateContactRequest(
    personId = personId,
    title = CodedValue("MR", "Mr"),
    lastName = lastName,
    firstName = firstName,
    dateOfBirth = dateOfBirth,
    gender = CodedValue("Male", "Male"),
  ).also {
    it.createUsername = aUsername
    it.createDateTime = aDateTime
    it.modifyUsername = aUsername
    it.modifyDateTime = aDateTime
  }

  private fun phoneNumbers() =
    listOf(
      MigratePhoneNumber(phoneId = 101L, number = "11111", extension = "1", type = CodedValue("HOME", "Home")),
      MigratePhoneNumber(phoneId = 102L, number = "22222", extension = "2", type = CodedValue("WORK", "Work")),
    )

  private fun addresses() =
    listOf(
      MigrateAddress(
        addressId = 201L,
        type = CodedValue("HOME", "Home"),
        premise = "10",
        street = "Dublin Road",
        postCode = "D1 1DN",
        primaryAddress = true,
      ),
      MigrateAddress(
        addressId = 202L,
        type = CodedValue("WORK", "Work"),
        premise = "11",
        street = "Dublin Road",
        postCode = "D1 2DN",
      ),
    )

  private fun addressesWithPhones() =
    listOf(
      MigrateAddress(
        addressId = 201L,
        type = CodedValue("HOME", "Home"),
        premise = "10",
        street = "Dublin Road",
        postCode = "D1 1DN",
        primaryAddress = true,
        phoneNumbers = listOf(
          MigratePhoneNumber(phoneId = 101L, number = "11111", extension = "1", type = CodedValue("HOME", "Home")),
          MigratePhoneNumber(phoneId = 102L, number = "22222", extension = "2", type = CodedValue("WORK", "Work")),
        ),
      ),
    )

  private fun emails() =
    listOf(
      MigrateEmailAddress(emailAddressId = 301L, email = "a@.com"),
      MigrateEmailAddress(emailAddressId = 302L, email = "b@b.com"),
    )

  private fun restrictions() =
    listOf(
      MigrateRestriction(
        id = 401L,
        type = CodedValue("ESCORTED", "Desc"),
        comment = "Active",
        staffUsername = aUsername,
        effectiveDate = LocalDate.now(),
        expiryDate = LocalDate.now().plusDays(30),
      ),
      MigrateRestriction(
        id = 402L,
        type = CodedValue("CHILDREN", "Desc"),
        comment = "Expired",
        staffUsername = aUsername,
        effectiveDate = LocalDate.now().minusDays(30),
        expiryDate = LocalDate.now().minusDays(1),
      ),
    )

  private fun identifiers() =
    listOf(
      MigrateIdentifier(sequence = 601L, type = CodedValue("DRIVING_LIC", "Driving Licence"), identifier = "DL1", issuedAuthority = "DVLA"),
      MigrateIdentifier(sequence = 602L, type = CodedValue("PASSPORT", "Passport"), identifier = "PASS1", issuedAuthority = "UKBORDER"),
    )

  private fun employments() =
    listOf(
      MigrateEmployment(sequence = 501L, corporate = Corporate(id = 123L, name = "Big Blue"), active = true),
      MigrateEmployment(sequence = 502L, corporate = Corporate(id = 124L, name = "Big Yellow"), active = false),
    )
}
