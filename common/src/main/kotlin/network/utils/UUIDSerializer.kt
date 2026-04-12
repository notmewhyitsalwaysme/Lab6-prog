package lab6.prog.network.utils

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.*
import kotlinx.serialization.encoding.*
import java.util.UUID

/**
 * Кастомный KSerializer для UUID.
 * UUID не поддерживается kotlinx.serialization из коробки.
 */
object UUIDSerializer : KSerializer<UUID> {
    override val descriptor = PrimitiveSerialDescriptor("UUID", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: UUID) =
        encoder.encodeString(value.toString())

    override fun deserialize(decoder: Decoder): UUID =
        UUID.fromString(decoder.decodeString())
}
