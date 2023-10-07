package io.github.pavelannin.vexillum.generator

import java.io.Serializable

open class VexillumExtensions {
    var loggingEnabled: Boolean = true

    internal val spaces = mutableListOf<Space>()
    fun space(name: String, packageClass: String, init: Space.() -> Unit) {
        val space = Space(name, packageClass).apply(init)
        spaces.add(space)
    }

    class Space internal constructor(
        internal val name: String,
        internal val packageClass: String
    ) : Serializable {
        internal val features = mutableListOf<Feature>()

        fun static(name: String, enabled: Boolean, init: Feature.Static.() -> Unit = {}) {
            val feature = Feature.Static(name, enabled).apply(init)
            features.add(feature)
        }

        fun dynamic(name: String, defaultEnabled: Boolean, init: Feature.Dynamic.() -> Unit = {}) {
            val feature = Feature.Dynamic(name, defaultEnabled).apply(init)
            features.add(feature)
        }
    }

    sealed class Feature(
        internal open val name: String,
        internal open val enabled: Boolean
    ) : Serializable {
        internal var payload: Payload? = null
        var comment: String? = null
        var description: String? = null

        class Static internal constructor(
            override val name: String,
            override val enabled: Boolean
        ) : Feature(name, enabled) {
            fun payload(type: String, value: String) {
                payload = Payload(type, value)
            }
        }

        class Dynamic internal constructor(
            override val name: String,
            override val enabled: Boolean
        ) : Feature(name, enabled) {
            fun defaultPayload(type: String, value: String) {
                payload = Payload(type, value)
            }
        }
    }

    internal data class Payload(val type: String, val value: String)

    internal companion object {
        const val NAME = "vexillum"
    }
}
