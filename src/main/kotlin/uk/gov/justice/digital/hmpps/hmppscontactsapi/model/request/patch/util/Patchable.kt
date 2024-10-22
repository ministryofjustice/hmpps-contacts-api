package uk.gov.justice.digital.hmpps.hmppscontactsapi.model.request.patch.util

import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.annotation.JsonSerialize

@JsonDeserialize(using = PatchableDeserializer::class)
@JsonSerialize(using = PatchableSerializer::class)
@Suppress("UNCHECKED_CAST")
sealed class Patchable<T> {
  object Undefined : Patchable<Nothing>()

  data class Present<T>(val value: T) : Patchable<T>()

  fun get(): T? = when (this) {
    is Present -> value
    is Undefined -> null
  }

  companion object {
    @Suppress("UNCHECKED_CAST")
    fun <T> undefined(): Patchable<T> = Undefined as Patchable<T>

    fun <T> from(value: T) = Present(value)
  }
}
