package cn.nostmc.pixgame.function

/**
 *  友好化报错
 */
class FriendException(
    override val message: String
) : Exception() {
    override fun getLocalizedMessage(): String {
        return  super.getLocalizedMessage()
    }

    override fun getStackTrace(): Array<StackTraceElement> {
        val stackTrace = super.getStackTrace().toMutableList()
        stackTrace.removeIf { it.className.contains("cn.nostmc.pixgame") }
        return stackTrace.toTypedArray()
    }

}