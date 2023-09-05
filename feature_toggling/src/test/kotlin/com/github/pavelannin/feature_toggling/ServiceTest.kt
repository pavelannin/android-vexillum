package com.github.pavelannin.feature_toggling

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

internal class ServiceTest {

    @Test
    fun `isEnabled should return true when feature is enabled`() {
        val service = FeatureTogglingService()
        val feature = FeatureToggle.Static(isEnabled = true)
        assertTrue(service.isEnabled(feature))
    }

    @Test
    fun `isEnabled should return false when feature is disabled`() {
        val service = FeatureTogglingService()
        val feature = FeatureToggle.Static(isEnabled = false)
        assertFalse(service.isEnabled(feature))
    }

    @Test
    fun `payload should return the correct payload for a static feature`() {
        val service = FeatureTogglingService()
        val feature = FeatureToggle.Static(isEnabled = false, payload = "my_key")
        assertEquals("my_key", service.payload(feature))
    }

    @Test
    fun `observeEnabled should observe emit provider when dynamic feature is enabled`() {
        val provider = MutableStateFlow<Boolean?>(value = null)
        val feature = FeatureToggle.Dynamic(defaultEnabled = true)
        val service = FeatureTogglingService(
            FeatureTogglingService.Provider {
                provider
                    .filterNotNull()
                    .map { FeatureTogglingService.Provider.Result(feature, newEnabled = it) }
                    .map(::setOf)
            }
        )

        runTest {
            val observe = service.observeEnabled(feature)
            // default value
            assertTrue(observe.first())

            provider.emit(value = false)
            assertFalse(observe.drop(count = 1).first())

            provider.emit(value = true)
            assertTrue(observe.drop(count = 1).first())
        }
    }

    @Test
    fun `observePayload should emit the correct payload for a dynamic feature`() {
        val provider = MutableStateFlow<String?>(value = null)
        val feature = FeatureToggle.Dynamic(defaultEnabled = true, defaultPayload = "key_default")
        val service = FeatureTogglingService(
            FeatureTogglingService.Provider {
                provider
                    .filterNotNull()
                    .map { FeatureTogglingService.Provider.Result(feature, newPayload = it) }
                    .map(::setOf)
            }
        )

        runTest {
            val observe = service.observePayload(feature)
            // default value
            assertEquals("key_default", observe.first())

            provider.emit(value = "key_1")
            assertEquals("key_1", observe.drop(count = 1).first())

            provider.emit(value = "key_2")
            assertEquals("key_2", observe.drop(count = 1).first())
        }
    }
}
