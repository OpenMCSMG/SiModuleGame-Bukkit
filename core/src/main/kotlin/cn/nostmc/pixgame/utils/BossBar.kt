package cn.nostmc.pixgame.utils

import com.comphenix.protocol.PacketType
import com.comphenix.protocol.ProtocolLibrary
import com.comphenix.protocol.utility.MinecraftVersion
import com.comphenix.protocol.wrappers.WrappedDataWatcher
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.boss.BarColor
import org.bukkit.boss.BarStyle
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.event.server.PluginDisableEvent
import org.bukkit.plugin.Plugin
import org.bukkit.scheduler.BukkitTask
import java.util.*

class BossBar(
    private val player: Player,
    var text: String,
    var percent: Float,
    var color: Color = Color.PINK,
    var style: Style = Style.PROGRESS
) {
    val uniqueId = UUID.randomUUID()
    private val entityId = uniqueId.hashCode()
    companion object {
        private var plugin: Plugin? = null
        private val data = mutableMapOf<Player, BossBar>()
        private val tasks = mutableMapOf<Player, BukkitTask>()
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
                    if (data.contains(event.player))
                        data[event.player]!!.close()
                }
            }, plugin)
        }

        fun remove() {
            data.values.forEach(BossBar::close)
            data.clear()
        }
    }

    private var location = Location(player.world, 0.0, 0.0, 0.0)

    private fun updateDistantLocation() {
        val location = player.eyeLocation.clone()
        location.pitch -= 21
        val vector = location.direction.normalize().multiply(50)
        this.location = location.add(vector.x, vector.y, vector.z)
    }

    private var update: (BossBar) -> Unit = {}

    fun close() {
        tasks[player]!!.cancel()
        tasks.remove(player)
        if (ProtocolLibrary.getProtocolManager().minecraftVersion < MinecraftVersion.COMBAT_UPDATE) {
            val packetDestroy =
                ProtocolLibrary.getProtocolManager().createPacket(PacketType.Play.Server.ENTITY_DESTROY)
            packetDestroy.integerArrays.write(0, intArrayOf(entityId))
            ProtocolLibrary.getProtocolManager().sendServerPacket(player, packetDestroy, false)
        } else {
            (bossBar as org.bukkit.boss.BossBar).removePlayer(player)
            bossBar = null
        }
        data.remove(player)
    }

    private val dataWatcher = WrappedDataWatcher()

    init {
        if (data.containsKey(player)) {
            data[player]!!.close()
        }
        data[player] = this
        if (ProtocolLibrary.getProtocolManager().minecraftVersion >= MinecraftVersion.COMBAT_UPDATE) {
            modern()
        } else {
            legacy()
        }
    }

    var bossBar : Any? =null

    private fun modern() {
        bossBar = Bukkit.createBossBar(
            text, BarColor.valueOf(color.name), BarStyle.entries[style.ordinal]
        )
        (bossBar as org.bukkit.boss.BossBar).progress = percent.toDouble()
        (bossBar as org.bukkit.boss.BossBar).addPlayer(player)
        tasks[player] = Bukkit.getScheduler().runTaskTimerAsynchronously(plugin!!, Runnable {
            update(this)
            (bossBar as org.bukkit.boss.BossBar).progress = percent.toDouble()
            (bossBar as org.bukkit.boss.BossBar).setTitle(text)
            (bossBar as org.bukkit.boss.BossBar).color = BarColor.valueOf(color.name)
            (bossBar as org.bukkit.boss.BossBar).style = BarStyle.entries.toTypedArray()[style.ordinal]
        }, 0L, 10L)
    }

    private fun legacy() {
        updateDistantLocation()
        val packetSpawn =
            ProtocolLibrary.getProtocolManager().createPacket(PacketType.Play.Server.SPAWN_ENTITY_LIVING)
        packetSpawn.integers.write(0, entityId)
        packetSpawn.integers.write(1, 64) // Wither
        packetSpawn.integers.write(3, (location.blockX * 32))
        packetSpawn.integers.write(4, (location.blockY * 32))
        packetSpawn.integers.write(5, (location.blockZ * 32))
        dataWatcher.setObject(0, 0x20.toByte())
        dataWatcher.setObject(1, 300.toShort())
        dataWatcher.setObject(2, text)
        dataWatcher.setObject(3, 1.toByte())
        dataWatcher.setObject(
            6,
            (300 * (if (percent > 1.0f) 1.0f else if (percent < 0f) 0f else percent)),
            true
        )
        dataWatcher.setObject(7, org.bukkit.Color.BLACK.asRGB())
        dataWatcher.setObject(8, 0.toByte())
        dataWatcher.setObject(15, 1.toByte())
        dataWatcher.setObject(20, 819)
        packetSpawn.dataWatcherModifier.write(0, dataWatcher)
        ProtocolLibrary.getProtocolManager().sendServerPacket(player, packetSpawn, false)
        tasks[player] = Bukkit.getScheduler().runTaskTimer(plugin!!, Runnable {
            update(this)
            updateDistantLocation()
            val packetTeleport =
                ProtocolLibrary.getProtocolManager().createPacket(PacketType.Play.Server.ENTITY_TELEPORT)
            packetTeleport.integers.write(0, entityId)
            packetTeleport.integers.write(1, (location.blockX * 32))
            packetTeleport.integers.write(2, (location.blockY * 32))
            packetTeleport.integers.write(3, (location.blockZ * 32))
            packetTeleport.bytes.write(0, (location.yaw * 256 / 360).toInt().toByte())
            packetTeleport.bytes.write(0, (location.pitch * 256 / 360).toInt().toByte())
            packetTeleport.booleans.write(0, true)
            ProtocolLibrary.getProtocolManager().sendServerPacket(player, packetTeleport, false)
            val packetMeta =
                ProtocolLibrary.getProtocolManager().createPacket(PacketType.Play.Server.ENTITY_METADATA)
            packetMeta.integers.write(0, entityId)
            dataWatcher.setObject(2, text)
            dataWatcher.setObject(
                6,
                (300 * (if (percent > 1.0f) 1.0f else if (percent < 0f) 0f else percent)),
                true
            )
            packetMeta.watchableCollectionModifier.write(0, dataWatcher.watchableObjects)
            ProtocolLibrary.getProtocolManager().sendServerPacket(player, packetMeta, false)
        }, 0L, 5L)
    }

    fun update(consumer: (BossBar) -> Unit) {
        update = consumer
    }

    enum class Color {
        PINK,
        BLUE,
        RED,
        GREEN,
        YELLOW,
        PURPLE,
        WHITE;
    }

    enum class Style {
        PROGRESS,
        NOTCHED_6,
        NOTCHED_10,
        NOTCHED_12,
        NOTCHED_20;
    }
}