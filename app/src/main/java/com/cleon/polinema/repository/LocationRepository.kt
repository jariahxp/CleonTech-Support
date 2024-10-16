package com.cleon.polinema.repository


import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import android.net.Uri
import com.cleon.polinema.network.dataclass.Location

class LocationRepository {
    private val databaseRef = FirebaseDatabase.getInstance().getReference("lokasi")
    private val storageRef = FirebaseStorage.getInstance().getReference("uploads")

    fun uploadGambarAndSaveLokasi(nama: String, nomorTelepon: String, linkMaps: String, imageUri: Uri, callback: (Boolean, String?) -> Unit) {
        val fileRef = storageRef.child("$nama.jpg")

        // Unggah gambar ke Firebase Storage
        fileRef.putFile(imageUri)
            .addOnSuccessListener {
                // Ambil URL setelah berhasil diunggah
                fileRef.downloadUrl.addOnSuccessListener { uri ->
                    val downloadUrl = uri.toString()
                    // Simpan data ke Realtime Database
                    val lokasi = Location(downloadUrl, nama, nomorTelepon, linkMaps)
                    databaseRef.child(nama).setValue(lokasi).addOnCompleteListener { task ->
                        callback(task.isSuccessful, if (task.isSuccessful) null else task.exception?.message)
                    }
                }
            }
            .addOnFailureListener { exception ->
                callback(false, exception.message)
            }
    }
}

