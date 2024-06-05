package cn.nostmc.pixgame.commands.fixed

import cn.nostmc.pixgame.service.BuiltInListener.ps
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

object KnockBackCommand : Command("knockback")    {
    override fun execute(p0: CommandSender, p1: String, p2: Array<out String>): Boolean {
        // 击飞指令 /knockback <方向> <水平> <垂直> [player]
        if (p2.size < 3) {
            p0.sendMessage("§c§l[!] §r§c参数不足 应该是 /knockback <方向> <水平> <垂直> [player]")
            return false
        }
        val direction = p2[0].toDoubleOrNull() ?: return false
        val horizontal = p2[1].toDoubleOrNull() ?: return false
        val vertical = p2[2].toDoubleOrNull() ?: return false
        // 完全写出  direction horizontal  vertical 并且都用上一点也不长
        if (p2.size == 3) {
            if (p0 is Player) {
                // 给玩家击飞按照设定的方向
                p0.velocity = p0.location.direction.multiply(direction).setY(vertical).setX(horizontal)
            } else {
                ps.forEach {
                    it.velocity = it.location.direction.multiply(direction).setY(vertical).setX(horizontal)
                }
            }
        } else {
            val player = Bukkit.getPlayer(p2[3]) ?: return false
            player.velocity = player.location.direction.multiply(direction).setY(vertical).setX(horizontal)
        }
        return true
    }
}