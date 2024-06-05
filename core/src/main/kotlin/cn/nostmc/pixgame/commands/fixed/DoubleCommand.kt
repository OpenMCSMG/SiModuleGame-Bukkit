package cn.nostmc.pixgame.commands.fixed

import cn.nostmc.pixgame.utils.DataLoader.doubleJump
import org.bukkit.command.Command
import org.bukkit.command.CommandSender

object DoubleCommand : Command("double") {
    override fun execute(p0: CommandSender, p1: String, p2: Array<String>): Boolean {
        // 根据值设置双倍经验
        // /double <amount>
        if (p2.isEmpty()) {
            p0.sendMessage("§e |  §6§l倍率快乐为 $doubleJump")
            return true
        }
        try {
            doubleJump = p2[0].toInt()
            p0.sendMessage("§e |  §6§l倍率快乐设置为 ${p2[0]}")
        } catch (e: Exception) {
            p0.sendMessage("§e |  §6§l倍率快乐设置失败 ${p2[0]}不是一个数字")
        }
        return true
    }


}