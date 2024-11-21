package uk.gov.justice.digital.hmpps.hmppscontactsapi.helpers

import uk.gov.justice.digital.hmpps.hmppscontactsapi.entity.ContactAddressDetailsEntity
import uk.gov.justice.digital.hmpps.hmppscontactsapi.entity.ContactEmailEntity
import uk.gov.justice.digital.hmpps.hmppscontactsapi.entity.ContactIdentityDetailsEntity
import uk.gov.justice.digital.hmpps.hmppscontactsapi.entity.ContactPhoneDetailsEntity
import uk.gov.justice.digital.hmpps.hmppscontactsapi.model.response.ContactAddressDetails
import uk.gov.justice.digital.hmpps.hmppscontactsapi.model.response.ContactEmailDetails
import uk.gov.justice.digital.hmpps.hmppscontactsapi.model.response.ContactIdentityDetails
import uk.gov.justice.digital.hmpps.hmppscontactsapi.model.response.ContactPhoneDetails
import uk.gov.justice.digital.hmpps.hmppscontactsapi.model.response.PrisonerContactRelationshipDetails
import java.time.LocalDate
import java.time.LocalDateTime

fun createContactPhoneDetailsEntity(
  id: Long = 99,
  contactId: Long = 45,
  phoneType: String = "HOME",
  phoneTypeDescription: String = "Home phone",
  phoneNumber: String = "123456789",
  extNumber: String? = "987654321",
  createdBy: String = "CREATOR",
  createdTime: LocalDateTime = LocalDateTime.of(2024, 2, 3, 4, 5, 6),
  amendedBy: String? = "AM",
  amendedTime: LocalDateTime? = LocalDateTime.of(2026, 5, 4, 3, 2, 1),
): ContactPhoneDetailsEntity = ContactPhoneDetailsEntity(
  id,
  contactId,
  phoneType,
  phoneTypeDescription,
  phoneNumber,
  extNumber,
  createdBy,
  createdTime,
  amendedBy,
  amendedTime,
)

fun createContactPhoneNumberDetails(
  id: Long = 99,
  contactId: Long = 45,
  phoneType: String = "HOME",
  phoneTypeDescription: String = "Home phone",
  phoneNumber: String = "123456789",
  extNumber: String? = "987654321",
  createdBy: String = "CREATOR",
  createdTime: LocalDateTime = LocalDateTime.of(2024, 2, 3, 4, 5, 6),
  amendedBy: String? = "AM",
  amendedTime: LocalDateTime? = LocalDateTime.of(2026, 5, 4, 3, 2, 1),
): ContactPhoneDetails = ContactPhoneDetails(
  id,
  contactId,
  phoneType,
  phoneTypeDescription,
  phoneNumber,
  extNumber,
  createdBy,
  createdTime,
  amendedBy,
  amendedTime,
)

fun createContactAddressDetailsEntity(
  id: Long = 0,
  contactId: Long = 0,
  addressType: String? = "HOME",
  addressTypeDescription: String? = "Home address",
  primaryAddress: Boolean = true,
  flat: String? = "Flat",
  property: String? = "Property",
  street: String? = "Street",
  area: String? = "Area",
  cityCode: String? = "CIT",
  cityDescription: String? = "City",
  countyCode: String? = "COUNT",
  countyDescription: String? = "County",
  postCode: String? = "POST CODE",
  countryCode: String? = "ENG",
  countryDescription: String? = "England",
  verified: Boolean = true,
  verifiedBy: String? = "VERIFIED",
  verifiedTime: LocalDateTime? = LocalDateTime.of(2021, 1, 1, 11, 15, 0),
  mailFlag: Boolean = true,
  startDate: LocalDate? = LocalDate.of(2020, 2, 3),
  endDate: LocalDate? = LocalDate.of(2050, 4, 5),
  noFixedAddress: Boolean = true,
  comments: String? = "Some comments",
  createdBy: String = "USER1",
  createdTime: LocalDateTime = LocalDateTime.of(2023, 2, 3, 11, 15, 15),
  amendedBy: String? = "AMEND_USER",
  amendedTime: LocalDateTime = LocalDateTime.of(2024, 5, 6, 12, 30, 30),
): ContactAddressDetailsEntity = ContactAddressDetailsEntity(
  id,
  contactId,
  addressType,
  addressTypeDescription,
  primaryAddress,
  flat,
  property,
  street,
  area,
  cityCode,
  cityDescription,
  countyCode,
  countyDescription,
  postCode,
  countryCode,
  countryDescription,
  verified,
  verifiedBy,
  verifiedTime,
  mailFlag,
  startDate,
  endDate,
  noFixedAddress,
  comments,
  createdBy,
  createdTime,
  amendedBy,
  amendedTime,
)

fun createContactAddressDetails(
  id: Long = 0,
  contactId: Long = 0,
  addressType: String? = "HOME",
  addressTypeDescription: String? = "Home address",
  primaryAddress: Boolean = true,
  flat: String? = "Flat",
  property: String? = "Property",
  street: String? = "Street",
  area: String? = "Area",
  cityCode: String? = "CIT",
  cityDescription: String? = "City",
  countyCode: String? = "COUNT",
  countyDescription: String? = "County",
  postCode: String? = "POST CODE",
  countryCode: String? = "ENG",
  countryDescription: String? = "England",
  verified: Boolean = true,
  verifiedBy: String? = "VERIFIED",
  verifiedTime: LocalDateTime? = LocalDateTime.of(2021, 1, 1, 11, 15, 0),
  mailFlag: Boolean = true,
  startDate: LocalDate? = LocalDate.of(2020, 2, 3),
  endDate: LocalDate? = LocalDate.of(2050, 4, 5),
  noFixedAddress: Boolean = true,
  comments: String? = "Some comments",
  phoneNumbers: List<ContactPhoneDetails> = emptyList(),
  createdBy: String = "USER1",
  createdTime: LocalDateTime = LocalDateTime.of(2023, 2, 3, 11, 15, 15),
  amendedBy: String? = "AMEND_USER",
  amendedTime: LocalDateTime = LocalDateTime.of(2024, 5, 6, 12, 30, 30),
): ContactAddressDetails = ContactAddressDetails(
  id,
  contactId,
  addressType,
  addressTypeDescription,
  primaryAddress,
  flat,
  property,
  street,
  area,
  cityCode,
  cityDescription,
  countyCode,
  countyDescription,
  postCode,
  countryCode,
  countryDescription,
  verified,
  verifiedBy,
  verifiedTime,
  mailFlag,
  startDate,
  endDate,
  noFixedAddress,
  comments,
  phoneNumbers,
  createdBy,
  createdTime,
  amendedBy,
  amendedTime,
)

fun createContactEmailEntity(
  id: Long = 1,
  contactId: Long = 1,
  emailAddress: String = "test@example.com",
  createdBy: String = "USER",
  createdTime: LocalDateTime = LocalDateTime.now(),
  amendedBy: String? = "AMEND_USER",
  amendedTime: LocalDateTime? = LocalDateTime.now(),
) = ContactEmailEntity(
  id,
  contactId,
  emailAddress,
  createdBy,
  createdTime,
  amendedBy,
  amendedTime,
)

fun createContactEmailDetails(
  id: Long = 1,
  contactId: Long = 1,
  emailAddress: String = "test@example.com",
  createdBy: String = "USER",
  createdTime: LocalDateTime = LocalDateTime.now(),
  amendedBy: String? = "AMEND_USER",
  amendedTime: LocalDateTime? = LocalDateTime.now(),
) = ContactEmailDetails(
  id,
  contactId,
  emailAddress,
  createdBy,
  createdTime,
  amendedBy,
  amendedTime,
)

fun createContactIdentityDetailsEntity(
  id: Long = 1,
  contactId: Long = 1,
  identityType: String? = "PASS",
  identityTypeDescription: String? = "Passport",
  identityTypeIsActive: Boolean = false,
  identityValue: String? = "132456789",
  issuingAuthority: String? = "UK",
  createdBy: String = "CRE",
  createdTime: LocalDateTime = LocalDateTime.of(2024, 2, 2, 2, 2, 2),
  amendedBy: String? = "AMD",
  amendedTime: LocalDateTime? = LocalDateTime.of(2024, 3, 3, 3, 3, 3),
) = ContactIdentityDetailsEntity(
  id,
  contactId,
  identityType,
  identityTypeDescription,
  identityTypeIsActive,
  identityValue,
  issuingAuthority,
  createdBy,
  createdTime,
  amendedBy,
  amendedTime,
)

fun createContactIdentityDetails(
  id: Long = 1,
  contactId: Long = 1,
  identityType: String? = "PASS",
  identityTypeDescription: String? = "Passport",
  identityTypeIsActive: Boolean = false,
  identityValue: String? = "132456789",
  issuingAuthority: String? = "UK",
  createdBy: String = "CRE",
  createdTime: LocalDateTime = LocalDateTime.of(2024, 2, 2, 2, 2, 2),
  amendedBy: String? = "AMD",
  amendedTime: LocalDateTime? = LocalDateTime.of(2024, 3, 3, 3, 3, 3),
) = ContactIdentityDetails(
  id,
  contactId,
  identityType,
  identityTypeDescription,
  identityTypeIsActive,
  identityValue,
  issuingAuthority,
  createdBy,
  createdTime,
  amendedBy,
  amendedTime,
)

fun createPrisonerContactRelationshipDetails(
  id: Long = 1,
  contactId: Long = 99,
  prisonerNumber: String = "A1234BC",
  relationshipCode: String = "FRI",
  relationshipDescription: String = "Friend",
  emergencyContact: Boolean = false,
  nextOfKin: Boolean = false,
  isRelationshipActive: Boolean = true,
  comments: String? = null,
) = PrisonerContactRelationshipDetails(
  id,
  contactId,
  prisonerNumber,
  relationshipCode,
  relationshipDescription,
  emergencyContact,
  nextOfKin,
  isRelationshipActive,
  comments,
)
