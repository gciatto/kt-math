package org.gciatto.kt.math

import java.io.File
import java.lang.reflect.Modifier
import kotlin.reflect.KVisibility
import kotlin.reflect.full.companionObject
import kotlin.reflect.jvm.kotlinFunction
import kotlin.reflect.jvm.kotlinProperty
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * Checks that [TestJavaUsage] covers the whole API as Java sees it: every public method, field and constructor of
 * every public class (and of every file facade with public functions, e.g. `BigDecimalMath`) must be mentioned in
 * the section of `TestJavaUsage.java` about its class, i.e. the lines from `// api:<Class>` to the next `// api:`.
 */
class TestJavaUsageCoverage {
    private val source = File("src/jvmTest/java/org/gciatto/kt/math/TestJavaUsage.java").readText()

    private val sections: Map<String, String> =
        source
            .split(Regex("""^\s*// api:""", RegexOption.MULTILINE))
            .drop(1)
            .groupBy({ it.substringBefore('\n').trim() }, { it.substringAfter('\n') })
            .mapValues { (_, parts) -> parts.joinToString("\n") }

    /** The API as Java sees it: the members of each public class or facade, by simple name. */
    private fun api(): Map<String, Set<String>> {
        val root =
            File(
                BigInteger::class.java.protectionDomain.codeSource.location
                    .toURI(),
            )
        val classes =
            root
                .walk()
                .filter { it.extension == "class" && "/internal/" !in it.path && '$' !in it.name }
                .map {
                    it
                        .relativeTo(root)
                        .path
                        .removeSuffix(".class")
                        .replace(File.separatorChar, '.')
                }.map { Class.forName(it, false, javaClass.classLoader) }
        // merged by simple name (e.g. big/Utils.kt and Utils.kt)
        return classes
            .map { api(it) }
            .groupBy({ it.first }, { it.second })
            .mapValues { (_, members) -> members.flatten().toSet() }
            .filterValues { it.isNotEmpty() }
    }

    private fun api(type: Class<*>): Pair<String, Set<String>> {
        val members =
            when (type.getAnnotation(Metadata::class.java)?.kind) {
                CLASS -> if (type.kotlin.visibility == KVisibility.PUBLIC) classMembers(type) else emptyList()
                FILE_FACADE -> facadeMembers(type)
                else -> emptyList()
            }
        return type.simpleName to members.toSet()
    }

    private fun classMembers(type: Class<*>): List<String> {
        val companion = type.kotlin.companionObject?.java
        val statics = type.declaredMethods.map { it.name }.toSet()
        val constructors = if (type.constructors.any { !it.isSynthetic }) listOf(CONSTRUCTOR) else emptyList()
        return publicMembers(type) + publicMembers(companion).filter { it !in statics } + constructors
    }

    private fun facadeMembers(type: Class<*>): List<String> =
        type.declaredMethods.filter { it.kotlinFunction?.visibility == KVisibility.PUBLIC }.map { it.name } +
            type.declaredFields.filter { it.kotlinProperty?.visibility == KVisibility.PUBLIC }.map { it.name }

    private fun publicMembers(type: Class<*>?): List<String> =
        type
            ?.let { (it.declaredMethods.toList() + it.declaredFields.toList()) }
            .orEmpty()
            .filter { Modifier.isPublic(it.modifiers) && !it.isSynthetic && '$' !in it.name && it.name != "Companion" }
            .filterNot { it is java.lang.reflect.Method && it.isBridge }
            .map { it.name }

    @Test
    fun everyMemberIsUsed() {
        val missing =
            api().flatMap { (type, members) ->
                val section = sections[type].orEmpty()
                members
                    .filterNot { member ->
                        val usage = if (member == CONSTRUCTOR) """new $type\(""" else """\b$member\b"""
                        Regex(usage).containsMatchIn(section)
                    }.map { "$type.$it" }
            }
        assertEquals(emptyList(), missing.sorted(), "TestJavaUsage misses part of the API")
    }

    private companion object {
        const val CLASS = 1
        const val FILE_FACADE = 2
        const val CONSTRUCTOR = "<init>"

        @JvmStatic
        fun main(args: Array<String>) {
            TestJavaUsageCoverage().api().forEach { (type, members) -> println("$type: ${members.sorted()}") }
        }
    }
}
