package com.cleon.polinema.view.ui.auth.register

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.cleon.polinema.repository.UserRepository

class RegisterViewModel(application: Application) : AndroidViewModel(application)  {

    private val userRepository = UserRepository(application)

    private val _registerResult = MutableLiveData<Pair<Boolean, String?>>()
    val registerResult: LiveData<Pair<Boolean, String?>> get() = _registerResult

    fun register(email: String, password: String, username: String, jabatan: String) {
        userRepository.registerUser(email, password, username, jabatan) { isSuccess, message ->
            _registerResult.value = Pair(isSuccess, message)
        }
    }
}
