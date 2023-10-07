@Suppress("DSL_SCOPE_VIOLATION") // TODO: Remove once KTIJ-19369 is fixed
plugins {
    alias(deps.plugins.android.application) apply false
    alias(deps.plugins.android.library) apply false
    alias(deps.plugins.kotlin.android) apply false
    alias(deps.plugins.kotlin.jvm) apply false
    alias(deps.plugins.publish) apply true
    alias(deps.plugins.publish.gradle.plugin) apply false
}
true // Needed to make the Suppress annotation work for the plugins block

nexusPublishing {
    repositories {
        sonatype {
            nexusUrl.set(uri("https://s01.oss.sonatype.org/service/local/"))
            snapshotRepositoryUrl.set(uri("https://s01.oss.sonatype.org/content/repositories/snapshots/"))
            stagingProfileId.set(property("sonatypeStagingProfileId").toString())
            username.set(project.property("ossrhUsername").toString())
            password.set(project.property("ossrhPassword").toString())
        }
    }
}
