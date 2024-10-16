package com.cleon.polinema

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import android.widget.Button
import android.widget.TextView
import com.cleon.polinema.utility.UserPreference
import com.cleon.polinema.view.ui.auth.login.LoginViewModel

class MainActivity : AppCompatActivity() {
    private lateinit var loginViewModel: LoginViewModel
    private lateinit var tvUsername: TextView
    private lateinit var tvJabatan: TextView
    private lateinit var btnLogout: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        // Inisialisasi ViewModel
        loginViewModel = ViewModelProvider(this).get(LoginViewModel::class.java)

        // Inisialisasi TextView dan Button
        tvUsername = findViewById(R.id.tvUsername)
        tvJabatan = findViewById(R.id.tvJabatan)
        btnLogout = findViewById(R.id.btnLogout)

        // Ambil data dari UserPreference
        val userPreference = UserPreference(this)
        val username = userPreference.getUsername()
        val jabatan = userPreference.getJabatan()

        // Tampilkan data di TextView
        tvUsername.text = "Username: $username"
        tvJabatan.text = "Jabatan: $jabatan"

        // Logout ketika tombol ditekan
        btnLogout.setOnClickListener {
            loginViewModel.logout()
            finish() // Menutup MainActivity dan kembali ke LoginActivity
        }

        // Mengatur padding untuk insets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}
