package com.cleon.polinema.utility.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Switch
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.cleon.polinema.R
import com.cleon.polinema.network.dataclass.Recap
import kotlinx.coroutines.NonDisposableHandle.parent
class RecapAdapter(
    private val context: Context,
    private val recapList: MutableList<Recap> // Ubah menjadi MutableList
) : RecyclerView.Adapter<RecapAdapter.RecapViewHolder>() {

    inner class RecapViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val locationName: TextView = itemView.findViewById(R.id.tv_tokorecap)
        val statusSwitch: Switch = itemView.findViewById(R.id.switch_status)
        val salesTextView: TextView = itemView.findViewById(R.id.et_penggunaan)  // Ambil TextView untuk sales

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecapViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_recap_lokasi, parent, false)
        return RecapViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecapViewHolder, position: Int) {
        val recap = recapList[position]
        holder.locationName.text = recap.name
        holder.statusSwitch.isChecked = recap.status == "on"
        holder.salesTextView.text = recap.sales.toString()

        // Listener untuk mendeteksi perubahan switch atau sales
        // Set visibilitas salesTextView berdasarkan statusSwitch
        holder.salesTextView.visibility = if (holder.statusSwitch.isChecked) View.VISIBLE else View.GONE

        holder.statusSwitch.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                // Jika switch dinyalakan (on), tampilkan salesTextView
                holder.salesTextView.visibility = View.VISIBLE
            } else {
                // Jika switch dimatikan (off), sembunyikan salesTextView dan set nilai sales ke 0
                holder.salesTextView.visibility = View.GONE
                recap.sales = 0
            }
            recap.status = if (isChecked) "on" else "off"
        }
        // Listener untuk mengubah nilai sales dari EditText
        holder.salesTextView.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                val salesValue = holder.salesTextView.text.toString().toIntOrNull()
                recap.sales = salesValue ?: 0 // Update sales jika input valid, jika tidak 0
            }
        }
    }

    override fun getItemCount(): Int {
        return recapList.size
    }

    // Fungsi untuk mengambil recap list
    fun getRecapData(): List<Recap> {
        return recapList
    }
}

