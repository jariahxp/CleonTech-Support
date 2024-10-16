package com.cleon.polinema.view.ui.auth.login

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.cleon.polinema.MainActivity
import com.cleon.polinema.R
import com.cleon.polinema.databinding.ActivityLoginBinding
import com.cleon.polinema.view.ui.auth.register.RegisterActivity
import com.cleon.polinema.view.ui.dashboard.DasboardActivity

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var loginViewModel: LoginViewModel
    private lateinit var loadingDialog: AlertDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        loginViewModel = ViewModelProvider(this).get(LoginViewModel::class.java)
        setupLoadingDialog()

        binding.btnLogin.setOnClickListener {
            val email = binding.etEmail.text.toString()
            val password = binding.etPassword.text.toString()
            if (email.isEmpty()) {
                binding.etEmail.error = "Email tidak boleh kosong"
                return@setOnClickListener
            }
            if (password.isEmpty()) {
                binding.etPassword.error = "Password tidak boleh kosong"
                return@setOnClickListener
            }
            loadingDialog.show()
            loginViewModel.login(email, password)
        }
        loginViewModel.loginResult.observe(this) { result ->
            val (isSuccess, message) = result
            loadingDialog.dismiss()

            if (isSuccess) {
                Toast.makeText(this, "Login successful", Toast.LENGTH_SHORT).show()
                // Dalam Activity A
                val intent = Intent(this, DasboardActivity::class.java)
                startActivity(intent)

            } else {
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
            }
        }
        loginViewModel.loginResult.observe(this) { result ->
            val (isSuccess, message) = result
            if (isSuccess) {
                Toast.makeText(this, "Login successful", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, DasboardActivity::class.java)
                startActivity(intent)
            } else {
                if (message != null) {
                    showLoginFailedDialog("Ada kesalahan pada email dan password anda, silahkan cek lagi!")
                }
            }
        }
        binding.tvToRegister.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }
    private fun showLoginFailedDialog(message: String) {
        val alertDialog = AlertDialog.Builder(this)
            .setTitle("Login gagal")
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
        builder.setCancelable(false)
        loadingDialog = builder.create()
    }
}