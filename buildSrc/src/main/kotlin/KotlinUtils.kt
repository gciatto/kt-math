import dev.petuska.npm.publish.dsl.NpmPublishExtension
import dev.petuska.npm.publish.dsl.PackageJson
import org.gradle.api.JavaVersion
import org.gradle.api.Project
import org.gradle.api.logging.LogLevel
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.api.provider.Provider
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.targets.js.nodejs.NodeJsRootExtension
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

fun Project.nodeVersion(default: Provider<String>, override: Any? = null) {
    configure<NodeJsRootExtension> {
        nodeVersion = override?.toString() ?: default.takeIf { it.isPresent }?.get() ?: nodeVersion
        project.logger.log(LogLevel.LIFECYCLE, "Using NodeJS v{}", nodeVersion)
    }
}

fun Project.packageJson(handler: PackageJson.() -> Unit) {
    configure<NpmPublishExtension> {
        publications {
            all {
                packageJson(handler)
            }
        }
    }
}
