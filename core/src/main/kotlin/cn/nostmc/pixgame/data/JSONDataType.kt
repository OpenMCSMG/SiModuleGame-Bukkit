package cn.nostmc.pixgame.data

import cn.nostmc.pixgame.api.data.Streamer
import cn.nostmc.pixgame.api.data.User
import cn.nostmc.pixgame.sendDebugMessage
import com.alibaba.fastjson2.JSONObject


open class DefaultMessage(
    open val user: cn.nostmc.pixgame.api.data.User,
    open val whoStreamer: cn.nostmc.pixgame.api.data.Streamer
)

data class ChatMessage(
    val message: String,
    override val user: cn.nostmc.pixgame.api.data.User,
    override val whoStreamer: cn.nostmc.pixgame.api.data.Streamer
) : DefaultMessage(user, whoStreamer)

open class ServerMessage(
    open val type: ServerMessageType,
    open val timeStamps: Long,
    whichPlantFrom: cn.nostmc.pixgame.api.data.Streamer
) : DefaultMessage(cn.nostmc.pixgame.api.data.User("", "", ""), whichPlantFrom)

enum class ServerMessageType {
    PING,
    HEARTBEAT,
    CONNECTED,
    AUTH_SUCCESS,
    CLOSE,
    OTHER
}


data class GiftMessage(
    val giftID: String,
    val giftName: String,
    val giftNum: Int,
    val giftPrice: Int,
    val giftUrl: String,
    override val user: cn.nostmc.pixgame.api.data.User,
    override val whoStreamer: cn.nostmc.pixgame.api.data.Streamer
) : DefaultMessage(user, whoStreamer)

data class LikeMessage(
    val likeNum: Int,
    override val user: cn.nostmc.pixgame.api.data.User,
    override val whoStreamer: cn.nostmc.pixgame.api.data.Streamer
) : DefaultMessage(user, whoStreamer)

data class JoinMessage(
    override val user: cn.nostmc.pixgame.api.data.User,
    override val whoStreamer: cn.nostmc.pixgame.api.data.Streamer
) : DefaultMessage(user, whoStreamer)

data class FollowMessage(
    override val user: cn.nostmc.pixgame.api.data.User,
    override val whoStreamer: cn.nostmc.pixgame.api.data.Streamer
) : DefaultMessage(user, whoStreamer)

fun JSONObject.parseMessage(): DefaultMessage {
    val room = if (this.containsKey("room")) this.getJSONObject("room") else JSONObject()
    val streamer = cn.nostmc.pixgame.api.data.Streamer(
        if (room.containsKey("owner")) room.getString("owner") else "",
        if (room.containsKey("id")) room.getString("id") else ""
    )
    val userObj = if (this.containsKey("user")) this.getJSONObject("user") else JSONObject()
    val user = cn.nostmc.pixgame.api.data.User(
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

        "follow" -> {
            FollowMessage(user, streamer)
        }


        "server" -> {
            val type = when (this.getString("data")) {
                "ping" -> ServerMessageType.PING
                "heartbeat" -> ServerMessageType.HEARTBEAT
                "connected" -> ServerMessageType.CONNECTED
                "auth success" -> ServerMessageType.AUTH_SUCCESS
                "closed" -> ServerMessageType.CLOSE
                else -> ServerMessageType.OTHER
            }
            ServerMessage(type,  this.getLongValue("timestamp"), streamer)
        }

        else -> {
            sendDebugMessage("§a处理类型消息时收到未知的类型“${this.getString("type")}”")
            DefaultMessage(user, streamer)
        }
    }
}