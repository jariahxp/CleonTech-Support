package com.cleon.polinema.view.ui.tutorial

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.cleon.polinema.R
import com.cleon.polinema.databinding.ActivityTutorialBinding
import com.cleon.polinema.network.dataclass.VideoTutorial
import com.cleon.polinema.utility.adapter.VideoAdapter
import com.google.firebase.storage.FirebaseStorage

class TutorialActivity : AppCompatActivity() {
    private lateinit var binding: ActivityTutorialBinding
    private lateinit var videoAdapter: VideoAdapter
    private lateinit var videoList: MutableList<VideoTutorial>
    private val storage = FirebaseStorage.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inisialisasi View Binding
        binding = ActivityTutorialBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Inisialisasi videoList
        videoList = mutableListOf()

        // Setup RecyclerView
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        videoAdapter = VideoAdapter(this, videoList)
        binding.recyclerView.adapter = videoAdapter

        // Ambil daftar video dari Firebase Storage
        loadVideosFromFirebase()
    }

    private fun loadVideosFromFirebase() {
        // Akses folder 'tutorial' di Firebase Storage
        val storageRef = storage.reference.child("tutorial")

        // Ambil semua file di dalam folder 'tutorial'
        storageRef.listAll()
            .addOnSuccessListener { listResult ->
                for (fileRef in listResult.items) {
                    // Dapatkan URL untuk setiap file
                    fileRef.downloadUrl.addOnSuccessListener { uri ->
                        val videoTitle = fileRef.name // Nama file sebagai judul
                        val videoUrl = uri.toString() // URL download video

                        // Tambahkan data video ke dalam list
                        videoList.add(VideoTutorial(title = videoTitle, videoUrl = videoUrl))

                        // Perbarui RecyclerView dengan data baru
                        videoAdapter.notifyDataSetChanged()
                    }.addOnFailureListener {
                        // Jika gagal mengambil URL
                        Toast.makeText(this, "Failed to load video: ${fileRef.name}", Toast.LENGTH_SHORT).show()
                    }
                }
            }.addOnFailureListener {
                // Jika gagal mengambil daftar file
                Toast.makeText(this, "Failed to retrieve videos", Toast.LENGTH_SHORT).show()
            }
    }
}