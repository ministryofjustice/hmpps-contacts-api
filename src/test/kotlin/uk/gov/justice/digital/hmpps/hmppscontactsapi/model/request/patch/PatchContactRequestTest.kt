package uk.gov.justice.digital.hmpps.hmppscontactsapi.model.request.patch

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import uk.gov.justice.digital.hmpps.hmppscontactsapi.model.request.patch.util.Patchable

class PatchContactRequestTest {

  private val objectMapper = jacksonObjectMapper()

  @Test
  fun `test serialization and deserialization of Present`() {
    val presentValue = PatchContactRequest(
      languageCode = Patchable.from("BEN"),
      updatedBy = "JD000001",
    )
    val json = objectMapper.writeValueAsString(presentValue)

    assertThat(json).isEqualTo("{\"languageCode\":\"BEN\",\"updatedBy\":\"JD000001\"}")

    val deserialized: PatchContactRequest = objectMapper.readValue(json)

    assertThat(deserialized).isEqualTo(presentValue)
  }

  @Test
  fun `test serialization and deserialization of Undefined`() {
    val presentValue = PatchContactRequest(
      updatedBy = "JD000001",
    )
    val json = objectMapper.writeValueAsString(presentValue)

    assertThat(json).isEqualTo("{\"updatedBy\":\"JD000001\"}")

    val deserialized: PatchContactRequest = objectMapper.readValue(json)

    assertThat(deserialized).isEqualTo(presentValue)
  }
}
