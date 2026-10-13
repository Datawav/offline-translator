package dev.davidv.translator

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

class TranslationLoggingPrivacyTest {
  @Test
  fun `exported Binder diagnostics accept only literal event messages`() {
    val source = File("src/main/java/dev/davidv/translator/AidlTranslationService.kt").readText()
    val calls = source.lineSequence().filter { it.contains("Log.") }.toList()
    assertTrue("The lifecycle diagnostics still provide useful events", calls.isNotEmpty())
    val literalEvent = Regex("""\s*Log\.[vdiew]\(tag, "[^"$]*"\)\s*""")
    calls.forEach { call ->
      assertTrue("Binder diagnostics must not expose request values or error payloads", literalEvent.matches(call))
    }
  }

  @Test
  fun `automatic detection and process text do not log message payloads`() {
    val detector = File("src/main/java/dev/davidv/translator/LanguageDetector.kt").readText()
    assertFalse("Detection receives the original private text", detector.contains("Log."))
    val processText = File("src/main/java/dev/davidv/translator/ProcessTextActivity.kt").readText()
    val response = processText.substringAfter("val replyModalIntent").substringBefore("toTranslate")
    assertFalse("PROCESS_TEXT responses must not interpolate translated text into diagnostics", response.contains("'\$text'"))
  }
}
