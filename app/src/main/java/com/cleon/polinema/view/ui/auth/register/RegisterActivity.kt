package com.cleon.polinema.view.ui.auth.register

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.cleon.polinema.R
import com.cleon.polinema.databinding.ActivityRegisterBinding
import com.cleon.polinema.view.ui.auth.login.LoginActivity
import com.cleon.polinema.view.ui.lokasi.ListLocationActivity

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private lateinit var registerViewModel: RegisterViewModel
    private lateinit var loadingDialog: AlertDialog
    private var selectedPosisi: String = "" // Properti untuk menyimpan posisi yang dipilih

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        registerViewModel = ViewModelProvider(this).get(RegisterViewModel::class.java)
        setupLoadingDialog()
        val spinnerPosisi: Spinner = findViewById(R.id.sp_posisi)
        spinnerPosisi.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                selectedPosisi = parent.getItemAtPosition(position).toString()
                // Lakukan sesuatu dengan posisi yang dipilih
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // Lakukan sesuatu jika tidak ada yang dipilih
            }
        }

        binding.btnDaftar.setOnClickListener {
            val email = binding.etEmail.text.toString()
            val password = binding.etPassword.text.toString()
            val username = binding.etUsername.text.toString()


            if (username.isEmpty()) {
                binding.etUsername.error = "Username tidak boleh kosong"
                return@setOnClickListener
            }else if (username.length > 10) {
                binding.etPassword.error = "Username harus terdiri dari maksimal 10 karakter"
                return@setOnClickListener
            }
            if (email.isEmpty()) {
                binding.etEmail.error = "Email tidak boleh kosong"
                return@setOnClickListener
            }
            if (password.isEmpty()) {
                binding.etPassword.error = "Password tidak boleh kosong"
                return@setOnClickListener
            }else if (password.length < 8) {
                binding.etPassword.error = "Password harus terdiri dari minimal 8 karakter"
                return@setOnClickListener
            }
            if (selectedPosisi == "Magang-Admin" && selectedPosisi == "Magang-Teknisi") {
                Toast.makeText(this, "Silahkan pilih posisi Anda", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            loadingDialog.show()
            registerViewModel.register(email, password, username, selectedPosisi)
        }

        registerViewModel.registerResult.observe(this) { result ->
            val (isSuccess, message) = result
            loadingDialog.dismiss()
            if (isSuccess) {
                Toast.makeText(this, "Registration successful", Toast.LENGTH_SHORT).show()
                val intent = Intent(this@RegisterActivity, LoginActivity::class.java)
                startActivity(intent)
            } else {
                showLoginFailedDialog("Akun sudah terdaftar")
            }
        }
        binding.tvToLogin.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }
    private fun showLoginFailedDialog(message: String) {
        val alertDialog = AlertDialog.Builder(this)
            .setTitle("Registrasi gagal")
            .setMessage(message)
            .setPositiveButton("OK") { dialog, _ ->
                dialog.dismiss()
            }
            .create()
        alertDialog.show()
    }
    private fun setupLoadingDialog() {
        val builder = AlertDialog.Builder(this)
        val inflater = this.layoutInflater
        val dialogView = inflater.inflate(R.layout.show_loading, null) // Buat layout dialog_loading.xml
        builder.setView(dialogView)
        builder.setCancelable(false) // Dialog tidak bisa ditutup sebelum proses selesai
        loadingDialog = builder.create()
    }
}