package cn.nostmc.pixgame

import cn.nostmc.pixgame.commands.ManualInteractionCommand.isLink
import cn.nostmc.pixgame.connect.DefaultSocketConnect
import cn.nostmc.pixgame.cyanlib.launcher.CyanPluginLauncher.cyanPlugin
import cn.nostmc.pixgame.function.FriendException
import cn.nostmc.pixgame.function.reloadJS
import cn.nostmc.pixgame.presets.MineCraft
import cn.nostmc.pixgame.service.BuiltInListener
import cn.nostmc.pixgame.service.PlayList
import cn.nostmc.pixgame.service.hook.JsPAPI
import cn.nostmc.pixgame.service.hook.RandomNumberPAPI
import cn.nostmc.pixgame.utils.BossBar
import cn.nostmc.pixgame.utils.DataLoader
import cn.nostmc.pixgame.utils.Scoreboard
import cn.nostmc.pixgame.utils.Title
import cn.nostmc.pixgame.view.BindsGUIListener
import org.bukkit.scheduler.BukkitRunnable
import java.net.URI


fun realEnable() {
    DataLoader.initGet()
    Scoreboard.init(cyanPlugin)
    BossBar.init(cyanPlugin)
    Title.init(cyanPlugin)
    MineCraft.init(cyanPlugin)
    cyanPlugin.server.scheduler.runTaskTimerAsynchronously(cyanPlugin, PlayList.RunTask(), 0, 5)
    RandomNumberPAPI.register()
    JsPAPI.register()
    cyanPlugin.server.pluginManager.registerEvents(BuiltInListener, cyanPlugin)
    cyanPlugin.server.pluginManager.registerEvents(BindsGUIListener, cyanPlugin)
    Thread.sleep(2000)
    reloadJS()
}

fun updatePlugin() {
    cyanPlugin.config.options().copyDefaults(true)
    cyanPlugin.config.options().header(
        """
        -----------------------------------------------------------
        debug: 的关键词是说明测试模式开启后，会在控制台输出调试信息
        GUI: GUi的设置
            Platform: 平台显示对应的礼物贴图使用材质包下可见
            Title: GUI的标题
        LinkMode: 是连接模式
        填写格式为 "手动连接/自动连接“  注意 连接 的文字
        手动连接时：插件开启后需要进服执行指令 /startlive 或者 按这SHIFT 头朝上 左键点击
        自动连接时：插件开启后会自动连接到服务器
        什么是LinkInfo？
        LinkInfo 是连接信息
            Node:服务器节点
            GroupID: 主播组ID
            GroupSecretKey: 主播组密钥
        -----------------------------------------------------------
    """.trimIndent()
    )
    if (cyanPlugin.config.contains("PKMode")) {
        cyanPlugin.config.set("PKMode", null)
    }
    if (cyanPlugin.config.contains("GUI.快速执行")) {
        val origin = cyanPlugin.config.getString("GUI.快速执行")!!
        cyanPlugin.config.set("GUI.快速执行", null)
        cyanPlugin.config.set("GUI.Title", origin)
    }
    cyanPlugin.saveConfig()


    cyanPlugin.bindsConfig.options().copyDefaults(true)
    cyanPlugin.bindsConfig.options().header(
        """
        -----------------------------------------------------------
        配置教程指引 \!/
        1. 请在技术指导下修改配置文件
        2. 请严格按照按照格式去使用
        关键词 - - -
        gift:             -  为按照礼物数量整列循环循环在首行填写
        cooldown: 60      -  冷却60秒后向下执行期间不会触发不会执行
        accumulation: 60  -  累计60次向下执行后清空累计重新计算
        delay: 5          -  延迟5 TICK 后向下执行
        title: 大标题|小标题|持续时间|渐变时间|消失时间
        actionbar: 内容
        message: 内容
        command: 指令
        command: 5 [x] 指令      -  5为执行次数 [x] 为与指令的间隔符
        random: 指令|指令|指令     -  随机执行指令支持title以下的所有关键词
        jail: bedrock           - 用基岩围住玩家
        rtp: 100                -  100以内随机传送
        damage: 10              - 伤害玩家10点
        health: 10              - 给予玩家10点血量
        nuke: 20               -  爆炸
        freeze: true             -  冻结玩家
        freeze: false            -  解冻玩家
        bfunc: 方法,参数,参数...   -  调用内置js方法
        binvoke: 方法(xxxx)      -  以js语句调用方法
        内置字符变量 - - -
        %user%         -  直播间用户名称
        %streamer%     -  主播名称
        %chat-msg%     -  直播间聊天
        %gift-name%    -  礼物名称/ID
        %gift-num%     -  礼物数量
        %like-num%     -  点赞数量
        内置PlaceholderAPI- - -
        %randomnumber_1_5%  -  随机数1-5
        %bfunc_xxx%         -  调用js方法获取js返回值
        -----------------------------------------------------------
    """.trimIndent()
    )
    cyanPlugin.bindsConfig.save(cyanPlugin.binds)
    // 验证加检查更新
    matchStartMode()
}


fun matchStartMode() {
    val linkMode = cyanPlugin.config.getString("LinkMode")!!
    if (linkMode.isEmpty()) {
        if (!cyanPlugin.config.getBoolean("debug")) {
            throw FriendException("亲亲你需要在配置文件输入 主播组 特有的链接码")
        }
    }
    if (linkMode.contains("自动连接")) link()
    realEnable()
}

lateinit var linkAnimation: BukkitRunnable

/**
 * 链接的时候必须走着一条路
 */
fun linkTextAnimation() {
    var count = 1
    linkAnimation = object : BukkitRunnable() {
        override fun run() {
            when (count) {
                1 -> {
                    cyanPlugin.server.onlinePlayers.forEach {
                        Title.title(it, "§6§l开始直播", "§7§lLinking.", 0, 20, 0)
                    }
                }

                2 -> {
                    cyanPlugin.server.onlinePlayers.forEach {
                        Title.title(it, "§6§l开始直播", "§7§lLinking..", 0, 20, 0)
                    }
                }

                3 -> {
                    cyanPlugin.server.onlinePlayers.forEach {
                        Title.title(it, "§6§l开始直播", "§7§lLinking...", 0, 20, 0)
                    }
                }
            }
            if (count == 3) {
                count = 1
            } else {
                count++
            }
        }
    }
    linkAnimation.runTaskTimer(cyanPlugin, 0, 20)
}


fun stopAnimation() {
    if (::linkAnimation.isInitialized) {
        linkAnimation.cancel()
        cyanPlugin.server.onlinePlayers.forEach {
            Title.title(it, "§6§l开始直播", "§7§l连接成功", 0, 20, 0)
        }
    }
}

/**
 * 不管模式啥连就行了！管它呢
 */
fun link() {
    linkTextAnimation()
    val config = cyanPlugin.config.getConfigurationSection("LinkInfo")
    if (config == null) {
        cyanPlugin.server.consoleSender.sendMessage("§c配置文件错误, 请联系管理员")
        return
    }
    val node = config.getString("Node")!!
    val group = config.getString("GroupID")!!
    val secret = config.getString("GroupSecretKey")!!
    val uri = try {
        URI("${node}/v1/live/group/ws?uuid=${group}")
    } catch (e: Exception) {
        cyanPlugin.server.consoleSender.sendMessage("§c连接地址不合法, 请检查配置文件")
        return
    }
    val headers = mutableMapOf<String, String>()
    headers["Authorization"] = secret
    default = DefaultSocketConnect(uri, headers)
    default.connect()
    isLink = true
}


fun sendDebugMessage(message: String) {
    cyanPlugin.server.consoleSender.sendMessage("§a核心:§7$message")
}

lateinit var default: DefaultSocketConnect