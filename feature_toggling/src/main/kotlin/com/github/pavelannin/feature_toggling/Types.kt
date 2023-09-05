package com.github.pavelannin.feature_toggling

import java.util.UUID

sealed class FeatureToggle<Payload>(
    internal open val key: UUID,
    internal open val isEnabled: Boolean,
    open val description: String?,
    internal open val payload: Payload,
    internal open val payloadType: Class<Payload>
) {

    data class Static<Payload> internal constructor(
        override val isEnabled: Boolean,
        override val description: String?,
        override val payload: Payload,
        override val payloadType: Class<Payload>
    ) : FeatureToggle<Payload>(
        key = UUID.randomUUID(),
        isEnabled = isEnabled,
        description = description,
        payload = payload,
        payloadType = payloadType
    )

    data class Dynamic<Payload> internal constructor(
        override val isEnabled: Boolean,
        override val description: String?,
        override val payload: Payload,
        override val payloadType: Class<Payload>
    ) : FeatureToggle<Payload>(
        key = UUID.randomUUID(),
        isEnabled = isEnabled,
        description = description,
        payload = payload,
        payloadType = payloadType
    )

    companion object {
        fun Static(isEnabled: Boolean, description: String? = null) = Static(
            isEnabled = isEnabled,
            description = description,
            payload = Unit,
            payloadType = Unit::class.java
        )

        fun <Payload> Static(isEnabled: Boolean, description: String? = null, payload: Payload, payloadClass: Class<Payload>) = Static(
            isEnabled = isEnabled,
            description = description,
            payload = payload,
            payloadType = payloadClass
        )

        inline fun <reified Payload> Static(isEnabled: Boolean, description: String? = null, payload: Payload) = Static(
            isEnabled = isEnabled,
            description = description,
            payload = payload,
            payloadClass = Payload::class.java
        )

        fun Dynamic(defaultEnabled: Boolean, description: String? = null) = Dynamic(
            isEnabled = defaultEnabled,
            description = description,
            payload = Unit,
            payloadType = Unit::class.java
        )

        fun <Payload> Dynamic(
            defaultEnabled: Boolean,
            description: String? = null,
            defaultPayload: Payload,
            payloadClass: Class<Payload>
        ) = Dynamic(
            isEnabled = defaultEnabled,
            description = description,
            payload = defaultPayload,
            payloadType = payloadClass
        )

        inline fun <reified Payload> Dynamic(
            defaultEnabled: Boolean,
            description: String? = null,
            defaultPayload: Payload
        ) = Dynamic(
            defaultEnabled = defaultEnabled,
            description = description,
            defaultPayload = defaultPayload,
            payloadClass = Payload::class.java
        )
    }
}
