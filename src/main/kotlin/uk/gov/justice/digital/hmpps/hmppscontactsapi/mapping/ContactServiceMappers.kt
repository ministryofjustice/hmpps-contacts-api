package uk.gov.justice.digital.hmpps.hmppscontactsapi.mapping

import uk.gov.justice.digital.hmpps.hmppscontactsapi.entity.ContactEntity
import uk.gov.justice.digital.hmpps.hmppscontactsapi.entity.PrisonerContactEntity
import uk.gov.justice.digital.hmpps.hmppscontactsapi.model.request.ContactRelationshipRequest
import uk.gov.justice.digital.hmpps.hmppscontactsapi.model.request.CreateContactRequest
import uk.gov.justice.digital.hmpps.hmppscontactsapi.model.request.IsOverEighteen
import uk.gov.justice.digital.hmpps.hmppscontactsapi.model.response.Contact
import java.time.Clock
import java.time.LocalDate
import java.time.LocalDateTime

open class ContactServiceMappers(private val clock: Clock) {

  protected fun mapEntityToContact(entity: ContactEntity) = Contact(
    id = entity.contactId,
    title = entity.title,
    lastName = entity.lastName,
    firstName = entity.firstName,
    middleName = entity.middleName,
    dateOfBirth = entity.dateOfBirth,
    isOverEighteen = mapIsOverEighteen(entity),
    createdBy = entity.createdBy,
    createdTime = entity.createdTime,
  )

  protected fun mapRelationShip(
    createdContact: Contact,
    relationship: ContactRelationshipRequest,
    request: CreateContactRequest,
  ) = newPrisonerContact(
    createdContact.id,
    relationship.prisonerNumber,
    relationship.relationshipCode,
    relationship.isNextOfKin,
    relationship.isEmergencyContact,
    relationship.comments,
    request.createdBy,
  )

  protected fun mapContact(request: CreateContactRequest) =
    newContact(
      title = request.title,
      lastName = request.lastName,
      firstName = request.firstName,
      middleName = request.middleName,
      dateOfBirth = request.dateOfBirth,
      isOverEighteen = mapIsOverEighteen(request),
      createdBy = request.createdBy,
    )

  private fun mapIsOverEighteen(entity: ContactEntity): IsOverEighteen {
    return if (entity.dateOfBirth != null) {
      if (isOver18(entity.dateOfBirth)) {
        IsOverEighteen.YES
      } else {
        IsOverEighteen.NO
      }
    } else {
      when (entity.isOverEighteen) {
        true -> IsOverEighteen.YES
        false -> IsOverEighteen.NO
        null -> IsOverEighteen.DO_NOT_KNOW
      }
    }
  }

  private fun isOver18(dateOfBirth: LocalDate) = !dateOfBirth.isAfter(LocalDate.now(clock).minusYears(18))

  private fun mapIsOverEighteen(request: CreateContactRequest): Boolean? {
    return if (request.dateOfBirth != null) {
      null
    } else {
      when (request.isOverEighteen) {
        IsOverEighteen.YES -> true
        IsOverEighteen.NO -> false
        IsOverEighteen.DO_NOT_KNOW -> null
        null -> null
      }
    }
  }

  private fun newContact(
    title: String?,
    firstName: String,
    lastName: String,
    middleName: String?,
    dateOfBirth: LocalDate?,
    isOverEighteen: Boolean?,
    createdBy: String,
  ): ContactEntity {
    return ContactEntity(
      0,
      title,
      firstName,
      lastName,
      middleName,
      dateOfBirth,
      isOverEighteen,
      createdBy,
      LocalDateTime.now(),
    )
  }

  private fun newPrisonerContact(
    contactId: Long,
    prisonerNumber: String,
    relationshipType: String,
    nextOfKin: Boolean,
    emergencyContact: Boolean,
    comments: String?,
    createdBy: String,
  ): PrisonerContactEntity {
    return PrisonerContactEntity(
      0,
      contactId,
      prisonerNumber,
      relationshipType,
      nextOfKin,
      emergencyContact,
      comments,
      createdBy,
      LocalDateTime.now(),
    )
  }
}
