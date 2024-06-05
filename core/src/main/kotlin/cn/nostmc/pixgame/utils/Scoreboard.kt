package cn.nostmc.pixgame.utils

import cn.nostmc.pixgame.presets.Scheduler.tasks
import com.comphenix.protocol.PacketType
import com.comphenix.protocol.ProtocolLibrary
import com.comphenix.protocol.events.PacketContainer
import com.comphenix.protocol.utility.MinecraftReflection
import com.comphenix.protocol.utility.MinecraftVersion
import com.comphenix.protocol.wrappers.EnumWrappers
import com.comphenix.protocol.wrappers.WrappedChatComponent
import org.apache.commons.lang.RandomStringUtils
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.event.server.PluginDisableEvent
import org.bukkit.plugin.Plugin
import org.bukkit.scheduler.BukkitTask
import java.util.*

/***
 * @author LC_Official
 */
class Scoreboard(private val player: Player, private val titles: List<String> = listOf(""), repeatTime: Long = 20L) :
    Listener {
    companion object {
        private var plugin: Plugin? = null
        private val scoreboards = mutableMapOf<UUID, Scoreboard>()
        private val scoreboardNames = mutableMapOf<UUID, String>()
        private val scoreboardTasks = mutableMapOf<UUID, BukkitTask>()

        fun init(plugin: Plugin) {
            Companion.plugin = plugin
            plugin.server.pluginManager.registerEvents(object : Listener {
                @EventHandler
                fun handle(event: PluginDisableEvent) {
                    if (event.plugin.name == plugin.name)
                        remove()
                }

                @EventHandler
                fun handle(event: PlayerQuitEvent) {
                    if (scoreboards.containsKey(event.player.uniqueId)) {
                        scoreboards[event.player.uniqueId]?.close()
                        scoreboards.remove(event.player.uniqueId)
                        scoreboardNames.remove(event.player.uniqueId)
                        scoreboardTasks[event.player.uniqueId]!!.cancel()
                        scoreboardTasks.remove(event.player.uniqueId)
                    }
                }
            }, plugin)
        }

        fun remove() {
            scoreboards.values.forEach(Scoreboard::close)
            scoreboardNames.clear()
            scoreboardTasks.forEach { (t, u) ->
                u.cancel()
                scoreboardTasks.remove(t)
            }
        }

        fun name(): String {
            val name = RandomStringUtils.randomAlphanumeric(8)
            return if (scoreboardNames.containsValue(name)) name() else name
        }
    }

    private var titleIndex = 0
    private val indexes = mutableListOf<Int>()
    private val update = mutableListOf<(Scoreboard) -> Unit>()

    init {
        if (plugin == null) throw NullPointerException("Plugin is null")
        scoreboards[player.uniqueId] = this
        if (scoreboardNames.containsKey(player.uniqueId)) {
            val packet = ProtocolLibrary.getProtocolManager().createPacket(PacketType.Play.Server.SCOREBOARD_OBJECTIVE)
            packet.strings.write(0, scoreboardNames[player.uniqueId])
            packet.integers.write(0, 1)
            ProtocolLibrary.getProtocolManager().sendServerPacket(player, packet)
            scoreboardNames.remove(player.uniqueId)
        }
        val name = name()
        scoreboardNames[player.uniqueId] = name
        val packetCreate =
            ProtocolLibrary.getProtocolManager().createPacket(PacketType.Play.Server.SCOREBOARD_OBJECTIVE)
        packetCreate.strings.write(0, name)
        packetCreate.strings.writeSafely(1, titles[0])
        packetCreate.chatComponents.writeSafely(0, WrappedChatComponent.fromText(titles[0]))
        packetCreate.integers.write(0, 0)
        ProtocolLibrary.getProtocolManager().sendServerPacket(player, packetCreate)
        val packetDisplay =
            ProtocolLibrary.getProtocolManager().createPacket(PacketType.Play.Server.SCOREBOARD_DISPLAY_OBJECTIVE)
        packetDisplay.integers.writeSafely(0, 1)
        packetDisplay.getEnumModifier(
            DisplaySlot::class.java,
            MinecraftReflection.getMinecraftClass("world.scores.DisplaySlot")
        ).writeSafely(0, DisplaySlot.SIDEBAR)
        packetDisplay.strings.write(0, name)
        ProtocolLibrary.getProtocolManager().sendServerPacket(player, packetDisplay)
        scoreboardTasks[player.uniqueId] = plugin!!.server.scheduler.runTaskTimer(plugin!!, Runnable {
            val packetUpdateName =
                ProtocolLibrary.getProtocolManager().createPacket(PacketType.Play.Server.SCOREBOARD_OBJECTIVE)
            packetUpdateName.strings.write(0, name)
            val index = titleIndex++
            packetUpdateName.strings.writeSafely(1, titles[index])
            packetUpdateName.chatComponents.writeSafely(0, WrappedChatComponent.fromText(titles[index]))
            packetUpdateName.integers.write(0, 2)
            ProtocolLibrary.getProtocolManager().sendServerPacket(player, packetUpdateName)
            if (titleIndex >= titles.size) titleIndex = 0
            update.forEach { it(this@Scoreboard) }
        }, 0L, repeatTime)
        tasks.add(scoreboardTasks[player.uniqueId]!!.taskId)
    }

    private fun packetUpdate(name: String, prefix: String, suffix: String): PacketContainer {
        val packetUpdate =
            ProtocolLibrary.getProtocolManager().createPacket(PacketType.Play.Server.SCOREBOARD_TEAM)
        packetUpdate.strings.write(0, name)
        packetUpdate.strings.writeSafely(2, prefix)
        packetUpdate.strings.writeSafely(3, suffix)
        packetUpdate.chatComponents.writeSafely(1, WrappedChatComponent.fromText(prefix))
        packetUpdate.chatComponents.writeSafely(2, WrappedChatComponent.fromText(suffix))
        packetUpdate.integers.write(if (packetUpdate.integers.size() > 1) 1 else 0, 2)
        if (packetUpdate.optionalStructures.size() > 0) {
            packetUpdate.optionalStructures.read(0).get().let {
                it.chatComponents.writeSafely(1, WrappedChatComponent.fromText(prefix))
                it.chatComponents.writeSafely(2, WrappedChatComponent.fromText(suffix))
                packetUpdate.optionalStructures.writeSafely(0, Optional.of(it))
            }
        }
        return packetUpdate
    }

    private fun packetTeamCreate(name: String, prefix: String, suffix: String, fakePlayer: String): PacketContainer {
        val packetTeamCreate =
            ProtocolLibrary.getProtocolManager().createPacket(PacketType.Play.Server.SCOREBOARD_TEAM)
        packetTeamCreate.strings.write(0, name)
        packetTeamCreate.strings.writeSafely(2, prefix)
        packetTeamCreate.strings.writeSafely(3, suffix)
        packetTeamCreate.chatComponents.writeSafely(1, WrappedChatComponent.fromText(prefix))
        packetTeamCreate.chatComponents.writeSafely(2, WrappedChatComponent.fromText(suffix))
        packetTeamCreate.integers.write(if (packetTeamCreate.integers.size() > 1) 1 else 0, 0)
        packetTeamCreate.modifier.withType<Collection<*>>(Collection::class.java).write(0, listOf(fakePlayer))
        if (packetTeamCreate.optionalStructures.size() > 0) {
            packetTeamCreate.optionalStructures.read(0).get().let {
                it.chatComponents.writeSafely(1, WrappedChatComponent.fromText(prefix))
                it.chatComponents.writeSafely(2, WrappedChatComponent.fromText(suffix))
                packetTeamCreate.optionalStructures.writeSafely(0, Optional.of(it))
            }
        }
        return packetTeamCreate
    }

    private fun packetScore(fakePlayer: String, objective: String, line: Int): PacketContainer {
        val packet = ProtocolLibrary.getProtocolManager().createPacket(PacketType.Play.Server.SCOREBOARD_SCORE)
        packet.strings.write(0, fakePlayer)
        packet.scoreboardActions.write(0, EnumWrappers.ScoreboardAction.CHANGE)
        packet.strings.write(1, objective)
        packet.integers.write(0, line)
        return packet
    }

    fun set(content: String, line: Int) {
        val split = Utils.split(player, content)
        val prefix = split[0].replace("&", "§")
        val suffix = split[1]


        if (indexes.contains(line)) {
            ProtocolLibrary.getProtocolManager().sendServerPacket(player, packetUpdate("score$line", prefix, suffix))
        } else {
            val fakePlayer =
                "§${if (line < 10) 0 else line.toString()[0]}§${line.toString()[line.toString().length - 1]}§r"
            ProtocolLibrary.getProtocolManager().sendServerPacket(
                player, packetTeamCreate(
                    "score$line",
                    prefix,
                    suffix,
                    fakePlayer
                )
            )
            ProtocolLibrary.getProtocolManager().sendServerPacket(
                player, packetScore(
                    fakePlayer,
                    scoreboardNames[player.uniqueId]!!, line
                )
            )
            indexes.add(line)
        }
    }

    fun update(consumer: (Scoreboard) -> Unit) {
        update.add(consumer)
    }

    fun close() {
        indexes.forEach {
            val packetDelete = ProtocolLibrary.getProtocolManager()
                .createPacket(PacketType.Play.Server.SCOREBOARD_TEAM)
            packetDelete.strings.write(0, "score$it")
            packetDelete.integers.write(if (packetDelete.integers.size() > 1) 1 else 0, 1)
            ProtocolLibrary.getProtocolManager().sendServerPacket(player, packetDelete)
        }
        val packet = ProtocolLibrary.getProtocolManager().createPacket(PacketType.Play.Server.SCOREBOARD_OBJECTIVE)
        packet.strings.write(0, scoreboardNames[player.uniqueId])
        packet.integers.write(0, 1)
        ProtocolLibrary.getProtocolManager().sendServerPacket(player, packet)
    }

    object Utils {
        const val COLOR_CHAR = '§'
        const val COLOR_STRING = "§"

        fun split(player: Player, text: String): Array<String> {
            var charLimit = 16
            if (ProtocolLibrary.getProtocolManager().minecraftVersion >= MinecraftVersion.AQUATIC_UPDATE &&
                ProtocolLibrary.getProtocolManager().getProtocolVersion(player) > 404
            ) {
                val lastColors: String = getLastColors(text.substring(0, 16.coerceAtMost(text.length)))
                charLimit -= if (lastColors.isEmpty()) 2 else lastColors.length
            }
            return if (text.length > charLimit && ProtocolLibrary.getProtocolManager()
                    .getProtocolVersion(player) <= 340
            ) {
                val prefix = java.lang.StringBuilder(text)
                val suffix = java.lang.StringBuilder(text)
                prefix.setLength(charLimit)
                suffix.delete(0, charLimit)
                if (prefix[charLimit - 1] == COLOR_CHAR) {
                    prefix.setLength(prefix.length - 1)
                    suffix.insert(0, COLOR_CHAR)
                }
                val prefixString = prefix.toString()
                suffix.insert(0, getLastColors(prefixString))
                arrayOf(prefixString, suffix.toString())
            } else {
                arrayOf(text, "")
            }
        }

        private fun getLastColors(input: String?): String {
            if (input == null) return ""
            val result = StringBuilder()
            val length = input.length
            for (index in length - 1 downTo -1 + 1) {
                val section = input[index]
                if ((section == COLOR_CHAR || section == '&') && index < length - 1) {
                    val c = input[index + 1]
                    if ("0123456789AaBbCcDdEeFfKkLlMmNnOoRr".contains(c.toString())) {
                        result.insert(0, COLOR_CHAR)
                        result.insert(1, c)
                        if ("0123456789AaBbCcDdEeFfRr".contains(c.toString())) {
                            break
                        }
                    }
                }
            }
            return result.toString()
        }
    }

    enum class DisplaySlot {
        LIST,
        SIDEBAR,
        BELOW_NAME,
        SIDEBAR_BLACK,
        SIDEBAR_DARK_BLUE,
        SIDEBAR_DARK_GREEN,
        SIDEBAR_DARK_AQUA,
        SIDEBAR_DARK_RED,
        SIDEBAR_DARK_PURPLE,
        SIDEBAR_GOLD,
        SIDEBAR_GRAY,
        SIDEBAR_DARK_GRAY,
        SIDEBAR_BLUE,
        SIDEBAR_GREEN,
        SIDEBAR_AQUA,
        SIDEBAR_RED,
        SIDEBAR_LIGHT_PURPLE,
        SIDEBAR_YELLOW,
        SIDEBAR_WHITE;
    }
}