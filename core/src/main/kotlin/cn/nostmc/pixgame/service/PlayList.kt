package cn.nostmc.pixgame.service

import cn.nostmc.pixgame.cyanlib.launcher.CyanPluginLauncher
import java.util.*

object PlayList {


    // 综合播放列表
    val playList: Queue<GameHandler> = LinkedList()
    var last: GameHandler? = null

    fun add(game: GameHandler) {
        playList.offer(game)
    }


    class RunTask : Runnable {
        override fun run() {
            if (playList.isNotEmpty()) {
                if ( CyanPluginLauncher.cyanPlugin.config.getBoolean("debug")) {
                    println("playList size: ${playList.size}")
                }
                if (last == null || last!!.isRunEnd) {
                    last = playList.remove()
                    last!!.run()
                }
            }
        }
    }


}