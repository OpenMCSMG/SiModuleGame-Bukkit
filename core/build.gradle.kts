import java.io.IOException
import java.net.URL
import javax.net.ssl.HttpsURLConnection

val group = "cn.nostmc.pixgame"
version = "24.6.8"

bukkit {
    name = "SiModuleGame"
    description = "NostMC"
    authors = listOf("NostMC")
    main = "${group}.cyanlib.launcher.CyanPluginLauncher"
    prefix = "§bNostMC§7"
    depend = listOf("PlaceholderAPI", "ProtocolLib")
    softDepend = listOf("SIInMineCraft")
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
    compileOnly("com.comphenix.protocol:ProtocolLib:5.0.0")
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
    // 以token获取token并上传版本
    val initToken = URL("https://api.cyanbukkit.cn/v1/user/token").openConnection() as HttpsURLConnection
    initToken.requestMethod = "GET"
    initToken.setRequestProperty(
        "Authorization",
        "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJjZW50cmFsIiwic3ViIjoiMyIsImV4cCI6MTcxNzQ4NTU2NCwiaWF0IjoxNzE2ODgwNzY0fQ.jiz97V_UcvshpVaY-Xvm_dBb8yuMJFmMmtGHBdIsTxp4GNl6WHz6kflFuf8SuduAdg8HTJ-qtX382-uW1TNQ6fk0pXSNl5MfawhSYJbb4CbFaFNLmHG20QDZ1z2NJ5JYCQOvvuEya1qq_nDYD95YbHST_0LdtTar88o2yYQLvDnQ5qsjFJ1T21-uzcXVyjPm3AOUWAZMJuJz_r6cC_TwSJNSoYutJO_h_oJp0T4hiOsc5r4LDwXWLf9-8qNrGJVoVpHFjWB7-iMEeTrpbxdK-G-PjJRsIGw13wRtkTwnJOy4Fj7j8YzwRY2xfhsOB6QAZuCwmB3a3KIrp7wn4ppfrA"
    )
    try {
        initToken.connect()
    } catch (e: IOException) {
        println("Failed to connect: ${e.message}")
        return
    }
    var token = initToken.inputStream.bufferedReader().readText()
    token = token.substring(token.indexOf("access_token") + 15, token.indexOf("refresh_token") - 3)
    println("token: $token")
    val s = "https://api.cyanbukkit.cn/v1/live/game/upload?name=${rootProject.name}&version=${version}"
    val url = URL(s).openConnection() as HttpsURLConnection
    url.setRequestProperty("Content-Type", "application/java-archive")
    url.setRequestProperty("Authorization", token)
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
        println("Failed to upload: ${url.responseCode}")
        throw IOException("""
            This version number of the jar has been uploaded
            please modify the version number
            Note that if the plugin has already modified its content,
            please change the version
             The version format is "year.month.issue number"
              where the issue number is the number of compilations for new additions or bug fixes plus one
               If the previous version was .1 and you have made changes it should be .2,
                following the coding work order for fixes and additions.
        """.trimIndent())
    }
}