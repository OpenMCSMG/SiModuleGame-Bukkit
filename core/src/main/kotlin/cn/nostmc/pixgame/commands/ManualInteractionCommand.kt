package cn.nostmc.pixgame.commands

import cn.nostmc.pixgame.link
import org.bukkit.command.Command
import org.bukkit.command.CommandSender

object ManualInteractionCommand : Command("startlive"){

    var isLink = false

    override fun execute(sender: CommandSender, commandLabel: String, args: Array<String>): Boolean {
        if (!isLink) {
            link()
        }
        return true
    }


}