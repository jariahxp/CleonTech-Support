package com.cleon.polinema.view.ui.auth.login

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.cleon.polinema.repository.UserRepository

class LoginViewModel(application: Application) : AndroidViewModel(application) {

    private val userRepository = UserRepository(application)

    private val _loginResult = MutableLiveData<Pair<Boolean, String?>>()
    val loginResult: LiveData<Pair<Boolean, String?>> get() = _loginResult

    // Fungsi login yang memanggil repository
    fun login(email: String, password: String) {
        userRepository.loginUser(email, password) { isSuccess, message ->
            _loginResult.value = Pair(isSuccess, message)
        }
    }

    // Fungsi logout
    fun logout() {
        userRepository.logoutUser()
    }
}
