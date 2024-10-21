package uk.gov.justice.digital.hmpps.hmppscontactsapi.model.request.patch.util

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.SerializerProvider
import org.junit.jupiter.api.Test
import org.mockito.Mockito.any
import org.mockito.Mockito.mock
import org.mockito.Mockito.never
import org.mockito.Mockito.verify

class PatchableSerializerTest {

  private val serializer = PatchableSerializer()

  @Test
  fun `test serialize with Present value`() {
    val gen = mock(JsonGenerator::class.java)
    val serializers = mock(SerializerProvider::class.java)
    val patchablePresent = Patchable.Present("Test Value")

    serializer.serialize(patchablePresent, gen, serializers)

    verify(gen).writeObject("Test Value")
  }

  @Test
  fun `test serialize with Undefined value`() {
    val gen = mock(JsonGenerator::class.java)
    val serializers = mock(SerializerProvider::class.java)
    val patchableUndefined = Patchable.Undefined

    serializer.serialize(patchableUndefined, gen, serializers)

    verify(gen).writeNull()
  }

  @Test
  fun `test serialize with null value`() {
    val gen = mock(JsonGenerator::class.java)
    val serializers = mock(SerializerProvider::class.java)
    val patchableNull: Patchable<*>? = null

    serializer.serialize(patchableNull, gen, serializers)

    verify(gen, never()).writeObject(any())
    verify(gen, never()).writeNull()
  }
}
