package com.cleon.polinema.utility.adapter


import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.cleon.polinema.R
import com.cleon.polinema.network.dataclass.DocumentData

class DocumentationAdapter(
    private val context: Context,
    private val documents: List<DocumentData>
) : RecyclerView.Adapter<DocumentationAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvJudul: TextView = itemView.findViewById(R.id.tv_judul)
        val tvNama: TextView = itemView.findViewById(R.id.tv_nama)
        val tvTanggal: TextView = itemView.findViewById(R.id.tv_tanggal)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_list_dokumentasi, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val document = documents[position]

        holder.tvJudul.text = document.judul
        holder.tvNama.text = document.username
        holder.tvTanggal.text = document.tanggal

        holder.itemView.setOnClickListener {
            // Show dialog with description and download option
            AlertDialog.Builder(context).apply {
                setTitle(document.judul)
                setMessage("Keterangan: ${document.keterangan}")
                setPositiveButton("Download") { _, _ ->
                    // Download the document
                    val intent = Intent(Intent.ACTION_VIEW)
                    intent.data = Uri.parse(document.fileUrl)
                    context.startActivity(intent)
                }
                setNegativeButton("Close", null)
                show()
            }
        }
    }

    override fun getItemCount(): Int = documents.size
}
