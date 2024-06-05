package cn.nostmc.pixgame.function

import cn.nostmc.pixgame.cyanlib.launcher.CyanPluginLauncher.cyanPlugin
import cn.nostmc.pixgame.presets.Console
import cn.nostmc.pixgame.presets.EventBus
import cn.nostmc.pixgame.presets.LoadPresents
import cn.nostmc.pixgame.presets.Scheduler.cancelAll
import cn.nostmc.pixgame.service.BuiltInListener.isLoading
import com.oracle.truffle.js.scriptengine.GraalJSEngineFactory
import org.bukkit.Bukkit
import javax.script.ScriptException

fun reloadJS(isExecute: Boolean = false) {
    if (isExecute) {
        cancelAll()
        EventBus.clear()
        cyanPlugin.engine.close()
        cyanPlugin.engine = GraalJSEngineFactory().scriptEngine;
    }
    LoadPresents.load()
    val folder = cyanPlugin.dataFolder.resolve("function")
    if (!folder.exists()) {
        folder.mkdirs()
    }
    val files = folder.listFiles()
    if (files.isNullOrEmpty() || !folder.resolve("app.js").exists()) {
        // 释放包内的app.js
        cyanPlugin.server.consoleSender.sendMessage("释放默认app.js本次不加载了先开发吧")
        cyanPlugin.getResource("function/app.js")!!.copyTo(folder.resolve("app.js").outputStream())
        return
    }
    val list = folder.listFiles()!!.toMutableList()
    cyanPlugin.server.consoleSender.sendMessage("加载JS文件 ${list.size}")
    val classLoads = list.filter {
        it.readText().contains("class ")
    }
    // 从总的list中移除class文件
    list.removeAll(classLoads)
    list.remove(folder.resolve("app.js"))
    classLoads.forEach {
        val script = it.readText()
        cyanPlugin.server.consoleSender.sendMessage("加载class文件 ${it.name}")
        try {
            cyanPlugin.engine.eval(script)
        } catch (e: ScriptException) {
            Bukkit.broadcastMessage(
                """
        §c加载class文件 ${it.name} 出现错误
        是js的语句问题可能存在方法调用错误不对 请查看§fhttps://live.cyanbukkit.cn/docs
        §c<JS>问题 ${e.localizedMessage}""".trimIndent()
            )
        }
        Thread.sleep(1000)
    }
    list.forEach {
        val script = it.readText()
        cyanPlugin.server.consoleSender.sendMessage("加载文件 ${it.name}")
        try {
            cyanPlugin.engine.eval(script)
        } catch (e: ScriptException) {
            Bukkit.broadcastMessage(
                """
        §c加载 ${it.name} 出现错误
        是js的语句问题可能存在方法调用错误不对 请查看§fhttps://live.cyanbukkit.cn/docs
       §c <JS>问题 ${e.localizedMessage}""".trimIndent()
            )
        }
        Thread.sleep(1000)
    }
    // 最后加载APP.JS
    cyanPlugin.server.consoleSender.sendMessage("加载文件 app.js")
    try {
        cyanPlugin.engine.eval(folder.resolve("app.js").readText())
    } catch (e: ScriptException) {
        Bukkit.broadcastMessage(
            """
        §c加载 app.js 出现错误
        是js的语句问题可能存在方法调用错误不对 请查看§fhttps://live.cyanbukkit.cn/docs
        §c<JS>问题 ${e.localizedMessage}""".trimIndent()
        )
    }
    isLoading = false
}


/**
 * 根据js方法名字调用参数
 * @param name 方法名
 * @param args 参数
 */
fun callFunction(name: String, list: MutableList<String>): Any {
    Console().log("调用方法 $name 参数 $list")
    // 倒是把参数 args 分开
    val result = try {
        cyanPlugin.engine.invokeFunction(name, *list.toTypedArray())
    } catch (e: Exception) {
        Bukkit.broadcastMessage(
            """
            §c<JS>问题 ${e.localizedMessage}""".trimIndent()
        )
        "§c没有方法为§f$name §c或者参数§f${list}§c不对"
    }
    return result ?: "void"
}
/**
 * 直接执行js语句
 * @param js js语句
 */
fun invokeJS(js: String): String {
    return try {
        val obj = cyanPlugin.engine.eval(js)
        if  (obj == null) {
            "§c执行js语句§f$js§c无返回值(可能是void)"
        } else {
            "§6执行结果: §f$obj"
        }
    } catch (e: Exception) {
        e.printStackTrace()
        "§c执行js语句§f$js§c出现错误"
    }
}


/**
 * 输出js中的变量
 * @param name 变量名
 */
fun getVariable(name: String): Any {
    try {
        return cyanPlugin.engine.get(name)
    } catch (e: Exception) {
        Bukkit.broadcastMessage(
            """
            §c<JS>问题 ${e.localizedMessage}""".trimIndent()
        )
        return "§c没有找到变量§f$name"
    }
}

/**
 * 检索js语句中有哪些方法以及参数
 */
fun getFieldList(): List<String> {
    val list = mutableListOf<String>()
    val bindings = cyanPlugin.engine.getBindings(100)
    bindings.forEach {
        list.add("§6方法名字: §f${it.key}， §6参数:§f${cyanPlugin.engine.get(it.key.toString())}")
    }
    return list
}



