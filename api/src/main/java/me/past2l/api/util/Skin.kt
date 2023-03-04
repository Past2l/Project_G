package me.past2l.api.util

import me.past2l.api.type.skin.SkinResult
import java.util.Base64

class Skin {
    companion object {
        fun getByUUID(uuid: String): SkinResult {
            val content = Web.parse("https://sessionserver.mojang.com/session/minecraft/profile/$uuid?unsigned=false")
                ?.replace("\n","")
                ?.replace(" ", "")
                ?: return SkinResult()
            val texture = Regex("(?<=value\":\")(.*?)(?=\")").find(content)?.value ?: ""
            val signature = Regex("(?<=signature\":\")(.*?)(?=\")").find(content)?.value ?: ""
            var skin = ""
            var cape = ""
            if (texture.isNotEmpty()) {
                val decode = String(Base64.getDecoder().decode(texture))
                    .replace("\n","")
                    .replace(" ", "")
                skin = Regex("(?<=\"SKIN\":.\"url\":\")(.*?)(?=\")").find(decode)?.value ?: ""
                cape = Regex("(?<=\"CAPE\":.\"url\":\")(.*?)(?=\")").find(decode)?.value ?: ""
            }
            return SkinResult(texture, signature, skin, cape)
        }

        fun getByNickname(nickname: String): SkinResult {
            val content = Web.parse("https://api.mojang.com/users/profiles/minecraft/$nickname")
                ?.replace("\n","")
                ?.replace(" ", "")
                ?: return SkinResult()
            val uuid = Regex("(?<=id\":\")(.*?)(?=\")").find(content) ?: return SkinResult()
            return getByUUID(uuid.value)
        }
    }
}