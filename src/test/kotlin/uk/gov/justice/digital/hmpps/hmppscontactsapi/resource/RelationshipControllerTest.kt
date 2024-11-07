package uk.gov.justice.digital.hmpps.hmppscontactsapi.resource

import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.mockito.Mockito.doNothing
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.kotlin.whenever
import uk.gov.justice.digital.hmpps.hmppscontactsapi.model.request.AddContactRelationshipRequest
import uk.gov.justice.digital.hmpps.hmppscontactsapi.model.request.ContactRelationship
import uk.gov.justice.digital.hmpps.hmppscontactsapi.service.RelationshipService

class RelationshipControllerTest {
  private val relationshipService: RelationshipService = mock()
  private val controller = RelationshipController(relationshipService)

  @Nested
  inner class CreateContactRelationship {
    private val id = 123456L
    private val relationship = ContactRelationship(
      prisonerNumber = "A1234BC",
      relationshipCode = "MOT",
      isNextOfKin = true,
      isEmergencyContact = false,
      comments = "Foo",
    )
    private val request = AddContactRelationshipRequest(relationship, "USER")

    @Test
    fun `should create a contact relationship successfully`() {
      doNothing().whenever(relationshipService).create(id, request)

      controller.create(id, request)

      verify(relationshipService).create(id, request)
    }

    @Test
    fun `should propagate exceptions getting a contact`() {
      whenever(relationshipService.create(id, request)).thenThrow(RuntimeException("Bang!"))

      org.junit.jupiter.api.assertThrows<RuntimeException>("Bang!") {
        controller.create(id, request)
      }
    }
  }
}
