package com.cleon.polinema.utility.adapter

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.cleon.polinema.R
import com.cleon.polinema.network.dataclass.Location

class AdapterListLocation(
    private val context: Context,
    private val locationList: List<Location>
) : RecyclerView.Adapter<AdapterListLocation.LocationViewHolder>() {

    class LocationViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val locationImage: ImageView = itemView.findViewById(R.id.imgLokasi)
        val locationName: TextView = itemView.findViewById(R.id.tv_viewName)
        val locationPhone: TextView = itemView.findViewById(R.id.tv_viewTelepon)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LocationViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_lokasi, parent, false)
        return LocationViewHolder(view)
    }

    override fun onBindViewHolder(holder: LocationViewHolder, position: Int) {
        val location = locationList[position]
        holder.locationName.text = location.nama
        holder.locationPhone.text = location.nomorTelepon
        Glide.with(context)
            .load(location.urlGambar)
            .centerCrop()
            .into(holder.locationImage)

        // Menambahkan listener untuk mengklik item
        holder.itemView.setOnClickListener {
            showDialog(location.nama, location.nomorTelepon, location.linkMaps)
        }
    }

    override fun getItemCount(): Int {
        return locationList.size
    }

    private fun showDialog(name: String, phone: String, url:String) {
        val builder = AlertDialog.Builder(context)
        builder.setTitle("Detail Lokasi")

        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialogbutton, null)
        val btnCopyName: Button = dialogView.findViewById(R.id.btn_copy_name)
        val btnCopyPhone: Button = dialogView.findViewById(R.id.btn_copy_phone)
        val btnCopyUrlMaps: Button = dialogView.findViewById(R.id.btn_copy_urlmaps)

        builder.setView(dialogView)

        btnCopyName.setOnClickListener {
            copyToClipboard("Nama", name)
        }

        btnCopyPhone.setOnClickListener {
            copyToClipboard("Telepon", phone)
        }
        btnCopyUrlMaps.setOnClickListener {
            copyToClipboard("Link Maps", url)

        }

        builder.setNegativeButton("Tutup") { dialog, _ -> dialog.dismiss() }
        builder.create().show()
    }

    private fun copyToClipboard(label: String, text: String) {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText(label, text)
        clipboard.setPrimaryClip(clip)

        // Anda dapat menampilkan toast untuk memberi tahu pengguna bahwa teks telah disalin
        Toast.makeText(context, "$label berhasil disalin", Toast.LENGTH_SHORT).show()
    }
}
