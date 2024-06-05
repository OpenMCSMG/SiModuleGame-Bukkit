package cn.nostmc.pixgame.cyanlib.launcher;


import cn.nostmc.pixgame.VerifyKt;
import cn.nostmc.pixgame.commands.*;
import cn.nostmc.pixgame.commands.fixed.DamageCommand;
import cn.nostmc.pixgame.commands.fixed.DoubleCommand;
import cn.nostmc.pixgame.commands.fixed.KnockBackCommand;
import cn.nostmc.pixgame.commands.fixed.LotteryCommand;
import cn.nostmc.pixgame.cyanlib.loader.KotlinBootstrap;
import com.oracle.truffle.js.scriptengine.GraalJSEngineFactory;
import com.oracle.truffle.js.scriptengine.GraalJSScriptEngine;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.FileOutputStream;
import java.lang.reflect.Field;
import java.util.Properties;

/**
 * 嵌套框架
 */

public class CyanPluginLauncher extends JavaPlugin {

    public static CyanPluginLauncher cyanPlugin;

    public GraalJSScriptEngine engine;
    public File lottery;
    public YamlConfiguration lotteryConfig;
    public File binds;
    public YamlConfiguration bindsConfig;
    public File whitelist;
    public YamlConfiguration whitelistConfig;

    public CyanPluginLauncher() {
        System.setProperty("polyglot.js.nashorn-compat", "true");
        Thread.currentThread().setContextClassLoader(org.graalvm.polyglot.Context.class.getClassLoader());
        cyanPlugin = this;
        KotlinBootstrap.init();
        KotlinBootstrap.loadDepend("org.java-websocket", "Java-WebSocket", "1.5.3");
        KotlinBootstrap.loadDepend("com.alibaba.fastjson2", "fastjson2-kotlin", "2.0.47");
        KotlinBootstrap.loadDepend("com.alibaba.fastjson2", "fastjson2", "2.0.47");
        KotlinBootstrap.loadDepend("org.slf4j", "slf4j-api", "1.7.30");
        engine = new GraalJSEngineFactory().getScriptEngine();
    }

    public String getSeparator() {
        if (System.getProperty("os.name").toLowerCase().contains("windows")) {
            return "\\\\";
        } else {
            return "/";
        }
    }

    @Override
    public void onLoad() {
        // 获取当前运行的目录的文件夹名字 就是比如/home/xxx跑的服务端我要获取这个xxx的文件夹名字
        File file = new File("");
        String[] sp = file.getAbsolutePath().split(getSeparator());
        String name = sp[sp.length - 1];
        String[] what = name.split("_");
        if (what.length > 1) {
            String usePort = what[1].trim();
            int port = 0;
            try {
                port = Integer.parseInt(usePort);
            } catch (Exception e) {
                getServer().getConsoleSender().sendMessage("§c非用户制服务端无法自动更改端口！识别到字符" + usePort);
                return;
            }
            getServer().getConsoleSender().sendMessage("§a检测客户端口为" + port);
            File serverProperties = new File("server.properties");
            if (serverProperties.exists()) {
                Properties properties = new Properties();
                try {
                    properties.load(serverProperties.toURI().toURL().openStream());
                    String now = properties.getProperty("server-port").trim();
                    if (now.equals(usePort)) {
                        getServer().getConsoleSender().sendMessage("§a端口已经是" + what[1] + "了跳过！");
                    } else {
                        properties.setProperty("server-port", what[1]);
                        // properties 保存更改的端口
                        FileOutputStream fos = new FileOutputStream(serverProperties);
                        properties.store(fos, null);
                        fos.close();
                        getServer().getConsoleSender().sendMessage("§a端口已经更改为" + what[1]);
                        Bukkit.shutdown();
                    }
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }


    @Override
    public void onEnable() {
        loadConfig();
        // 校验
        VerifyKt.updatePlugin();
        regCommand();
    }

    private void loadConfig() {
        saveDefaultConfig();
        lottery = new File(getDataFolder(), "lottery.yml");
        binds = new File(getDataFolder(), "binds.yml");
        whitelist = new File(getDataFolder(), "whitelist.yml");
        if (!lottery.exists()) saveResource("lottery.yml", false);
        if (!binds.exists()) saveResource("binds.yml", false);
        if (!whitelist.exists()) saveResource("whitelist.yml", false);
        bindsConfig = YamlConfiguration.loadConfiguration(binds);
        lotteryConfig = YamlConfiguration.loadConfiguration(lottery);
        whitelistConfig = YamlConfiguration.loadConfiguration(whitelist);

    }

    public void registerCommand(Command cmd) {
        Class<?> clazz = getServer().getPluginManager().getClass();
        try {
            Field field = clazz.getDeclaredField("commandMap");
            field.setAccessible(true);
            SimpleCommandMap commandMap = (SimpleCommandMap) field.get(getServer().getPluginManager());
            commandMap.register(cyanPlugin.getName(), cmd);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public void regCommand() {
        registerCommand(BReload.INSTANCE);
        registerCommand(DamageCommand.INSTANCE);
        registerCommand(DoubleCommand.INSTANCE);
        registerCommand(KnockBackCommand.INSTANCE);
        registerCommand(LotteryCommand.INSTANCE);
        registerCommand(RunJsFunction.INSTANCE);
        registerCommand(GroupCommand.INSTANCE);
        registerCommand(SystemCommand.INSTANCE);
        registerCommand(HelpCommand.INSTANCE);
        registerCommand(ManualInteractionCommand.INSTANCE);
    }


}