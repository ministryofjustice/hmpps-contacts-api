package uk.gov.justice.digital.hmpps.hmppscontactsapi.integration.resource

import org.apache.commons.lang3.RandomStringUtils
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import org.junit.jupiter.params.provider.ValueSource
import org.springframework.http.MediaType
import uk.gov.justice.digital.hmpps.hmppscontactsapi.integration.H2IntegrationTestBase
import uk.gov.justice.digital.hmpps.hmppscontactsapi.model.request.AddContactRelationshipRequest
import uk.gov.justice.digital.hmpps.hmppscontactsapi.model.request.ContactRelationship
import uk.gov.justice.digital.hmpps.hmppscontactsapi.model.request.CreateContactRequest
import uk.gov.justice.digital.hmpps.hmppscontactsapi.model.request.Relationship
import uk.gov.justice.digital.hmpps.hmppscontactsapi.model.request.RelationshipType
import uk.gov.justice.digital.hmpps.hmppscontactsapi.model.response.ContactDetails
import uk.gov.justice.digital.hmpps.hmppscontactsapi.service.events.OutboundEvent
import uk.gov.justice.digital.hmpps.hmppscontactsapi.service.events.PersonReference
import uk.gov.justice.digital.hmpps.hmppscontactsapi.service.events.PrisonerContactInfo
import uk.gov.justice.digital.hmpps.hmppscontactsapi.service.events.Source
import uk.gov.justice.hmpps.kotlin.common.ErrorResponse

class AddContactRelationshipIntegrationTest : H2IntegrationTestBase() {

  private lateinit var contact: ContactDetails

  @BeforeEach
  fun setUp() {
    contact = testAPIClient.createAContact(
      CreateContactRequest(
        firstName = RandomStringUtils.secure().nextAlphabetic(10),
        lastName = RandomStringUtils.secure().nextAlphabetic(10),
        createdBy = "USER",
      ),
    )
  }

  @Test
  fun `should return unauthorized if no token`() {
    webTestClient.post()
      .uri("/prisoner-contact")
      .accept(MediaType.APPLICATION_JSON)
      .contentType(MediaType.APPLICATION_JSON)
      .bodyValue(aMinimalRequest())
      .exchange()
      .expectStatus()
      .isUnauthorized
  }

  @Test
  fun `should return forbidden if no role`() {
    webTestClient.post()
      .uri("/prisoner-contact")
      .accept(MediaType.APPLICATION_JSON)
      .contentType(MediaType.APPLICATION_JSON)
      .bodyValue(aMinimalRequest())
      .headers(setAuthorisation())
      .exchange()
      .expectStatus()
      .isForbidden
  }

  @Test
  fun `should return forbidden if wrong role`() {
    webTestClient.post()
      .uri("/prisoner-contact")
      .accept(MediaType.APPLICATION_JSON)
      .contentType(MediaType.APPLICATION_JSON)
      .bodyValue(aMinimalRequest())
      .headers(setAuthorisation(roles = listOf("ROLE_CONTACTS__R")))
      .exchange()
      .expectStatus()
      .isForbidden
  }

  @ParameterizedTest
  @CsvSource(
    value = [
      "relationship must not be null;{\"relationship\": null, \"createdBy\": \"USER\"}",
      "relationship must not be null;{\"createdBy\": \"USER\"}",
      "createdBy must not be null;{\"relationship\": {\"prisonerNumber\": \"A1324BC\", \"relationshipDetails\": { \"type\": \"SOCIAL\", \"code\": \"MOT\" }, \"isNextOfKin\": false, \"isEmergencyContact\": false}, \"createdBy\": null}",
      "createdBy must not be null;{\"relationship\": {\"prisonerNumber\": \"A1324BC\", \"relationshipDetails\": { \"type\": \"SOCIAL\", \"code\": \"MOT\" }, \"isNextOfKin\": false, \"isEmergencyContact\": false}}",
      "relationship.prisonerNumber must not be null;{\"relationship\": {\"prisonerNumber\": null, \"relationshipDetails\": { \"type\": \"SOCIAL\", \"code\": \"MOT\" }, \"isNextOfKin\": false, \"isEmergencyContact\": false}, \"createdBy\": \"USER\"}",
      "relationship.prisonerNumber must not be null;{\"relationship\": {\"relationshipDetails\": { \"type\": \"SOCIAL\", \"code\": \"MOT\" }, \"isNextOfKin\": false, \"isEmergencyContact\": false}, \"createdBy\": \"USER\"}",
      "relationship.relationshipDetails must not be null;{\"relationship\": {\"prisonerNumber\": \"A1324BC\", \"relationshipDetails\": null, \"isNextOfKin\": false, \"isEmergencyContact\": false}, \"createdBy\": \"USER\"}",
      "relationship.relationshipDetails must not be null;{\"relationship\": {\"prisonerNumber\": \"A1324BC\", \"isNextOfKin\": false, \"isEmergencyContact\": false}, \"createdBy\": \"USER\"}",
      "relationship.isNextOfKin must not be null;{\"relationship\": {\"prisonerNumber\": \"A1324BC\", \"relationshipDetails\": { \"type\": \"SOCIAL\", \"code\": \"MOT\" }, \"isEmergencyContact\": false}, \"createdBy\": \"USER\"}",
      "relationship.isEmergencyContact must not be null;{\"relationship\": {\"prisonerNumber\": \"A1324BC\", \"relationshipDetails\": { \"type\": \"SOCIAL\", \"code\": \"MOT\" }, \"isNextOfKin\": false}, \"createdBy\": \"USER\"}",
    ],
    delimiter = ';',
  )
  fun `should return bad request if required fields are null`(expectedMessage: String, json: String) {
    val errors = webTestClient.post()
      .uri("/prisoner-contact")
      .accept(MediaType.APPLICATION_JSON)
      .contentType(MediaType.APPLICATION_JSON)
      .headers(setAuthorisation(roles = listOf("ROLE_CONTACTS_ADMIN")))
      .bodyValue(json)
      .exchange()
      .expectStatus()
      .isBadRequest
      .expectHeader().contentType(MediaType.APPLICATION_JSON)
      .expectBody(ErrorResponse::class.java)
      .returnResult().responseBody!!

    assertThat(errors.userMessage).isEqualTo("Validation failure: $expectedMessage")
  }

  @Test
  fun `Should return 404 if the prisoner can't be found`() {
    val errors = webTestClient.post()
      .uri("/prisoner-contact")
      .accept(MediaType.APPLICATION_JSON)
      .contentType(MediaType.APPLICATION_JSON)
      .headers(setAuthorisation(roles = listOf("ROLE_CONTACTS_ADMIN")))
      .bodyValue(aMinimalRequest())
      .exchange()
      .expectStatus()
      .isNotFound
      .expectHeader().contentType(MediaType.APPLICATION_JSON)
      .expectBody(ErrorResponse::class.java)
      .returnResult().responseBody!!

    assertThat(errors.userMessage).isEqualTo("Entity not found : Prisoner (A1234BC) could not be found")
  }

  @Test
  fun `Should return 404 if the contact can't be found`() {
    stubPrisonSearchWithResponse("A1234BC")

    val errors = webTestClient.post()
      .uri("/prisoner-contact")
      .accept(MediaType.APPLICATION_JSON)
      .contentType(MediaType.APPLICATION_JSON)
      .headers(setAuthorisation(roles = listOf("ROLE_CONTACTS_ADMIN")))
      .bodyValue(aMinimalRequest().copy(contactId = 123456789))
      .exchange()
      .expectStatus()
      .isNotFound
      .expectHeader().contentType(MediaType.APPLICATION_JSON)
      .expectBody(ErrorResponse::class.java)
      .returnResult().responseBody!!

    assertThat(errors.userMessage).isEqualTo("Entity not found : Contact (123456789) could not be found")
  }

  @ParameterizedTest
  @ValueSource(strings = ["ROLE_CONTACTS_ADMIN", "ROLE_CONTACTS__RW"])
  fun `should create the contact relationship with minimal fields`(role: String) {
    stubPrisonSearchWithResponse("A1234BC")

    val request = AddContactRelationshipRequest(
      contactId = contact.id,
      relationship = ContactRelationship(
        prisonerNumber = "A1234BC",
        relationshipDetails = Relationship(RelationshipType.SOCIAL, "MOT"),
        isNextOfKin = true,
        isEmergencyContact = false,
      ),
      createdBy = "USER",
    )

    val createdRelationship = testAPIClient.addAContactRelationship(request, role)

    assertThat(createdRelationship.relationshipCode).isEqualTo("MOT")
    assertThat(createdRelationship.relationshipDescription).isEqualTo("Mother")
    assertThat(createdRelationship.nextOfKin).isTrue()
    assertThat(createdRelationship.emergencyContact).isFalse()
    assertThat(createdRelationship.comments).isNull()

    stubEvents.assertHasEvent(
      event = OutboundEvent.PRISONER_CONTACT_CREATED,
      additionalInfo = PrisonerContactInfo(createdRelationship.prisonerContactId, source = Source.DPS),
      personReference = PersonReference(dpsContactId = contact.id, nomsNumber = request.relationship.prisonerNumber),
    )
  }

  @Test
  fun `should create the contact with all fields`() {
    stubPrisonSearchWithResponse("A1234BC")

    val request = AddContactRelationshipRequest(
      contactId = contact.id,
      relationship = ContactRelationship(
        prisonerNumber = "A1234BC",
        relationshipDetails = Relationship(RelationshipType.SOCIAL, "MOT"),
        isNextOfKin = false,
        isEmergencyContact = true,
        comments = "Some comments",
      ),
      createdBy = "USER",
    )

    val createdRelationship = testAPIClient.addAContactRelationship(request)
    assertThat(createdRelationship.relationshipCode).isEqualTo("MOT")
    assertThat(createdRelationship.relationshipDescription).isEqualTo("Mother")
    assertThat(createdRelationship.nextOfKin).isFalse()
    assertThat(createdRelationship.emergencyContact).isTrue()
    assertThat(createdRelationship.comments).isEqualTo("Some comments")

    stubEvents.assertHasEvent(
      event = OutboundEvent.PRISONER_CONTACT_CREATED,
      additionalInfo = PrisonerContactInfo(createdRelationship.prisonerContactId, source = Source.DPS),
      personReference = PersonReference(dpsContactId = contact.id, nomsNumber = request.relationship.prisonerNumber),
    )
  }

  private fun aMinimalRequest() = AddContactRelationshipRequest(
    contactId = contact.id,
    relationship = ContactRelationship(
      prisonerNumber = "A1234BC",
      relationshipDetails = Relationship(RelationshipType.SOCIAL, "MOT"),
      isNextOfKin = true,
      isEmergencyContact = false,
    ),
    createdBy = "USER",
  )
}
