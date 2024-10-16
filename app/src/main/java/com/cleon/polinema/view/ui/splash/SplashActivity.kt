package com.cleon.polinema.view.ui.splash

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.cleon.polinema.R

import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.view.animation.AnimationUtils
import com.cleon.polinema.MainActivity
import com.cleon.polinema.databinding.ActivitySplashBinding
import com.cleon.polinema.utility.UserPreference
import com.cleon.polinema.view.ui.auth.login.LoginActivity
import com.cleon.polinema.view.ui.dashboard.DasboardActivity

class SplashActivity : AppCompatActivity() {

    // Deklarasi binding
    private lateinit var binding: ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inisialisasi binding
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Load animasi dari bawah ke tengah
        val moveUpAnimation = AnimationUtils.loadAnimation(this, R.anim.move_up)
        binding.logoImageView.startAnimation(moveUpAnimation)

        // Handler untuk pindah ke MainActivity setelah beberapa detik
        Handler(Looper.getMainLooper()).postDelayed({
// Cek data dari UserPreference
            val userPreference = UserPreference(this)
            val username = userPreference.getUsername()
            val jabatan = userPreference.getJabatan()

            // Jika salah satu nilai null, arahkan ke LoginActivity
            if (username == null || jabatan == null) {
                startActivity(Intent(this, LoginActivity::class.java))
            } else {
                startActivity(Intent(this, DasboardActivity::class.java))
            }
            finish()
        }, 3000) // Splash screen akan tampil selama 3 detik
    }
}