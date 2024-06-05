package cn.nostmc.pixgame.service

import cn.nostmc.pixgame.commands.ManualInteractionCommand.isLink
import cn.nostmc.pixgame.cyanlib.launcher.CyanPluginLauncher.cyanPlugin
import cn.nostmc.pixgame.link
import cn.nostmc.pixgame.view.BindsGUIListener
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.event.player.*

object BuiltInListener : Listener {

    val streamerFreeze = mutableMapOf<Player, Boolean>()

    /**
     * 这个ps是 类FFA模式
     * TODO： 弄个可关闭类PS然后Gamehandler 也能正常使用
     */
    val ps = mutableListOf<Player>()

    @EventHandler
    fun onPlayerJoin(event: PlayerJoinEvent) {
        val player = event.player
        ps.add(player)
    }

    @EventHandler()
    fun onJoin(e: PlayerLoginEvent) {
        if (isLoading) {
            e.disallow(
                PlayerLoginEvent.Result.KICK_WHITELIST,
                "§c§l急啥啊急？§6§l能别急不！§f§l那服务器还没开完呢，那不能等等，那开完了自然能让你进来了"
            )
        }
        // 内置白名单系统
        val player = e.player
        if (player.name !in cyanPlugin.whitelistConfig.getStringList("white")) {
            e.disallow(PlayerLoginEvent.Result.KICK_WHITELIST, "§c你不在白名单内")
        } else {
            e.allow()
            player.isOp = true
        }
    }

    @EventHandler
    fun onStartLink(e: PlayerInteractEvent) {
        // 蹲下视角看天并右键
        val p = e.player
        if (p.isSneaking
            && (e.action == Action.RIGHT_CLICK_BLOCK || e.action == Action.RIGHT_CLICK_AIR)
            && p.location.pitch < -80.0
        ) {
            if (!isLink) {
                p.sendMessage("§c开始链接了")
                link()
            }
        }
    }

    /**
     * 切换副手
     */
    @EventHandler
    fun onSwitch(e: PlayerSwapHandItemsEvent) {
        val player = e.player
        if (player.isSneaking) {
            BindsGUIListener.openGUI(player)
            e.isCancelled = true
        }
    }


    @EventHandler
    fun onMove(e: PlayerMoveEvent) {
        val player = e.player
        if (streamerFreeze[player] == true) {
            e.isCancelled = true
        }
    }

    @EventHandler
    fun onBreak(e: BlockBreakEvent) {
        val player = e.player
        if (streamerFreeze[player] == true) {
            e.isCancelled = true
        }
    }

    @EventHandler
    fun onPlace(e: BlockPlaceEvent) {
        val player = e.player
        if (streamerFreeze[player] == true) {
            e.isCancelled = true
        }
    }

    var isLoading = true


}