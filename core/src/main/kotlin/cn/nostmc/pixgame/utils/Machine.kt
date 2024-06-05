package cn.nostmc.pixgame.utils

import java.io.BufferedReader
import java.io.InputStreamReader
import java.security.MessageDigest

object Machine {

    fun getMainBordIdWindows() : String {
        val system = listOf("cpu get Name", "baseboard get product", "diskdrive where \"Index=0\" get model", "memorychip get Capacity").joinToString("") { getSystemInfo(it) }
        return system.md5().uppercase()
    }



    private fun getSystemInfo(name: String): String {
        val info = StringBuilder()
        val process = Runtime.getRuntime().exec("cmd /c wmic $name")
        val reader = BufferedReader(InputStreamReader(process.inputStream))
        reader.readLine()
        reader.readLines().filter { it.isNotEmpty() && !it.contains("Product") }.forEach { info.append(it) }
        process.waitFor()
        return info.toString()
    }



}

fun main() {
    println(Machine.getMainBordIdWindows())
}


fun String.md5(): String{
    val messageDigest = MessageDigest.getInstance("MD5")
    val bytes = messageDigest.digest(this.toByteArray())
    val hexString = StringBuilder()
    for (aByte in bytes) {
        val hex = Integer.toHexString(0xff and aByte.toInt())
        if (hex.length == 1) {
            hexString.append('0')
        }
        hexString.append(hex)
    }
    return hexString.toString()
}