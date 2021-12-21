import org.gradle.api.JavaVersion
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.api.provider.Provider
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.the
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.targets.js.nodejs.NodeJsRootExtension
import org.jetbrains.kotlin.gradle.targets.js.nodejs.NodeJsRootPlugin
import org.jetbrains.kotlin.gradle.targets.jvm.KotlinJvmTarget

fun Project.jvmVersion(provider: Provider<String>) {
    val version = provider.map { JavaVersion.toVersion(it) }.getOrElse(JavaVersion.current())
    configure<JavaPluginExtension> {
        targetCompatibility = version
        sourceCompatibility = version
    }
    configure<KotlinMultiplatformExtension> {
        targets.withType<KotlinJvmTarget> {
            compilations.all {
                kotlinOptions {
                    jvmTarget = version.toString()
                }
            }
        }
    }
}

fun Project.nodeVersion(provider: Provider<String>) {
    the<NodeJsRootExtension>().let { nodeJsExt ->
        provider.takeIf { it.isPresent }?.let {
            nodeJsExt.nodeVersion = it.get()
        }
    }
}
