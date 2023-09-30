package dev.nikomaru.lycoris_radiata.file

import kotlinx.serialization.Serializable
import java.util.Date

@Serializable
data class ConfigData(
    var webhookUrl: String? = null,
    val format: String = "yyyy/MM/dd HH:mm:ss z",
    val recordPlayerName : Boolean = true,
    val recordLocation : Boolean = true,
    val recordServerAddress : Boolean = true,
    val recordDate: Boolean = true,
)
