package uk.gov.justice.digital.hmpps.hmppscontactsapi.integration

import org.junit.jupiter.api.Test

class NotFoundTest : H2IntegrationTestBase() {

  @Test
  fun `Resources that aren't found should return 404 - test of the exception handler`() {
    webTestClient.get().uri("/some-url-not-found")
      .headers(setAuthorisation())
      .exchange()
      .expectStatus().isNotFound
  }
}
