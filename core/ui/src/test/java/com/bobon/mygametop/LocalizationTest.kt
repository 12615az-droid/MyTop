package com.bobon.mygametop

import com.bobon.mygametop.domain.localization.AppLanguages
import org.junit.Assert.*
import org.junit.Test
import java.io.File
import java.util.Locale
import javax.xml.parsers.DocumentBuilderFactory

class LocalizationTest {
    private val resourceRoot = listOf(File("src/main/res"), File("app/src/main/res")).first { it.isDirectory }
    private fun strings(file: File): Map<String, String> {
        val nodes = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(file).getElementsByTagName("string")
        return (0 until nodes.length).associate { index ->
            val node = nodes.item(index)
            node.attributes.getNamedItem("name").nodeValue to node.textContent
        }
    }
    private fun placeholders(value: String) = Regex("%[0-9]+\\$[ds]").findAll(value).map { it.value }.sorted().toList()

    @Test fun everySupportedLanguageHasCompleteResourcesAndMatchingArguments() {
        val english = strings(File(resourceRoot, "values/strings.xml"))
        assertEquals(13, AppLanguages.supported.size)
        AppLanguages.supported.forEach { language ->
            val directory = if (language.tag == "en") "values" else "values-${language.tag}"
            val translations = strings(File(resourceRoot, "$directory/strings.xml"))
            assertEquals("Missing keys for ${language.tag}", english.keys, translations.keys)
            english.forEach { (key, original) ->
                val translated = translations.getValue(key)
                assertTrue("Empty ${language.tag}:$key", translated.isNotBlank())
                assertEquals("Arguments in ${language.tag}:$key", placeholders(original), placeholders(translated))
                if (placeholders(original).isNotEmpty()) {
                    val tokens = Regex("%([0-9]+)\\$([ds])").findAll(translated).toList()
                    val args = Array<Any>(tokens.maxOf { it.groupValues[1].toInt() }) { "Example" }
                    tokens.forEach { if (it.groupValues[2] == "d") args[it.groupValues[1].toInt() - 1] = 3 }
                    assertTrue(String.format(Locale.forLanguageTag(language.tag), translated, *args).isNotBlank())
                }
            }
        }
    }

    @Test fun localeSelectionFollowsSupportedSystemLanguagesAndFallsBackToEnglish() {
        assertEquals(Locale.ENGLISH, AppLanguages.resolve(listOf(Locale.JAPANESE), ""))
        assertEquals(Locale.GERMANY, AppLanguages.resolve(listOf(Locale.JAPANESE, Locale.GERMANY), ""))
        assertEquals(Locale.forLanguageTag("uk"), AppLanguages.resolve(listOf(Locale.GERMANY), "uk"))
        assertEquals(Locale.UK, AppLanguages.resolve(listOf(Locale.UK), ""))
    }

}
