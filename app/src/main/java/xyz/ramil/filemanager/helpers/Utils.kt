package xyz.ramil.filemanager.helpers

import com.google.gson.Gson
import org.json.JSONException
import org.json.JSONObject
import org.jsoup.nodes.Document
import xyz.ramil.filemanager.model.FileModel


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
}
