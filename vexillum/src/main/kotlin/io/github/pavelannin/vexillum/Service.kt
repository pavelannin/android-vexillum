package io.github.pavelannin.vexillum

import io.github.pavelannin.vexillum.internal.DefaultService
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface Vexillum {
    fun isEnabled(feature: FeatureToggle.Static<*>): Boolean
    fun observeEnabled(feature: FeatureToggle.Dynamic<*>): StateFlow<Boolean>
    fun <Payload> payload(feature: FeatureToggle.Static<Payload>): Payload
    fun <Payload> observePayload(feature: FeatureToggle.Dynamic<Payload>): Flow<Payload>

    fun interface Provider {
        fun observe(): Flow<Set<Result<*>>>

        data class Result<Payload> internal constructor(
            val feature: FeatureToggle.Dynamic<Payload>,
            val newEnabled: Boolean,
            val newPayload: Payload
        )

        companion object {
            fun <Payload> Result(
                feature: FeatureToggle.Dynamic<Payload>,
                newEnabled: Boolean = feature.isEnabled,
                newPayload: Payload = feature.payload
            ) = Provider.Result(feature, newEnabled, newPayload)
        }
    }

    companion object {
        operator fun invoke(
            providers: List<Provider> = emptyList(),
            dispatcher: CoroutineDispatcher = Dispatchers.Default
        ): Vexillum = DefaultService(providers, dispatcher)

        operator fun invoke(
            vararg providers: Provider,
            dispatcher: CoroutineDispatcher = Dispatchers.Default
        ): Vexillum = DefaultService(providers.toList(), dispatcher)
    }
}
