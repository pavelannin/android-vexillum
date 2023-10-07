package io.github.pavelannin.vexillum.internal

import io.github.pavelannin.vexillum.Vexillum
import io.github.pavelannin.vexillum.FeatureToggle
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

internal class DefaultService(
    providers: List<Vexillum.Provider>,
    dispatcher: CoroutineDispatcher
) : Vexillum, CoroutineScope by CoroutineScope(dispatcher) {

    private val cache = ObservableMutableMap<FeatureToggle.Key, FeatureToggle.Dynamic<*>>()


    init {
        if (providers.isNotEmpty()) {
            launch {
                providers.map(Vexillum.Provider::observe)
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

    override fun <Payload> observePayload(feature: FeatureToggle.Dynamic<Payload>): StateFlow<Payload> = cache.observe(feature.key)
        .map { cacheFeature -> feature.payloadType.cast((cacheFeature ?: feature).payload) }
        .stateIn(
            scope = this,
            started = SharingStarted.Eagerly,
            initialValue = feature.payloadType.cast((cache[feature.key] ?: feature).payload)
        )
}

private fun <Payload> Vexillum.Provider.Result<Payload>.updateFeature() = this.feature
    .copy(isEnabled = this.newEnabled, payload = this.newPayload)
