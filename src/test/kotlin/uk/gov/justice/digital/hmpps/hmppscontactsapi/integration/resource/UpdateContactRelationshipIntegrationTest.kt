package uk.gov.justice.digital.hmpps.hmppscontactsapi.integration.resource

import org.apache.commons.lang3.RandomStringUtils
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import org.openapitools.jackson.nullable.JsonNullable
import org.springframework.http.MediaType
import org.springframework.web.util.UriComponentsBuilder
import uk.gov.justice.digital.hmpps.hmppscontactsapi.client.prisonersearchapi.model.ErrorResponse
import uk.gov.justice.digital.hmpps.hmppscontactsapi.integration.H2IntegrationTestBase
import uk.gov.justice.digital.hmpps.hmppscontactsapi.model.request.ContactRelationship
import uk.gov.justice.digital.hmpps.hmppscontactsapi.model.request.CreateContactRequest
import uk.gov.justice.digital.hmpps.hmppscontactsapi.model.request.UpdateRelationshipRequest
import uk.gov.justice.digital.hmpps.hmppscontactsapi.model.response.PrisonerContactSummary

class UpdateContactRelationshipIntegrationTest : H2IntegrationTestBase() {

  @ParameterizedTest
  @CsvSource(
    value = [
      "Unsupported relationship type null.;{\"relationshipCode\": null,  \"updatedBy\": \"Admin\"}",
      "Unsupported emergency contact null.;{\"isEmergencyContact\": null,  \"updatedBy\": \"Admin\"}",
      "Unsupported next of kin null.;{\"isNextOfKin\": null,  \"updatedBy\": \"Admin\"}",
      "Unsupported relationship status null.;{\"isRelationshipActive\": null,  \"updatedBy\": \"Admin\"}",
    ],
    delimiter = ';',
  )
  fun `should return bad request when optional fields set with a null value `(expectedMessage: String, relationShipJson: String) {
    val prisonerNumber = getRandomPrisonerCode()
    stubPrisonSearchWithResponse(prisonerNumber)
    val prisonerContact = cretePrisonerContact(prisonerNumber)

    val prisonerContactId = prisonerContact.prisonerContactId
    val contactId = prisonerContact.contactId

    val errors = webTestClient.patch()
      .uri("/contact/$contactId/relationship/$prisonerContactId")
      .accept(MediaType.APPLICATION_JSON)
      .contentType(MediaType.APPLICATION_JSON)
      .headers(setAuthorisation(roles = listOf("ROLE_CONTACTS_ADMIN")))
      .bodyValue(relationShipJson)
      .exchange()
      .expectStatus()
      .isBadRequest
      .expectHeader().contentType(MediaType.APPLICATION_JSON)
      .expectBody(ErrorResponse::class.java)
      .returnResult().responseBody!!

    assertThat(errors.userMessage).isEqualTo("Validation failure: $expectedMessage")
  }

  @Test
  fun `should return a 404 if the prisoner can not be found`() {
    val prisonerNumber = "A1234AB"
    stubPrisonSearchWithNotFoundResponse(prisonerNumber)

    val request = UpdateRelationshipRequest(
      relationshipCode = JsonNullable.of("FRI"),
      isNextOfKin = JsonNullable.of(true),
      isEmergencyContact = JsonNullable.of(true),
      isRelationshipActive = JsonNullable.of(true),
      comments = JsonNullable.of("FRI"),
      updatedBy = "Admin",
    )

    val uri = UriComponentsBuilder.fromPath("/contact/123456/relationship/1")
      .build()
      .toUri()
    val errors = testAPIClient.getBadResponseErrorsWithPatch(request, uri)

    assertThat(errors.userMessage)
      .isEqualTo("Validation failure: The relationship between the prisoner and the contact is not associated with this contact ID: 123456 and prisoner contact ID: 1.")
  }

  @Test
  fun `should update the contact relationship with relationship code fields`() {
    val prisonerNumber = getRandomPrisonerCode()
    stubPrisonSearchWithResponse(prisonerNumber)
    val prisonerContact = cretePrisonerContact(prisonerNumber)

    val updateRequest = UpdateRelationshipRequest(
      relationshipCode = JsonNullable.of("SIS"),
      updatedBy = "Admin",
    )

    val prisonerContactId = prisonerContact.prisonerContactId
    val contactId = prisonerContact.contactId

    testAPIClient.updateRelationship(contactId, prisonerContactId, updateRequest)

    val updatedPrisonerContacts = testAPIClient.getPrisonerContacts(prisonerNumber).content
    assertThat(updatedPrisonerContacts).hasSize(1)
    assertThat(updatedPrisonerContacts[0].relationshipCode).isEqualTo("SIS")
  }

  @Test
  fun `should update the contact relationship with next of kin fields`() {
    val prisonerNumber = getRandomPrisonerCode()
    stubPrisonSearchWithResponse(prisonerNumber)
    val prisonerContact = cretePrisonerContact(prisonerNumber)

    val updateRequest = UpdateRelationshipRequest(
      isNextOfKin = JsonNullable.of(true),
      updatedBy = "Admin",
    )

    val prisonerContactId = prisonerContact.prisonerContactId
    val contactId = prisonerContact.contactId

    testAPIClient.updateRelationship(contactId, prisonerContactId, updateRequest)

    val updatedPrisonerContacts = testAPIClient.getPrisonerContacts(prisonerNumber).content
    assertThat(updatedPrisonerContacts).hasSize(1)
    assertThat(updatedPrisonerContacts[0].nextOfKin).isTrue
  }

  @Test
  fun `should update the contact relationship with emergency contact fields`() {
    val prisonerNumber = getRandomPrisonerCode()
    stubPrisonSearchWithResponse(prisonerNumber)
    val prisonerContact = cretePrisonerContact(prisonerNumber)

    val updateRequest = UpdateRelationshipRequest(
      isEmergencyContact = JsonNullable.of(true),
      updatedBy = "Admin",
    )

    val prisonerContactId = prisonerContact.prisonerContactId
    val contactId = prisonerContact.contactId

    testAPIClient.updateRelationship(contactId, prisonerContactId, updateRequest)

    val updatedPrisonerContacts = testAPIClient.getPrisonerContacts(prisonerNumber).content
    assertThat(updatedPrisonerContacts).hasSize(1)
    assertThat(updatedPrisonerContacts[0].emergencyContact).isTrue
  }

  @Test
  fun `should update the contact relationship with relationship active fields`() {
    val prisonerNumber = getRandomPrisonerCode()
    stubPrisonSearchWithResponse(prisonerNumber)
    val prisonerContact = cretePrisonerContact(prisonerNumber)

    val updateRequest = UpdateRelationshipRequest(
      isRelationshipActive = JsonNullable.of(true),
      updatedBy = "Admin",
    )

    val prisonerContactId = prisonerContact.prisonerContactId
    val contactId = prisonerContact.contactId

    testAPIClient.updateRelationship(contactId, prisonerContactId, updateRequest)

    val updatedPrisonerContacts = testAPIClient.getPrisonerContacts(prisonerNumber).content
    assertThat(updatedPrisonerContacts).hasSize(1)
    assertThat(updatedPrisonerContacts[0].isRelationshipActive).isTrue
  }

  @Test
  fun `should update the contact relationship with comment fields`() {
    val prisonerNumber = getRandomPrisonerCode()
    stubPrisonSearchWithResponse(prisonerNumber)
    val prisonerContact = cretePrisonerContact(prisonerNumber)

    val updateRequest = UpdateRelationshipRequest(
      comments = JsonNullable.of("New comment"),
      updatedBy = "Admin",
    )

    val prisonerContactId = prisonerContact.prisonerContactId
    val contactId = prisonerContact.contactId

    testAPIClient.updateRelationship(contactId, prisonerContactId, updateRequest)

    val updatedPrisonerContacts = testAPIClient.getPrisonerContacts(prisonerNumber).content
    assertThat(updatedPrisonerContacts).hasSize(1)
    assertThat(updatedPrisonerContacts[0].comments).isEqualTo("New comment")
  }

  @Test
  fun `should update the contact relationship with minimal fields`() {
    val prisonerNumber = getRandomPrisonerCode()
    stubPrisonSearchWithResponse(prisonerNumber)
    val prisonerContact = cretePrisonerContact(prisonerNumber)

    val updateRequest = UpdateRelationshipRequest(
      updatedBy = "Admin",
    )

    val prisonerContactId = prisonerContact.prisonerContactId
    val contactId = prisonerContact.contactId

    testAPIClient.updateRelationship(contactId, prisonerContactId, updateRequest)

    val updatedPrisonerContacts = testAPIClient.getPrisonerContacts(prisonerNumber).content
    assertThat(updatedPrisonerContacts).hasSize(1)
  }

  @Test
  fun `should update the contact relationship with all fields`() {
    val prisonerNumber = getRandomPrisonerCode()
    stubPrisonSearchWithResponse(prisonerNumber)
    val prisonerContact = cretePrisonerContact(prisonerNumber)

    val updateRequest = UpdateRelationshipRequest(
      relationshipCode = JsonNullable.of("FRI"),
      isNextOfKin = JsonNullable.of(true),
      isEmergencyContact = JsonNullable.of(true),
      isRelationshipActive = JsonNullable.of(true),
      comments = JsonNullable.of("comments added"),
      updatedBy = "Admin",
    )

    val prisonerContactId = prisonerContact.prisonerContactId
    val contactId = prisonerContact.contactId

    testAPIClient.updateRelationship(contactId, prisonerContactId, updateRequest)

    val updatedPrisonerContacts = testAPIClient.getPrisonerContacts(prisonerNumber).content

    assertThat(updatedPrisonerContacts).hasSize(1)
    assertUpdatedPrisonerContactEquals(updatedPrisonerContacts[0], updateRequest)
  }

  private fun assertUpdatedPrisonerContactEquals(prisonerContact: PrisonerContactSummary, relationship: UpdateRelationshipRequest) {
    with(prisonerContact) {
      assertThat(relationshipCode).isEqualTo(relationship.relationshipCode.get())
      assertThat(nextOfKin).isEqualTo(relationship.isNextOfKin.get())
      assertThat(emergencyContact).isEqualTo(relationship.isEmergencyContact.get())
      assertThat(comments).isEqualTo(relationship.comments.get())
    }
  }

  private fun getRandomPrisonerCode(): String {
    val letters = ('A'..'Z')
    val numbers = ('0'..'9')

    val firstLetter = letters.random()
    val lastLetter = letters.random()
    val numberPart = (1..4).map { numbers.random() }.joinToString("")

    return "$firstLetter$numberPart$lastLetter"
  }

  private fun cretePrisonerContact(prisonerNumber: String = "A1234AB"): PrisonerContactSummary {
    val requestedRelationship = ContactRelationship(
      prisonerNumber = prisonerNumber,
      relationshipCode = "BRO",
      isNextOfKin = false,
      isEmergencyContact = false,
      comments = null,
    )
    val request = CreateContactRequest(
      lastName = RandomStringUtils.random(35),
      firstName = "a new guy",
      createdBy = "created",
      relationship = requestedRelationship,
    )

    testAPIClient.createAContact(request)

    val prisonerContacts = testAPIClient.getPrisonerContacts(prisonerNumber).content
    assertThat(prisonerContacts).hasSize(1)
    val prisonerContact = prisonerContacts[0]
    assertExistingContact(prisonerContact, requestedRelationship)
    return prisonerContact
  }

  private fun assertExistingContact(
    prisonerContact: PrisonerContactSummary,
    relationship: ContactRelationship,
  ) {
    with(prisonerContact) {
      assertThat(relationshipCode).isEqualTo(relationship.relationshipCode)
      assertThat(nextOfKin).isEqualTo(relationship.isNextOfKin)
      assertThat(emergencyContact).isEqualTo(relationship.isEmergencyContact)
      assertThat(comments).isEqualTo(relationship.comments)
    }
  }
}
