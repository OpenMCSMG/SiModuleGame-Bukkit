package cn.nostmc.pixgame.service.hook

import cn.nostmc.pixgame.function.callFunction
import me.clip.placeholderapi.expansion.PlaceholderExpansion
import org.bukkit.entity.Player

/**
 * 把JS方法返回值装进PAPI
 */
object JsPAPI : PlaceholderExpansion()      {
    override fun getIdentifier(): String {
        return "bfunc"
    }

    override fun getAuthor(): String {
        return "Nostmc"
    }

    override fun getVersion(): String {
        return "1.0"
    }

    override fun onPlaceholderRequest(player: Player, params: String): String {
        val split = params.split(",")
        return  callFunction(split[0], split.drop(1).toMutableList()).toString()
    }
}