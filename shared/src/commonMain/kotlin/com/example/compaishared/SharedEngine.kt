
package com.example.compaishared

class SharedEngine {

    // Normalise user input so Android and iOS
    // can understand commands consistently.
    fun normalizeCommand(command: String): String {
        var lower = command
            .lowercase()
            .replace(Regex("[^a-z0-9\\s]"), " ")
            .replace(Regex("\\s+"), " ")
            .trim()

        val politePrefixes = listOf(
            "can you please ",
            "could you please ",
            "would you please ",
            "can you ",
            "could you ",
            "would you ",
            "please ",
            "hey compai ",
            "compai "
        )

        for (prefix in politePrefixes) {
            if (lower.startsWith(prefix)) {
                lower = lower.removePrefix(prefix).trim()
                break
            }
        }

        val intentionPrefixes = listOf(
            "i want to open ",
            "i want to use ",
            "i would like to open ",
            "i would like to use ",
            "i d like to open ",
            "i d like to use ",
            "i need to open ",
            "i need to use "
        )

        for (prefix in intentionPrefixes) {
            if (lower.startsWith(prefix)) {
                lower = "open " + lower.removePrefix(prefix).trim()
                break
            }
        }

        return lower
    }

    // Shared command processing.
    // Date and time values are supplied by each platform.
    fun processNormalizedCommand(
        lower: String,
        currentTime: String,
        currentDate: String
    ): String? {

        return when {

            // HELP
            lower == "help" ||
            lower == "commands" ||
            lower == "show commands" ||
            lower == "available commands" -> """
                COMPAI MOBILE - AVAILABLE COMMANDS

                GENERAL
                help - Show this help screen
                hello - Greet CompAI
                thanks - Get a response

                DATE AND TIME
                time - Show the current time
                date - Show today's date

                DEVICE INFORMATION
                battery - Check battery status
                storage - Check device storage
                device info - Show device information
                status - Check CompAI status

                APPS AND SETTINGS
                open settings - Open Android Settings
                open calculator - Open Calculator
                open [app name] - Open an installed app

                CHAT
                clear - Clear the conversation
                version - Show the app version

                Tip: You can also phrase commands naturally!
            """.trimIndent()

            // GREETINGS
            lower in listOf("hello", "hi", "hey", "hey compai") ->
                "Hello! How can I help you today?"

            // TIME
            lower == "time" ||
            lower.contains("what time") ||
            lower.contains("current time") ||
            lower.contains("the time") ||
            lower.contains("clock") ->
                "The current time is $currentTime."

            // DATE
            lower == "date" ||
            lower == "today" ||
            lower.contains("date") ||
            lower.contains("today") ||
            lower.contains("what day is it") ->
                "Today is $currentDate."

            // VERSION
            lower == "version" ->
                "You are running CompAI Mobile v0.18.0."

            // THANK YOU
            lower == "thanks" || lower == "thank you" ->
                "You're welcome! I'm always ready to help."

            // Not handled by the shared engine.
            else -> null
        }
    }

    fun status(): String {
        return "CompAI shared engine is ready!"
    }
}
