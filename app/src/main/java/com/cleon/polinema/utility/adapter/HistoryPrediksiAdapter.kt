package com.cleon.polinema.utility.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.cleon.polinema.R
import com.cleon.polinema.network.dataclass.HistoryPrediksi
class HistoryPrediksiAdapter(
    private val historyList: List<HistoryPrediksi>,
    private val onItemClick: (HistoryPrediksi) -> Unit
) : RecyclerView.Adapter<HistoryPrediksiAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvTanggal: TextView = itemView.findViewById(R.id.tvTanggal)
        val tvUsername: TextView = itemView.findViewById(R.id.tvUsername)
        val tvGejala: TextView = itemView.findViewById(R.id.tvGejala)

        init {
            itemView.setOnClickListener {
                onItemClick(historyList[adapterPosition])
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_prediksi, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val history = historyList[position]
        holder.tvTanggal.text = history.tanggal
        holder.tvUsername.text = history.username
        holder.tvGejala.text = history.gejala.joinToString(", ")
    }

    override fun getItemCount(): Int = historyList.size
}
