
pluginManagement {
    repositories {
        maven("https://nexus.cyanbukkit.cn/repository/maven-public/")
        gradlePluginPortal()
        mavenCentral()
    }
}
rootProject.name = "SiModuleGame-Bukkit"
include("core")
//include("jdk17")
