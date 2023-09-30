package dev.nikomaru.lycoris_radiata.file

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File

object Config {

    lateinit var config: ConfigData

    private val json = Json {
        prettyPrint = true
        encodeDefaults = true
    }

    fun load() {
        val file = File("config/lycoris_radiata.json")

        if (!file.exists()) {
            file.createNewFile()
            file.writeText(
                json.encodeToString(ConfigData())
            )
        }
        config = json.decodeFromString(file.readText())
        config.webhookUrl?.let {
            if (!it.startsWith("https://discord.com/api/webhooks/")) {
                config.webhookUrl = null
            }
        }
    }
}