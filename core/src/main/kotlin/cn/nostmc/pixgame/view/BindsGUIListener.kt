package cn.nostmc.pixgame.view

import cn.nostmc.pixgame.commands.BReload
import cn.nostmc.pixgame.cyanlib.launcher.CyanPluginLauncher.cyanPlugin
import cn.nostmc.pixgame.utils.DataLoader
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack
import org.bukkit.scheduler.BukkitRunnable
import java.lang.reflect.Method


object BindsGUIListener : Listener {

    private val previousPage = ItemStack(Material.ARROW).apply {
        itemMeta = itemMeta.apply {
            displayName = "§b上一页"
        }
    }
    private val nextPage = ItemStack(Material.ARROW).apply {
        itemMeta = itemMeta.apply {
            displayName = "§b下一页"
        }
    }
    private val glass = ItemStack(Material.GLASS).apply {
        itemMeta = itemMeta.apply {
            displayName = "§bCyanBukkit"
        }
    }
    private val changeAmount = ItemStack(Material.APPLE).apply {
        itemMeta = itemMeta.apply {
            displayName = "§c§l点击切换礼物数(SHIFT 10倍 左键增加右键减少)"
            lore = listOf("1")
        }
    }

    private fun error(ex: Exception) {
        cyanPlugin.logger.warning("""
                                ${ex.message} 
                                报错类型是 ${ex.javaClass.simpleName}
                                前三行报错信息
                            """.trimIndent())
        ex.stackTrace.forEach {
            cyanPlugin.logger.warning(it.toString())
        }
    }

    fun getAllKeys(): MutableList<String> = cyanPlugin.bindsConfig.getKeys(true).toMutableList()

    private fun setDefaultMenu(p: Player): Inventory {
        val bInv = Bukkit.createInventory(p, 54, cyanPlugin.config.getString("GUI.快速执行", ":offset_0::binds_class:"))
        bInv.setItem(45, previousPage)
        bInv.setItem(49,  changeAmount)
        bInv.setItem(53, nextPage)
        return bInv
    }

    private val map = mutableMapOf<Player, NowOpen>()

    fun openGUI(p0: CommandSender): Boolean {
        BReload.execute(p0, "breload", arrayOf())
        if (p0 !is Player) return false
        val p = p0
        val iv = setDefaultMenu(p)
        val run = object : BukkitRunnable() {
            override fun run() {
            }
        }
        run.runTaskTimer(cyanPlugin, 0, 20L)
        val nowOpen = NowOpen(iv, run)
        nowOpen.openDefault(p)
        map[p] = nowOpen
        return true
    }

    /**
     * "xx.xx.xxx" 的格式
     */
    fun String.switchItem() : ItemStack {
        val newItem = ItemStack(Material.COAL)
        val im = newItem.itemMeta
        im.displayName = this
        val newLore = mutableListOf<String>()
        if (this.contains("gift")) {
            try {
                // 用表达式找出string中的int
                val giftID = split(".")[1].toInt()
                newLore.add("§7ID: $giftID")
                val giftName = DataLoader.giftInfo[giftID]
                newLore.add("§7礼物名: $giftName")
            } catch (_: Exception) {
            }
            try {
                val method: Method = im.javaClass.getMethod("setCustomModelData", Integer::class.java)
                method.isAccessible = true
                method.invoke(im, split(".")[1].toInt())
            } catch (_: NumberFormatException){
            } catch (_: NoSuchMethodException) {
            } catch (e: Exception) {
                error(e)
            }
        }
        newLore.add("§7触发:")
        val bindsList = cyanPlugin.bindsConfig.getStringList(this)
        bindsList.forEach {
            newLore.add("§7$it")
        }
        im.lore = newLore
        newItem.itemMeta = im
        return newItem
    }


    @EventHandler
    fun onClick(e: InventoryClickEvent) {
        val p = e.whoClicked as Player
        if(map.contains(p)) {
            e.isCancelled = true
            val nowOpen = map[p]!!
            val item = e.currentItem
            if (item == null || item.type == Material.AIR) return
            nowOpen.click(item, p, e.click)
        }
    }


    @EventHandler
    fun onCloseRemove(e: InventoryCloseEvent) {
        val p = e.player as Player
        if(map.contains(p)) {
            val nowOpen = map[p]!!
            nowOpen.close()
            map.remove(p)
        }
    }




}
