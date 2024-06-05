package cn.nostmc.pixgame.utils

enum class Type(val type: Int, val keyName: String, val description: String) {
    CHAT(1,"chat","聊天"),
    GIFT(2,"gift","礼物"),
    LIKE(3,"like","点赞"),
    MEMBER(4,"member","进入"),
    SOCIAL(5,"social","关注");

    companion object {
        @JvmStatic
        fun getThisType(key : String) : Type {
            for (type in entries) {
                if (type.keyName == key) {
                    return type
                }
            }
            return CHAT
        }
    }

}