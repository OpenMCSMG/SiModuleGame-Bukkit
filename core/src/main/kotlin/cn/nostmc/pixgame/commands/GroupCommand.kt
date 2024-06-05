package cn.nostmc.pixgame.commands

import cn.nostmc.pixgame.cyanlib.launcher.CyanPluginLauncher.cyanPlugin
import cn.nostmc.pixgame.service.PlayList
import cn.nostmc.pixgame.service.GameHandler
import cn.nostmc.pixgame.view.BindsGUIListener
import org.bukkit.command.Command
import org.bukkit.command.CommandSender

object GroupCommand : Command(
    "binds",
    "绑定一个key到一个列表",
    "/group <key> [amount]",
    listOf("group", "bind")
) {
    override fun execute(p0: CommandSender, p1: String, p2: Array<String>): Boolean {
        if (p2.isEmpty()) {
            p0.sendMessage("§7请指定要测试的key 正确使用方法: /${p1} <key> [amount]")
            return true
        }
        val key = p2[0]
        if (key == "gui") {
            return BindsGUIListener.openGUI(p0)
        }
        val amount = if (p2.size == 2) {
            p2[1].toInt()
        } else {
            1
        }
        val list = cyanPlugin.bindsConfig.getStringList(key)
        for ((index, s) in list.withIndex()) {
            list[index] = s
                .replace("%user%", "测试")
                .replace("%gift-name%", key)
                .replace("%gift-num%", amount.toString())
                .replace("%like-num%",  1.toString())
                .replace("%chat-msg%", key)
                .replace("%like-num%", amount.toString())
        }
        PlayList.add(GameHandler(list, amount, key))
        return true
    }

    override fun tabComplete(sender: CommandSender, alias: String, args: Array<out String>): MutableList<String> {
        val allKeys = BindsGUIListener.getAllKeys()
        if (args.isEmpty()) {
            return allKeys
        }
        val input = args[0]
        return allKeys.filter { it.startsWith(input) }.toMutableList()
    }

}