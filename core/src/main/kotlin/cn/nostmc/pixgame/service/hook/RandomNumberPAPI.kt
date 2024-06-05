package cn.nostmc.pixgame.service.hook

import me.clip.placeholderapi.expansion.PlaceholderExpansion
import org.bukkit.OfflinePlayer

/**
 * 我不再用Clip家的随机数了我自写
 * %randomnumber_1_5%
 */
object RandomNumberPAPI : PlaceholderExpansion() {
    override fun getIdentifier(): String {
        return "randomnumber"
    }

    override fun getAuthor(): String {
        return "Nostmc"
    }

    override fun getVersion(): String {
        return "1.0"
    }

    override fun onRequest(player: OfflinePlayer, params: String): String {
        val split = params.split("_")
        if (split.size != 2) {
            return "§c参数错误"
        }
        val min = split[0].toIntOrNull() ?: return "§c参数错误"
        val max = split[1].toIntOrNull() ?: return "§c参数错误"
        return (min..max).random().toString()
    }
}