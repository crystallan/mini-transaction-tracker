package com.transaction.shared.storage

import com.transaction.shared.domain.utils.toDecimalOrNull
import io.github.kormium.decimal.Decimal
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerializationException
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

object DecimalAsStringSerializer : KSerializer<Decimal> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("Decimal", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: Decimal) {
        encoder.encodeString(value.toString())
    }

    override fun deserialize(decoder: Decoder): Decimal {
        val raw = decoder.decodeString()
        return raw.toDecimalOrNull()
            ?: throw SerializationException("Invalid decimal: $raw")
    }
}