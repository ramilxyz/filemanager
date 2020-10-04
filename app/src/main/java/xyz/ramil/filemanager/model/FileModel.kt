package xyz.ramil.filemanager.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters

@Entity
data class FileModel(
    @PrimaryKey
    var name: String,
    var last_modified: String?,
    var size: String?,
    val image:String?,
    var isDownload: Boolean?,
    var location: String?
)



