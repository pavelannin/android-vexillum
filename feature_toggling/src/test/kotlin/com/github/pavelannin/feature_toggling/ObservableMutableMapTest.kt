package com.github.pavelannin.feature_toggling

import com.github.pavelannin.feature_toggling.internal.ObservableMutableMap
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

internal class ObservableMutableMapTest {
    @Test
    fun `observe should emit value when key-value pair changes`() = runTest {
        val observableMap = ObservableMutableMap<String, Int>()
        val flow = observableMap.observe("key")

        assertNull(null, flow.first())

        observableMap["key"] = 1
        assertEquals(1, flow.first())

        observableMap["key"] = 2
        assertEquals(2, flow.first())

        observableMap.remove("key")
        assertNull(null, flow.first())

        observableMap.clear()
        assertNull(null, flow.first())
    }
}
