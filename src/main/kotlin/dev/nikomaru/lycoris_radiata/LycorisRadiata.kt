package dev.nikomaru.lycoris_radiata

import club.minnced.discord.webhook.WebhookClient
import club.minnced.discord.webhook.send.WebhookEmbed
import club.minnced.discord.webhook.send.WebhookEmbedBuilder
import club.minnced.discord.webhook.send.WebhookMessageBuilder
import com.github.shynixn.mccoroutine.fabric.launch
import com.github.shynixn.mccoroutine.fabric.mcCoroutineConfiguration
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents
import net.minecraft.client.MinecraftClient
import org.slf4j.LoggerFactory
import java.io.File
import java.nio.file.*
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.Executor
import kotlin.collections.HashMap
import kotlin.io.path.Path


object LycorisRadiata : ModInitializer {
    private val logger = LoggerFactory.getLogger("lycoris_radiata")
    val url =
        "https://discord.com/api/webhooks/1157620141769625762/QbAFhpSZlkWyIFx75zhFJObP_JskHP3y09hEsjLVaIfPcn4mP0azbp4XO10TFDjn1y9I"
    val webhookClient = WebhookClient.withUrl(url)

    override fun onInitialize() {
        logger.info("Hello Fabric world!")

        ClientLifecycleEvents.CLIENT_STARTED.register(ClientLifecycleEvents.ClientStarted { cli ->
            mcCoroutineConfiguration.minecraftExecutor = Executor { r ->
                cli.submitAndJoin(r)
            }
            launch {
                onScanning()
            }
        })
        ClientLifecycleEvents.CLIENT_STOPPING.register {
            mcCoroutineConfiguration.disposePluginSession()
        }
    }

    private suspend fun onScanning() {

        logger.info("file is watching...")
        withContext(Dispatchers.IO) {
            val wsrv = FileSystems.getDefault().newWatchService()
            val path = Path("screenshots")
            val watchMap: HashMap<WatchKey, Path> = hashMapOf()
            watchMap[path.register(wsrv, StandardWatchEventKinds.ENTRY_MODIFY)] = path
            do {
                val key = wsrv.take()
                for (event in key.pollEvents()) {
                    val fileName = event.context() as Path
                    val file = File("screenshots/${fileName.toFile().name}")

                    if (Files.size(file.toPath()) == 0L) {
                        logger.info("file is empty")
                        continue
                    }

                    val playerName = MinecraftClient.getInstance().session!!.username

                    val sdf = SimpleDateFormat("yyyy/MM/dd HH:mm:ss z");

                    val embed = WebhookEmbedBuilder().setTitle(WebhookEmbed.EmbedTitle("Record Screenshot", null))
                        .setColor(0x00ff00)
                        .addField(WebhookEmbed.EmbedField(true, "Player Name", playerName))
                        .addField(WebhookEmbed.EmbedField(true, "Record Date", sdf.format(Date())))
                        .setFooter(WebhookEmbed.EmbedFooter("Data is recorded by Lycoris Radiata Mod", null)).build()

                    val builder = WebhookMessageBuilder().setUsername("Lycoris Radiata Mod")
                        .setAvatarUrl("https://upload.wikimedia.org/wikipedia/commons/4/47/Lycoris_radiata_-_Kinchakuda_2018_-_1.jpg")
                        .addEmbeds(embed).addFile(file).build()

                    webhookClient.send(builder)
                }
            } while (key.reset())
        }
        logger.info("file is unwatching...")
    }

}