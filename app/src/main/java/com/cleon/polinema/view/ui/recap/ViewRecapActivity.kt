package com.cleon.polinema.view.ui.recap

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cleon.polinema.R
import com.cleon.polinema.databinding.ActivityViewRecapBinding
import com.cleon.polinema.utility.adapter.ViewRecapAdapter
import com.cleon.polinema.view.ui.dashboard.DasboardActivity
import com.cleon.polinema.view.ui.sistempakar.SistemPakarActivity
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ViewRecapActivity : AppCompatActivity() {

    private lateinit var binding: ActivityViewRecapBinding
    private lateinit var database: DatabaseReference
    private lateinit var recapDateAdapter: ViewRecapAdapter
    private lateinit var recapRecyclerView: RecyclerView
    private val recapDateList = mutableListOf<String>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityViewRecapBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.apply {
            floatingActionButton.setOnClickListener {
                val intent = Intent(this@ViewRecapActivity, AddRecapActivity::class.java)
                startActivity(intent)
            }
        }
        recapRecyclerView = findViewById(R.id.recyclerView)
        recapRecyclerView.layoutManager = LinearLayoutManager(this)
        database = FirebaseDatabase.getInstance().getReference("recap")

        recapDateAdapter = ViewRecapAdapter(this, recapDateList)
        recapRecyclerView.adapter = recapDateAdapter

        // Ambil data recap dari Firebase
        fetchRecapDates()
    }

    private fun fetchRecapDates() {
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                recapDateList.clear() // Kosongkan list sebelum menambahkan data baru
                for (dataSnapshot in snapshot.children) {
                    val key = dataSnapshot.key // Ambil key yang berisi tanggal recap
                    if (key != null) {
                        recapDateList.add(key) // Tambahkan tanggal ke list
                    }
                }
                recapDateAdapter.notifyDataSetChanged() // Beritahu adapter bahwa data berubah
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@ViewRecapActivity, "Gagal mengambil data", Toast.LENGTH_SHORT).show()
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