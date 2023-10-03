@Suppress("DSL_SCOPE_VIOLATION") // TODO: Remove once KTIJ-19369 is fixed
plugins {
    alias(deps.plugins.kotlin.jvm)
}

group = "io.github.pavelannin"
version = "1.0.0"

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}

dependencies {
    api(deps.kotlin.coroutines.core)
    testImplementation(deps.bundles.tests.unit)
}
