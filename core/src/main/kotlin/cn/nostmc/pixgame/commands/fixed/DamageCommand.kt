package cn.nostmc.pixgame.commands.fixed

import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

object DamageCommand : Command("damage") {
    override fun execute(p0: CommandSender, p1: String, p2: Array<String>): Boolean {
        // 根据执行指令的给予伤害 /damage <amount> [player] 如果是玩家直接输入数字就掉血
        // 如果是控制台输入数字就给所有玩家掉血
        if (p2.isEmpty()) return true
        val amount = p2[0].toDoubleOrNull() ?: return true
        val p = if (p2.size == 1) {
            if (p0 is Player) {
                p0
            } else {
                return true
            }
        } else {
           Bukkit.getPlayer(p2[1]) ?: return true
        }
        p.damage(amount)
        return true
    }


}