package com.github.pavelannin.feature_toggling

import com.github.pavelannin.feature_toggling.internal.DefaultFeatureTogglingService
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface FeatureTogglingService {
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
        ): FeatureTogglingService = DefaultFeatureTogglingService(providers, dispatcher)

        operator fun invoke(
            vararg providers: Provider,
            dispatcher: CoroutineDispatcher = Dispatchers.Default
        ): FeatureTogglingService = DefaultFeatureTogglingService(providers.toList(), dispatcher)
    }
}
