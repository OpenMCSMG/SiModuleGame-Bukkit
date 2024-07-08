package cn.nostmc.pixgame.connect

import cn.nostmc.pixgame.cyanlib.launcher.CyanPluginLauncher.cyanPlugin
import cn.nostmc.pixgame.data.Handle4String
import cn.nostmc.pixgame.sendDebugMessage
import cn.nostmc.pixgame.stopAnimation
import org.bukkit.Bukkit
import org.java_websocket.client.WebSocketClient
import org.java_websocket.enums.ReadyState
import org.java_websocket.handshake.ServerHandshake
import java.net.URI
import java.util.*
import kotlin.concurrent.schedule

/**
 *
 *  要在if上套个 累计 与papi
 *
 */
class DefaultSocketConnect(
    val url: URI,
    headers: Map<String, String>
) : WebSocketClient(url, headers) {

    private var reconnectTimes = 0
    private var isLink = false

    init {
        Timer().schedule(0, 3000) {
            if (readyState != ReadyState.OPEN) {
                sendDebugMessage("链接状态处于$readyState")
            }
            if (!isLink) {
                return@schedule
            } else {
                send("ping")
                if (readyState.equals(ReadyState.NOT_YET_CONNECTED)) {
                    try {
                        cyanPlugin.server.consoleSender.sendMessage("重连次数: $reconnectTimes")
                        reconnect()
                    } catch (_: java.lang.Exception) {
                    }
                } else if (readyState.equals(ReadyState.CLOSING)
                    || readyState.equals(ReadyState.CLOSED)) {
                    cyanPlugin.server.consoleSender.sendMessage("疑似被架空请重新链接去修复该问题")
                    Bukkit.shutdown()
                }
            }
        }
    }

    override fun onOpen(handshakedata: ServerHandshake) {
        cyanPlugin.server.consoleSender.sendMessage("链接${url}成功")
        reconnectTimes = 0
        isLink = true
        stopAnimation()
    }

    // 消息
    override fun onMessage(data: String) {
        Handle4String(data)
    }

    override fun onClose(code: Int, reason: String, remote: Boolean) {
        cyanPlugin.server.consoleSender.sendMessage("模块关闭连接, 重连次数: $reconnectTimes")
        Bukkit.shutdown()
    }

    override fun onError(ex: Exception) {
        ex.printStackTrace()
        Bukkit.getOnlinePlayers().forEach {
            it.sendTitle("§c链接直播间服务器异常..", "请联系管理员", 10, 20, 10)
        }
    }


}