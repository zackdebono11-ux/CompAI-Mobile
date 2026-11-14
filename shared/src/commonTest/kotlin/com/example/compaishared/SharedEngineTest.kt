
package com.example.compaishared

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class SharedEngineTest {

    private val engine = SharedEngine()

    @Test
    fun normalizesCommands() {
        assertEquals("hello", engine.normalizeCommand("HELLO!"))
        assertEquals("tell me the date", engine.normalizeCommand("please tell me the date"))
        assertEquals("open calculator", engine.normalizeCommand("can you open calculator"))
    }

    @Test
    fun recognizesGreetings() {
        val result = engine.processNormalizedCommand(
            "hello", "3:30 PM", "Saturday, 14 November 2026"
        )

        assertEquals("Hello! How can I help you today?", result)
    }

    @Test
    fun recognizesHelp() {
        val result = engine.processNormalizedCommand(
            "help", "3:30 PM", "Saturday, 14 November 2026"
        )

        assertTrue(result?.contains("AVAILABLE COMMANDS") == true)
    }

    @Test
    fun recognizesTime() {
        val result = engine.processNormalizedCommand(
            "what time is it", "3:30 PM", "Saturday, 14 November 2026"
        )

        assertEquals("The current time is 3:30 PM.", result)
    }

    @Test
    fun recognizesDate() {
        val result = engine.processNormalizedCommand(
            "what is the date", "3:30 PM", "Saturday, 14 November 2026"
        )

        assertEquals("Today is Saturday, 14 November 2026.", result)
    }

    @Test
    fun recognizesVersion() {
        val result = engine.processNormalizedCommand(
            "version", "3:30 PM", "Saturday, 14 November 2026"
        )

        assertEquals("You are running CompAI Mobile v0.18.0.", result)
    }

    @Test
    fun recognizesThanks() {
        val result = engine.processNormalizedCommand(
            "thanks", "3:30 PM", "Saturday, 14 November 2026"
        )

        assertEquals("You're welcome! I'm always ready to help.", result)
    }

    @Test
    fun leavesUnknownCommandsForPlatform() {
        val result = engine.processNormalizedCommand(
            "battery", "3:30 PM", "Saturday, 14 November 2026"
        )

        assertNull(result)
    }
}
