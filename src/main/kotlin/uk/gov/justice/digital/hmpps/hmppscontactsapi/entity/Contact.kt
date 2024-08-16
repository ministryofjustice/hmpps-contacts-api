package uk.gov.justice.digital.hmpps.hmppscontactsapi.entity

import jakarta.persistence.*
import java.time.Instant
import java.time.LocalDate

@Entity
@Table(name = "contact")
class Contact(
  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "contact_id_gen")
  @SequenceGenerator(name = "contact_id_gen", sequenceName = "contact_contact_id_seq", allocationSize = 1)
  @Column(name = "contact_id", nullable = false)
  var id: Long? = null,

  @Column(name = "contact_type_code", length = 10)
  var contactTypeCode: String? = null,

  @Column(name = "title", length = 12)
  var title: String? = null,

  @Column(name = "last_name", nullable = false, length = 35)
  var lastName: String,

  @Column(name = "first_name", nullable = false, length = 35)
  var firstName: String,

  @Column(name = "middle_name", length = 35)
  var middleName: String? = null,

  @Column(name = "date_of_birth")
  var dateOfBirth: LocalDate? = null,

  @Column(name = "place_of_birth", length = 25)
  var placeOfBirth: String? = null,

  @Column(name = "active", nullable = false)
  var active: Boolean = true,

  @Column(name = "suspended", nullable = false)
  var suspended: Boolean = false,

  @Column(name = "staff_flag", nullable = false)
  var staffFlag: Boolean = false,

  @Column(name = "deceased_flag", nullable = false)
  var deceasedFlag: Boolean = false,

  @Column(name = "deceased_date")
  var deceasedDate: LocalDate? = null,

  @Column(name = "coroner_number", length = 32)
  var coronerNumber: String? = null,

  @Column(name = "gender", length = 20)
  var gender: String? = null,

  @Column(name = "marital_status", length = 12)
  var maritalStatus: String? = null,

  @Column(name = "language_code", length = 12)
  var languageCode: String? = null,

  @Column(name = "interpreter_required", nullable = false)
  var interpreterRequired: Boolean = false,

  @Column(name = "comments", length = 200)
  var comments: String? = null,

  @Column(name = "created_by", nullable = false, length = 100)
  var createdBy: String,

  @Column(name = "created_time", nullable = false)
  var createdTime: Instant = Instant.now(),

  @Column(name = "amended_by", length = 100)
  var amendedBy: String? = null,

  @Column(name = "amended_time")
  var amendedTime: Instant? = null,
) {

  // Copy method
  fun copy(
    id: Long? = this.id,
    contactTypeCode: String? = this.contactTypeCode,
    title: String? = this.title,
    lastName: String = this.lastName,
    firstName: String = this.firstName,
    middleName: String? = this.middleName,
    dateOfBirth: LocalDate? = this.dateOfBirth,
    placeOfBirth: String? = this.placeOfBirth,
    active: Boolean = this.active,
    suspended: Boolean = this.suspended,
    staffFlag: Boolean = this.staffFlag,
    deceasedFlag: Boolean = this.deceasedFlag,
    deceasedDate: LocalDate? = this.deceasedDate,
    coronerNumber: String? = this.coronerNumber,
    gender: String? = this.gender,
    maritalStatus: String? = this.maritalStatus,
    languageCode: String? = this.languageCode,
    interpreterRequired: Boolean = this.interpreterRequired,
    comments: String? = this.comments,
    createdBy: String = this.createdBy,
    createdTime: Instant = this.createdTime,
    amendedBy: String? = this.amendedBy,
    amendedTime: Instant? = this.amendedTime,
  ) = Contact(
      id,
      contactTypeCode,
      title,
      lastName,
      firstName,
      middleName,
      dateOfBirth,
      placeOfBirth,
      active,
      suspended,
      staffFlag,
      deceasedFlag,
      deceasedDate,
      coronerNumber,
      gender,
      maritalStatus,
      languageCode,
      interpreterRequired,
      comments,
      createdBy,
      createdTime,
      amendedBy,
      amendedTime,
  )

  // Apply method example usage
  fun update(action: Contact.() -> Unit): Contact = this.apply(action)
}
