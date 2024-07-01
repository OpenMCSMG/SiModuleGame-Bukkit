import org.jetbrains.kotlin.js.translate.context.Namer.kotlin
import java.io.IOException
import java.net.URL
import javax.net.ssl.HttpsURLConnection

val group = "cn.nostmc.pixgame"
version = "24.7.1"

bukkit {
    name = "SiModuleGame"
    description = "NostMC"
    authors = listOf("NostMC")
    main = "${group}.cyanlib.launcher.CyanPluginLauncher"
    prefix = "§bNostMC§7"
    depend = listOf("PlaceholderAPI", "ProtocolLib", "TAB")
}


plugins {
    kotlin("jvm") version "1.9.20"
    id("org.jetbrains.dokka") version "1.9.20"
    id("net.minecrell.plugin-yml.bukkit") version "0.6.0"
    // shadow Jar
    id("com.github.johnrengelman.shadow") version "8.0.0"
}
repositories {
    //aliyun
    maven("https://maven.aliyun.com/repository/public")
    maven("https://nexus.cyanbukkit.cn/repository/maven-public")
    maven("https://maven.elmakers.com/repository")
}


val GRAAL_VERSION = "21.0.0.2"
val JNA_VERSION = "5.14.0"
dependencies {
    compileOnly(kotlin("stdlib"))
    compileOnly("org.java-websocket:Java-WebSocket:1.5.3")
    compileOnly("com.alibaba.fastjson2:fastjson2-kotlin:2.0.26")

    implementation("org.graalvm.sdk:graal-sdk:$GRAAL_VERSION")
    implementation("org.graalvm.js:js:$GRAAL_VERSION")
    implementation("org.graalvm.js:js-scriptengine:$GRAAL_VERSION")

    compileOnly("commons-io:commons-io:2.13.0")
    compileOnly("org.spigotmc:spigot-api:1.12.2-R0.1-SNAPSHOT")
    compileOnly("com.comphenix.protocol:ProtocolLib:5.1.0")
    compileOnly("org.apache.commons:commons-lang3:3.12.0")
    compileOnly("me.clip:placeholderapi:2.11.3") { isTransitive = false }
    compileOnly(fileTree("libs") { include("*.jar") })
}

kotlin {
    jvmToolchain(8)
}


tasks.compileJava {
    options.encoding = "UTF-8"
    // 给java定义版本为8\
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

tasks {

    compileJava {
        options.encoding = "UTF-8"
    }

    compileKotlin {
        kotlinOptions {
            freeCompilerArgs = listOf("-Xjsr305=strict")
            jvmTarget = "1.8"
        }

    }



    shadowJar {
        archiveFileName.set("SiModuleGame-Bukkit-${version}.jar")
        doLast {
            val shadowJarFile = layout.buildDirectory.file("libs/${archiveFileName.get()}").get().asFile
            uploadTo(shadowJarFile)
            // 读插件到什么版本了 就在sh里面写开服读一下并且新增一个sh
        }
    }


}



fun uploadTo(shadowJarFile: File) {
    val s = "https://api.cyanbukkit.cn/v1/live/game/upload?name=${rootProject.name}&version=${version}"
    val url = URL(s).openConnection() as HttpsURLConnection
    url.setRequestProperty("Content-Type", "application/java-archive")
    url.setRequestProperty("x-token", "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJjZW50cmFsIiwic3ViIjoiMiIsImV4cCI6MTcyMDMzMzE2NywiaWF0IjoxNzE5NzI4MzY3LCJ1c2VybmFtZSI6InNtYWxseHkiLCJnZW5kZXIiOjIsInBob25lIjoiIiwiZW1haWwiOiIifQ.gzL_bQxfzy2ZOi9N2EdDi9DzxYeFYAuC1URxSlRr_pyoOrUT3vXliMq58uguDeAYsNVVRk3ASvuImBpzYt4JrR4e4kU5-x0mxxTipUji3ZwICDeZ_mpiVa5cDILriRHE4K_5DsqrZfMRjtSGyDrxA0lMBaDH1J5xMyCCVgne_wt02X-EkcyJ0Ei9sVOKgt43YDHQ1cFfFRDev-MCuXVBLRG8tTdC9Pu7VJ_NlAIq2-k8jhqnjT3o7v0ol1FCVM8whGpY5BVc3GevcFAQBnnhyLqO1rs3NZMDPu-lckwv1UBYXc0zaK5nI4dCJ7QFZyNabyc3WEuQR_85gxyBfFB62A")
    println("start upload ")
    url.requestMethod = "PUT"
    url.doOutput = true
    try {
        url.outputStream.use { output ->
            shadowJarFile.inputStream().use { input ->
                input.copyTo(output)
            }
        }
    } catch (e: Exception) {
        println("Error during file transfer: ${e.message}")
    }
    println("uploading")
    if (url.responseCode != 200) {
        throw IOException(url.content.toString())
    } else {
        println("upload success")
    }
}