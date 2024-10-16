package com.cleon.polinema.view.ui.sistempakar

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.cleon.polinema.databinding.ActivitySistemPakarBinding
import com.cleon.polinema.network.dataclass.Prediksi
import com.cleon.polinema.utility.UserPreference
import com.cleon.polinema.view.ui.dashboard.DasboardActivity
import com.cleon.polinema.view.ui.tutorial.TutorialActivity
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class SistemPakarActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySistemPakarBinding
    private val viewModel: SistemPakarViewModel by viewModels()

    // Menyimpan hasil prediksi dan gejala terpilih
    private var selectedGejala: List<Pair<String, String>> = emptyList() // List of Pair for code and description
    private var hasilPrediksi: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySistemPakarBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val userPreference = UserPreference(this)
        val username = userPreference.getUsername() ?: ""

        // Mengamati hasil CF
        viewModel.hasilCF.observe(this) { hasilCF ->
            if (hasilCF.isNotEmpty()) {
                val hasilText = hasilCF.entries.mapIndexed { index, entry ->
                    "${index + 1}. ${entry.key}: Tingkat kepercayaan sistem = ${entry.value * 100}%\n"
                }.joinToString(separator = "\n")
                binding.tvHasil.text = hasilText

                // Simpan hasil prediksi untuk digunakan saat tombol Simpan diklik
                hasilPrediksi = hasilText // Simpan hasil prediksi
            } else {
                binding.tvHasil.text = "Tidak ada diagnosa yang sesuai."
            }
        }

        binding.clearcb.setOnClickListener {
            val cb = listOf(
                binding.cbG1, binding.cbG2, binding.cbG3, binding.cbG4, binding.cbG5,
                binding.cbG6, binding.cbG7, binding.cbG8, binding.cbG9, binding.cbG10,
                binding.cbG11, binding.cbG12, binding.cbG13, binding.cbG14, binding.cbG15
            )
            cb.forEach { it.isChecked = false }
        }
        binding.btnHistory.setOnClickListener {
            val intent = Intent(this@SistemPakarActivity, HistoryPrediksiActivity::class.java)
            startActivity(intent)
        }
        binding.btnSimpan.setOnClickListener {
            if (selectedGejala.isNotEmpty() && hasilPrediksi.isNotEmpty()) {
                val tanggal = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())
                val gejalaDeskripsi = selectedGejala.map { "$it" }

                val prediksi = Prediksi(
                    username = username,
                    tanggal = tanggal,
                    gejala = gejalaDeskripsi, // Menggunakan deskripsi gejala
                    hasil = hasilPrediksi
                )

                // Menyimpan prediksi ke Realtime Database
                viewModel.simpanPrediksi(prediksi,
                    onSuccess = {
                        // Menampilkan dialog setelah berhasil disimpan
                        AlertDialog.Builder(this)
                            .setTitle("Berhasil")
                            .setMessage("Prediksi berhasil disimpan.")
                            .setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
                            .show()
                        binding.btnSimpan.visibility = View.GONE
                        binding.tvHasil.visibility = View.GONE

                    },
                    onFailure = { exception ->
                        AlertDialog.Builder(this)
                            .setTitle("Gagal")
                            .setMessage("Gagal menyimpan prediksi: ${exception.message}")
                            .setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
                            .show()
                    }
                )
            } else {
                AlertDialog.Builder(this)
                    .setTitle("Peringatan")
                    .setMessage("Anda harus melakukan prediksi terlebih dahulu sebelum menyimpan.")
                    .setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
                    .show()
            }
        }

        binding.btnPrediksi.setOnClickListener {
            selectedGejala = mutableListOf() // Reset gejala terpilih setiap kali tombol prediksi diklik
            val checkboxes = listOf(
                binding.cbG1, binding.cbG2, binding.cbG3, binding.cbG4, binding.cbG5,
                binding.cbG6, binding.cbG7, binding.cbG8, binding.cbG9, binding.cbG10,
                binding.cbG11, binding.cbG12, binding.cbG13, binding.cbG14, binding.cbG15
            )

            checkboxes.forEachIndexed { index, checkBox ->
                if (checkBox.isChecked) {
                    // Menambahkan pasangan kode dan deskripsi gejala yang terpilih
                    selectedGejala = selectedGejala + Pair("G${index + 1}", getGejalaDeskripsi("G${index + 1}")) // getGejalaDeskripsi adalah fungsi yang mengembalikan deskripsi gejala berdasarkan kode
                }
            }

            when {
                selectedGejala.size > 3 -> {
                    AlertDialog.Builder(this)
                        .setTitle("Peringatan")
                        .setMessage("Anda hanya dapat memilih maksimal 3 gejala.")
                        .setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
                        .show()
                }
                selectedGejala.isEmpty() -> {
                    AlertDialog.Builder(this)
                        .setTitle("Peringatan")
                        .setMessage("Anda harus memilih minimal 1 gejala")
                        .setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
                        .show()
                }
                else -> {
                    binding.tvHasil.visibility = View.VISIBLE

                    viewModel.hitungCF(selectedGejala.map { it.first }) // Menghitung CF menggunakan hanya kode gejala
                    binding.btnSimpan.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun getGejalaDeskripsi(kode: String): String {
        return when (kode) {
            "G1" -> "Koin tidak terdeteksi"
            "G2" -> "LED tidak menyala"
            "G3" -> "LED merah tidak menyala"
            "G4" -> "LED hijau tidak menyala saat klik insert"
            "G5" -> "Koin acceptor macet"
            "G6" -> "Koin susah kedeteksi"
            "G7" -> "Koin terdeteksi lebih dari 1"
            "G8" -> "NodeMCU gagal login Mikrotik"
            "G9" -> "Mikrotik tidak terhubung internet"
            "G10" -> "Landingpage tidak muncul"
            "G11" -> "Juanfi setup tidak muncul"
            "G12" -> "Koin selain 1000 bisa terdeteksi"
            "G13" -> "Sudah ganti NodeMCU namun sistem tetap trouble"
            "G14" -> "NodeMCU, LED, dan coinacceptor sering bermasalah"
            "G15" -> "Juanfi setup muncul tapi tidak bisa diakses"
            else -> "Deskripsi tidak tersedia"
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        startActivity(Intent(this, DasboardActivity::class.java))
        finish()
    }
}
