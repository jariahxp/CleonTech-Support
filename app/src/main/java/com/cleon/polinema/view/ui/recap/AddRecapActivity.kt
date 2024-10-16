package com.cleon.polinema.view.ui.recap

import android.content.Intent
import android.os.Bundle
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.forEach
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cleon.polinema.R
import com.cleon.polinema.databinding.ActivityAddRecapBinding
import com.cleon.polinema.network.dataclass.Location
import com.cleon.polinema.network.dataclass.Recap
import com.cleon.polinema.utility.adapter.AdapterListLocation
import com.cleon.polinema.utility.adapter.RecapAdapter
import com.cleon.polinema.view.ui.lokasi.ListLocationActivity
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AddRecapActivity : AppCompatActivity() {

    private lateinit var database: DatabaseReference
    private lateinit var recapAdapter: RecapAdapter
    private lateinit var recapRecyclerView: RecyclerView
    private val recapList = mutableListOf<Recap>()
    private lateinit var binding: ActivityAddRecapBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityAddRecapBinding.inflate(layoutInflater)
        setContentView(binding.root)



        recapRecyclerView = findViewById(R.id.recyclerView)
        recapRecyclerView.layoutManager = LinearLayoutManager(this)

        // Inisialisasi Firebase Database
        database = FirebaseDatabase.getInstance().getReference("lokasi")

        // Tambahkan contoh data recap (ini hanya contoh, nanti bisa diisi data dinamis)
        recapList.add(Recap("Toko A", "on", 100))
        recapList.add(Recap("Toko B", "off", 50))

        // Setup adapter
        recapAdapter = RecapAdapter(this, recapList)
        recapRecyclerView.adapter = recapAdapter

        binding.apply {
            btnTambahkan.setOnClickListener {
                recapRecyclerView.clearFocus()

                saveRecapToFirebse()
            }
        }

        fetchRecapData()
    }


    private fun saveRecapToFirebse() {
        // Ambil data dari adapter
        val recapData = recapAdapter.getRecapData()

        val sdf = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
        val currentDate = sdf.format(Date())

        val recapMap = mutableMapOf<String, HashMap<String, Any>>()

        recapData.forEach { recap ->
            // Konversi ke HashMap
            val tokoData = hashMapOf<String, Any>(
                "status" to recap.status,
                "sales" to recap.sales
            )
            recapMap[recap.name] = tokoData
        }

        val database = FirebaseDatabase.getInstance().getReference("recap")
        database.child(currentDate).setValue(recapMap)
            .addOnSuccessListener {

                Toast.makeText(this, "Data berhasil disimpan!", Toast.LENGTH_SHORT).show()
                val intent = Intent(this@AddRecapActivity, ViewRecapActivity::class.java)
                startActivity(intent)
            }
            .addOnFailureListener {
                Toast.makeText(this, "Gagal menyimpan data", Toast.LENGTH_SHORT).show()
            }
    }


    private fun fetchRecapData() {
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                recapList.clear() // Kosongkan list sebelum menambahkan data baru
                for (dataSnapshot in snapshot.children) {
                    val key = dataSnapshot.key // Ambil key dari setiap child
                    val recap = Recap(
                        name = key ?: "", // Gunakan key sebagai nama
                        status = "off", // Jika kamu tidak perlu status atau sales, bisa kosongkan atau isi nilai default
                        sales = 0
                    )
                    recapList.add(recap) // Tambahkan ke recapList
                }
                recapAdapter.notifyDataSetChanged() // Beritahu adapter bahwa data berubah
            }

            override fun onCancelled(error: DatabaseError) {
                // Tangani error jika ada
            }
        })
    }
}