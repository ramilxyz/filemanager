package xyz.ramil.filemanager.helpers

import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Environment
import androidx.core.content.FileProvider
import com.google.gson.Gson
import org.json.JSONException
import org.json.JSONObject
import org.jsoup.nodes.Document
import xyz.ramil.filemanager.BuildConfig
import xyz.ramil.filemanager.model.FileModel
import java.io.File


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

    fun downloadFile(context: Context, url: String?, fileName: String) {
        val savePath = context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)!!
            .path + "/OpenSSL/"
        val attachmentDownloadCompleteReceive: BroadcastReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                val action = intent.action
                if (DownloadManager.ACTION_DOWNLOAD_COMPLETE == action) {
                    val downloadId = intent.getLongExtra(
                        DownloadManager.EXTRA_DOWNLOAD_ID, 0
                    )
                    val downloadManager =
                        context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
                    val query = DownloadManager.Query()
                    query.setFilterById(downloadId)
                    val cursor = downloadManager.query(query)
                    if (cursor.moveToFirst()) {
                        val downloadStatus =
                            cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS))
                        val downloadLocalUri =
                            cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI))
                        val downloadMimeType =
                            cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_MEDIA_TYPE))
                        if (downloadStatus == DownloadManager.STATUS_SUCCESSFUL && downloadLocalUri != null) {
                            if (Uri.parse(downloadLocalUri) != null) {
                                if (ContentResolver.SCHEME_FILE == Uri.parse(downloadLocalUri).scheme) {
                                    val openIntent = Intent()
                                    intent.action = Intent.ACTION_VIEW
                                    val file = File(savePath + fileName)
                                    val contentUri: Uri
                                    contentUri = FileProvider.getUriForFile(
                                        context,
                                        BuildConfig.APPLICATION_ID,
                                        file
                                    )
                                    openIntent.setDataAndType(contentUri, "downloads/*")
                                    openIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                                }
                            }
                        }
                    }
                    cursor.close()
                }
            }
        }
    }
}
