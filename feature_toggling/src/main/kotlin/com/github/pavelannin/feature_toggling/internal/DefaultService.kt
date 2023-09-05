package com.github.pavelannin.feature_toggling.internal

import com.github.pavelannin.feature_toggling.FeatureTogglingService
import com.github.pavelannin.feature_toggling.FeatureToggle
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.UUID

internal class DefaultFeatureTogglingService(
    providers: List<FeatureTogglingService.Provider>,
    dispatcher: CoroutineDispatcher
) : FeatureTogglingService, CoroutineScope by CoroutineScope(dispatcher) {

    private val cache = ObservableMutableMap<UUID, FeatureToggle.Dynamic<*>>()


    init {
        if (providers.isNotEmpty()) {
            launch {
                providers.map(FeatureTogglingService.Provider::observe)
                    .merge()
                    .map { list -> list.associate { it.feature.key to it.updateFeature() } }
                    .collect(cache::putAll)
            }
        }
    }

    override fun isEnabled(feature: FeatureToggle.Static<*>): Boolean = feature.isEnabled

    override fun observeEnabled(feature: FeatureToggle.Dynamic<*>): StateFlow<Boolean> = cache.observe(feature.key)
        .map { cacheFeature -> (cacheFeature ?: feature).isEnabled }
        .stateIn(scope = this, started = SharingStarted.Eagerly, initialValue = (cache[feature.key] ?: feature).isEnabled)


    override fun <Payload> payload(feature: FeatureToggle.Static<Payload>): Payload = feature.payload

    override fun <Payload> observePayload(feature: FeatureToggle.Dynamic<Payload>): Flow<Payload> = cache.observe(feature.key)
        .map { cacheFeature -> feature.payloadType.cast((cacheFeature ?: feature).payload) }
        .stateIn(
            scope = this,
            started = SharingStarted.Eagerly,
            initialValue = feature.payloadType.cast((cache[feature.key] ?: feature).payload)
        )
}

private fun <Payload> FeatureTogglingService.Provider.Result<Payload>.updateFeature() = this.feature
    .copy(isEnabled = this.newEnabled, payload = this.newPayload)
