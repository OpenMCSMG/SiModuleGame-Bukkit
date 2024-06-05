package cn.nostmc.pixgame.presets

import cn.nostmc.pixgame.presets.Utils.allBossBar
import cn.nostmc.pixgame.presets.Utils.allScoreboard
import cn.nostmc.pixgame.utils.Scoreboard
import org.bukkit.Bukkit
import org.graalvm.polyglot.Context

object Scheduler {

    val tasks = mutableListOf<Int>()

    /**
     * 运行任务
     * @param task 任务
     * @param async 是否异步
     * @return 任务ID
     */
    fun run(task: JSFunction, async: Boolean): Int {
        return if (async) {
            val id = MineCraft.plugin.server.scheduler
                .runTaskAsynchronously(MineCraft.plugin, Runnable {
                task.apply(arrayOf()) }).taskId
            tasks.add(id)
            id
        } else {
            val id = MineCraft.plugin.server.scheduler
                .runTask(MineCraft.plugin, Runnable
             { task.apply(arrayOf()) }).taskId
            tasks.add(id)
            id
        }
    }

    /**
     * 延迟运行任务
     * @param task 任务
     * @param delay 延迟时间
     * @param async 是否异步
     * @return 任务ID
     */
    fun delay(task: JSFunction, delay: Long, async: Boolean): Int {
        return if (async) {
            val id = MineCraft.plugin.server.scheduler
                .runTaskLaterAsynchronously(MineCraft.plugin,
                    Runnable { task.apply(arrayOf()) }, delay
            ).taskId
            tasks.add(id)
            id
        } else {
            val id =MineCraft.plugin.server.scheduler.runTaskLater(
                MineCraft.plugin,
                Runnable { task.apply(arrayOf()) }, delay
            ).taskId
            tasks.add(id)
            id
        }
    }

    /**
     * 重复运行任务
     * @param task 任务
     * @param delay 延迟时间
     * @param period 重复时间
     * @param async 是否异步
     * @return 任务ID
     */
    fun repeat(task: JSFunction, delay: Long, period: Long, async: Boolean): Int {
        return if (async) {
            val id = MineCraft.plugin.server.scheduler.runTaskTimerAsynchronously(
                MineCraft.plugin,
                Runnable{ task.apply(arrayOf()) }, delay, period
            ).taskId
            tasks.add(id)
            id
        } else {
            val id = MineCraft.plugin.server.scheduler.runTaskTimer(
                MineCraft.plugin,
                Runnable {
                    try {
                        task.apply(arrayOf())
                    } catch (e: IllegalStateException) {
                        Bukkit.broadcastMessage("慢点: ${e.localizedMessage}")
                    }
                }, delay, period
            ).taskId
            tasks.add(id)
            id
        }
    }

    /**
     * 取消任务
     * @param taskId 任务ID
     */
    fun cancel(taskId: Int) {
        MineCraft.plugin.server.scheduler.cancelTask(taskId)
    }

    fun cancelAll() {
        tasks.forEach {
            MineCraft.plugin.server.scheduler.cancelTask(it)
        }
        Scoreboard.remove()
        // 清空MineCraft.plugin.server的所有BossBar
        allBossBar.forEach {
            it.removeAll()
        }
        allScoreboard.forEach {
            it.close()
        }
    }
}