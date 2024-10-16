package com.cleon.polinema.view.ui.lokasi

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cleon.polinema.R
import com.cleon.polinema.network.dataclass.Location
import com.cleon.polinema.utility.adapter.AdapterListLocation
import com.cleon.polinema.view.ui.dashboard.DasboardActivity
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ListLocationActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView // Ganti ke RecyclerView
    private lateinit var adapter: AdapterListLocation
    private val locationList: MutableList<Location> = mutableListOf()
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list_location)

        // Inisialisasi RecyclerView
        recyclerView = findViewById(R.id.recyclerView)
        adapter = AdapterListLocation(this, locationList)
        recyclerView.layoutManager = LinearLayoutManager(this) // Set layout manager
        recyclerView.adapter = adapter

        // Inisialisasi database
        database = FirebaseDatabase.getInstance().getReference("lokasi")

        // Ambil data dari Firebase
        getDataFromFirebase()
    }

    private fun getDataFromFirebase() {
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                locationList.clear() // Hapus data lama

                for (locationSnapshot in snapshot.children) {
                    val location = locationSnapshot.getValue(Location::class.java)
                    location?.let {
                        locationList.add(it) // Tambahkan data baru ke list
                    }
                }

                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@ListLocationActivity, "Gagal memuat data: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
    override fun onBackPressed() {
        super.onBackPressed()
        // Arahkan kembali ke Activity Dashboard
        val intent = Intent(this, DasboardActivity::class.java) // Ganti dengan nama Activity dashboard Anda
        startActivity(intent)
        finish() // Tutup ListLocationActivity agar tidak kembali ke activity ini saat menekan tombol kembali
    }
}
