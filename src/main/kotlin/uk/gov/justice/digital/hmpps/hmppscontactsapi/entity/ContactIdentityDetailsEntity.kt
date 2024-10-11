package uk.gov.justice.digital.hmpps.hmppscontactsapi.entity

import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.LocalDateTime

@Entity
@Table(name = "v_contact_identities")
data class ContactIdentityDetailsEntity(
  @Id
  val contactIdentityId: Long,
  val contactId: Long,
  val identityType: String?,
  val identityTypeDescription: String?,
  val identityValue: String?,
  val issuingAuthority: String?,
  val verified: Boolean,
  val verifiedBy: String?,
  val verifiedTime: LocalDateTime?,
  val createdBy: String,
  val createdTime: LocalDateTime,
  val amendedBy: String?,
  val amendedTime: LocalDateTime?,
)