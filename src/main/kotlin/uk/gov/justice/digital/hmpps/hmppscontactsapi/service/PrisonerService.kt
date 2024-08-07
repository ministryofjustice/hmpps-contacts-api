package uk.gov.justice.digital.hmpps.hmppscontactsapi.service

import jakarta.validation.ValidationException
import org.springframework.stereotype.Service
import uk.gov.justice.digital.hmpps.hmppscontactsapi.client.prisonersearch.Prisoner
import uk.gov.justice.digital.hmpps.hmppscontactsapi.client.prisonersearch.PrisonerSearchClient
import java.time.LocalDate

@Service
class PrisonerService(private val prisonerSearchClient: PrisonerSearchClient) {

  fun validatePrisonerAtPrison(prisonerNumber: String, prisonCode: String): Prisoner =
    prisonerSearchClient.getPrisoner(prisonerNumber)?.takeUnless { prisoner -> prisoner.prisonId != prisonCode }
      ?: throw ValidationException("Prisoner $prisonerNumber not found at prison $prisonCode")
}

data class Prisoner(
  val prisonerNumber: String,
  val prisonId: String? = null,
  val firstName: String,
  val lastName: String,
  val dateOfBirth: LocalDate,
  val bookingId: String? = null,
  val lastPrisonId: String? = null,
)
