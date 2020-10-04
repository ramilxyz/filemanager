package xyz.ramil.filemanager.viewmodel

import androidx.lifecycle.MutableLiveData
import xyz.ramil.filemanager.data.Event
import xyz.ramil.filemanager.model.FileModel

class MainViewModel : BaseViewModel() {

    val filesLiveData = MutableLiveData<Event<List<FileModel>>>()

    fun getFiles() {
        requestWithLiveData(filesLiveData) {
            api.getFiles()
        }
    }

}