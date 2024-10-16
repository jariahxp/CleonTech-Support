package com.cleon.polinema.repository

import android.content.Context
import com.cleon.polinema.network.dataclass.User
import com.cleon.polinema.utility.UserPreference
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.firestore.FirebaseFirestore

class UserRepository (private val context: Context){

    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()
    private val userPreference = UserPreference(context)

    fun registerUser(email: String, password: String, username: String, jabatan: String, result: (Boolean, String?) -> Unit) {
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val user = User(username, email, jabatan)
                val userId = auth.currentUser?.uid
                userId?.let {
                    firestore.collection("users").document(it).set(user).addOnCompleteListener { taskFirestore ->
                        if (taskFirestore.isSuccessful) {
                            result(true, "User registered successfully")
                        } else {
                            result(false, taskFirestore.exception?.message)
                        }
                    }
                }
            } else {
                result(false, task.exception?.message)
            }
        }
    }

    fun loginUser(email: String, password: String, result: (Boolean, String?) -> Unit) {
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                // Setelah login berhasil, ambil data dari Firestore
                val uid = auth.currentUser?.uid
                uid?.let {
                    firestore.collection("users").document(it).get()
                        .addOnSuccessListener { document ->
                            if (document.exists()) {
                                val username = document.getString("username") ?: ""
                                val jabatan = document.getString("jabatan") ?: ""

                                // Simpan data di UserPreference
                                userPreference.saveUser(username, jabatan)

                                result(true, "Login successful")
                            } else {
                                result(false, "Data not found")
                            }
                        }
                        .addOnFailureListener { e ->
                            result(false, e.message)
                        }
                }
            } else {
                val errorCode = (task.exception as FirebaseAuthException).errorCode
                result(false, errorCode) // Mengembalikan kode error
            }
        }
    }

    // Fungsi untuk logout dan hapus data dari UserPreference
    fun logoutUser() {
        auth.signOut()
        userPreference.clearUser()
    }
}
