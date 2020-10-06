package xyz.ramil.filemanager.view.fragments

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.github.kittinunf.fuel.Fuel
import com.google.android.material.textview.MaterialTextView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.jsoup.nodes.Document
import xyz.ramil.filemanager.R
import xyz.ramil.filemanager.data.Status
import xyz.ramil.filemanager.database.DataBaseManager
import xyz.ramil.filemanager.helpers.Constants.BASE_URL
import xyz.ramil.filemanager.helpers.Utils
import xyz.ramil.filemanager.model.FileModel
import xyz.ramil.filemanager.view.adapters.FileAdapter
import xyz.ramil.filemanager.view.customview.WrapContentGridLayoutManager
import xyz.ramil.filemanager.viewmodel.MainViewModel
import java.io.File


class MainFragment : Fragment(), SwipeRefreshLayout.OnRefreshListener {

    private var mainViewModel: MainViewModel? = null
    var swiper: SwipeRefreshLayout? = null
    var tvNotSavedPosts: MaterialTextView? = null
    var recyclerView: RecyclerView? = null
    var contentLayout: FrameLayout? = null
    var fileAdapter: FileAdapter? = null
    var isSave: Boolean = false

    var fileModelLocal: FileModel? = null
    var progressBarLocal: ProgressBar? = null
    var dialogVisible = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainViewModel = ViewModelProvider(activity!!).get(MainViewModel::class.java)
        observeGetPosts()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        mainViewModel?.getFiles()
    }

    fun initView() {
        swiper = view!!.findViewById(R.id.swiper)
        swiper?.setOnRefreshListener(this)
        recyclerView = view!!.findViewById(R.id.rv)
        recyclerView?.setHasFixedSize(true)
        recyclerView?.layoutManager = WrapContentGridLayoutManager(activity, 1)
        contentLayout = view!!.findViewById<FrameLayout>(R.id.rootView)
        tvNotSavedPosts = view!!.findViewById(R.id.tvNotSaveFiles)

        setupRecyclerView()
    }

    fun setupRecyclerView() {
        (recyclerView?.layoutManager as WrapContentGridLayoutManager).spanCount = 1
        fileAdapter = context?.let { FileAdapter(mutableListOf(), it, view) }
        recyclerView?.adapter = fileAdapter

        fileAdapter?.setOnItemClickListener(object : FileAdapter.OnItemClickListener {
            override fun OnItemClick(fileModel: FileModel, progressBar: ProgressBar) {
                fileModelLocal = fileModel
                progressBarLocal = progressBar

                if (!fileModel.isDownload!!) {

                    if (ContextCompat.checkSelfPermission(
                            context!!,
                            Manifest.permission.READ_CONTACTS
                        ) == PackageManager.PERMISSION_GRANTED
                    ) {


                    } else {
                        requestPermissions(
                            listOf(
                                Manifest.permission.READ_EXTERNAL_STORAGE,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE
                            ).toTypedArray(), 1
                        )
                        return
                    }
                } else md5Dialog()
            }
        })

        observerRvData()
    }

    fun observerRvData() {
        DataBaseManager.getData(context!!)?.observe(viewLifecycleOwner, Observer { data ->
            if (isSave) {
                val filterData = data.filter { it.isDownload == true }

                if (filterData.isEmpty())
                    tvNotSavedPosts?.visibility = View.VISIBLE
                else
                    tvNotSavedPosts?.visibility = View.GONE

                fileAdapter?.update(filterData, view)
            } else {
                fileAdapter?.update(data, view)
            }
        })
    }

    private fun observeGetPosts() {
        mainViewModel?.filesLiveData?.observe(activity!!, Observer {
            when (it.status) {
                Status.LOADING -> loading()
                Status.SUCCESS -> success(it.data)
                Status.ERROR -> connectionError(it.error)
            }
        })
    }

    private fun loading() {
        swiper?.isRefreshing = true
    }

    private fun success(data: Any?) {
        val document: org.jsoup.nodes.Document = data as Document

        val fileList: List<FileModel>? = Utils.documentToJSONObject(document)

        fileList?.forEach {
            if (it.last_modified!!.length > 1) {
                if (it.isDownload == null) {
                    it.isDownload = false
                }
                val post = DataBaseManager.getFile(context!!, it.name)

                if (post == null)
                    DataBaseManager.insertData(context!!, it)
                else if (!post.isDownload!!) {
                    it.isDownload = false
                    DataBaseManager.insertData(context!!, it)
                }
            }
        }
        swiper?.isRefreshing = false
    }

    private fun connectionError(error: Error?) {
        swiper?.isRefreshing = false
        if (error?.message != null)
            Toast.makeText(context, "${error.message}", Toast.LENGTH_SHORT).show() else
            Toast.makeText(context, getString(R.string.error), Toast.LENGTH_SHORT).show()
    }

    override fun onRefresh() {
        mainViewModel?.getFiles()
    }

    fun setIsSaveScreen(boolean: Boolean) {
        isSave = boolean
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            downloadFiles(fileModelLocal!!, progressBarLocal!!)
        }
    }

    fun downloadFiles(fileModel: FileModel, progressBar: ProgressBar) {
        Fuel.download(BASE_URL + "/source/snapshot/" + fileModel.name)
            .destination { response, url ->
                val dir = File(
                    Environment.getExternalStoragePublicDirectory(
                        Environment.DIRECTORY_DOWNLOADS
                    ).toString()
                )
                File(dir, fileModel.name)
            }.progress { readBytes, totalBytes ->
                val progress =
                    (readBytes.toFloat() * 100 / totalBytes.toFloat() * 100).toInt() / 100
                progressBarLocal?.progress =
                    (readBytes.toFloat() * 100 / totalBytes.toFloat() * 100).toInt() / 100
                if (progress == 100) {
                    GlobalScope.launch(Dispatchers.Main) {
                        Toast.makeText(
                            context,
                            "${fileModelLocal?.name} успешно загружен!",
                            Toast.LENGTH_SHORT
                        ).show()
                        fileModelLocal?.isDownload = true
                        fileModelLocal?.location =
                            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                                .toString() + "/" + fileModelLocal!!.name

                        DataBaseManager.insertData(context!!, fileModelLocal!!)
                        md5Dialog()
                    }
                }
            }.response { req, res, result ->

            }
    }

    fun md5Dialog() {
        val location =
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                .toString() + "/" + fileModelLocal!!.name
        if (Utils.fileToMD5(location) == null) {
            downloadFiles(fileModelLocal!!, progressBarLocal!!)
            return
        }
        if (!dialogVisible) {
            AlertDialog.Builder(context!!)
                .setTitle("MD5 файла")
                .setMessage(Utils.fileToMD5(location))
                .setPositiveButton("Ok") { _, _ -> dialogVisible = false }
                .create()
                .show()
            dialogVisible = true
        }
    }
}



