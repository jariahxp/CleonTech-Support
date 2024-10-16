package com.cleon.polinema.view.ui.sistempakar

import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.cleon.polinema.R
import com.cleon.polinema.databinding.ActivityHistoryPrediksiBinding
import com.cleon.polinema.network.dataclass.HistoryPrediksi
import com.cleon.polinema.utility.adapter.HistoryPrediksiAdapter
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class HistoryPrediksiActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHistoryPrediksiBinding
    private lateinit var adapter: HistoryPrediksiAdapter
    private val historyList: MutableList<HistoryPrediksi> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHistoryPrediksiBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Inisialisasi RecyclerView
        adapter = HistoryPrediksiAdapter(historyList) { history ->
            showDetailDialog(history)
        }
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = adapter

        // Ambil data dari Firebase
        loadHistoryFromFirebase()
    }

    private fun loadHistoryFromFirebase() {
        val database = FirebaseDatabase.getInstance()
        val ref = database.getReference("prediksi")

        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                historyList.clear() // Kosongkan daftar sebelum menambahkan data baru
                for (dataSnapshot in snapshot.children) {
                    val username = dataSnapshot.child("username").getValue(String::class.java)
                    val tanggal = dataSnapshot.child("tanggal").getValue(String::class.java)
                    val hasil = dataSnapshot.child("hasil").getValue(String::class.java)

                    // Mengambil gejala dari node 'gejala'
                    val gejalaList = mutableListOf<String>()
                    val gejalaSnapshot = dataSnapshot.child("gejala")
                    gejalaSnapshot.children.forEach { childSnapshot ->
                        val gejala = childSnapshot.getValue(String::class.java)
                        gejala?.let { gejalaList.add(it) }
                    }

                    if (username != null && tanggal != null && hasil != null) {
                        val historyPrediksi = HistoryPrediksi(username, tanggal, gejalaList, hasil)
                        historyList.add(historyPrediksi)
                    }
                }
                adapter.notifyDataSetChanged() // Notifikasi adapter bahwa data telah berubah
            }

            override fun onCancelled(error: DatabaseError) {
                // Tangani kesalahan di sini
                Log.e("Firebase", "Error loading data: ${error.message}")
            }
        })
    }

    private fun showDetailDialog(history: HistoryPrediksi) {
        // Membuat dialog untuk menampilkan detail
        val dialogView = layoutInflater.inflate(R.layout.dialog_history_detail, null)
        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .setTitle("Detail Prediksi")
            .setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
            .create()

        dialogView.findViewById<TextView>(R.id.tvDialogUsername).text = history.username
        dialogView.findViewById<TextView>(R.id.tvDialogGejala).text = history.gejala.joinToString(", ")
        dialogView.findViewById<TextView>(R.id.tvDialogHasil).text = history.hasil

        dialog.show()
    }
}
