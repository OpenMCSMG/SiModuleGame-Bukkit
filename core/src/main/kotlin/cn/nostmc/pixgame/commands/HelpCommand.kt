package cn.nostmc.pixgame.commands

import net.md_5.bungee.api.chat.ClickEvent
import net.md_5.bungee.api.chat.TextComponent
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

object HelpCommand : Command(
    "help",
    "帮助",
    "/help",
    listOf("h")
)   {
    override fun execute(sender: CommandSender, commandLabel: String, args: Array<String>): Boolean {
        if (args.isNotEmpty() && sender is Player) {
            when (args[0]) {
                "super" -> {
                    sender.sendMessage("----------------")
                    sender.sendClickableMessage("""
                        §6 /damage <amount> [player] 给予玩家伤害
                    """.trimIndent(),"/damage")
                    sender.sendMessage("----------------")
                    sender.sendClickableMessage("""
                        §6 /double <amount> 设置倍率快乐
                    """.trimIndent(),"/double")
                    sender.sendMessage("----------------")
                    sender.sendClickableMessage("""
                        §6 /knockback <方向> <水平> <垂直> [player] 击飞指令
                    """.trimIndent(),"/knockback")
                    sender.sendMessage("----------------")
                    sender.sendClickableMessage("""
                        §6 /lottery <start|execute> [name] 抽奖指令
                    """.trimIndent(),"/lottery")
                    sender.sendMessage("----------------")
                }
                "bind" -> {
                    sender.sendMessage("----------------")
                    sender.sendClickableMessage("""
                        §6 /breload  重载配置文件
                    """.trimIndent(),"/breload")
                    sender.sendMessage("----------------")
                    sender.sendClickableMessage("""
                        §6 /binds 绑定一个key到一个列表
                            §7 参数 <key> [amount] 绑定一个key到一个列表
                            §7 参数 gui 打开绑定GUI
                    """.trimIndent(),"/binds gui")
                    sender.sendMessage("----------------")
                }
                "system" -> {
                    sender.sendMessage("----------------")
                    sender.sendClickableMessage("""
                        §6 /sisystem  SI内置系统管理
                            §7 简写 /sim 或者 /si
                            §7 参数 wl
                                §7 参数2 add <玩家名> 添加白名单
                                §7 参数2 remove <玩家名> 移除白名单
                                §7 参数2 list 列出白名单
                            §7 参数 exit
                                §9 意思是退出主播内置互动组
                    """.trimIndent(),"/sisystem")
                    sender.sendMessage("----------------")
                    sender.sendClickableMessage("""
                        §6 /bdebug  运行js函数
                            §7 简写 /bd
                            §7 参数 func <name> <args...>  执行js方法 name 为方法名字 args为参数
                            §7 参数 var <变量名字>  获取变量的值 varName为变量名字
                            §7 参数 invoke <js语句>  直接执行js语句
                            §7 参数 list  获取所有方法列表
                    """.trimIndent(),"/bdebug help")
                    sender.sendMessage("----------------")

                }
                "link" -> {
                    sender.sendClickableMessage("""
                        ----------------
                        §6 链接直播间点击这里直接链接
                        ----------------
                    """.trimIndent(),"/startlive")
                }
            }
            return true
        }
        // 发送可以点击的帮助信息
        if (sender is Player) {
            sender.sendMessage("§6§l[§e§lCyanBukkit互游 ModuleGame§6§l] §a§l帮助(点击可以查看那部分的教程)\n")
            sender.sendClickableMessage("§b 特殊指令部分", "/help super")
            sender.sendClickableMessage("§b 测试触发绑定部分", "/help bind")
            sender.sendClickableMessage("§b 系统功能管理部分", "/help system")
            sender.sendClickableMessage("§b 链接直播间部分", "/help link")
        } else {
            sender.sendMessage("""
                /damage <amount> [player] 给予玩家伤害
                /double <amount> 设置倍率快乐
                /knockback <方向> <水平> <垂直> [player] 击飞指令
                /lottery <start|execute> [name] 抽奖指令
                /breload 重载配置文件
                /binds <key> [amount] 绑定一个key到一个列表
                /binds gui 打开绑定GUI
                /startlive 开始直播
                /bdebug 运行js函数
                /sisystem  SI内置系统管理
            """.trimIndent())
        }
        return true
    }

    fun Player.sendClickableMessage(text: String, command: String) {
        val c = TextComponent()
        c.addExtra(text)
        c.clickEvent = ClickEvent(ClickEvent.Action.RUN_COMMAND, command)
        this.spigot().sendMessage(c)
    }

}