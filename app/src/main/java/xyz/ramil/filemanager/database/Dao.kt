package xyz.ramil.filemanager.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import xyz.ramil.filemanager.model.FileModel

@Dao
interface DAOAccess {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun InsertData(fileModel: FileModel)

    @Query("SELECT * FROM FileModel")
    fun getData(): LiveData<List<FileModel>>

    @Query("SELECT * FROM FileModel WHERE  :name=name")
    fun getPost(name: String): FileModel?
}