package xyz.ramil.filemanager.view.activities

import android.content.BroadcastReceiver
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import xyz.ramil.filemanager.R
import xyz.ramil.filemanager.data.network.NetworkChangeReceiver

class MainActivity : AppCompatActivity() {

    private var mNetworkReceiver: BroadcastReceiver? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //TODO ТЕМНАЯ ТЕМА
        // AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        //TODO СВЕТЛАЯ ТЕМА
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        supportFragmentManager.addOnBackStackChangedListener {
            if (supportFragmentManager.backStackEntryCount > 0) {
                supportActionBar?.setDisplayHomeAsUpEnabled(true)
            } else {
                supportActionBar?.setDisplayHomeAsUpEnabled(false)
            }
        }

        mNetworkReceiver = NetworkChangeReceiver()
        (mNetworkReceiver as NetworkChangeReceiver).setConnectionListner(object :
            NetworkChangeReceiver.IConnectionListner {
            override fun isConnection(boolean: Boolean) {
                if (boolean)
                    supportActionBar?.setTitle(getString(R.string.app_name)) else supportActionBar?.setTitle(
                    R.string.offline_mode
                )
            }
        })
        registerNetworkBroadcastForNougat()


    }

    private fun registerNetworkBroadcastForNougat() {
        registerReceiver(
            mNetworkReceiver,
            IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
        )
    }

    protected fun unregisterNetworkChanges() {
        try {
            unregisterReceiver(mNetworkReceiver)
        } catch (e: IllegalArgumentException) {
            e.printStackTrace()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterNetworkChanges()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        for (item in supportFragmentManager.fragments) {
            item.onActivityResult(requestCode, resultCode, data)
        }
    }


}