import org.gradle.api.Project

fun capitalize(s: String) = s[0].toUpperCase() + s.substring(1)

val Project.docDir: String
    get() = "$buildDir/doc"

fun Project.getPropertyOrWarnForAbsence(key: String): String? {
    val value = property(key)?.toString()
    if (value.isNullOrBlank()) {
        System.err.println("WARNING: $key is not set")
    }
    return value
}