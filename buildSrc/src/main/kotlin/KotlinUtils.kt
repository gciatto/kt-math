import org.gradle.api.JavaVersion
import org.gradle.api.NamedDomainObjectCollection
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.api.provider.Provider
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.dsl.KotlinProjectExtension
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinJsCompilation
import org.jetbrains.kotlin.gradle.plugin.mpp.pm20.targets
import org.jetbrains.kotlin.gradle.targets.js.KotlinJsTarget
import org.jetbrains.kotlin.gradle.targets.js.nodejs.NodeJsRootExtension
import org.jetbrains.kotlin.gradle.targets.js.npm.PackageJson
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

fun Project.jvmVersion(provider: Provider<String>) {
    val version = provider.map { JavaVersion.toVersion(it) }.getOrElse(JavaVersion.current())
    configure<JavaPluginExtension> {
        targetCompatibility = version
        sourceCompatibility = version
    }
    tasks.withType<KotlinCompile> {
        kotlinOptions {
            jvmTarget = version.toString()
        }
    }
}

fun Project.nodeVersion(provider: Provider<String>) {
    configure<NodeJsRootExtension> {
        provider.takeIf { it.isPresent }?.let {
            nodeVersion = it.get()
        }
    }
}

private fun NamedDomainObjectCollection<KotlinJsCompilation>.configPackageJson(handler: PackageJson.() -> Unit) {
    getByName("main").packageJson(handler)
}

fun Project.packageJson(handler: PackageJson.() -> Unit) {
    configure<KotlinProjectExtension> {
        targets.filterIsInstance<KotlinJsTarget>().forEach {
            it.compilations.configPackageJson(handler)
        }
    }
}
