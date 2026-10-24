plugins {
    alias(libs.plugins.kotlin.jvm) apply false
    alias(libs.plugins.kotlin.serialization) apply false
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.ksp) apply false
}

// Check production imports as well as declared project dependencies to keep layer boundaries explicit.
tasks.register("verifyArchitecture") {
    group = "verification"
    description = "Checks module dependencies and forbidden imports between architecture layers."
    doLast {
        val dependencies = mapOf(
            "domain" to emptySet<String>(),
            "data" to setOf(":domain"),
            "core/ui" to setOf(":domain"),
            "feature/collection" to setOf(":domain", ":core:ui"),
            "feature/editor" to setOf(":domain", ":core:ui"),
            "feature/settings" to setOf(":domain", ":core:ui")
        )
        val forbidden = mapOf(
            "domain" to listOf("android.", "androidx.", "org.koin.", "kotlinx.serialization.", "com.bobon.mygametop.data.", "com.bobon.mygametop.presentation.", "com.bobon.mygametop.localization.", "com.bobon.mygametop.ui.", "com.bobon.mygametop.core."),
            "data" to listOf("androidx.compose.", "com.bobon.mygametop.presentation.", "com.bobon.mygametop.localization.", "com.bobon.mygametop.ui.", "com.bobon.mygametop.core."),
            "core/ui" to listOf("com.bobon.mygametop.data.", "com.bobon.mygametop.presentation.main.", "com.bobon.mygametop.presentation.add.", "com.bobon.mygametop.presentation.settings.")
        )
        dependencies.forEach { (module, allowed) ->
            val script = file("$module/build.gradle.kts").readLines().filterNot {
                it.trimStart().startsWith("testImplementation") || it.trimStart().startsWith("androidTestImplementation")
            }.joinToString("\n")
            Regex("project\\(\"([^\"]+)\"\\)").findAll(script).forEach { dependency ->
                check(dependency.groupValues[1] in allowed) { "$module depends on forbidden module ${dependency.groupValues[1]}" }
            }
            val blocked = forbidden[module] ?: listOf("com.bobon.mygametop.data.")
            fileTree("$module/src/main") { include("**/*.kt") }.forEach { source ->
                source.readLines().filter { it.startsWith("import ") }.forEach { line ->
                    check(blocked.none { line.removePrefix("import ").startsWith(it) }) {
                        "Forbidden import in ${source.relativeTo(rootDir)}: $line"
                    }
                }
            }
        }
    }
}
