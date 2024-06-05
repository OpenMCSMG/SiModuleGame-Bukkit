package cn.nostmc.pixgame.commands

import cn.nostmc.pixgame.function.callFunction
import cn.nostmc.pixgame.function.getFieldList
import cn.nostmc.pixgame.function.getVariable
import cn.nostmc.pixgame.function.invokeJS
import org.bukkit.command.Command
import org.bukkit.command.CommandSender

object RunJsFunction: Command(
    "bdebug",
    "运行js函数",
    "/runjsfunction",
    listOf("bd")
) {

    const val PREFIX = "§c未知参数/未找到参数 请使用 /help 查看帮助"

    override fun execute(sender: CommandSender, commandLabel: String, args: Array<out String>): Boolean {
        if (args.isNotEmpty()) {
            when (args[0]) {
                "func" -> {
                    val functionName = try {
                        args[1]
                    } catch (e: Exception) {
                        sender.sendMessage("找不到方法")
                        return true
                    }
                    val functionArgs = args.sliceArray(2 until args.size)
                    val result = callFunction(functionName, functionArgs.toMutableList())
                    sender.sendMessage("执行结果: $result")
                }
                "var" -> {
                    // 从文件中读取
                    val path = try {
                        args[1]
                    } catch (e: Exception) {
                        sender.sendMessage("§c参数不足")
                        return true
                    }
                    sender.sendMessage("变量${path}值: ${getVariable(path)}")
                }
                "invoke" -> {
                    // 直接写个js语句
                    val js = args.sliceArray(1 until args.size).joinToString(" ")
                    sender.sendMessage("执行结果: ${invokeJS(js)}")
                }
                "list" -> {
                    val list = getFieldList()
                    sender.sendMessage("方法列表:")
                    list.forEach(sender::sendMessage)
                }
                else -> {
                    sender.sendMessage(PREFIX)
                }
            }
        } else {
            sender.sendMessage(PREFIX)
        }
        return true
    }


    override fun tabComplete(sender: CommandSender, alias: String, args: Array<String>): MutableList<String> {
        return mutableListOf("func", "var", "help", "invoke", "list")
    }
}