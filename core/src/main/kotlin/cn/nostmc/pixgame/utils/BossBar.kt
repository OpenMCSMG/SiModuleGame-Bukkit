package cn.nostmc.pixgame.utils

import com.comphenix.protocol.ProtocolLibrary
import com.comphenix.protocol.utility.MinecraftVersion
import me.neznamy.tab.api.TabAPI
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
import java.util.*
import java.util.function.Consumer

class BossBar(
    private val player: Player,
    var text: String,
    var percent: Float,
    var color: Color = Color.PINK,
    var style: Style = Style.PROGRESS
) {
    private val uniqueId: UUID = UUID.randomUUID()
    private val entityId = uniqueId.hashCode()

    companion object {
        private var plugin: Plugin? = null
        lateinit var api: TabAPI
        private val data = mutableMapOf<Player, BossBar>()
        fun init(plugin: Plugin) {
            Companion.plugin = plugin
            api = TabAPI.getInstance()
            plugin.server.pluginManager.registerEvents(object : Listener {
                @EventHandler
                fun handle(event: PluginDisableEvent) {
                    if (event.plugin.name == plugin.name) remove()
                }

                @EventHandler
                fun handle(event: PlayerQuitEvent) {
                    if (data.contains(event.player)) data[event.player]!!.close()
                }
            }, plugin)
        }

        fun remove() {
            data.values.forEach(BossBar::close)
            data.clear()
        }
    }

    var location = Location(player.world, 0.0, 0.0, 0.0)
        private set

    private fun updateDistantLocation() {
        val location = player.eyeLocation.clone()
        location.pitch -= 21
        val vector = location.direction.normalize().multiply(50)
        this.location = location.add(vector.x, vector.y, vector.z)
    }

    private var update: Consumer<BossBar> = Consumer<BossBar> { }

    fun close() {
        if (ProtocolLibrary.getProtocolManager().minecraftVersion < MinecraftVersion.COMBAT_UPDATE) {
            (bossBar as me.neznamy.tab.api.bossbar.BossBar).removePlayer(api.getPlayer(player.uniqueId)!!)
            bossBar = null
        } else {
            (bossBar as org.bukkit.boss.BossBar).removePlayer(player)
            bossBar = null
        }
        data.remove(player)
    }


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

    var bossBar: Any? = null

    private fun modern() {
        bossBar = Bukkit.createBossBar(
            text, BarColor.valueOf(color.name), BarStyle.entries[style.ordinal]
        )
        (bossBar as org.bukkit.boss.BossBar).progress = percent.toDouble()
        (bossBar as org.bukkit.boss.BossBar).addPlayer(player)
        Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, {
            if (bossBar == null) return@runTaskTimerAsynchronously
            update.accept(this)
            (bossBar as org.bukkit.boss.BossBar).progress = percent.toDouble()
            (bossBar as org.bukkit.boss.BossBar).title = text
            (bossBar as org.bukkit.boss.BossBar).color = BarColor.valueOf(color.name)
            (bossBar as org.bukkit.boss.BossBar).style = BarStyle.entries.toTypedArray()[style.ordinal]
        }, 0L, 10L)
    }

    private fun legacy() {
        updateDistantLocation()
        // 显示设置
        bossBar = api.bossBarManager!!.createBossBar(
            text,
            percent,
            me.neznamy.tab.api.bossbar.BarColor.valueOf(color.name),
            me.neznamy.tab.api.bossbar.BarStyle.entries[style.ordinal]
        )
        (bossBar as me.neznamy.tab.api.bossbar.BossBar).addPlayer(api.getPlayer(player.uniqueId)!!)
        Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, {
            if (bossBar == null) return@runTaskTimerAsynchronously
            update.accept(this)
            (bossBar as me.neznamy.tab.api.bossbar.BossBar).setProgress(percent)
            (bossBar as me.neznamy.tab.api.bossbar.BossBar).title = text
            (bossBar as me.neznamy.tab.api.bossbar.BossBar).color = color.name
            (bossBar as me.neznamy.tab.api.bossbar.BossBar).style = style.name
        }, 0L, 5L)
    }

    fun update(consumer: Consumer<BossBar>) {
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