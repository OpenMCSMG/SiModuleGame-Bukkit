package cn.nostmc.pixgame.data

import cn.nostmc.pixgame.api.data.Streamer
import cn.nostmc.pixgame.api.data.User
import cn.nostmc.pixgame.cyanlib.launcher.CyanPluginLauncher.cyanPlugin
import com.alibaba.fastjson2.JSONObject



open class DefaultMessage(
    open val user: User,
    open val whoStreamer: Streamer
)

data class ChatMessage(
    val message: String,
    override val user: User,
    override val whoStreamer: Streamer
) : DefaultMessage(user, whoStreamer)

open class ServerMessage(
    open val type: ServerMessageType,
    open val timeStamps: Long,
    whichPlantFrom: Streamer
) : DefaultMessage(User("", "",""), whichPlantFrom)

enum class ServerMessageType {
    PING,
    HEARTBEAT,
    OTHER
}


data class GiftMessage(
    val giftID: String,
    val giftName: String,
    val giftNum: Int,
    val giftPrice: Int,
    val giftUrl: String,
    override val user: User,
    override val whoStreamer: Streamer
) : DefaultMessage(user, whoStreamer)

data class LikeMessage(
    val likeNum: Int,
    override val user: User,
    override val whoStreamer: Streamer
) : DefaultMessage(user, whoStreamer)

data class JoinMessage(
    override val user: User,
    override val whoStreamer: Streamer
) : DefaultMessage(user, whoStreamer)

data class SubScribeMessage(
    override val user: User,
    override val whoStreamer: Streamer
) : DefaultMessage(user, whoStreamer)

fun JSONObject.parseMessage(): DefaultMessage {
    val room = if (this.containsKey("room")) this.getJSONObject("room") else JSONObject()
    val streamer = Streamer(
        if (room.containsKey("owner")) room.getString("owner") else "",
        if (room.containsKey("id")) room.getString("id") else ""
    )
    val userObj = if (this.containsKey("user")) this.getJSONObject("user") else JSONObject()
    val user = User(
        if (userObj.containsKey("name")) userObj.getString("name") else "",
        if (userObj.containsKey("headUrl")) userObj.getString("headUrl") else "",
        if (userObj.containsKey("id")) userObj.getString("id") else ""
    )
    return when (this.getString("type")) {
        "chat" -> {
            ChatMessage(
                if (this.containsKey("data")) this.getString("data") else "",
                user, streamer)
        }

        "gift" -> {
            val gift = this.getJSONObject("data")
            GiftMessage(
                if (gift.containsKey("gift_id")) gift.getString("gift_id") else "",
                if (gift.containsKey("gift_name")) gift.getString("gift_name") else "",
                if (gift.containsKey("gift_count")) gift.getInteger("gift_count") else 0,
                if (gift.containsKey("gift_price")) gift.getInteger("gift_price") else 0,
                "",
                user,
                streamer
            )
        }

        "like" -> {
            LikeMessage(
                (if (this.containsKey("data")) this.getInteger("data") else -1),
                user, streamer)
        }

        "join" -> {
            JoinMessage(user, streamer)
        }

        "subscribe" -> {
            SubScribeMessage(user, streamer)
        }

        "server" -> {
            val type = when (this.getString("data")) {
                "ping" -> ServerMessageType.PING
                "heartbeat" -> ServerMessageType.HEARTBEAT
                else -> {
                    if (cyanPlugin.config.getBoolean("debug")) {
                        cyanPlugin.server.consoleSender.sendMessage("§a未知服务器消息类型: ${this.getString("data")}")
                    }
                    ServerMessageType.OTHER
                }
            }
            ServerMessage(type,  this.getLongValue("timestamp"), streamer)
        }

        else -> {
            if (cyanPlugin.config.getBoolean("debug")) {
                cyanPlugin.server.consoleSender.sendMessage("§a未知的消息类型: ${this.getString("type")}")
            }
            DefaultMessage(user, streamer)
        }
    }
}