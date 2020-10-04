package xyz.ramil.filemanager.helpers

import com.google.gson.Gson
import okhttp3.internal.and
import org.json.JSONException
import org.json.JSONObject
import org.jsoup.nodes.Document
import xyz.ramil.filemanager.model.FileModel
import java.io.FileInputStream
import java.io.InputStream
import java.security.MessageDigest


object Utils {
    @Throws(JSONException::class)
    fun documentToJSONObject(document: Document?): List<FileModel>? {
        val jsonParentObject = JSONObject()
        for (table in document?.select("table")!!) {
            for (row in table.select("tr")) {
                val jsonObject = JSONObject()
                val tds = row.select("td")
                val name = if (tds.isNotEmpty() && tds.size > 1) tds[1].text() ?: "" else ""
                val icon =
                    if (tds.isNotEmpty() && tds.size > 0) tds[0].children()[0].attributes().get(
                        "src"
                    ) ?: "" else ""
                val last_modified =
                    if (tds.isNotEmpty() && tds.size > 2) tds[2].text() ?: "" else ""
                val size = if (tds.isNotEmpty() && tds.size > 3) tds[3].text() ?: "" else ""
                jsonObject.put("name", name)
                jsonObject.put("last_modified", last_modified)
                jsonObject.put("size", size)
                jsonObject.put("image", icon)
                jsonParentObject.put(name, jsonObject)
            }
        }

        return jSONObjectToList(jsonParentObject)
    }

    fun jSONObjectToList(jsonObject: JSONObject): List<FileModel>? {

        val list: MutableList<FileModel>? = mutableListOf()
        for (item in jsonObject.keys()) {
            val gson = Gson()
            val file: FileModel = gson.fromJson(jsonObject[item].toString(), FileModel::class.java)
            list?.add(file)
        }
        return list
    }

    fun fileToMD5(filePath: String?): String? {
        var inputStream: InputStream? = null
        return try {
            inputStream = FileInputStream(filePath)
            val buffer = ByteArray(1024)
            val digest = MessageDigest.getInstance("MD5")
            var numRead = 0
            while (numRead != -1) {
                numRead = inputStream.read(buffer)
                if (numRead > 0) digest.update(buffer, 0, numRead)
            }
            val md5Bytes = digest.digest()
            convertHashToString(md5Bytes)
        } catch (e: Exception) {
            null
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close()
                } catch (e: Exception) {
                }
            }
        }
    }

    private fun convertHashToString(md5Bytes: ByteArray): String {
        var returnVal = ""
        for (i in md5Bytes.indices) {
            returnVal += Integer.toString((md5Bytes[i] and 0xff) + 0x100, 16).substring(1)
        }
        return returnVal.toUpperCase()
    }
}
