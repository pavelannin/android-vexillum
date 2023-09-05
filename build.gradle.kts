@Suppress("DSL_SCOPE_VIOLATION") // TODO: Remove once KTIJ-19369 is fixed
plugins {
    alias(deps.plugins.android.application) apply false
    alias(deps.plugins.android.library) apply false
    alias(deps.plugins.kotlin.android) apply false
    alias(deps.plugins.kotlin.jvm) apply false
}
true // Needed to make the Suppress annotation work for the plugins block
