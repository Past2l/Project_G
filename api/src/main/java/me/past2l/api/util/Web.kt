package me.past2l.api.util

import java.io.*
import java.net.*

class Web {
    companion object {
        fun parse(url: String): String? {
            return try {
                val connection = URL(url).openConnection() as HttpURLConnection
                if (connection.responseCode == 200) {
                    val lines = ArrayList<String>()
                    val reader = BufferedReader(InputStreamReader(connection.inputStream))
                    reader.lines().forEach { e: String -> lines.add(e) }
                    java.lang.String.join(" ", lines)
                } else {
                    print("[ Error ] ${connection.responseCode} : $url")
                    null
                }
            } catch (e: IOException) {
                e.printStackTrace()
                null
            }
        }
    }
}