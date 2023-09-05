@Suppress("DSL_SCOPE_VIOLATION") // TODO: Remove once KTIJ-19369 is fixed
plugins {
    alias(deps.plugins.kotlin.jvm)
}

group = "com.github.pavelannin"
version = "0.1.0"

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

dependencies {
    api(deps.kotlin.coroutines.core)
    testImplementation(deps.bundles.tests.unit)
}
