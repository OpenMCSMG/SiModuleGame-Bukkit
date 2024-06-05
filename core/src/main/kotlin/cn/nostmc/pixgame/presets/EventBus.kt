package cn.nostmc.pixgame.presets

import org.bukkit.event.*
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerMoveEvent
import org.bukkit.event.player.PlayerQuitEvent
import java.util.function.Consumer
import javax.script.Bindings

/**
 * 监听列车
 */

fun createEmptyConsumer(f: () -> Unit): Consumer<Any> {
    return object : Consumer<Any> {
        fun accept() {
            f()
        }

        override fun accept(t: Any) {}
    }
}




typealias JSFunction = java.util.function.Function<Array<out Any?>, Any>
typealias JSObject = Bindings

annotation class EventName(val value: String)

val EVENT_TYPES = mutableSetOf<String>()

object EventBus : Listener {
    init {
        for (field in EventBus::class.java.declaredMethods) {
            if (field.isAnnotationPresent(EventName::class.java)) {
                EVENT_TYPES.add(field.getAnnotation(EventName::class.java).value)
            }
        }
    }

    var globalID = 0
    class TaskContainer {
        data class Task(val id: Int, val task: JSFunction)
        private val tasks = mutableMapOf<Int, JSFunction>()
        fun add(task: JSFunction): Int {
            val id = globalID + 1
            tasks[id] = task
            return id
        }

        fun run(vararg args: Any?) {
            tasks.forEach { (_, it) ->
                it.apply(args)
            }
        }

        fun remove(id: Int) = tasks.remove(id)

        fun clear() {
            tasks.clear()
        }
    }

    private fun cancel(e: Cancellable) = createEmptyConsumer { e.isCancelled = true }

    private val tasks = mutableMapOf<String, TaskContainer>()

    fun clear() {
        tasks.forEach { (_, task) ->
            task.clear()
        }
        tasks.clear()
    }

    fun remove(type: String, id: Int) {
        tasks[type]?.remove(id)
    }

    fun createOnJS(event: Class<out Event>, handler: JSEventHandler, ep: EventPriority?, ignoreCancelled: Boolean) {
         val eventPriority = ep ?: EventPriority.NORMAL
        MineCraft.plugin.server.pluginManager.registerEvent(event,
            EventBus, eventPriority, { _, e ->
                handler.handleEvent(e)
            }, MineCraft.plugin, ignoreCancelled)
    }

    /**
     * 注册事件
     */
    fun on(type: String, task: JSFunction): Int {
        if (!EVENT_TYPES.contains(type)) {
            throw IllegalArgumentException("Unknown event type: $type")
        } else {
            return tasks.getOrPut(type) { TaskContainer() }.add(task)
        }
    }

    /**
     * 进入事件  player.join
     * @param 1 玩家
     */
    @EventName("player.join")
    @EventHandler
    fun handle(event: PlayerJoinEvent) {
        tasks["player.join"]?.run(
            event.player
        )
    }

    /**
     * 离开事件  player.quit
     * @param 1 玩家
     */
    @EventName("player.quit")
    @EventHandler
    fun handle(event: PlayerQuitEvent) {
        tasks["player.quit"]?.run(
            event.player
        )
    }

    /**
     * 破坏方块事件  block.break
     * @param 1 玩家
     * @param 2 方块
     * @param 3 取消
     */
    @EventName("block.break")
    @EventHandler
    fun handle(e: BlockBreakEvent) {
        tasks["block.break"]?.run(
            e.player,
            e.block,
            cancel(e)
        )
    }

    /**
     * 放置方块事件  block.place
     * @param 1 玩家
     * @param 2 方块
     * @param 3 取消
     */
    @EventName("block.place")
    @EventHandler
    fun handle(e: BlockPlaceEvent) {
        tasks["block.place"]?.run(
            e.player,
            e.block,
            cancel(e)
        )
    }

    /**
     * 玩家移动事件  player.move
     * @param 1 玩家
     * @param 2 取消
     * @param 3 从
     * @param 4 到
     */
    @EventName("player.move")
    @EventHandler
    fun handle(e: PlayerMoveEvent) {
        tasks["player.move"]?.run(
            e.player,
            cancel(e),
            e.from,
            e.to
        )
    }

    /**
     * 玩家点击背包事件  inventory.click
     * @param 1 玩家
     * @param 2 当前物品
     * @param 3 点击的背包
     * @param 4 插槽
     * @param 5 取消
     */
    @EventName("inventory.click")
    @EventHandler
    fun handle(e: org.bukkit.event.inventory.InventoryClickEvent) {
        tasks["inventory.click"]?.run(
            e.whoClicked,
            e.currentItem,
            e.clickedInventory,
            e.slot,
            cancel(e)
        )
    }

    /**
     * 玩家关闭背包事件  inventory.close
     * @param 1 玩家
     * @param 2 背包
     */
    @EventName("inventory.close")
    @EventHandler
    fun handle(e: org.bukkit.event.inventory.InventoryCloseEvent) {
        tasks["inventory.close"]?.run(
            e.player,
            e.inventory,
        )
    }

    /**
     * 实体改变方块事件  entity.change.block
     * @param 1 实体
     * @param 2 方块
     * @param 3 到
     * @param 4 取消
     */
    @EventName("entity.change.block")
    @EventHandler
    fun handle(e: org.bukkit.event.entity.EntityChangeBlockEvent) {
        tasks["entity.change.block"]?.run(
            e.entity,
            e.block,
            e.to,
            cancel(e)
        )
    }

    /**
     * 实体爆炸事件  entity.explode
     * @param 1 实体
     * @param 2 实体类型
     * @param 3 方块列表
     * @param 4 取消
     */
    @EventName("entity.explode")
    @EventHandler
    fun handle(e: org.bukkit.event.entity.EntityExplodeEvent) {
        tasks["entity.explode"]?.run(
            e
        )
    }

    /**
     * 玩家蹲下事件  player.toggle.sneak
     * @param 1 玩家
     * @param 2 是否蹲下
     * @param 3 取消
     */
    @EventName("player.toggle.sneak")
    @EventHandler
    fun handle(e: org.bukkit.event.player.PlayerToggleSneakEvent) {
        tasks["player.toggle.sneak"]?.run(
            e.player,
            e.isSneaking,
            cancel(e)
        )
    }

    /**
     * 玩家交互事件  player.interact
     * @param 1 玩家
     * @param 2 动作
     * @param 3 点击的方块
     *
     */
    @EventName("player.interact")
    @EventHandler
    fun handle(e: org.bukkit.event.player.PlayerInteractEvent) {
        tasks["player.interact"]?.run(
            e.player,
            e.action,
            e.clickedBlock,
            e.item,
            cancel(e)
        )
    }

    /**
     * 实体被实体伤害事件  entity.damage.by.entity
     * @param 1 被伤害的实体
     * @param 2 伤害者
     * @param 3 伤害值
     * @param 4 取消
     */
    @EventName("entity.damage.by.entity")
    @EventHandler
    fun handle(e: org.bukkit.event.entity.EntityDamageByEntityEvent) {
        tasks["entity.damage.by.entity"]?.run(
            e.entity,
            e.damager,
            e.damage,
            cancel(e)
        )
    }

    /**
     * 方块物理事件  block.physics
     * @param 1 方块
     * @param 2 取消
     */
    @EventName("block.physics")
    @EventHandler
    fun handle(e: org.bukkit.event.block.BlockPhysicsEvent) {
        tasks["block.physics"]?.run(
            e.block,
            cancel(e)
        )
    }

    /**
     * 实体伤害事件  entity.damage
     * @param 1 实体
     * @param 2 伤害值
     * @param 3 取消
     */
    @EventName("entity.damage")
    @EventHandler
    fun handle(e: org.bukkit.event.entity.EntityDamageEvent) {
        tasks["entity.damage"]?.run(
            e.entity,
            e.damage,
            cancel(e)
        )
    }

    /**
     * 实体死亡事件  entity.death
     * @param 1 实体
     * @param 2 掉落经验
     * @param 3 掉落物品
     */
    @EventName("entity.death")
    @EventHandler
    fun handle(e: org.bukkit.event.entity.EntityDeathEvent) {
        tasks["entity.death"]?.run(
            e.entity,
            e.droppedExp,
            e.drops,
        )
    }

    /**
     * 投掷物发射事件  projectile.launch
     * @param 1 投掷物
     * @param 2 投掷物类型
     * @param 3 取消
     */
    @EventName("projectile.launch")
    @EventHandler
    fun handle(e: org.bukkit.event.entity.ProjectileLaunchEvent) {
        tasks["projectile.launch"]?.run(
            e.entity,
            e.entityType,
            cancel(e)
        )
    }

    /**
     * 玩家食物等级改变事件  food.level.change
     * @param 1 玩家
     * @param 2 食物等级
     * @param 3 取消
     */
    @EventName("food.level.change")
    @EventHandler
    fun handle(e: org.bukkit.event.entity.FoodLevelChangeEvent) {
        tasks["food.level.change"]?.run(
            e.entity,
            e.foodLevel,
            cancel(e)
        )
    }

    /**
     * 玩家钓鱼事件  player.fish
     * @param 1 玩家
     * @param 2 状态
     * @param 3 钓鱼钩
     * @param 4 取消
     */
    @EventName("player.fish")
    @EventHandler
    fun handle(e: org.bukkit.event.player.PlayerFishEvent) {
        tasks["player.fish"]?.run(
            e.player,
            e.state,
            e.hook,
            cancel(e)
        )
    }


    /**
     * 玩家飞行事件  player.fly
     * @param 1 玩家
     * @param 2 飞行状态
     * @param 3 取消
     */
    @EventName("player.fly")
    @EventHandler
    fun handle(e: org.bukkit.event.player.PlayerToggleFlightEvent) {
        tasks["player.fly"]?.run(
            e.player,
            e.isFlying,
            cancel(e),
        )
    }






}