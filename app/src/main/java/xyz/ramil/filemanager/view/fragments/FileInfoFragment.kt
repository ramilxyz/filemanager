package xyz.ramil.filemanager.view.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import xyz.ramil.filemanager.R
import xyz.ramil.filemanager.model.FileModel

class FileInfoFragment(fileModel: FileModel) : Fragment() {
    var recyclerView: RecyclerView? = null
    var title: TextView? = null
    var body: TextView? = null
    var menu: ImageView? = null
    var fileModel: FileModel? = null

    init {
        this.fileModel = fileModel
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_file_info, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        setHasOptionsMenu(true)
    }

    fun initView() {

        title = view?.findViewById(R.id.tvFileName)
        body = view?.findViewById(R.id.tvLastModified)
        menu = view?.findViewById(R.id.ivMenu)

        title?.text = fileModel?.name
        body?.text = fileModel?.last_modified


        recyclerView = view!!.findViewById(R.id.rvPost)
        recyclerView?.setHasFixedSize(true)
        val verticalLinearLayoutManager =
            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        recyclerView?.layoutManager = verticalLinearLayoutManager

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                activity?.supportFragmentManager?.popBackStack()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
}





