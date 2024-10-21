package uk.gov.justice.digital.hmpps.hmppscontactsapi.model.request.patch.util

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import com.fasterxml.jackson.databind.JsonNode

class PatchableDeserializer : JsonDeserializer<Patchable<*>>() {
  override fun deserialize(p: JsonParser, ctxt: DeserializationContext): Patchable<*> {
    val node: JsonNode = p.codec.readTree(p)
    return if (node.isNull || node.isMissingNode) {
      Patchable.Undefined
    } else {
      Patchable.Present(node.asText())
    }
  }
}
