package com.cleon.polinema.utility.adapter


import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.cleon.polinema.R

class SalesAdapter(private val context: Context, private val salesList: List<Pair<String, Int>>) : RecyclerView.Adapter<SalesAdapter.SalesViewHolder>() {

    class SalesViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvName: TextView = itemView.findViewById(R.id.tv_name)
        val tvSales: TextView = itemView.findViewById(R.id.tv_sales)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SalesViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_view_sales, parent, false)
        return SalesViewHolder(view)
    }

    override fun onBindViewHolder(holder: SalesViewHolder, position: Int) {
        val (key, sales) = salesList[position] // Mengambil key dan sales
        holder.tvName.text = key // Menampilkan key (nama)
        holder.tvSales.text = sales.toString() // Menampilkan sales
    }

    override fun getItemCount(): Int {
        return salesList.size
    }
}

