package me.past2l.api.util

import me.past2l.api.PluginManager
import java.io.*

class File {
    companion object {
        private val plugin = PluginManager.plugin

        fun write(path: String, data: String) {
            val file = File(plugin.dataFolder, path)
            if(!file.exists()) {
                if(!file.parentFile.exists()) file.parentFile.mkdirs()
                file.createNewFile()
            }
            FileOutputStream(file).use { fos ->
                OutputStreamWriter(fos, Charsets.UTF_8).use { osw ->
                    BufferedWriter(osw).use { bf -> bf.write(data) }
                }
            }
        }

        fun read(path: String): String? {
            val file = File(plugin.dataFolder, path)
            return if (file.exists()) file.readText(Charsets.UTF_8) else null
        }

        fun list(path: String): Array<String> {
            val res: MutableList<String> = mutableListOf()
            val file = File(plugin.dataFolder, path)
            if(!file.exists()) return res.toTypedArray()
            val list = file.listFiles() ?: return res.toTypedArray()
            for(data in list) res.add(data.name)
            return res.toTypedArray()
        }

        fun exist(path: String): Boolean {
            return File(plugin.dataFolder, path).exists()
        }

        fun remove(path: String): Boolean {
            val file = File(plugin.dataFolder, path)
            return file.delete()
        }
    }
}