package xyz.ramil.filemanager.database

import android.content.Context
import androidx.lifecycle.LiveData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import xyz.ramil.filemanager.model.FileModel

class DataBaseManager {
    companion object {

        var fileDataBase: DataBase? = null

        var data: LiveData<List<FileModel>>? = null

        var file: FileModel? = null

        fun initializeDB(context: Context): DataBase {
            return DataBase.getDataseClient(context)
        }

        fun insertData(context: Context, fileModel: FileModel) {
            fileDataBase = initializeDB(context)
            CoroutineScope(IO).launch {
                fileDataBase!!.fileDao().InsertData(fileModel)
            }
        }

        fun getData(context: Context): LiveData<List<FileModel>>? {
            fileDataBase = initializeDB(context)
            data = fileDataBase!!.fileDao().getData()
            return data
        }

        fun getFile(context: Context, name: String): FileModel? {
            fileDataBase = initializeDB(context)
            file = fileDataBase!!.fileDao().getPost(name)

            return file
        }
    }
}