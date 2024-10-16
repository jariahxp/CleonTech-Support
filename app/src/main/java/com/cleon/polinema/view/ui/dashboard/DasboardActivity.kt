package com.cleon.polinema.view.ui.dashboard

import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.cleon.polinema.R
import com.cleon.polinema.databinding.ActivityDasboardBinding
import com.cleon.polinema.utility.UserPreference
import com.cleon.polinema.view.ui.auth.login.LoginActivity
import com.cleon.polinema.view.ui.auth.login.LoginViewModel
import com.cleon.polinema.view.ui.document.DocumentationActivity
import com.cleon.polinema.view.ui.lokasi.AddLocationActivity
import com.cleon.polinema.view.ui.lokasi.ListLocationActivity
import com.cleon.polinema.view.ui.recap.ViewRecapActivity
import com.cleon.polinema.view.ui.sistempakar.SistemPakarActivity
import com.cleon.polinema.view.ui.tutorial.TutorialActivity
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage

class DasboardActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDasboardBinding
    private lateinit var loginViewModel: LoginViewModel
    private var selectedImageUri: Uri? = null

    private val rotateOpen: Animation by lazy { AnimationUtils.loadAnimation(this, R.anim.slide_in) }
    private val rotateClose: Animation by lazy { AnimationUtils.loadAnimation(this, R.anim.slide_out) }
    private val fromBottom: Animation by lazy { AnimationUtils.loadAnimation(this, R.anim.from_bottom_anim) }
    private val toBottom: Animation by lazy { AnimationUtils.loadAnimation(this, R.anim.to_bottom_anim) }

    private lateinit var storage: FirebaseStorage
    private lateinit var database: FirebaseDatabase
    private var jabatan: String? = ""
    private var clicked = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDasboardBinding.inflate(layoutInflater)
        setContentView(binding.root)
        loginViewModel = ViewModelProvider(this).get(LoginViewModel::class.java)

        // Inisialisasi Firebase Storage dan Realtime Database
        storage = FirebaseStorage.getInstance()
        database = FirebaseDatabase.getInstance()

        // Dapatkan username dari UserPreference
        val userPreference = UserPreference(this)
        val username = userPreference.getUsername()
        jabatan = userPreference.getJabatan()
        Log.d("DasboardActivity", "Jabatan: $jabatan")  // Menampilkan jabatan di log


        // Tampilkan nama pengguna di TextView
        binding.textName.text = username

        // Set listener untuk nama pengguna (textName) untuk logout
        binding.textName.setOnClickListener {
            showLogoutDialog() // Panggil fungsi untuk menampilkan dialog
        }

        // Cek apakah sudah ada foto profil, jika ada tampilkan dari Realtime Database
        if (username != null) {
            val userProfileRef = database.getReference("users").child(username)
            userProfileRef.child("photosprofil").get().addOnSuccessListener { dataSnapshot ->
                val photoUrl = dataSnapshot.value as? String
                if (photoUrl != null) {
                    // Tampilkan foto profil menggunakan Glide
                    Glide.with(this)
                        .load(photoUrl)
                        .into(binding.imageViewProfile)
                }
            }.addOnFailureListener {
                Toast.makeText(this, "Gagal memuat data profil", Toast.LENGTH_SHORT).show()
            }
        }

        // Set listener untuk gambar profil
        binding.imageViewProfile.setOnClickListener {
            ImagePicker.with(this@DasboardActivity)
                .crop()
                .compress(10024)
                .maxResultSize(1080, 1080)
                .start()
        }

        // FAB click handling
        binding.floatingActionButton.setOnClickListener {
            fabClicked()
        }
        binding.btnTutorial.setOnClickListener {
            val intent = Intent(this@DasboardActivity, TutorialActivity::class.java)
            startActivity(intent)
        }
        binding.btnPengembang.setOnClickListener {
            val intent = Intent(this@DasboardActivity, DocumentationActivity::class.java)
            startActivity(intent)
        }

        // Button List Lokasi click handling
        binding.btnListlokasi.setOnClickListener {
            val intent = Intent(this@DasboardActivity, ListLocationActivity::class.java)
            startActivity(intent)
        }

        // Button Tambah Lokasi click handling
        binding.btnTambahlokasi.setOnClickListener {
            if (jabatan=="Magang-Admin"){
                val intent = Intent(this@DasboardActivity, AddLocationActivity::class.java)
                startActivity(intent)
            }else{
                // Menampilkan dialog setelah berhasil disimpan
                androidx.appcompat.app.AlertDialog.Builder(this)
                    .setTitle("Peringatan")
                    .setMessage("Hanya Admin yang dapat mengakses fitur ini")
                    .setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
                    .show()
            }
        }

        // Button Sistem Pakar click handling
        binding.toSistemPakar.setOnClickListener {
            val intent = Intent(this@DasboardActivity, SistemPakarActivity::class.java)
            startActivity(intent)
        }

        // Button Recap click handling
        binding.toRecap.setOnClickListener {
            if (jabatan=="Magang-Admin"){
                val intent = Intent(this@DasboardActivity, ViewRecapActivity::class.java)
                startActivity(intent)
            }else{
                // Menampilkan dialog setelah berhasil disimpan
                androidx.appcompat.app.AlertDialog.Builder(this)
                    .setTitle("Peringatan")
                    .setMessage("Hanya Admin yang dapat mengakses fitur ini")
                    .setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
                    .show()
            }
        }
    }

    // Fungsi untuk menampilkan dialog konfirmasi logout
    private fun showLogoutDialog() {
        AlertDialog.Builder(this)
            .setTitle("Logout")
            .setMessage("Apakah Anda yakin ingin logout?")
            .setPositiveButton("OK") { dialog, _ ->
                logout() // Panggil fungsi logout
                dialog.dismiss()
            }
            .setNegativeButton("Batal") { dialog, _ ->
                dialog.dismiss()
            }
            .create()
            .show()
    }

    // Fungsi logout
    private fun logout() {
        loginViewModel.logout()
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    // Method untuk mengatur visibilitas FAB
    private fun fabClicked() {
        setVisibility(clicked)
        setAnimation(clicked)
        clicked = !clicked
    }

    private fun setVisibility(clicked: Boolean) {
        if (!clicked) {
            binding.apply {
                btnListlokasi.visibility = View.VISIBLE
                btnTambahlokasi.visibility = View.VISIBLE
            }
        } else {
            binding.apply {
                btnListlokasi.visibility = View.INVISIBLE
                btnTambahlokasi.visibility = View.INVISIBLE
            }
        }
    }

    private fun setAnimation(clicked: Boolean) {
        if (!clicked) {
            binding.apply {
                btnTambahlokasi.startAnimation(fromBottom)
                btnListlokasi.startAnimation(fromBottom)
                floatingActionButton.startAnimation(rotateOpen)
            }
        } else {
            binding.apply {
                btnTambahlokasi.startAnimation(toBottom)
                btnListlokasi.startAnimation(toBottom)
                floatingActionButton.startAnimation(rotateClose)
            }
        }
    }

    // Method untuk menangani hasil pemilihan gambar dari ImagePicker
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == RESULT_OK) {
            selectedImageUri = data?.data

            Toast.makeText(this, "Gambar berhasil dipilih", Toast.LENGTH_SHORT).show()

            val username = UserPreference(this).getUsername() // Dapatkan username

            if (selectedImageUri != null) {
                val storageRef = storage.reference.child("profile_photos/$username.jpg") // Path di Storage
                storageRef.putFile(selectedImageUri!!)
                    .addOnSuccessListener {
                        // Setelah berhasil diupload, dapatkan URL foto dari Storage
                        storageRef.downloadUrl.addOnSuccessListener { uri ->
                            val photoUrl = uri.toString()

                            // Simpan URL foto ke Realtime Database
                            if (username != null) {
                                val userProfileRef = database.getReference("users").child(username)
                                userProfileRef.child("photosprofil").setValue(photoUrl)
                                    .addOnSuccessListener {
                                        Toast.makeText(this, "Gambar berhasil diunggah ke Profile", Toast.LENGTH_SHORT).show()

                                        // Tampilkan gambar di ImageView jika berhasil
                                        Glide.with(this)
                                            .load(photoUrl)
                                            .into(binding.imageViewProfile)
                                    }
                                    .addOnFailureListener { e ->
                                        Toast.makeText(this, "Gagal menyimpan gambar ke Profile", Toast.LENGTH_SHORT).show()
                                    }
                            }
                        }
                    }
                    .addOnFailureListener {
                        Toast.makeText(this, "Gagal mengunggah gambar ke Profile", Toast.LENGTH_SHORT).show()
                    }
            }
        } else if (resultCode == ImagePicker.RESULT_ERROR) {
            Toast.makeText(this, ImagePicker.getError(data), Toast.LENGTH_SHORT).show()
        }
    }
}
