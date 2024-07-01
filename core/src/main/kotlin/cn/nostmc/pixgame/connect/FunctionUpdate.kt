package cn.nostmc.pixgame.connect

import com.alibaba.fastjson2.JSONObject
import org.bukkit.Bukkit
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL
import kotlin.system.exitProcess


object FunctionUpdate {

    private fun getNewVersion(funName: FunctionSort): String {
        try {// 构建 URL 对象
            val url = URL("https://api.cyanbukkit.cn/v1/live/game/update?name=SiModule-${funName.display}")
            val connection = url.openConnection() as HttpURLConnection// 打开连接
            connection.requestMethod = "GET"
            val responseCode = connection.responseCode// 读取响应
            return if (responseCode == HttpURLConnection.HTTP_OK) {
                val response = connection.inputStream.bufferedReader().readText()
                val jsonObject = JSONObject.parseObject(response)// 解析 JSON
                val version = jsonObject.getJSONObject("data").getString("version")
                Bukkit.getConsoleSender().sendMessage("获取到(${funName.display})最新版本: $version")
                if (version.toString().isEmpty()) {// 检查版本信息是否为空
                    "无法获取版本信息。"
                } else {
                    version
                }
            } else {
                "请求失败，响应码：$responseCode"
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return e.toString()
        }
    }


    private fun contrast(funName: FunctionSort, funFile: File) {
        val version = getNewVersion(funName)
        val newPluginFileName = "SiModule-${funName.display}-${version}.jar"
        val newPluginFile = File("plugins", newPluginFileName)
        if (newPluginFile.exists()) {
            Bukkit.getConsoleSender().sendMessage("插件已是最新版本: ${newPluginFile.name}")
        } else { // 插件卸载掉
            // 获取 funFile 在spigot的加载器设置关
            // 通过系统命令删除文件
            deleteOld(funFile)
            Bukkit.getConsoleSender().sendMessage("删除旧版: ${funFile.name}")
            val downloadUrl =
                URL("https://api.cyanbukkit.cn/v1/live/game/download?name=SiModule-${funName.display}&version=${version}")
            val connection = downloadUrl.openConnection()
            connection.connect()
            val inputStream = connection.getInputStream() // 保存下载的文件
            val outputFile = newPluginFile.absoluteFile
            outputFile.outputStream().use { outputStream ->
                inputStream.copyTo(outputStream)
            }
            Bukkit.getConsoleSender().sendMessage("插件已下载: ${outputFile.name}")
            needRestart = true
        }
    }

    private fun deleteOld(funFile: File) {
        // 强行从jvm中解除占用
        try {
            FileInputStream(funFile).use { }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        val list = Bukkit.getPluginManager().plugins
        for (plugin in list) {
            if (funFile.name.contains(plugin.name)) {
                Bukkit.getConsoleSender().sendMessage("卸载插件: ${plugin.name}")
                try {
                    Bukkit.getPluginManager().enablePlugin(plugin)
                    Bukkit.getPluginManager().disablePlugin(plugin)
                } catch (_: Exception) {
                }
            }
        }
        // 如果是linux
        funFile.delete()
        if (System.getProperty("os.name").contains("Linux")) {
            val command = "rm -f ${funFile.absolutePath}"
            Runtime.getRuntime().exec(command)
        } else if (System.getProperty("os.name").contains("Windows")) {
            val command = "cmd /c del /f ${funFile.absolutePath}"
            Runtime.getRuntime().exec(command)
        }
        while (funFile.exists()) {
            Thread.sleep(1000)
            deleteOld(funFile)
        }
    }


    var needRestart = false

    fun find() {
        val funList = mutableMapOf<FunctionSort, File>()
        // 识别插件列表中存在的玩法
        val pluginDir = File("plugins")
        if (pluginDir.exists()) {
            val pluginFiles = pluginDir.listFiles()
            if (pluginFiles != null) {
                for (pluginFile in pluginFiles) {
                    // 如果不是jar结尾下一个
                    if (!pluginFile.name.endsWith(".jar")) {
                        continue
                    }
                    // 如果开头带有SiModule- 保存并识别出FunctionSort
                    if (pluginFile.name.startsWith("SiModule-")) {
                        val funSort = FunctionSort.getFunctionSort(pluginFile.name)
                        if (funSort != FunctionSort.ERROR) {
                            funList[funSort] = pluginFile
                            Bukkit.getConsoleSender().sendMessage("找到玩法插件: ${pluginFile.name}")
                        } else {
                            Bukkit.getConsoleSender().sendMessage("未知玩法插件: ${pluginFile.name}")
                            continue
                        }
                    }
                }
            }
        }

        if (funList.isEmpty()) {// 如果没有找到玩法插件
            Bukkit.getConsoleSender().sendMessage("未找到任何玩法插件。")
        } else {// 如果找到玩法插件
            for ((funName, funFile) in funList) {
                contrast(funName, funFile)
            }
            if (needRestart){
                Bukkit.getConsoleSender().sendMessage("所有玩法插件已更新完毕需要重新启动服务器。")
                Bukkit.shutdown()
            }
        }
    }

}