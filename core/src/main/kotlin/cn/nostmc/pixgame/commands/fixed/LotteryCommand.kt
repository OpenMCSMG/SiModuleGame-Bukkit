package cn.nostmc.pixgame.commands.fixed

import cn.nostmc.pixgame.service.GameHandler
import cn.nostmc.pixgame.cyanlib.launcher.CyanPluginLauncher
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.command.ConsoleCommandSender
import java.util.concurrent.ThreadLocalRandom

object LotteryCommand : Command("lottery") {
    override fun execute(p0: CommandSender, p1: String, p2: Array<out String>): Boolean {
        if(p0 is ConsoleCommandSender) {
            p0.sendMessage("§e | 请查看帮助/help")
            return true
        }
        if (p2.isEmpty()) {
            p0.sendMessage("§e | 请查看帮助/help")
            return true
        }
        when (p2[0]) {
            "start" -> {
                val name = if (p2.size == 1) {
                    "default"
                } else {
                    p2[1]
                }
                 CyanPluginLauncher.cyanPlugin.lotteryConfig.getStringList("box.${name}").start()
                p0.sendMessage("§e正在打开盲盒${name}")
            }
            "execute" -> {
                if(p2.size == 1) {
                    p0.sendMessage("§e | 请查看帮助/help")
                    return true
                }
                val keyName = if( p2[1].contains("group.")) {
                    p2[1]
                } else {
                    "group.${p2[1]}"
                }
                p0.sendMessage("§e正在测试盲盒触发组${p2[1]}的执行")
                GameHandler( CyanPluginLauncher.cyanPlugin.lotteryConfig.getStringList(keyName),1,keyName).run()
            }
            else -> {
                p0.sendMessage("§e | 请查看帮助/help")
            }
        }

        return true
    }


    private fun MutableList<String>.start() {
        // 随机一个随机数
        val random = ThreadLocalRandom.current().nextInt(0, this.size)
        val text = this[random]
        val key = text.replace(":",".").replace(" ","").trim()
        if ( CyanPluginLauncher.cyanPlugin.config.getBoolean("debug")) {
            println("[调试] 抽中: $key , 随机数: $random")
        }
        GameHandler( CyanPluginLauncher.cyanPlugin.lotteryConfig.getStringList(key),1,key).run()

    }


}