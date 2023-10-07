package io.github.pavelannin.vexillum.generator

import com.android.build.gradle.AppExtension
import com.android.build.gradle.BaseExtension
import org.gradle.api.GradleException
import org.gradle.api.Plugin
import org.gradle.api.Project
import com.android.build.gradle.LibraryExtension
import com.android.build.gradle.api.BaseVariant
import com.android.build.gradle.internal.dsl.BaseAppModuleExtension
import org.gradle.configurationcache.extensions.capitalized
import java.io.File

class VexillumPlugin : Plugin<Project> {

    override fun apply(project: Project) {
        val pluginExtension = project.extensions.create(VexillumExtensions.NAME, VexillumExtensions::class.java)
        val baseExtension = project.extensions.findByType(BaseAppModuleExtension::class.java)
            ?: project.extensions.findByType(LibraryExtension::class.java)
            ?: throw GradleException("Vexillum: must be used with android plugin or library")

        val generatedDir = File(project.buildDir, "generated/source/vexillum")
        forEachVariants(baseExtension) { variant: BaseVariant ->
            val outputDir = File(generatedDir, variant.dirName)
            baseExtension.sourceSets.onEach { it.java.srcDir(outputDir) }

            val variantName = variant.name.takeIf { it.isNotBlank() }?.capitalized()
            val generateTask = project.tasks
                .create("vexillumGenerate$variantName", GenerateTask::class.java) { task ->
                    task.isLoggingEnabled = pluginExtension.loggingEnabled
                    task.spaces = pluginExtension.spaces
                    task.outputDir = outputDir
                }
            variant.registerJavaGeneratingTask(generateTask, outputDir)
        }

        with(project.dependencies) {
            add("implementation", "io.github.pavelannin:vexillum:0.1.2")
        }
    }
}

private fun forEachVariants(extension: BaseExtension, action: (BaseVariant) -> Unit) = when (extension) {
    is AppExtension -> extension.applicationVariants.all(action)
    is LibraryExtension -> extension.libraryVariants.all(action)
    else -> throw GradleException("Vexillum: must be used with android plugin or library")
}
