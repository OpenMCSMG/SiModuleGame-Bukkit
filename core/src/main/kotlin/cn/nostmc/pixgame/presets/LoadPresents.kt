package cn.nostmc.pixgame.presets

import cn.nostmc.pixgame.cyanlib.launcher.CyanPluginLauncher.cyanPlugin
import com.comphenix.protocol.ProtocolLibrary
import org.bukkit.entity.EntityType
import org.bukkit.event.EventPriority

object LoadPresents {

    fun load() {
        cyanPlugin.engine.put("mc", MineCraft)
        cyanPlugin.server.pluginManager.registerEvents(EventBus, cyanPlugin)
        cyanPlugin.engine.put("eventBus", EventBus)
        cyanPlugin.engine.put("papi", Placeholder)
        cyanPlugin.engine.put("scheduler", Scheduler)
        cyanPlugin.engine.put("utils", Utils)
        cyanPlugin.engine.put("console", Console())
        cyanPlugin.engine.put("protocol", ProtocolLibrary.getProtocolManager())
        cyanPlugin.engine.put("CreatePixScript", CreatePixScript)
        cyanPlugin.engine.put("JSEventHandler", JSEventHandler::class.java)
        cyanPlugin.engine.put("EventPriority", EventPriority::class.java)
        cyanPlugin.engine.put("EntityType", EntityType::class.java)
        // 不知道为哦啥要这个
    }

}