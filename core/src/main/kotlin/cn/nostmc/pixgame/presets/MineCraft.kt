package cn.nostmc.pixgame.presets

import org.bukkit.*
import org.bukkit.command.CommandSender
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType

typealias Callback = () -> Unit
typealias CallbackA<T> = (T) -> Unit
typealias BiCallback<A, B> = (A, B) -> Unit

/**
 * Minecraft 的调用 是mc.xxx
 */
object MineCraft {

    lateinit var plugin: JavaPlugin
    fun init(plugin: JavaPlugin) {
        this.plugin = plugin
    }

    /**
     * 获取在线玩家
     * @return 在线玩家
     */
    fun online() = Bukkit.getOnlinePlayers()


    /**
     * 群发
     * @param message 消息
     */
    fun broadcast(message: String) {
        Bukkit.broadcastMessage(message)
    }


    /**
     * 获取玩家
     * @param name 玩家名字
     * @return 玩家
     */
    fun getPlayerByName(name: String) = Bukkit.getPlayer(name)

    /**
     * 获取世界
     * @param name 世界名字
     * @return 世界
     */
    fun getWorldByName(name: String) = Bukkit.getWorld(name)

    /**
     * 传送玩家
     * @param player 玩家
     * @param x x坐标
     * @param y y坐标
     * @param z z坐标
     */
    fun teleport(player: Player, x: Double, y: Double, z: Double) {
        player.teleport(Location(player.world, x, y, z))
    }

    /**
     * 指令
     * @param command 指令
     */
    fun command(command: String, executor: CommandSender) {
        Bukkit.dispatchCommand(executor, command)
    }

    /**
     * 转换为位置
     * @param world 世界名字
     * @param x x坐标
     * @param y y坐标
     * @param z z坐标
     */
    fun toLocation(world: String, x: Double, y: Double, z: Double) = Location(plugin.server.getWorld(world), x, y, z)

    /**
     * 转换为位置
     * @param world 世界名字
     * @param x x坐标
     * @param y y坐标
     * @param z z坐标
     * @param yaw 偏航角
     * @param pitch 俯仰角
     */
    fun toLocation(world: String, x: Double, y: Double, z: Double, yaw: Float, pitch: Float) = Location(plugin.server.getWorld(world), x, y, z, yaw, pitch)

    /**
     * 生成实体
     * @param world 世界名字
     * @param x x坐标
     */
    fun summon(world: String, x: Double, y: Double, z: Double, type: String) : org.bukkit.entity.Entity {
        return plugin.server.getWorld(world)!!.spawnEntity(Location(plugin.server.getWorld(world), x, y, z),
            org.bukkit.entity.EntityType.valueOf(type))
    }

    /**
     * 获取世界
     * @param name 世界名字
     */
    fun getWorld(name: String): World? = plugin.server.getWorld(name)

    /**
     * 给玩家添加效果
     * @param player 玩家
     * @param type 效果类型
     * @param duration 持续时间
     * @param amplifier 等级
     * @throws IllegalArgumentException 未知效果类型
     */
    fun effect(player: Player, type: String, duration: Int, amplifier: Int) {
        val effect = cn.nostmc.pixgame.presets.PotionEffectType.getByName(type) ?: throw IllegalArgumentException("Unknown potion effect type: $type")
        player.addPotionEffect(PotionEffect(PotionEffectType.getByName(effect.name)!!, duration * 20, amplifier))
    }

    /**
     * 播放带有音效的效果
     * @param world 世界名字
     * @param x x坐标
     * @param y y坐标
     * @param z z坐标
     * @param effect 效果
     * @param data 数据
     * @param count 数量
     */
    fun play(world: String, x: Double, y: Double, z: Double, effect: String, data: Int, count: Int) {
        plugin.server.getWorld(world)!!.playEffect(Location(plugin.server.getWorld(world), x, y, z), Effect.valueOf(effect), data, count)
    }

    fun play(loc: Location, effect: String, obj: Any) {
        loc.world!!.playEffect(loc, Effect.valueOf(effect), obj)
    }

    fun play(loc: Location, effect: String, obj: Any, count: Int) {
        loc.world!!.playEffect(loc, Effect.valueOf(effect), obj, count)
    }



    fun play(world: String, x: Double, y: Double, z: Double, effect: String, obj: Any) {
        plugin.server.getWorld(world)!!.playEffect(Location(plugin.server.getWorld(world), x, y, z), Effect.valueOf(effect), obj)
    }

    /**
     * 播放音效
     * @param world 世界名字
     * @param x x坐标
     * @param y y坐标
     * @param z z坐标
     * @param sound 音效
     * @param volume 音量
     * @param pitch 音调
     * @throws IllegalArgumentException 未知音效
     * @throws IllegalArgumentException 未知世界
     * @throws IllegalArgumentException 未知音效
     */
    fun playSound(world: String, x: Double, y: Double, z: Double, sound: String, volume: Float, pitch: Float) {
        plugin.server.getWorld(world)!!.playSound(Location(plugin.server.getWorld(world), x, y, z), Sound.valueOf(sound), volume, pitch)
    }

    /**
     * 生成粒子
     * @param world 世界名字
     * @param x x坐标
     * @param y y坐标
     * @param z z坐标
     * @param effect 粒子效果
     * @param data 数据
     * @param count 数量
     * @throws IllegalArgumentException 未知粒子效果
     * @throws IllegalArgumentException 未知世界
     */
    fun particle(world: String, x: Double, y: Double, z: Double, effect: String, data: Int, count: Int) {
        plugin.server.getWorld(world)!!.spawnParticle(Particle.valueOf(effect), x, y, z, count, 0.0, 0.0, 0.0, data)
    }

    /**
     * 获取实体类别
     * @param s 实体名字
     * @throws IllegalArgumentException 未知实体
     * @return 实体类别
     */
    fun getEntity(s: String) : EntityType {
        return EntityType.valueOf(s)
    }



}