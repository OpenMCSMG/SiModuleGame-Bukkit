package cn.nostmc.pixgame.presets

import cn.nostmc.pixgame.cyanlib.launcher.CyanPluginLauncher.cyanPlugin

object CreatePixScript {


    fun make(name: String, author: String, version: String, description: String ) {
        cyanPlugin.server.consoleSender.sendMessage("""
            ${cyanPlugin.name} > 创建脚本 $name
            作者: $author
            版本: $version
            描述: $description
        """.trimIndent())
    }



}