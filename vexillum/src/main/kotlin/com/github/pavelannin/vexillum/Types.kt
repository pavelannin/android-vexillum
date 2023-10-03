package com.github.pavelannin.vexillum

import java.util.UUID

sealed class FeatureToggle<Payload>(
    internal open val key: Key,
    internal open val isEnabled: Boolean,
    open val name: String?,
    open val description: String?,
    internal open val payload: Payload,
    internal open val payloadType: Class<Payload>
) {

    data class Static<Payload> internal constructor(
        override val key: Key,
        override val isEnabled: Boolean,
        override val name: String?,
        override val description: String?,
        override val payload: Payload,
        override val payloadType: Class<Payload>
    ) : FeatureToggle<Payload>(
        key = key,
        isEnabled = isEnabled,
        name = name,
        description = description,
        payload = payload,
        payloadType = payloadType
    )

    data class Dynamic<Payload> internal constructor(
        override val key: Key,
        override val isEnabled: Boolean,
        override val name: String?,
        override val description: String?,
        override val payload: Payload,
        override val payloadType: Class<Payload>
    ) : FeatureToggle<Payload>(
        key = key,
        isEnabled = isEnabled,
        name = name,
        description = description,
        payload = payload,
        payloadType = payloadType
    )

    @JvmInline
    internal value class Key(val value: String) {
        companion object {
            fun random() = Key(UUID.randomUUID().toString())
        }
    }

    companion object {
        fun Static(
            isEnabled: Boolean,
            key: String? = null,
            name: String? = null,
            description: String? = null
        ) = Static(
            key = key?.let(::Key) ?: Key.random(),
            isEnabled = isEnabled,
            name = name ?: key,
            description = description,
            payload = Unit,
            payloadType = Unit::class.java
        )

        fun <Payload> Static(
            isEnabled: Boolean,
            payload: Payload,
            payloadClass: Class<Payload>,
            key: String? = null,
            name: String? = null,
            description: String? = null
        ) = Static(
            key = key?.let(::Key) ?: Key.random(),
            isEnabled = isEnabled,
            name = name ?: key,
            description = description,
            payload = payload,
            payloadType = payloadClass
        )

        inline fun <reified Payload> Static(
            isEnabled: Boolean,
            payload: Payload,
            key: String? = null,
            name: String? = null,
            description: String? = null,
        ) = Static(
            key = key,
            isEnabled = isEnabled,
            name = name,
            description = description,
            payload = payload,
            payloadClass = Payload::class.java
        )

        fun Dynamic(
            defaultEnabled: Boolean,
            key: String? = null,
            name: String? = null,
            description: String? = null
        ) = Dynamic(
            key = key?.let(::Key) ?: Key.random(),
            isEnabled = defaultEnabled,
            name = name,
            description = description,
            payload = Unit,
            payloadType = Unit::class.java
        )

        fun <Payload> Dynamic(
            defaultEnabled: Boolean,
            defaultPayload: Payload,
            payloadClass: Class<Payload>,
            key: String? = null,
            name: String? = null,
            description: String? = null
        ) = Dynamic(
            key = key?.let(::Key) ?: Key.random(),
            isEnabled = defaultEnabled,
            name = name,
            description = description,
            payload = defaultPayload,
            payloadType = payloadClass
        )

        inline fun <reified Payload> Dynamic(
            defaultEnabled: Boolean,
            defaultPayload: Payload,
            key: String? = null,
            name: String? = null,
            description: String? = null
        ) = Dynamic(
            key = key,
            defaultEnabled = defaultEnabled,
            name = name,
            description = description,
            defaultPayload = defaultPayload,
            payloadClass = Payload::class.java
        )
    }
}
