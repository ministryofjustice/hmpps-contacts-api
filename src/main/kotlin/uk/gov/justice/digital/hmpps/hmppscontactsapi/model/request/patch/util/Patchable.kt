package uk.gov.justice.digital.hmpps.hmppscontactsapi.model.request.patch.util

import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo

@JsonTypeInfo(
  use = JsonTypeInfo.Id.NAME,
  include = JsonTypeInfo.As.PROPERTY,
  property = "type",
)
@JsonSubTypes(
  JsonSubTypes.Type(value = Patchable.Present::class, name = "Present"),
  JsonSubTypes.Type(value = Patchable.Undefined::class, name = "Undefined"),
)
@Suppress("UNCHECKED_CAST")
sealed class Patchable<T> {
  object Undefined : Patchable<Nothing>()

  data class Present<T>(val value: T) : Patchable<T>()

  fun get(): T? = when (this) {
    is Present -> value
    Undefined -> null
  }

  companion object {
    @Suppress("UNCHECKED_CAST")
    fun <T> undefined(): Patchable<T> = Undefined as Patchable<T>

    fun <T> from(value: T) = Present(value)
  }
}
