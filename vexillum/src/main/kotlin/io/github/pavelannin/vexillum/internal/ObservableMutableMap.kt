package io.github.pavelannin.vexillum.internal

import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map

internal class ObservableMutableMap<Key, Value>(
    private val map: MutableMap<Key, Value> = mutableMapOf()
) : MutableMap<Key, Value> by map {

    private val sharedFlow: MutableSharedFlow<MutableMap<Key, Value>> =
        MutableSharedFlow(onBufferOverflow = BufferOverflow.DROP_OLDEST, replay = 1)

    init {
        sharedFlow.tryEmit(map)
    }

    override fun put(key: Key, value: Value): Value? = mutate { map.put(key, value) }

    override fun putAll(from: Map<out Key, Value>) = mutate { map.putAll(from) }

    override fun remove(key: Key): Value? = mutate { map.remove(key) }

    override fun clear() = mutate { map.clear() }

    fun observe(key: Key): Flow<Value?> = sharedFlow
        .map { it[key] }
        .distinctUntilChanged()

    private fun <T> mutate(block: MutableMap<Key, Value>.() -> T): T {
        val value = map.let(block)
        sharedFlow.tryEmit(map)
        return value
    }
}
