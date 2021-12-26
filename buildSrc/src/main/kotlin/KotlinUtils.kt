import dev.petuska.npm.publish.dsl.NpmPublishExtension
import dev.petuska.npm.publish.dsl.PackageJson
import org.gradle.api.JavaVersion
import org.gradle.api.Project
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

fun Project.nodeVersion(provider: Provider<String>) {
    configure<NodeJsRootExtension> {
        provider.takeIf { it.isPresent }?.let {
            nodeVersion = it.get()
        }
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
