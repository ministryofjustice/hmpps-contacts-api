package uk.gov.justice.digital.hmpps.hmppscontactsapi.resource

import jakarta.persistence.EntityNotFoundException
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.kotlin.whenever
import org.springframework.http.HttpStatus
import uk.gov.justice.digital.hmpps.hmppscontactsapi.helpers.createContactPhoneNumberDetails
import uk.gov.justice.digital.hmpps.hmppscontactsapi.model.request.CreatePhoneRequest
import uk.gov.justice.digital.hmpps.hmppscontactsapi.model.response.ContactPhoneDetails
import uk.gov.justice.digital.hmpps.hmppscontactsapi.service.ContactPhoneService
import java.time.LocalDateTime

class ContactPhoneControllerTest {

  private val service: ContactPhoneService = mock()
  private val controller = ContactPhoneController(service)

  @Nested
  inner class CreateContactPhone {
    @Test
    fun `should return 201 with created phone number if created successfully`() {
      val createdPhone = createContactPhoneNumberDetails(id = 99, contactId = 1)
      val request = CreatePhoneRequest(
        "MOB",
        "+07777777777",
        null,
        "JAMES",
      )
      whenever(service.create(1, request)).thenReturn(createdPhone)

      val response = controller.create(1, request)

      assertThat(response.statusCode).isEqualTo(HttpStatus.CREATED)
      assertThat(response.body).isEqualTo(createdPhone)
      verify(service).create(1, request)
    }

    @Test
    fun `should propagate exceptions if create fails`() {
      val request = CreatePhoneRequest(
        "MOB",
        "+07777777777",
        null,
        "JAMES",
      )
      val expected = EntityNotFoundException("Couldn't find contact")
      whenever(service.create(1, request)).thenThrow(expected)

      val exception = assertThrows<EntityNotFoundException> {
        controller.create(1, request)
      }

      assertThat(exception).isEqualTo(expected)
      verify(service).create(1, request)
    }
  }

  @Nested
  inner class GetPhone {
    private val phone = ContactPhoneDetails(
      contactPhoneId = 99,
      contactId = 11,
      phoneType = "MOB",
      phoneTypeDescription = "Mobile",
      phoneNumber = "07777777777",
      extNumber = null,
      createdBy = "USER1",
      createdTime = LocalDateTime.now(),
      amendedBy = null,
      amendedTime = null,
    )

    @Test
    fun `get phone if found by ids`() {
      whenever(service.get(11, 99)).thenReturn(phone)

      val returnedPhone = service.get(11, 99)

      assertThat(returnedPhone).isEqualTo(phone)
    }

    @Test
    fun `throw EntityNotFoundException when contact or phone cannot be found`() {
      whenever(service.get(11, 99)).thenReturn(null)
      val exception = assertThrows<EntityNotFoundException> {
        controller.get(11, 99)
      }
      assertThat(exception.message).isEqualTo("Contact phone with id (99) not found for contact (11)")
    }
  }
}
