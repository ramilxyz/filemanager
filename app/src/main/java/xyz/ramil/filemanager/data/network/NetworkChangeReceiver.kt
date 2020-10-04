package xyz.ramil.filemanager.data.network

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager

class NetworkChangeReceiver : BroadcastReceiver() {

    interface IConnectionListner {
        fun isConnection(boolean: Boolean)
    }

    private var iConnectionListner: IConnectionListner? = null

    fun setConnectionListner(iConnectionListner: IConnectionListner?) {
        this.iConnectionListner = iConnectionListner
    }

    override fun onReceive(context: Context, intent: Intent) {
        if (iConnectionListner != null)
            try {
                if (isOnline(context)) {
                    iConnectionListner!!.isConnection(true)
                } else {
                    iConnectionListner!!.isConnection(false)
                }
            } catch (e: NullPointerException) {
                e.printStackTrace()
            }
    }

    private fun isOnline(context: Context): Boolean {
        return try {
            val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val netInfo = cm.activeNetworkInfo
            netInfo != null && netInfo.isConnected
        } catch (e: NullPointerException) {
            e.printStackTrace()
            false
        }
    }
}