package cn.nostmc.pixgame.commands

import cn.nostmc.pixgame.cyanlib.launcher.CyanPluginLauncher.cyanPlugin
import cn.nostmc.pixgame.function.reloadJS
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.configuration.file.YamlConfiguration

object BReload : Command("breload") {
    override fun execute(sender: CommandSender, commandLabel: String, args: Array<String>): Boolean {
        // 测试方法
        reloadJS(true)
        cyanPlugin.bindsConfig = YamlConfiguration.loadConfiguration(cyanPlugin.binds)
        cyanPlugin.lotteryConfig = YamlConfiguration.loadConfiguration(cyanPlugin.lottery)
        cyanPlugin.reloadConfig()
        sender.sendMessage("加载已经执行")
        return true
    }
}