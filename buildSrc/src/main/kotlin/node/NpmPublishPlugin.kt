package node

import org.gradle.api.DefaultTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.tasks.Copy
import org.gradle.api.tasks.Exec
import java.io.File

class NpmPublishPlugin : Plugin<Project> {

    private lateinit var extension: NpmPublishExtension

    val nodePath: File
        get() = extension.nodePath

    val packageJson: File
        get() = extension.packageJson

    val token: String
        get() = extension.token

    val registry: String
        get() = extension.registry

    private val node: File
        get() = nodePath.resolve("node")

    private val npm: File
        get() = nodePath.resolve("node_modules/npm/bin/npm-cli.js")

    private val npmProject: File
        get() = File(packageJson.parent)

    private fun Project.createNpmLoginTask(name: String): DefaultTask {
        val setRegistryName = "${name}SetRegistry"
        val setRegistry = tasks.maybeCreate(setRegistryName, Exec::class.java).let {
            it.group = "nodeJs"
            it.standardOutput = System.out
            it.doFirst {
                it.executable = node.absolutePath
                it.args(npm, "set", "registry", "https://$registry")
            }
        }
        val setToken = tasks.maybeCreate("${name}SetToken", Exec::class.java).let {
            it.dependsOn(setRegistry)
            it.group = "nodeJs"
            it.standardOutput = System.out
            it.doFirst {
                it.executable = node.absolutePath
                it.args(npm, "set", "//$registry/:_authToken", token)
            }
        }
        return tasks.maybeCreate(name, DefaultTask::class.java).also {
            it.group = "nodeJs"
            it.dependsOn(setToken)
        }
    }

    private fun Project.createNpmPublishTask(name: String): Exec {
        return tasks.maybeCreate(name, Exec::class.java).also {
            it.group = "nodeJs"
            it.standardOutput = System.out
            it.doFirst {
                it.args(npm, "publish", npmProject, "--access", "public")
                it.executable = node.absolutePath
            }
        }
    }

    private fun Project.createCopyRootProjectFilesTask(name: String): Task {
        return tasks.maybeCreate(name, Copy::class.java).also {
            it.group = "nodeJs"
            it.from(rootProject.projectDir)
            it.include("README*")
            it.include("CONTRIB*")
            it.include("LICENSE*")
            it.destinationDir = buildDir
            it.doFirst {
                it.destinationDir = File(packageJson.parent)
            }
        }
    }

    private fun Project.createLiftPackageJsonTask(name: String): LiftPackageJsonTask {
        return tasks.maybeCreate(name, LiftPackageJsonTask::class.java).also {
            it.group = "nodeJs"
            it.doFirst {
                it.packageJsonFile = packageJson
                it.liftingActions = extension.liftingActions
                it.rawLiftingActions = extension.rawLiftingActions
            }
        }
    }

    override fun apply(target: Project) {
        extension = target.extensions.create("greeting", NpmPublishExtension::class.java)
        val login = target.createNpmLoginTask("npmLogin")
        val publish = target.createNpmPublishTask("npmPublish")
        val lift = target.createLiftPackageJsonTask("liftPackageJson")
        val copy = target.createCopyRootProjectFilesTask("copyFilesNextToPackageJson")
        publish.dependsOn(login)
        publish.dependsOn(lift)
        lift.dependsOn(copy)
    }
}

