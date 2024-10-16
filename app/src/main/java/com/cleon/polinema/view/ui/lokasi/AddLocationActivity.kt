package com.cleon.polinema.view.ui.lokasi

import android.Manifest
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import com.cleon.polinema.R
import com.cleon.polinema.databinding.ActivityAddLocationBinding
import com.cleon.polinema.repository.LocationRepository
import com.cleon.polinema.utility.LocationViewModelFactory
import com.cleon.polinema.view.ui.dashboard.DasboardActivity
import com.github.dhaval2404.imagepicker.ImagePicker
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream

class AddLocationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddLocationBinding
    private var selectedImageUri: Uri? = null
    private lateinit var viewModel: LocationViewModel
    private lateinit var loadingDialog: AlertDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddLocationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Inisialisasi Repository dan ViewModel
        val repository = LocationRepository()
        val factory = LocationViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory).get(LocationViewModel::class.java)
        setupLoadingDialog()

        binding.apply {
            imAddPhotos.setOnClickListener {
                ImagePicker.with(this@AddLocationActivity)
                    .crop()
                    .compress(10024 )
                    .maxResultSize(1080,1080)
                    .start()
            }
            btnTambahkan.setOnClickListener{
                if (selectedImageUri != null) {
                    val nama = etAddNamaPIC.text.toString()
                    val nomorTelepon = etAddTeleponPIC.text.toString()
                    val linkMaps = etAddUrlMapsLokasi.text.toString()

                    if (nama.isNotEmpty() && nomorTelepon.isNotEmpty() && linkMaps.isNotEmpty()){
                        loadingDialog.show()
                        viewModel.uploadGambarAndSaveLokasi(nama, nomorTelepon, linkMaps, selectedImageUri!!) { success, message ->
                            if (success) {
                                loadingDialog.dismiss()
                                Toast.makeText(this@AddLocationActivity, "Data berhasil disimpan", Toast.LENGTH_SHORT).show()
                                val intent = Intent(this@AddLocationActivity, ListLocationActivity::class.java)
                                startActivity(intent)
                            } else {
                                loadingDialog.dismiss()
                                Toast.makeText(this@AddLocationActivity, "Gagal menyimpan data: $message", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }else{
                        showLoginFailedDialog()
                    }
                } else {
                    showLoginFailedDialog()
                }
            }
        }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == RESULT_OK) {
            selectedImageUri = data?.data
            binding.imAddPhotos.setImageURI(selectedImageUri)
            Toast.makeText(this, "Gambar berhasil dipilih", Toast.LENGTH_SHORT).show()
        } else if (resultCode == ImagePicker.RESULT_ERROR) {
            Toast.makeText(this, ImagePicker.getError(data), Toast.LENGTH_SHORT).show()
        }
    }
    private fun showLoginFailedDialog() {
        val alertDialog = AlertDialog.Builder(this)
            .setTitle("Upload gagal")
            .setMessage("Pastikan semua data telah terisi")
            .setPositiveButton("OK") { dialog, _ ->
                dialog.dismiss()
            }
            .create()
        alertDialog.show()
    }
    private fun setupLoadingDialog() {
        val builder = AlertDialog.Builder(this)
        val inflater = this.layoutInflater
        val dialogView = inflater.inflate(R.layout.show_loading, null)
        builder.setView(dialogView)
        builder.setCancelable(false)
        loadingDialog = builder.create()
    }
}