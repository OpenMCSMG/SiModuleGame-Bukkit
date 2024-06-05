//plugins {
//    kotlin("jvm") version "1.9.20"
//    id("com.github.johnrengelman.shadow") version "7.0.0"
//    id("net.minecrell.plugin-yml.bukkit") version "0.6.0"
//    application
//}
//repositories {
//    maven("https://nexus.cyanbukkit.cn/repository/maven-public")
//}
//dependencies {
//    implementation(kotlin("stdlib"))// https://mvnrepository.com/artifact/org.yaml/snakeyaml
//    implementation("org.yaml:snakeyaml:2.2")
//    implementation("org.java-websocket:Java-WebSocket:1.5.3")
//    implementation("com.alibaba.fastjson2:fastjson2-kotlin:2.0.26")// https://mvnrepository.com/artifact/com.formdev/flatlaf
//    // https://mvnrepository.com/artifact/com.formdev/flatlaf
//    //flatlaf
//    implementation("com.formdev:flatlaf:1.0")
//    implementation("javazoom:jlayer:1.0.1") // 音效开发包
//    // https://mvnrepository.com/artifact/net.java.dev.jna/jna
//    implementation("net.java.dev.jna:jna:5.13.0")
//    implementation("net.java.dev.jna:jna-platform:5.13.0")
//    implementation("uk.co.caprica:vlcj:3.12.1")
//    implementation("uk.co.caprica:vlcj-natives:4.8.1")
//    implementation("org:jaudiotagger:2.0.1")
//    implementation("com.googlecode.mp4parser:isoparser:1.1.22")
//    //
//    compileOnly("org.spigotmc:spigot-api:1.12.2-R0.1-SNAPSHOT")
//    compileOnly("commons-io:commons-io:2.13.0")
//    compileOnly("me.clip:placeholderapi:2.11.3") { isTransitive = false }
//    compileOnly("com.comphenix.protocol:ProtocolLib:4.7.0")
//    // 导入在项目目录下的libs的所有jar包不会被打包进去
//    compileOnly(fileTree("Builder/libs") { include("*.jar") })
//    // 变身模组 龙核    worldloader
//}
//
//kotlin {
//    jvmToolchain(8)
//}
//
//group = "cn.nostmc"
//version = "1.7"
//
//tasks.compileJava {
//    options.encoding = "UTF-8"
//    // 给java定义版本为8\
//}
//
//java {
//    sourceCompatibility = JavaVersion.VERSION_1_8
//    targetCompatibility = JavaVersion.VERSION_1_8
//}
//
//// java 11
//// 软件直接运行
////launch4j {
////    mainClassName.set("cn.nostmc.bukkit.gui.SoftModeMainClass")
////    icon.set("${projectDir}/src/main/resources/img/icon.svg")
////    productName.set(project.name)
////    companyName.set("Cyanbukkit Union")
////    textVersion.set(project.version.toString())
////    headerType.set("console")
////    launch4j.version.set(project.version.toString())
////    copyright.set("Cyanbukkit")
////    outfile.set("${project.name}-${project.version}.exe")
////    downloadUrl.set("https://dl.cyanbukkit.cn/d/%E7%8E%AF%E5%A2%83%E5%AE%89%E8%A3%85/Java/jdk-8/jdk-8u361-windows-x64.exe")
////}
//
//
//// jar运行
//application {
//    mainClass.set("cn.nostmc.bukkit.gui.SoftModeMainClass")
//}
//
//// Bukkit插件入口
//bukkit {
//    name = "BukkitGame"
//    main = "cn.nostmc.bukkit.BukkitGame"
//    depend = listOf("PlaceholderAPI", "ProtocolLib")
//    website = "https://www.cyanbukkit.net"
//    authors = listOf("Cyanbukkit Union")
//    prefix = "§bBukkitGame§7"
//}
//
//tasks {
//
//    compileJava {
//        options.encoding = "UTF-8"
//    }
//
//    shadowJar {
//        archiveFileName.set("${project.name}-${version}.jar")
//        relocate("org.yaml.snakeyaml", "cn.nostmc.bukkit.lib.yaml")
//        // 编译后复制文件到 根目录下的Builder/Server/plugins
//        doLast {
//            copy {
//                from("build/libs") {
//                    include("*.jar")
//                }
//                into("/Builder/Server/plugins/")
//                println("Copy success!")
//                val keyFile = File("Builder/Server/plugins/BukkitGame/key.txt")
//                if (keyFile.exists()) {
//                    println("Delete Key Fang Bian Da Bao!")
//                    keyFile.delete()
//                }
//            }
//        }
//    }
//
//
//}
//
//
//// 新建一个gradle
//
