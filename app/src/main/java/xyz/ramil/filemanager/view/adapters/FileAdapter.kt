package xyz.ramil.filemanager.view.adapters

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityCompat.requestPermissions
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.github.kittinunf.fuel.Fuel
import xyz.ramil.filemanager.R
import xyz.ramil.filemanager.helpers.Constants.BASE_URL
import xyz.ramil.filemanager.model.FileModel
import xyz.ramil.filemanager.view.activities.MainActivity
import java.io.File


class FileAdapter(private var data: List<FileModel>, private val context: Context, view: View?) :
    RecyclerView.Adapter<FileAdapter.ViewHolder>() {

    interface OnItemClickListener {
        fun OnItemClick(fileModel: FileModel, progressBar: ProgressBar)
    }

    private var mOnItemClickListener: OnItemClickListener? = null

    fun setOnItemClickListener(onItemClickListener: OnItemClickListener?) {
        mOnItemClickListener = onItemClickListener
    }


    fun update(data: List<FileModel>, view: View?) {
        this.data = data
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val rowItem = LayoutInflater.from(parent.context).inflate(R.layout.item_file, parent, false)
        return ViewHolder(rowItem)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {


        if(data[position].isDownload!!) {
            holder.pb.visibility = View.GONE
        }


        if (mOnItemClickListener != null) {
            holder.rlFile.setOnClickListener(View.OnClickListener {
                mOnItemClickListener?.OnItemClick(
                    data[position],
                    holder.pb
                )
                holder.pb.visibility = View.VISIBLE
            })
        }

        if (data[position].image != null && !data[position].image?.isEmpty()!!) {
            holder.image.visibility = View.VISIBLE
            val url = BASE_URL+data[position].image

            Log.d("IIIIIIIII", " "+url)
            Glide.with(context)
                .load(url)
                .apply(
                    RequestOptions.placeholderOf(R.drawable.ic_launcher_background)
                        .error(R.drawable.ic_launcher_background)
                )
                .into(holder.image)
        } else {
            holder.image.visibility = View.GONE
        }

        if (data[position].last_modified != null) {
            if (data[position].last_modified?.isEmpty()!!)
                holder.body.visibility = View.GONE else
                holder.body.visibility = View.VISIBLE
            holder.body.text = data[position].last_modified
        } else holder.body.visibility = View.GONE

        holder.title.text = data[position].name



    }



    override fun getItemCount(): Int {
        return data.size
    }

    private fun hasPermissions(context: Context?, permissions: List<String>): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null) {
            return permissions.all { permission ->
                ActivityCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED
            }
        }

        return true
    }


    class ViewHolder(view: View) : RecyclerView.ViewHolder(view), View.OnClickListener {
        val title: TextView
        val body: TextView
        val image: ImageView

        val rlFile: RelativeLayout
        val pb:ProgressBar
        override fun onClick(view: View) {}

        init {
            view.setOnClickListener(this)
            title = view.findViewById(R.id.tvFileName)
            body = view.findViewById(R.id.tvLastModified)
            image = view.findViewById(R.id.ivPicture)
            pb = view.findViewById(R.id.progress_bar)

            rlFile = view.findViewById(R.id.rlFile)
        }
    }
}