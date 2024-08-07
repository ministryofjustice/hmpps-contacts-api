package uk.gov.justice.digital.hmpps.hmppscontactsapi.resource

import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.MediaType
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import uk.gov.justice.digital.hmpps.hmppscontactsapi.client.prisonersearch.Prisoner
import uk.gov.justice.digital.hmpps.hmppscontactsapi.service.PrisonerService

@Tag(name = "Get Prisoner")
@RestController
@RequestMapping(value = ["prisoner"], produces = [MediaType.APPLICATION_JSON_VALUE])
class ContactTypesController(private val prisonerService: PrisonerService) {

  @GetMapping(value = ["/prisoner"], produces = [MediaType.APPLICATION_JSON_VALUE])
  @PreAuthorize("hasAnyRole('PRISONER_SEARCH')")
  fun getScheduleForPrison(): Prisoner {
    val prisonerNumber = "123"
    val prisonCode = "joel"
    return prisonerService.validatePrisonerAtPrison(prisonerNumber, prisonCode)
  }
}
