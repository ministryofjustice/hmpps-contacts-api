package uk.gov.justice.digital.hmpps.hmppscontactsapi.model.response

data class CreateAddressResponse(val created: ContactAddressResponse, val otherUpdatedAddressIds: Set<Long>)
