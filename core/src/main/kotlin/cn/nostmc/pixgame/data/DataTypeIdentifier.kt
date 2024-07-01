package cn.nostmc.pixgame.data

import cn.nostmc.pixgame.api.LiveMessageEvent
import cn.nostmc.pixgame.api.data.*
import cn.nostmc.pixgame.cyanlib.launcher.CyanPluginLauncher.cyanPlugin
import cn.nostmc.pixgame.data.ServerMessageType.*
import cn.nostmc.pixgame.default
import cn.nostmc.pixgame.function.FriendException
import cn.nostmc.pixgame.service.GameHandler
import cn.nostmc.pixgame.service.PlayList
import com.alibaba.fastjson2.JSONException
import com.alibaba.fastjson2.JSONObject
import org.bukkit.Bukkit


fun chat(text: String, user: User, man: Streamer) {
    val chat = cyanPlugin.bindsConfig.getConfigurationSection("chat") ?: return
    if (chat.contains(text)) {
        val list = chat.getStringList(text).toMutableList()
        for ((index, s) in list.withIndex()) {
            list[index] = s
                .replace("%user%", user.name)
                .replace("%streamer%", man.anchorName)
                .replace("%chat-msg%", text)
        }
        PlayList.add(GameHandler(list, 1, text))
    }
    Bukkit.getScheduler().runTask(cyanPlugin) {
        cyanPlugin.server.pluginManager.callEvent(LiveMessageEvent(Chat(user, text), man))
    }
}

fun gift(giftID: String, giftName: String, giftNum: Int, giftUrl: String, user: User, man: Streamer) {
    val gift = cyanPlugin.bindsConfig.getConfigurationSection("gift") ?: return
    if (gift.contains(giftID)) {
        val list = gift.getStringList(giftID).toMutableList()
        for ((index, s) in list.withIndex()) {
            list[index] = s.replace("%user%", user.name)
                .replace("%streamer%", man.anchorName)
                .replace("%gift-name%", giftID)
                .replace("%gift-num%", giftNum.toString())
        }
        PlayList.add(GameHandler(list, giftNum, giftID))
    } else if (gift.contains(giftName)) {
        val list = gift.getStringList(giftName).toMutableList()
        for ((index, s) in list.withIndex()) {
            list[index] = s.replace("%user%", user.name)
                .replace("%streamer%", man.anchorName)
                .replace("%gift-name%", giftName)
                .replace("%gift-num%", giftNum.toString())
        }
        PlayList.add(GameHandler(list, giftNum, giftName))
    }
    Bukkit.getScheduler().runTask(cyanPlugin) {
        cyanPlugin.server.pluginManager.callEvent(LiveMessageEvent(Gift(giftID.toLong(), giftName,  giftUrl, giftNum.toLong(), user) , man))
    }
}

fun like(likeNum: Int, user: User, man: Streamer) {
    if (cyanPlugin.bindsConfig.contains("like")) {
        val list = cyanPlugin.bindsConfig.getStringList("like").toMutableList()
        for ((index, s) in list.withIndex()) {
            list[index] = s.replace("%user%", user.name)
                .replace("%streamer%", man.anchorName)
                .replace("%like-num%", likeNum.toString())
        }
        PlayList.add(GameHandler(list, likeNum, "like"))
    }
    Bukkit.getScheduler().runTask(cyanPlugin) {
        cyanPlugin.server.pluginManager.callEvent(LiveMessageEvent(Like(user, likeNum.toLong()) , man))
    }
}


fun member(user: User, man: Streamer) {
    if (cyanPlugin.bindsConfig.contains("member")) {
        val list = cyanPlugin.bindsConfig.getStringList("member").toMutableList()
        for ((index, s) in list.withIndex()) {
            list[index] = s.replace("%user%", user.name)
                .replace("%streamer%", man.anchorName)
        }
        PlayList.add(GameHandler(list, 1, "member"))
    }
    Bukkit.getScheduler().runTask(cyanPlugin) {
        cyanPlugin.server.pluginManager.callEvent(LiveMessageEvent(Join(user), man))
    }
}


fun social(user: User, man: Streamer) {
    if (cyanPlugin.bindsConfig.contains("social")) {
        val list = cyanPlugin.bindsConfig.getStringList("social").toMutableList()
        for ((index, s) in list.withIndex()) {
            list[index] = s.replace("%user%", user.name)
                .replace("%streamer%", man.anchorName)
        }
        PlayList.add(GameHandler(list, 1, "social"))
    }
    Bukkit.getScheduler().runTask(cyanPlugin) {
        cyanPlugin.server.pluginManager.callEvent(LiveMessageEvent(Join(user) , man))
    }
}


class Handle4String(val message: String) {
    lateinit var json: JSONObject

    init {
        parse()
    }

    private fun parse() {
        json  = try {
            JSONObject.parseObject(message)
        } catch (xe: JSONException) {
            cyanPlugin.logger.info("解析消息失败: $message")
            return
        }
        val type = json.parseMessage()
        when (type) {
            is ChatMessage -> {
                if (cyanPlugin.config.getBoolean("debug")) {
                    cyanPlugin.logger.info("收到消息Chat: $message")
                }
                chat(type.message, type.user, type.whoStreamer)
            }
            is GiftMessage -> {
                if (cyanPlugin.config.getBoolean("debug")) {
                    cyanPlugin.logger.info("收到消息Gift: $message")
                }
                gift(type.giftID, type.giftName, type.giftNum, type.giftUrl, type.user, type.whoStreamer)
            }
            is LikeMessage -> {
                if (cyanPlugin.config.getBoolean("debug")) {
                    cyanPlugin.logger.info("收到消息Like: $message")
                }
                like(type.likeNum, type.user, type.whoStreamer)
            }
            is JoinMessage -> {
                if (cyanPlugin.config.getBoolean("debug")) {
                    cyanPlugin.logger.info("收到消息Join: $message")
                }
                member(type.user, type.whoStreamer)
            }
            is FollowMessage -> {
                if (cyanPlugin.config.getBoolean("debug")) {
                    cyanPlugin.logger.info("收到消息关注: $message")
                }
                social(type.user, type.whoStreamer)
            }
            is ServerMessage -> {
                when (type.type) {
                    OTHER -> {
                        if (cyanPlugin.config.getBoolean("debug")) {
                            cyanPlugin.logger.info("收到消息Debug: $message")
                        }
                        cyanPlugin.logger.info("未知消息类型或者未解析消息类型看debug")
                    }
                    HEARTBEAT -> {
                        default.send("pong")
                    }
                    CONNECTED -> {
                        Bukkit.broadcastMessage("§6总站与直播间链接成功")
                        Bukkit.getConsoleSender().sendMessage("§6总站与直播间链接成功")
                    }
                    CLOSE -> {
                        Bukkit.broadcastMessage("直播间关闭了")
                        Bukkit.getConsoleSender().sendMessage("直播间关闭了")
                    }

                    PING -> {}
                }
            }

            else -> {
                if (cyanPlugin.config.getBoolean("debug")) {
                    cyanPlugin.logger.info("收到消息Debug: $message")
                }
                throw FriendException("未知消息类型")
            }
        }
    }
}

fun linkLiveHandle() {
    cyanPlugin.logger.info("与直播间链接成功")
}


class Handle4Binary(val message: ByteArray) {

}