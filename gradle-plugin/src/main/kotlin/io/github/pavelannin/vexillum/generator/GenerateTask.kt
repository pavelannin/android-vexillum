package io.github.pavelannin.vexillum.generator

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
import org.gradle.work.InputChanges
import java.io.File

open class GenerateTask : DefaultTask() {
    @get:Input var isLoggingEnabled: Boolean = false
    @get:Input lateinit var spaces: List<VexillumExtensions.Space>
    @get:OutputDirectory lateinit var outputDir: File

    @TaskAction
    fun execute(inputChanges: InputChanges) {
        if (!outputDir.exists() && !outputDir.mkdirs()) throw GradleException("Vexillum: Not found output dir")
        runBlocking(Dispatchers.IO) {
            generateSpaces(
                spaces = spaces,
                outputDir = outputDir,
                dispatcher = Dispatchers.IO,
                isLoggingEnabled = isLoggingEnabled
            )
        }
    }
}

private suspend fun generateSpaces(
    spaces: List<VexillumExtensions.Space>,
    outputDir: File,
    dispatcher: CoroutineDispatcher,
    isLoggingEnabled: Boolean,
    log: (String) -> Unit = ::println,
) = withContext(dispatcher) {
    if (isLoggingEnabled) log("Vexillum: Start generation files")
    spaces
        .onEach { space -> if (isLoggingEnabled) log("Vexillum: Generate ${space.name} space") }
        .map { it.generateFileSpec() }
        .forEach { fileSpec -> fileSpec.writeTo(outputDir) }
    if (isLoggingEnabled) log("Vexillum: End generation files")
}
