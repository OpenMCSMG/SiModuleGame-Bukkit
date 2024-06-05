package cn.nostmc.pixgame.presets

/**
 * C控制台主类
 */
class Console {
    /**
     * 输出日志
     */
    fun log(message: String) {
        MineCraft.plugin.logger.info(message)
    }

}