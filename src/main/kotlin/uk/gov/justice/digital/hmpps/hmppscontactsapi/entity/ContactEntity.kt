package uk.gov.justice.digital.hmpps.hmppscontactsapi.entity

import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import org.hibernate.annotations.CreationTimestamp
import uk.gov.justice.digital.hmpps.hmppscontactsapi.model.request.EstimatedIsOverEighteen
import java.time.LocalDate
import java.time.LocalDateTime

@Entity
@Table(name = "contact")
data class ContactEntity(
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  val contactId: Long,

  val title: String?,

  val firstName: String,

  val lastName: String,

  val middleName: String?,

  val dateOfBirth: LocalDate?,

  @Enumerated(EnumType.STRING)
  val estimatedIsOverEighteen: EstimatedIsOverEighteen,

  val createdBy: String,

  @CreationTimestamp
  val createdTime: LocalDateTime,
)
