import dev.petuska.npm.publish.extension.NpmPublishExtension
import dev.petuska.npm.publish.extension.domain.json.PackageJson
import dev.petuska.npm.publish.extension.domain.json.Person
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

val Project.npmCompliantVersion: String
    get() = version.toString().split("+")[0]

fun Project.nodeVersion(default: Provider<String>, override: Any? = null) {
    configure<NodeJsRootExtension> {
        nodeVersion = override?.toString() ?: default.takeIf { it.isPresent }?.get() ?: nodeVersion
        project.logger.log(LogLevel.LIFECYCLE, "Using NodeJS v{}", nodeVersion)
    }
}

fun Project.packageJson(handler: PackageJson.() -> Unit) {
    configure<NpmPublishExtension> {
        packages {
            all {
                packageJson(handler)
            }
        }
    }
}

fun PackageJson.person(developer: Developer): Person =
    this.Person {
        name.set(developer.name)
        email.set(developer.email)
        url.set(developer.url)
    }
    