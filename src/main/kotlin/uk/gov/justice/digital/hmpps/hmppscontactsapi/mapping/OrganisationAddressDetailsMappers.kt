package uk.gov.justice.digital.hmpps.hmppscontactsapi.mapping

import uk.gov.justice.digital.hmpps.hmppscontactsapi.entity.OrganisationAddressDetailsEntity
import uk.gov.justice.digital.hmpps.hmppscontactsapi.entity.OrganisationAddressPhoneEntity
import uk.gov.justice.digital.hmpps.hmppscontactsapi.entity.OrganisationPhoneDetailsEntity
import uk.gov.justice.digital.hmpps.hmppscontactsapi.model.response.OrganisationAddressDetails
import uk.gov.justice.digital.hmpps.hmppscontactsapi.model.response.OrganisationAddressPhoneDetails

fun OrganisationAddressDetailsEntity.toModel(phoneNumbers: List<Pair<OrganisationAddressPhoneEntity, OrganisationPhoneDetailsEntity>>): OrganisationAddressDetails = OrganisationAddressDetails(
  organisationAddressId = organisationAddressId,
  organisationId = organisationId,
  addressType = addressType,
  addressTypeDescription = addressTypeDescription,
  primaryAddress = primaryAddress,
  flat = flat,
  property = property,
  street = street,
  area = area,
  cityCode = cityCode,
  cityDescription = cityDescription,
  countyCode = countyCode,
  countyDescription = countyDescription,
  postcode = postCode,
  countryCode = countryCode,
  countryDescription = countryDescription,
  mailAddress = mailAddress,
  serviceAddress = serviceAddress,
  startDate = startDate,
  endDate = endDate,
  noFixedAddress = noFixedAddress,
  comments = comments,
  specialNeedsCode = specialNeedsCode,
  specialNeedsCodeDescription = specialNeedsCodeDescription,
  contactPersonName = contactPersonName,
  businessHours = businessHours,
  phoneNumbers = phoneNumbers.map { (addressPhoneEntity, phoneEntity) ->
    OrganisationAddressPhoneDetails(
      organisationAddressPhoneId = addressPhoneEntity.organisationAddressPhoneId,
      organisationPhoneId = phoneEntity.organisationPhoneId,
      organisationAddressId = organisationAddressId,
      organisationId = phoneEntity.organisationId,
      phoneType = phoneEntity.phoneType,
      phoneTypeDescription = phoneEntity.phoneTypeDescription,
      phoneNumber = phoneEntity.phoneNumber,
      extNumber = phoneEntity.extNumber,
      createdBy = phoneEntity.createdBy,
      createdTime = phoneEntity.createdTime,
      updatedBy = phoneEntity.updatedBy,
      updatedTime = phoneEntity.updatedTime,
    )
  },
  createdBy = createdBy,
  createdTime = createdTime,
  updatedBy = updatedBy,
  updatedTime = updatedTime,
)