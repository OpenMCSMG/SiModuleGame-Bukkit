package cn.nostmc.pixgame.utils

import cn.nostmc.pixgame.cyanlib.launcher.CyanPluginLauncher
import cn.nostmc.pixgame.cyanlib.launcher.CyanPluginLauncher.cyanPlugin
import cn.nostmc.pixgame.service.AccumulationData
import com.alibaba.fastjson2.JSONObject
import java.net.HttpURLConnection
import java.net.URI
import javax.swing.text.html.HTML.Tag.P

object DataLoader {

    val coolDown = mutableMapOf<String, Long>()
    val accumulation = mutableMapOf<String, AccumulationData>()
    var doubleJump = 1
    val giftInfo = mutableMapOf<Int, String>()

    fun initGet() {
        val pf = cyanPlugin.config.getString("GUI.Platform", "douyin")
        when {
            pf == "douyin"  -> {
                val url = URI.create("http://live.cyanbukkit.cn/info/gift/$pf")
                val http = url.toURL().openConnection() as HttpURLConnection
                http.connect()
                cyanPlugin.server.consoleSender.sendMessage("douyin链接 ${http.responseCode}")
                val gifts =
                    JSONObject.parseObject(http.inputStream.reader().readText()).getJSONArray("data").associate {
                        it as JSONObject
                        it.getIntValue("id") to it.getString("name")
                    }
                giftInfo.putAll(gifts)
            }
            else -> {
                cyanPlugin.server.consoleSender.sendMessage("未知平台")
            }
        }
    }

}

