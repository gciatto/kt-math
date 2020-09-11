import org.gradle.api.Project
import java.io.File

fun capitalize(s: String) = s[0].toUpperCase() + s.substring(1)

val Project.docDir: File
    get() = buildDir.resolve("doc")

fun Project.getPropertyOrWarnForAbsence(key: String): String? {
    val value = property(key)?.toString()
    if (value.isNullOrBlank()) {
        System.err.println("WARNING: $key is not set")
    }
    return value
}