@Suppress("DSL_SCOPE_VIOLATION") // TODO: Remove once KTIJ-19369 is fixed
plugins {
    alias(deps.plugins.kotlin.jvm)
    `maven-publish`
    signing
}

val PUBLISH_GROUP_ID = "io.github.pavelannin"
val PUBLISH_ARTIFACT_ID = "vexillum"
val PUBLISH_VERSION = "0.1.1"

group = PUBLISH_GROUP_ID
version = PUBLISH_VERSION

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}

afterEvaluate {
    publishing {
        publications {
            create<MavenPublication>("mavenJava") {
                groupId = PUBLISH_GROUP_ID
                artifactId = PUBLISH_ARTIFACT_ID
                version = PUBLISH_VERSION
                from(project.components.getByName("java"))

                java.withSourcesJar()
                java.withJavadocJar()

                pom {
                    name.set(PUBLISH_ARTIFACT_ID)
                    description.set("Android feature toggling")
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
            }
        }
    }
}

signing {
    sign(publishing.publications)
}

dependencies {
    api(deps.kotlin.coroutines.core)
    testImplementation(deps.bundles.tests.unit)
}
