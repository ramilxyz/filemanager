package xyz.ramil.filemanager.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import xyz.ramil.filemanager.data.Event
import xyz.ramil.filemanager.data.network.Api
import xyz.ramil.filemanager.data.network.NetworkService

abstract class BaseViewModel : ViewModel() {

    var api: Api = NetworkService.retrofitService()

    fun <T> requestWithLiveData(
        liveData: MutableLiveData<Event<T>>,
        request: suspend () -> Any
    ) {
        liveData.postValue(Event.loading())

        this.viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = request.invoke()

                liveData.postValue(Event.success(response))

            } catch (e: Exception) {
                e.printStackTrace()
                liveData.postValue(Event.error(null))
            }
        }
    }
}