package uk.gov.justice.digital.hmpps.hmppscontactsapi.integration.resource

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EnumSource
import org.junit.jupiter.params.provider.ValueSource
import org.springframework.http.MediaType
import uk.gov.justice.digital.hmpps.hmppscontactsapi.integration.H2IntegrationTestBase
import uk.gov.justice.digital.hmpps.hmppscontactsapi.model.request.CreateContactRequest
import uk.gov.justice.digital.hmpps.hmppscontactsapi.model.request.EstimatedIsOverEighteen
import java.time.LocalDate
import java.time.LocalDateTime

class GetContactByIdIntegrationTest : H2IntegrationTestBase() {

  @Test
  fun `should return unauthorized if no token`() {
    webTestClient.get()
      .uri("/contact/123456")
      .accept(MediaType.APPLICATION_JSON)
      .exchange()
      .expectStatus()
      .isUnauthorized
  }

  @Test
  fun `should return forbidden if no role`() {
    webTestClient.get()
      .uri("/contact/123456")
      .accept(MediaType.APPLICATION_JSON)
      .headers(setAuthorisation())
      .exchange()
      .expectStatus()
      .isForbidden
  }

  @Test
  fun `should return forbidden if wrong role`() {
    webTestClient.get()
      .uri("/contact/123456")
      .headers(setAuthorisation(roles = listOf("ROLE_WRONG")))
      .exchange()
      .expectStatus()
      .isForbidden
  }

  @Test
  fun `should return 404 if the contact doesn't exist`() {
    webTestClient.get()
      .uri("/contact/123456")
      .accept(MediaType.APPLICATION_JSON)
      .headers(setAuthorisation(roles = listOf("ROLE_CONTACTS_ADMIN")))
      .exchange()
      .expectStatus()
      .isNotFound
  }

  @Test
  fun `should get the contact with all fields`() {
    val contact = testAPIClient.getContact(1, "ROLE_CONTACTS_ADMIN")

    with(contact) {
      assertThat(id).isEqualTo(1)
      assertThat(title).isEqualTo("MR")
      assertThat(lastName).isEqualTo("Last")
      assertThat(firstName).isEqualTo("Jack")
      assertThat(middleNames).isEqualTo("Middle")
      assertThat(dateOfBirth).isEqualTo(LocalDate.of(2000, 11, 21))
      assertThat(estimatedIsOverEighteen).isNull()
      assertThat(isDeceased).isFalse()
      assertThat(deceasedDate).isNull()
      assertThat(createdBy).isEqualTo("TIM")
      assertThat(createdTime).isNotNull()
      assertThat(contact.addresses).hasSize(2)
    }
  }

  @Test
  fun `should get the contact with addresses`() {
    val contact = testAPIClient.getContact(1, "ROLE_CONTACTS_ADMIN")

    with(contact) {
      assertThat(id).isEqualTo(1)
      assertThat(id).isEqualTo(1)

      with(contact.addresses[0]) {
        assertThat(contactAddressId).isEqualTo(1)
        assertThat(contactId).isEqualTo(1)
        assertThat(addressType).isEqualTo("HOME")
        assertThat(addressTypeDescription).isEqualTo("Home address")
        assertThat(primaryAddress).isEqualTo(true)
        assertThat(flat).isNull()
        assertThat(property).isEqualTo("24")
        assertThat(street).isEqualTo("Acacia Avenue")
        assertThat(area).isEqualTo("Bunting")
        assertThat(cityCode).isEqualTo("25343")
        assertThat(cityDescription).isEqualTo("Sheffield")
        assertThat(countyCode).isEqualTo("S.YORKSHIRE")
        assertThat(countyDescription).isEqualTo("South Yorkshire")
        assertThat(postcode).isEqualTo("S2 3LK")
        assertThat(countryCode).isEqualTo("ENG")
        assertThat(countryDescription).isEqualTo("England")
        assertThat(mailFlag).isFalse()
        assertThat(noFixedAddress).isFalse()
        assertThat(comments).isEqualTo("Some comments")
        assertThat(createdBy).isEqualTo("TIM")
        assertThat(createdTime).isNotNull()
      }
      with(contact.addresses[1]) {
        assertThat(contactAddressId).isEqualTo(2)
        assertThat(contactId).isEqualTo(1)
        assertThat(addressType).isEqualTo("WORK")
        assertThat(addressTypeDescription).isEqualTo("Work address")
        assertThat(primaryAddress).isEqualTo(false)
        assertThat(flat).isEqualTo("Flat 1")
        assertThat(verified).isTrue()
        assertThat(verifiedBy).isEqualTo("BOB")
        assertThat(verifiedTime).isEqualTo(LocalDateTime.of(2020, 1, 1, 10, 30, 0))
        assertThat(mailFlag).isTrue()
        assertThat(noFixedAddress).isTrue()
        assertThat(startDate).isEqualTo(LocalDate.of(2020, 1, 2))
        assertThat(endDate).isEqualTo(LocalDate.of(2029, 3, 4))
      }
    }
  }

  @Test
  fun `should get the contact with phone numbers`() {
    val contact = testAPIClient.getContact(1, "ROLE_CONTACTS_ADMIN")

    with(contact) {
      assertThat(id).isEqualTo(1)
      assertThat(contact.phoneNumbers).hasSize(2)
      with(contact.phoneNumbers[0]) {
        assertThat(contactPhoneId).isEqualTo(1)
        assertThat(contactId).isEqualTo(1)
        assertThat(phoneType).isEqualTo("MOB")
        assertThat(phoneTypeDescription).isEqualTo("Mobile")
        assertThat(phoneNumber).isEqualTo("07878 111111")
        assertThat(extNumber).isNull()
        assertThat(createdBy).isEqualTo("TIM")
      }
      with(contact.phoneNumbers[1]) {
        assertThat(contactPhoneId).isEqualTo(2)
        assertThat(contactId).isEqualTo(1)
        assertThat(phoneType).isEqualTo("HOME")
        assertThat(phoneTypeDescription).isEqualTo("Home")
        assertThat(phoneNumber).isEqualTo("01111 777777")
        assertThat(extNumber).isEqualTo("+0123")
        assertThat(createdBy).isEqualTo("JAMES")
      }

      assertThat(contact.addresses).hasSize(2)
      assertThat(contact.addresses[0].phoneNumbers).hasSize(1)
      assertThat(contact.addresses[0].phoneNumbers[0].contactPhoneId).isEqualTo(2)
      assertThat(contact.addresses[1].phoneNumbers).isEmpty()
    }
  }

  @Test
  fun `should get the contact with emails`() {
    val contact = testAPIClient.getContact(3, "ROLE_CONTACTS_ADMIN")

    with(contact) {
      assertThat(id).isEqualTo(3)
      assertThat(contact.emailAddresses).hasSize(2)
      with(contact.emailAddresses[0]) {
        assertThat(contactEmailId).isEqualTo(3)
        assertThat(contactId).isEqualTo(3)
        assertThat(emailAddress).isEqualTo("mrs.last@example.com")
        assertThat(createdBy).isEqualTo("TIM")
      }
      with(contact.emailAddresses[1]) {
        assertThat(contactEmailId).isEqualTo(4)
        assertThat(contactId).isEqualTo(3)
        assertThat(emailAddress).isEqualTo("work@example.com")
        assertThat(createdBy).isEqualTo("JAMES")
      }
    }
  }

  @Test
  fun `should get the contact with identities`() {
    val contact = testAPIClient.getContact(1, "ROLE_CONTACTS_ADMIN")

    with(contact) {
      assertThat(id).isEqualTo(1)
      assertThat(contact.identities).hasSize(1)
      with(contact.identities[0]) {
        assertThat(contactIdentityId).isEqualTo(1)
        assertThat(contactId).isEqualTo(1)
        assertThat(identityType).isEqualTo("DL")
        assertThat(identityTypeDescription).isEqualTo("Driving Licence")
        assertThat(identityValue).isEqualTo("LAST-87736799M")
        assertThat(issuingAuthority).isEqualTo("DVLA")
        assertThat(createdBy).isEqualTo("TIM")
        assertThat(createdTime).isNotNull()
      }
    }
  }

  @Test
  fun `should get deceased contacts`() {
    val contact = testAPIClient.getContact(19, "ROLE_CONTACTS_ADMIN")

    with(contact) {
      assertThat(id).isEqualTo(19)
      assertThat(title).isNull()
      assertThat(lastName).isEqualTo("Dead")
      assertThat(firstName).isEqualTo("Currently")
      assertThat(middleNames).isNull()
      assertThat(dateOfBirth).isEqualTo(LocalDate.of(1980, 1, 1))
      assertThat(estimatedIsOverEighteen).isNull()
      assertThat(isDeceased).isTrue()
      assertThat(deceasedDate).isEqualTo(LocalDate.of(2000, 1, 1))
      assertThat(createdBy).isEqualTo("TIM")
      assertThat(createdTime).isNotNull()
    }
  }

  @Test
  fun `should get contacts with language details`() {
    val contact = testAPIClient.getContact(20, "ROLE_CONTACTS_ADMIN")

    with(contact) {
      assertThat(id).isEqualTo(20)
      assertThat(languageCode).isEqualTo("FRE-FRA")
      assertThat(languageDescription).isEqualTo("French")
      assertThat(interpreterRequired).isTrue()
    }
  }

  @Test
  fun `should get contacts with gender details`() {
    val contact = testAPIClient.getContact(16, "ROLE_CONTACTS_ADMIN")

    with(contact) {
      assertThat(id).isEqualTo(16)
      assertThat(gender).isEqualTo("F")
      assertThat(genderDescription).isEqualTo("Female")
    }
  }

  @Test
  fun `should get contacts with domestic status`() {
    val contact = testAPIClient.getContact(1, "ROLE_CONTACTS_ADMIN")

    with(contact) {
      assertThat(id).isEqualTo(1)
      assertThat(domesticStatusCode).isEqualTo("M")
      assertThat(domesticStatusDescription).isEqualTo("Married or in civil partnership")
    }
  }

  @ParameterizedTest
  @ValueSource(strings = ["ROLE_CONTACTS_ADMIN", "ROLE_CONTACTS__R", "ROLE_CONTACTS__RW"])
  fun `should get contacts with staff flag`(role: String) {
    val contact = testAPIClient.getContact(1, role)

    with(contact) {
      assertThat(id).isEqualTo(1)
      assertThat(isStaff).isTrue()
    }
  }

  @ParameterizedTest
  @EnumSource(EstimatedIsOverEighteen::class)
  fun `should return is over eighteen when DOB is not known`(estimatedIsOverEighteen: EstimatedIsOverEighteen) {
    val createdContactId = testAPIClient.createAContact(
      CreateContactRequest(
        firstName = "First",
        lastName = "Last",
        dateOfBirth = null,
        estimatedIsOverEighteen = estimatedIsOverEighteen,
        createdBy = "USER1",
      ),
      "ROLE_CONTACTS_ADMIN",
    ).id
    val contact = testAPIClient.getContact(createdContactId, "ROLE_CONTACTS_ADMIN")
    assertThat(contact.estimatedIsOverEighteen).isEqualTo(estimatedIsOverEighteen)
  }
}
