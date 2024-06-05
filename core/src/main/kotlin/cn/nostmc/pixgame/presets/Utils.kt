package cn.nostmc.pixgame.presets

import cn.nostmc.pixgame.cyanlib.launcher.CyanPluginLauncher
import cn.nostmc.pixgame.utils.BossBar
import cn.nostmc.pixgame.utils.Scoreboard
import cn.nostmc.pixgame.utils.Title
import org.bukkit.Bukkit
import org.bukkit.boss.BarColor
import org.bukkit.boss.BarStyle
import org.bukkit.entity.Player

object Utils {

    val allBossBar = mutableListOf<org.bukkit.boss.BossBar>()
    val allScoreboard = mutableListOf<Scoreboard>()

    /**
     * 创建一个BossBar
     * @param player 玩家
     * @param text 文本
     * @param percent 百分比
     * @param color 颜色
     * @param style 样式
     * @param callback(BossBar) 更新回调
     */
    fun bossbar(
        player: Player,
        text: String,
        percent: Float,
        color: String,
        style: String,
        callback: JSFunction
    ) {
//        BossBar(player, text, percent, BossBar.Color.valueOf(color), BossBar.Style.valueOf(style)).update(callback)
        val bar =  CyanPluginLauncher.cyanPlugin.server.createBossBar(text, BarColor.valueOf(color), BarStyle.valueOf(style))
        allBossBar.add(bar)
        bar.progress = percent.toDouble()
        bar.addPlayer(player)
        Scheduler.tasks.add(Bukkit.getScheduler().runTaskTimer(MineCraft.plugin, {
            callback.apply(arrayOf(bar))
        }, 0, 1).taskId)
    }

    /**
     * 创建一个计分板
     * @param player 玩家
     * @param title 标题
     * @param repeatTime 重复时间
     * @param callback(Scoreboard) 更新回调
     */
    fun scoreboard(player: Player, title: Array<String>, repeatTime: Int, callback: CallbackA<Scoreboard>) {
        val sb = Scoreboard(player, title.asList(), repeatTime * 20L)
        allScoreboard.add(sb)
            sb.update(callback)
    }


    /**
     * 显示标题
     * @param player 玩家
     * @param title 标题
     * @param subtitle 副标题
     */
    fun title(
        player: Player,
        title: String,
        subtitle: String = ""
    ) {
        player.sendTitle(title, subtitle, 20, 60, 20)
    }

    /**
     * 显示标题
     * @param player 玩家
     * @param title 标题
     * @param subtitle 副标题
     * @param fadeIn 淡入
     * @param stay 停留
     * @param fadeOut 淡出
     */
    fun title(
        player: Player,
        title: String,
        subtitle: String,
        fadeIn: Int,
        stay: Int,
        fadeOut: Int
    ) {
        player.sendTitle(title, subtitle, fadeIn, stay, fadeOut)
    }


    /**
     * 显示动作条
     * @param player 玩家
     * @param text 文本
     */
    fun actionBar(player: Player, text: String) {
        Title.actionbar(player, text)
    }

}