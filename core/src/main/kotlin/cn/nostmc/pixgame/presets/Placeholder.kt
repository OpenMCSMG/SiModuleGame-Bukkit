package cn.nostmc.pixgame.presets

object Placeholder {

    fun setPlaceholders(player: org.bukkit.entity.Player, text: String): String {
        return me.clip.placeholderapi.PlaceholderAPI.setPlaceholders(player, text)
    }

    fun setPlaceholders(player: org.bukkit.entity.Player, text: List<String>): List<String> {
        return me.clip.placeholderapi.PlaceholderAPI.setPlaceholders(player, text)
    }

    fun setPlaceholders(player: org.bukkit.entity.Player, text: Array<String>): Array<String> {
        return me.clip.placeholderapi.PlaceholderAPI.setPlaceholders(player, text.toList()).toTypedArray()
    }



}