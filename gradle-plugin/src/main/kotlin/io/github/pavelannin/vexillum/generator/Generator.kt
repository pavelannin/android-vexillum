package io.github.pavelannin.vexillum.generator

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.TypeVariableName
import com.squareup.kotlinpoet.asTypeName

internal fun VexillumExtensions.Space.generateFileSpec() = FileSpec
    .builder(this.packageClass, fileName = "${this.name.capitalize()}FeatureToggles")
    .addType(this.generateTypeSpec())
    .addImport("io.github.pavelannin.vexillum", "FeatureToggle")
    .build()

private fun VexillumExtensions.Space.generateTypeSpec() = TypeSpec.objectBuilder(name = "${this.name.capitalize()}FeatureToggles")
    .addProperties(this.features.map(VexillumExtensions.Feature::generatePropertySpec))
    .build()

private fun VexillumExtensions.Feature.generatePropertySpec(): PropertySpec {
    val featureType = when (this) {
        is VexillumExtensions.Feature.Dynamic -> ClassName("io.github.pavelannin.vexillum", "FeatureToggle.Dynamic")
        is VexillumExtensions.Feature.Static -> ClassName("io.github.pavelannin.vexillum", "FeatureToggle.Static")
    }.parameterizedBy(this.payload?.let { TypeVariableName(it.type) } ?: Unit::class.asTypeName())

    return PropertySpec
        .builder(name = this.name.decapitalize(), type = featureType)
        .mutable(mutable = false)
        .let { builder -> this.comment?.let(builder::addKdoc) ?: builder }
        .let { builder ->
            when (this) {
                is VexillumExtensions.Feature.Dynamic -> builder.initializer(
                    """
                    %T(
                        defaultEnabled = %L,
                        defaultPayload = %L,
                        key = %S,
                        name = %S,
                        description = %S
                    )
                    """.trimIndent(),
                    featureType, this.enabled, this.payload?.value ?: Unit, this.name.decapitalize(), this.name, this.description
                )

                is VexillumExtensions.Feature.Static -> builder.initializer(
                    """
                    %T(
                        isEnabled = %L,
                        payload = %L,
                        key = %S,
                        name = %S,
                        description = %S
                    )
                    """.trimIndent(),
                    featureType, this.enabled, this.payload?.value ?: Unit, this.name.decapitalize(), this.name, this.description
                )
            }
        }
        .build()
}
