package cn.nostmc.pixgame.view

import cn.nostmc.pixgame.cyanlib.launcher.CyanPluginLauncher
import cn.nostmc.pixgame.cyanlib.launcher.CyanPluginLauncher.cyanPlugin
import cn.nostmc.pixgame.service.GameHandler
import cn.nostmc.pixgame.service.PlayList
import cn.nostmc.pixgame.view.BindsGUIListener.switchItem
import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack
import org.bukkit.scheduler.BukkitRunnable

data class NowOpen(
    val inventory: Inventory, // 从这里获取内容调用执行
    val task: BukkitRunnable
) {

    private var page = 1
    private var am = 1

    fun close() {
        task.cancel()
    }

    private fun nextPage(p: Player) {
        p.playSound(p.location, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1f, 1f)
        page++
        pageChange()
    }

    private fun previousPage(p: Player) {
        p.playSound(p.location, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1f, 1f)
        page--
        pageChange()
    }

    private fun pageChange() {
        // 从这里获取内容调用执行
        val list = BindsGUIListener.getAllKeys()
        val slots = Array(45) { i -> i }
        slots.forEachIndexed { index, i ->
            if (list.size > index + (page - 1) * 45) {
                val keyString = list[index + (page - 1) * 45]
                val obj = cyanPlugin.bindsConfig.get(keyString)
                if (obj is List<*>) {
                    inventory.setItem(i, keyString.switchItem())
                }
            }
        }
    }

    fun openDefault(p: Player) {
        page = 1
        pageChange()
        p.openInventory(inventory)
    }

    // 不打开新的界面而是在这个界面上删除所有并替换
    fun click(item: ItemStack, p: Player, action: ClickType) {
        val mate = if (item.hasItemMeta()) item.itemMeta else return
        val name = if (mate!!.hasDisplayName()) mate.displayName else return
        if (name.contains("上一页")) {
            nextPage(p)
        } else if (name.contains("下一页")) {
            previousPage(p)
        } else if (name.contains("切换")) {
            if (action.isShiftClick) {
                if (action.isLeftClick) {
                    val newItem = inventory.getItem(49)!!.apply {
                        val im = itemMeta!!.apply {
                            this@NowOpen.am += 10
                            lore = listOf((this@NowOpen.am).toString())
                        }
                        itemMeta = im
                    }
                    inventory.setItem(49, newItem)
                } else if (action.isRightClick) {
                    val newItem = inventory.getItem(49)!!.apply {
                        val im = itemMeta!!.apply {
                            this@NowOpen.am -= 10
                            lore = listOf((this@NowOpen.am).toString())
                        }
                        itemMeta = im
                    }
                    inventory.setItem(49, newItem)
                }
            } else {
                if (action.isLeftClick) {
                    val newItem = inventory.getItem(49)!!.apply {
                        val im = itemMeta!!.apply {
                            this@NowOpen.am += 1
                            lore = listOf((this@NowOpen.am ).toString())
                        }
                        itemMeta = im
                    }
                    inventory.setItem(49, newItem)
                } else if (action.isRightClick) {
                    val newItem = inventory.getItem(49)!!.apply {
                        val im = itemMeta!!.apply {
                            this@NowOpen.am -= 1
                            lore = listOf((this@NowOpen.am).toString())
                        }
                        itemMeta = im
                    }
                    inventory.setItem(49, newItem)
                }
            }
        } else {
            val k = mate.lore!![0].replace("§7ID: ", "")
            p.playSound(p.location, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1f, 1f)
            val list =  CyanPluginLauncher.cyanPlugin.bindsConfig.getStringList(name)
            for ((index, s) in list.withIndex()) {
                list[index] = s
                    .replace("%user%", "测试")
                    .replace("%gift-name%", k)
                    .replace("%gift-num%", am.toString())
                    .replace("%like-num%",  am.toString())
                    .replace("%chat-msg%", k)
            }
            PlayList.add(GameHandler(list, 1, k))
        }
    }

}