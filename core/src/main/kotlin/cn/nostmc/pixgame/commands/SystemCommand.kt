package cn.nostmc.pixgame.commands

import cn.nostmc.pixgame.cyanlib.launcher.CyanPluginLauncher
import cn.nostmc.pixgame.cyanlib.launcher.CyanPluginLauncher.cyanPlugin
import cn.nostmc.pixgame.service.BuiltInListener.ps
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

object SystemCommand : Command("sisystem",
    "SI内置系统管理",
    "/sim <key> [amount]",
    listOf("sisystem", "si", "sim")) {
    override fun execute(sender: CommandSender, commandLabel: String, args: Array<out String>): Boolean {
        if (args.isEmpty()) {
            sender.sendMessage("§c参数不足 wl/exit add/remove/list")
            return true
        }
        when (args[0]) {
            "wl" -> {
                if (args.size < 2) {
                    sender.sendMessage("§c参数不足")
                    return true
                }
                when (args[1]) {
                    "add" -> {
                        if (args.size < 3) {
                            sender.sendMessage("§c参数不足")
                            return true
                        }
                        // 添加白名单
                        val list = cyanPlugin.whitelistConfig.getStringList("white").apply {
                            add(args[2])
                        }
                        cyanPlugin.whitelistConfig.set("white", list)
                        cyanPlugin.whitelistConfig.save(cyanPlugin.whitelist)
                        sender.sendMessage("§a添加成功")
                    }
                    "remove" -> {
                        if (args.size < 3) {
                            sender.sendMessage("§c参数不足")
                            return true
                        }
                        // 移除白名单
                        val list = cyanPlugin.whitelistConfig.getStringList("white").apply {
                            remove(args[2])
                        }
                        cyanPlugin.whitelistConfig.set("white", list)
                        cyanPlugin.whitelistConfig.save(cyanPlugin.whitelist)
                        sender.sendMessage("§a移除成功")
                    }
                    "list" -> {
                        // 列出白名单
                        val list = cyanPlugin.whitelistConfig.getStringList("white")
                        sender.sendMessage("§a白名单列表:")
                        list.forEach {
                            sender.sendMessage("§7- §f$it")
                        }
                    }
                    else -> {
                        sender.sendMessage("§c未知参数")
                    }
                }
            }
            "exit" -> {
                if (sender is Player) {
                    if (ps.contains(sender)) {
                        ps.remove(sender)
                        sender.sendMessage("§a退出成功")
                    } else {
                        sender.sendMessage("§c你不在同甘共苦中（BFUNC除外）冲进将刷新")
                    }
                } else {
                    sender.sendMessage("§c只有玩家才能使用此命令")
                }
            }
        }
        return true
    }


}