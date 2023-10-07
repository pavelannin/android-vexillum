@Suppress("DSL_SCOPE_VIOLATION") // TODO: Remove once KTIJ-19369 is fixed
plugins {
    alias(deps.plugins.kotlin.jvm)
    alias(deps.plugins.publish.gradle.plugin)
    `java-gradle-plugin`
    `maven-publish`
    signing
}

val PUBLISH_GROUP_ID = "io.github.pavelannin"
val PUBLISH_ARTIFACT_ID = "vexillum-plugin"
val PUBLISH_VERSION = "0.1.2"

group = PUBLISH_GROUP_ID
version = PUBLISH_VERSION

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}

gradlePlugin {
    plugins {
        create("VexillumGeneratorPlugin") {
            id = "io.github.pavelannin.vexillum"
            displayName = "Generate kotlin files for vexillum"
            implementationClass = "io.github.pavelannin.vexillum.generator.VexillumPlugin"
        }
    }
}

pluginBundle {
    website = "https://github.com/pavelannin/android-vexillum"
    vcsUrl = "https://github.com/pavelannin/android-vexillum"
    description = "Generate kotlin files for vexillum"
    tags = listOf("kotlin", "generation", "vexillum")
}
fun MavenPom.create() {
    name.set(PUBLISH_ARTIFACT_ID)
    description.set("Generate kotlin files for vexillum")
    url.set("https://github.com/pavelannin/android-vexillum")
    licenses {
        license {
            name.set("MIT License")
            url.set("http://www.opensource.org/licenses/mit-license.php")
        }
    }
    developers {
        developer {
            name.set("Pavel Annin")
            email.set("pavelannin.dev@gmail.com")
        }
    }
    scm {
        connection.set("scm:git:github.com/pavelannin/android-vexillum.git")
        developerConnection.set("scm:git:ssh://github.com/pavelannin/android-vexillum.git")
        url.set("https://github.com/pavelannin/android-vexillum/tree/main")
    }
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            groupId = PUBLISH_GROUP_ID
            artifactId = PUBLISH_ARTIFACT_ID
            version = PUBLISH_VERSION
            from(project.components.getByName("java"))

            java.withSourcesJar()
            java.withJavadocJar()

            pom { create() }
        }
    }
}

signing {
    sign(publishing.publications["mavenJava"])
}

configure<PublishingExtension> {
    publications {
        afterEvaluate {
            named<MavenPublication>("pluginMaven") {
                pom { create() }
            }
            named<MavenPublication>("VexillumGeneratorPluginPluginMarkerMaven") {
                pom { create() }
            }
        }
    }
}

dependencies {
    implementation(deps.gradle.android)
    implementation(deps.gradle.kotlin)
    implementation(deps.kotlin.poet)
    implementation(deps.kotlin.coroutines.core)
    testImplementation(deps.bundles.tests.unit)
}
