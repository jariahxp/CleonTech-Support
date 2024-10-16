package com.cleon.polinema.utility.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cleon.polinema.R
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ViewRecapAdapter(
    private val context: Context,
    private val recapDates: List<String> // Daftar tanggal recap
) : RecyclerView.Adapter<ViewRecapAdapter.RecapDateViewHolder>() {

    private val database: DatabaseReference = FirebaseDatabase.getInstance().getReference("recap") // Ubah sesuai dengan path di Firebase Anda

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecapDateViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_view_recap, parent, false)
        return RecapDateViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecapDateViewHolder, position: Int) {
        val date = recapDates[position]
        holder.recapDateTextView.text = date

        // Set LayoutManager untuk RecyclerView
        holder.recyclerViewSales.layoutManager = LinearLayoutManager(context)

        // Ambil data sales dari Firebase untuk setiap tanggal
        val salesList = mutableListOf<Pair<String, Int>>() // Buat list untuk menyimpan key dan sales

        // Ambil data dari Firebase untuk tanggal ini
        database.child(date).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (childSnapshot in snapshot.children) {
                        // Ambil kunci dari childSnapshot
                        val key = childSnapshot.key ?: "Unknown"
                        val sales = childSnapshot.child("sales").getValue(Int::class.java) ?: 0
                        salesList.add(Pair(key, sales)) // Tambahkan key dan sales ke list
                    }
                    // Update adapter untuk RecyclerView sales
                    val salesAdapter = SalesAdapter(context, salesList)
                    holder.recyclerViewSales.adapter = salesAdapter
                } else {
                    Toast.makeText(context, "Data tidak ditemukan untuk tanggal $date", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context, "Gagal mengambil data sales", Toast.LENGTH_SHORT).show()
            }
        })

        // Set listener untuk detail view
        holder.detailTextView.setOnClickListener {
            // Toggle visibilitas RecyclerView dan batas serta teks di bawahnya
            if (holder.recyclerViewSales.visibility == View.GONE) {
                holder.recyclerViewSales.visibility = View.VISIBLE // Ubah menjadi VISIBLE
                holder.batas1.visibility = View.VISIBLE // Tampilkan batas1
                holder.tvlokasi.visibility = View.VISIBLE // Tampilkan tvlokasi
                holder.tvsales.visibility = View.VISIBLE // Tampilkan tvsales
                holder.batas2.visibility = View.VISIBLE // Tampilkan batas2
            } else {
                holder.recyclerViewSales.visibility = View.GONE // Ubah menjadi GONE
                holder.batas1.visibility = View.GONE // Sembunyikan batas1
                holder.tvlokasi.visibility = View.GONE // Sembunyikan tvlokasi
                holder.tvsales.visibility = View.GONE // Sembunyikan tvsales
                holder.batas2.visibility = View.GONE // Sembunyikan batas2
            }
        }

    }


    override fun getItemCount(): Int {
        return recapDates.size
    }

    inner class RecapDateViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val recapDateTextView: TextView = itemView.findViewById(R.id.tv_viewrecap)
        val recyclerViewSales: RecyclerView = itemView.findViewById(R.id.recyclerViewSales)
        val detailTextView: TextView = itemView.findViewById(R.id.detail) // Ambil reference ke detail TextView

        // Tambahkan referensi untuk elemen yang ingin diubah visibilitasnya
        val batas1: View = itemView.findViewById(R.id.batas1)
        val tvlokasi: TextView = itemView.findViewById(R.id.tvlokasi)
        val tvsales: TextView = itemView.findViewById(R.id.tvsales)
        val batas2: View = itemView.findViewById(R.id.batas2)
    }
}
