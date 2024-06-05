package cn.nostmc.pixgame.service

import cn.nostmc.pixgame.cyanlib.launcher.CyanPluginLauncher.cyanPlugin
import cn.nostmc.pixgame.function.callFunction
import cn.nostmc.pixgame.function.invokeJS
import cn.nostmc.pixgame.service.BuiltInListener.ps
import cn.nostmc.pixgame.utils.BossBar
import cn.nostmc.pixgame.utils.DataLoader.accumulation
import cn.nostmc.pixgame.utils.DataLoader.coolDown
import cn.nostmc.pixgame.utils.DataLoader.doubleJump
import me.clip.placeholderapi.PlaceholderAPI
import net.md_5.bungee.api.ChatMessageType
import net.md_5.bungee.api.chat.TextComponent
import org.bukkit.Bukkit
import org.bukkit.attribute.Attribute
import org.bukkit.entity.Player
import java.util.*
import java.util.concurrent.atomic.AtomicInteger
import kotlin.random.Random

class GameHandler(private val commandList: MutableList<String>, private val amount: Int, private val giftID: String) {

    //  循环执行池子
    private val list = mutableListOf<String>()

    // 原子Integer跑了多少遍
    private val atomicInteger = AtomicInteger(0)

    fun run() {
        isRunStart = true
        // 先赋值到池子
        list.addAll(commandList)
        // 开跑
        runCommands()
    }

    var isRunStart = false
    var isRunEnd = false

    /**
     * 听过音乐么？ 列表循环！
     */
    private fun isListWhile(): Boolean {
        if (commandList.isEmpty()) {
            return false
        }
        return commandList[0].startsWith("gift", true)
    }

    //    private val repeat = Regex("^r[0-9]+")
    private fun runCommands() {
        if (list.isEmpty()) { // 不满足就把list加满 继续滚去跑下一哔
            if (isListWhile()) {
                if (cyanPlugin.config.getBoolean("debug")) {
                    println("[调试] 重置循环池子: ${commandList[0]}")
                }
                // 总值首位是gift 蓄满！
                list.addAll(commandList)
            } else {
                isRunEnd = true// 所有的都跑结束了
                return
            }
        }
        // 征程它运行执行第0个
        val cmd = if (list.size > 0) {
            if (cyanPlugin.config.getBoolean("debug")) {
                println("[调试] 执行: ${list[0]}")
            }
            list[0]
        } else {
            isRunEnd = true// 所有的都跑结束了
            return
        }
        // 删除最顶层的list
        val default: Long
        list.removeAt(0)
        /////////////////////// 关键词触发
        // 如果是带gift 关键词就是说明是外部循
        if (isListWhile() && cmd.startsWith("gift", true)) {
            // 如果 外部循环 就检测  循环使用列表池子的值是否跑没了  和 循环使用次数是不是满足了 满足了就不执行了
            // 情况1 蓄满了发现 已经跑到了就停止
            // 情况2 蓄满了发现 没跑到继续跑
            if (atomicInteger.get() >= amount) {
                //不执行了
                if (cyanPlugin.config.getBoolean("debug")) {
                    println("[调试] gift${giftID} 关键词触发满足条件不执行了")
                }
                isRunEnd = true// 所有的都跑结束了
                return
            } else {
                // 没满足就继续跑
                atomicInteger.incrementAndGet()
                if (cyanPlugin.config.getBoolean("debug")) {
                    println("[调试] 重置循环池子第${atomicInteger.get()}次")
                }
                // 下个指令
            }
            // 不管
            // 下个指令
        } else if (cmd.startsWith("cooldown", true)) {
            val time = cmd.split(":")[1].trim().toInt()// 获取冷却时间
            if (coolDown.contains(giftID)) {
                if (coolDown[giftID]!! >= System.currentTimeMillis()) {
                    coolDown.remove(giftID)
                    // 下个指令
                    if (cyanPlugin.config.getBoolean("debug")) {
                        println("[调试] $giftID 冷却完成了")
                    }
                } else {
                    isRunEnd = true// 所有的都跑结束了
                    if (cyanPlugin.config.getBoolean("debug")) {
                        println("[调试] $giftID 冷却中 记录的时间${coolDown[giftID]!!} / ${System.currentTimeMillis()}")
                    }
                    return
                }
            } else {
                coolDown[giftID] = System.currentTimeMillis() + (time * 1000L)
                if (cyanPlugin.config.getBoolean("debug")) {
                    println("[调试] $giftID 冷却中 添加的时间${coolDown[giftID]!!}")
                }
                // 下个指令
            }
        } else if (cmd.startsWith("accumulation", true)) {
            val need = cmd.split(":")[1].trim().toInt() //
            val name = giftID.keyReplace()// 获取累计数据
            if (accumulation.contains(name)) {// 包含检测这个值大于等于累计数据
                if (accumulation[name]!!.like + 1 >= need) { // 满足了往下跑
                    accumulation[name]!!.bar.close()
                    accumulation.remove(name)
                    if (cyanPlugin.config.getBoolean("debug")) {
                        println("[调试] $giftID 满足了累计数据往下跑")
                    }
                    // 下个指令
                } else {
                    accumulation[name]!!.like += amount
                    accumulation[name]!!.bar.update {
                        it.text = "§6${name}进度: ${accumulation[name]!!.like}/${need}"
                        it.percent = (accumulation[name]!!.like / need.toDouble()).toFloat()
                    }
                    isRunEnd = true// 所有的都跑结束了
                    if (cyanPlugin.config.getBoolean("debug")) {
                        println("[调试] $giftID 累计数据未完成 ${accumulation[name]!!.like}/${need}")
                    }
                    return
                }
            } else {
                val p = Bukkit.getOnlinePlayers().first() ?: return
                accumulation[name] = AccumulationData(
                    amount,
                    BossBar(p, "§6${name}进度", 1.0f, BossBar.Color.YELLOW, BossBar.Style.NOTCHED_20)
                )
                isRunEnd = true// 所有的都跑结束了
                if (cyanPlugin.config.getBoolean("debug")) {
                    println("[调试] $giftID 开始累计数据 ${accumulation[name]!!.like}/${need}")
                }
                return
            }
        } else if (cmd.startsWith("delay", true)) { // 如果是延迟指令 就执行 scheduleSyncDelayedTask 然后把剩余的指令也一起再下次执行
            default = cmd.split(":")[1].trim().toInt() * 1L
            cyanPlugin.server.scheduler.scheduleSyncDelayedTask(cyanPlugin, {
                runCommands()
            }, default)
            if (cyanPlugin.config.getBoolean("debug")) {
                println("[调试] $giftID 延迟 $default Tick 执行下个指令")
            }
            return
        } else if (cmd.lowercase().startsWith("g:")) {
            for (i in 0 until amount) {
                Bukkit.getScheduler().runTaskLater(cyanPlugin, {
                    cmd.replaceFirst("g:", "").trim().executeAsCommand()
                }, 5)
            }
            if (cyanPlugin.config.getBoolean("debug")) {
                println("[调试] $giftID 设置单个指令根据数量循环")
            }
            // 下个指令
        } else {
            cmd.executeAsCommand()
            // 下个指令
        }
        runCommands() // 下一个指令
    }

    /**
     * 专为累计用的文字和谐
     */
    private fun String.keyReplace(): String {
        return this.replace("like", "点赞")
            .replace("gift", "礼物")
            .replace("chat", "聊天")
            .replace("member", "进入房间")
            .replace("social", "关注")
    }

    fun String.placeHolder(player: Player): String {
        return PlaceholderAPI.setPlaceholders(player, this)
    }


    // 这方法只管“执行”指令，不建议乱搞
    private fun String.executeAsCommand() {
        // 变量取第一个:后面的所有内容并删除:前面的所有内容
        val cmd = this.substring(this.indexOf(':') + 1).trim()
        when (this.split(":")[0].lowercase()) {
            "title" -> { // title:TITLE|SUBTITLE|FADEIN|STAY|FADEOUT
                if (cyanPlugin.config.getBoolean("debug")) {
                    println("[调试] 识别关键词体'大头字': $cmd")
                }
                Bukkit.getScheduler().runTask(cyanPlugin) {
                    ps.forEach { player ->
                        val title = cmd.split("|")[0].replace('&', '§').placeHolder(player)
                        val subtitle = (if (cmd.split("|").size > 1) cmd.split("|")[1] else "").replace('&', '§').placeHolder(player)
                        val fadein = (if (cmd.split("|").size > 2) cmd.split("|")[2].toIntOrNull() ?: 1 else 1)
                        val stay = (if (cmd.split("|").size > 3) cmd.split("|")[3].toIntOrNull() ?: 1 else 1)
                        val fadeout = (if (cmd.split("|").size > 4) cmd.split("|")[4].toIntOrNull() ?: 1 else 1)
                        player.sendTitle(title, subtitle, fadein, stay, fadeout)
                    }
                }
            }


            "actionbar" -> {
                if (cyanPlugin.config.getBoolean("debug")) {
                    println("[调试] 识别关键词体'演示条': $cmd")
                }
                Bukkit.getScheduler().runTask(cyanPlugin) {
                    ps.forEach { player ->
                        player.spigot().sendMessage(
                            ChatMessageType.ACTION_BAR,
                            TextComponent(cmd.replace('&', '§').placeHolder(player))
                        )
                    }
                }
            }

            "message" -> {
                if (cyanPlugin.config.getBoolean("debug")) {
                    println("[调试] 识别关键词体'聊天文字': $cmd")
                }
                Bukkit.getScheduler().runTask(cyanPlugin) {
                    ps.forEach { player -> player.sendMessage(cmd.replace('&', '§').placeHolder(player)) }
                }
            }

            "command" -> {
                if (cyanPlugin.config.getBoolean("debug")) {
                    println("[调试] 识别关键词体'玩家指令': $cmd")
                }
                for (i in 0 until doubleJump) {
                    ps.forEach { player ->
                        if (!cmd.contains("[x]")) {
                            player.slowCommand(cmd.placeHolder(player))
                        } else {
                            val nA = cmd.split("[x]")
                            if (nA.size > 1) {
                                // 创个原子Integer
                                val number = nA[0].placeHolder(player).trim().toInt()
                                if (number <= 1) {
                                    player.slowCommand(cmd.split("[x]")[1].placeHolder(player).trim())
                                    return@forEach
                                }
                                val atomicInteger = AtomicInteger(number)
                                Timer().schedule(object : TimerTask() {
                                    override fun run() {
                                        if (atomicInteger.decrementAndGet().apply {
                                                if (cyanPlugin.config.getBoolean("debug")) {
                                                    println("[调试] 原子Integer: $this")
                                                }
                                            } < 0) { //atomicInteger -1
                                            cancel()
                                        } else {
                                            player.slowCommand(cmd.split("[x]")[1].placeHolder(player).trim())
                                        }
                                    }
                                }, 0, 500)
                            } else {
                                player.slowCommand(cmd.placeHolder(player))
                            }
                        }
                    }
                }
            }


            "random" -> {
                if (cyanPlugin.config.getBoolean("debug")) {
                    println("[调试] 识别关键词体'随机': $cmd")
                }
                Bukkit.getScheduler().runTask(cyanPlugin) {
                    val cmdList = cmd.split("|")
                    val real = cmdList.random()
                    if (cyanPlugin.config.getBoolean("debug")) {
                        println("抽中指令: $real")
                    }
                    real.executeAsCommand()
                }
            }

            "jail" -> {
                if (cyanPlugin.config.getBoolean("debug")) {
                    println("[调试] 识别关键词体'监狱': $cmd")
                }
                Bukkit.getScheduler().runTask(cyanPlugin) {
                    ps.forEach { player ->
                        // 先将玩家把xyz 对准方块中心
                        Bukkit.dispatchCommand(player, "tp ~ ~ ~")
                        Bukkit.dispatchCommand(player, "fill ~-1 ~-1 ~-1 ~1 ~2 ~1 ${cmd.placeHolder(player)}")
                        Bukkit.dispatchCommand(player, "fill ~ ~ ~ ~ ~1 ~ minecraft:air")
                    }
                }
            }

            "rtp" -> {
                if (cyanPlugin.config.getBoolean("debug")) {
                    println("[调试] 识别关键词体'随机传送': $cmd")
                }
                val range = cmd.toIntOrNull() ?: 100
                Bukkit.getScheduler().runTask(cyanPlugin) {
                    ps.forEach { player ->
                        val x = Random.nextInt(-range, range)
                        val z = Random.nextInt(-range, range)
                        val y = player.world.getHighestBlockYAt(x, z)
                        Bukkit.dispatchCommand(player, "tp $x $y $z")
                    }
                }
            }


            "damage" -> {
                if (cyanPlugin.config.getBoolean("debug")) {
                    println("[调试] 识别关键词体'伤害': $cmd")
                }
                Bukkit.getScheduler().runTask(cyanPlugin) {
                    ps.forEach { player ->
                        player.damage(cmd.toDouble())
                    }
                }
            }

            "health" -> {
                if (cyanPlugin.config.getBoolean("debug")) {
                    println("[调试] 识别关键词体'生命设置': $cmd")
                }
                Bukkit.getScheduler().runTask(cyanPlugin) {
                    ps.forEach { player ->
                        player.getAttribute(Attribute.GENERIC_MAX_HEALTH)!!.baseValue += cmd.toDouble()
                        player.health = player.getAttribute(Attribute.GENERIC_MAX_HEALTH)!!.baseValue
                    }
                }
            }

            "nuke" -> {
                if (cyanPlugin.config.getBoolean("debug")) {
                    println("[调试] 识别关键词体'炸弹': $cmd")
                }
                val amount = cmd.toDouble()
                // 根据 指定的数量 在玩家附近16格内下tnt雨
                Bukkit.getScheduler().runTask(cyanPlugin) {
                    ps.forEach { player ->
                        for (i in 0 until amount.toInt()) {
                            player.world.spawn(
                                player.location.add(
                                    Random.nextInt(-16, 16).toDouble(),
                                    30.0,
                                    Random.nextInt(-16, 16).toDouble()
                                ), org.bukkit.entity.TNTPrimed::class.java
                            )
                        }
                    }
                }
            }


            "freeze" -> {
                if (cyanPlugin.config.getBoolean("debug")) {
                    println("[调试] 识别关键词体'冻结': $cmd")
                }
                if (cmd == "true") {
                    ps.forEach { player ->
                        BuiltInListener.streamerFreeze[player] = true
                    }
                } else {
                    ps.forEach { player ->
                        BuiltInListener.streamerFreeze[player] = false
                    }
                }
            }


            "bfunc" -> {
                if (cyanPlugin.config.getBoolean("debug")) {
                    println("[调试] 识别关键词体'js拓展': $cmd")
                }
                Bukkit.getScheduler().runTask(cyanPlugin) {
                    // cmd的值是xxx(xxxx,xx,xxx)需要自己解析
                    val function = cmd.split(",")
                    // 1到结尾都是参数
                    val args = try {
                        function.subList(1, function.size).toMutableList()
                    } catch (e: Exception) {
                        mutableListOf()
                    }
                    callFunction(function[0],args )
                }
            }


            "binvoke" -> {
                if (cyanPlugin.config.getBoolean("debug")) {
                    println("[调试] 识别关键词体'js直接调用java方法': $cmd")
                }
                ps.forEach { player ->
                    invokeJS(cmd.placeHolder(player))
                }
            }


            else -> {
                if (cyanPlugin.config.getBoolean("debug")) {
                    println("[调试] 识别关键词体'未知': $cmd")
                }
                Bukkit.getScheduler().runTask(cyanPlugin) {
                    ps.forEach { player ->
                        Bukkit.dispatchCommand(player, cmd.placeHolder(player))
                        Bukkit.getScheduler().runTaskAsynchronously(cyanPlugin) {
                            Bukkit.getConsoleSender().sendMessage(
                                "${this.split(":")[0]}不是个关键词，我先当指令跑了哈~, 如果效果不出请你添加关键词"
                            )
                        }
                    }
                }
            }
        }


    }


    private fun Player.slowCommand(cmd: String) {
        Bukkit.getScheduler().runTaskLater(cyanPlugin, {
            Bukkit.dispatchCommand(this, cmd)
        }, 10)
    }


}


data class AccumulationData(
    var like : Int,
    val bar : BossBar,
)
