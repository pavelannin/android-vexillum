<p align="center">
  <a href="https://en.wikipedia.org/wiki/Vexillum">Vexillum</a> (/vɛkˈsɪləm/) was a flag-like object used as a military standard by units in the Ancient Roman army.
  <br> <b>Vexillum</b> is a Kotlin package for managing feature flags.
</p>

[![License](https://img.shields.io/badge/license-MIT-blue)](http://www.opensource.org/licenses/mit-license.php)
![Maven Central](https://img.shields.io/maven-central/v/io.github.pavelannin/vexillum)

# Setup
## Gradle
```gradle
implementation "io.github.pavelannin:vexillum:0.1.1"
```

# Quickstart
## 1. Feature flags spaces
```kotlin
// Space for feature flags
object LocalFeatureFlags {

    // Static type cannot be changed after compilation
    val isQrScannerEnabled = FeatureToggle.Static(
        isEnabled = true, // Required
        key = "qr_scanner", // Optional, unique to all space (by default, the UUID is used)
        name = "Qr Scanner", // Optional, human readable name
        description = "Enable the ability to scan QR codes", // Optional, human readable description,
        payload = Unit // Optional
    )

    // Dynamic type change in runtime
    val isAuthEnabled = FeatureToggle.Dynamic(
        defaultEnabled = true, // Required, initial value
        key = "auth", // Optional, unique to all space (by default, the UUID is used)
        name = "Auth", // Optional, human readable name
        description = "Enable user authorization", // Optional, human readable description,
        defaultPayload = Unit // Optional, initial payload value
    )
}
```

## 2. Creating providers for dynamic feature flags
```kotlin
class ExampleService {
    suspend fun fetchFeatureFlags(): FeatureFlagsResponse = TODO()
    data class FeatureFlagsResponse(val auth: Boolean)
}

fun exampleProviderFeatureFlags(exampleService: ExampleService) = Vexillum.Provider {
    flow {
        val response = exampleService.fetchFeatureFlags()
        emit(
            setOf(
                Vexillum.Provider.Result(
                    feature = LocalFeatureFlags.isAuthEnabled,
                    newEnabled = response.auth,
                    newPayload = Unit
                ),
                // other feature flags
            )
        )
    }
}
```

## 3. Creating Vexillum
```kotlin
val vexillum = Vexillum(
    exampleProviderFeatureFlags(ExampleService()),
    // supports multi providers
)
```

## 4. Usage
```kotlin
// Only for static
vexillum.isEnabled(LocalFeatureFlags.isQrScannerEnabled) // Boolean
vexillum.payload(LocalFeatureFlags.isQrScannerEnabled) // Payload generic type

// Only for dynamic
vexillum.observeEnabled(LocalFeatureFlags.isAuthEnabled) // StateFlow<Boolean>
vexillum.observePayload(LocalFeatureFlags.isAuthEnabled) // StateFlow<Payload> generic type
```
