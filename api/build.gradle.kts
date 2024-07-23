

val group = "cn.nostmc.pixgame"
version = "24.7.4"



plugins {
    kotlin("jvm") version "1.9.20"
    //上传到官方仓子
    `maven-publish`
}
repositories {
    //aliyun
    maven("https://maven.aliyun.com/repository/public")
    maven("https://nexus.cyanbukkit.cn/repository/maven-public")
    maven("https://maven.elmakers.com/repository")
}


dependencies {
    compileOnly(kotlin("stdlib"))
    compileOnly("org.spigotmc:spigot-api:1.12.2-R0.1-SNAPSHOT")
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

}


publishing {
    publications {
        create("mavenJava", MavenPublication::class) {
            groupId = "cn.nostmc.pixgame"
            artifactId = "SiModuleGame-Bukkit"
            pom {
                name.set("SiModuleGame-Bukkit")
                description.set("SiModuleGame-Bukkit")
                url.set("")
            }
        }
    }
    repositories {
        maven {
            url = uri("https://nexus.cyanbukkit.cn/repository/maven-public/")
            credentials {
                username = "smallxy"
                password = "smallxy"
            }
        }
    }
}

