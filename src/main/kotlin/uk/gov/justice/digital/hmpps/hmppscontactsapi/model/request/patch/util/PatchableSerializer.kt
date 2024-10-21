package uk.gov.justice.digital.hmpps.hmppscontactsapi.model.request.patch.util

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.SerializerProvider

class PatchableSerializer : JsonSerializer<Patchable<*>>() {

  override fun serialize(value: Patchable<*>?, gen: JsonGenerator, serializers: SerializerProvider) {
    if (value != null) {
      when (value) {
        is Patchable.Present<*> -> {
          val presentValue = value.get()
          if (presentValue != null) {
            gen.writeObject(presentValue)
          }
        }
        is Patchable.Undefined -> {
          gen.writeNull()
        }
      }
    }
  }
}
