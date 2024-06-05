package cn.nostmc.pixgame.presets

/**
 * 获取效果类型
 * @param name 中文效果名字
 * @return 效果类型
 */
enum class PotionEffectType(val text: String) {
    /**
     * 速度
     */
    SPEED("速度"),

    /**
     * 缓慢
     */
    SLOW("缓慢"),

    /**
     * 急迫
     */
    FAST_DIGGING("急迫"),

    /**
     * 挖掘疲劳
     */
    SLOW_DIGGING("挖掘疲劳"),

    /**
     * 伤害提升
     */
    INCREASE_DAMAGE("力量"),

    /**
     * 伤害减免
     */
    HEAL("瞬间治疗"),

    /**
     * 伤害
     */
    HARM("瞬间伤害"),

    /**
     * 跳跃提升
     */
    JUMP("跳跃提升"),

    /**
     * 伤害提升
     */
    CONFUSION("反胃"),

    /**
     *  长时间伤害
     */
    REGENERATION("生命恢复"),

    /**
     * 伤害减免
     */
    DAMAGE_RESISTANCE("抗性"),

    /**
     * 火焰抗性
     */
    FIRE_RESISTANCE("抗火"),

    /**
     * 水下呼吸
     */
    WATER_BREATHING("水下呼吸"),

    /**
     * 隐身
     */
    INVISIBILITY("隐身"),

    /**
     * 盲目
     */
    BLINDNESS("失明"),

    /**
     * 夜视
     */
    NIGHT_VISION("夜视"),

    /**
     * 饥饿
     */
    HUNGER("饥饿"),

    /**
     * 弱点
     */
    WEAKNESS("虚弱"),

    /**
     * 中毒
     */
    POISON("中毒"),

    /**
     * 凋零
     */
    WITHER("凋零"),

    /**
     * 健康提升
     */
    HEALTH_BOOST("生命提升"),

    /**
     * 伤害吸收
     */
    ABSORPTION("伤害吸收"),

    /**
     * 饱和
     */
    SATURATION("饱和"),

    /**
     * 发光
     */
    GLOWING("发光"),

    /**
     * 飘浮
     */
    LEVITATION("飘浮"),

    /**
     * 幸运
     */
    LUCK("幸运"),

    /**
     * 霉运
     */
    UNLUCK("霉运"),

    /**
     * 缓降
     */
    SLOW_FALLING("缓降"),

    /**
     * 伤害提升
     */
    CONDUIT_POWER("潮涌能量"),

    /**
     * 海豚的恩惠
     */
    DOLPHINS_GRACE("海豚的恩惠"),

    /**
     *  不祥之兆
     */
    BAD_OMEN("不祥之兆"),

    /**
     * 村民英雄
     */
    HERO_OF_THE_VILLAGE("村民英雄"),

    /**
     * 黑暗
     */
    DARKNESS("黑暗");
    companion object {
        fun getByName(name: String): PotionEffectType? {
            return entries.find { it.text == name }
        }
    }
}