package com.cleon.polinema.view.ui.sistempakar


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.cleon.polinema.network.dataclass.Prediksi
import com.cleon.polinema.repository.SistemPakarRepository
import com.google.firebase.database.FirebaseDatabase

class SistemPakarViewModel : ViewModel() {

    private val repository = SistemPakarRepository()
    private val database = FirebaseDatabase.getInstance().reference

    private val _hasilCF = MutableLiveData<Map<String, Double>>()
    val hasilCF: LiveData<Map<String, Double>> get() = _hasilCF

    fun hitungCF(selectedGejala: List<String>) {
        _hasilCF.value = repository.hitungCertaintyFactor(selectedGejala)
    }
    fun simpanPrediksi(prediksi: Prediksi) {
        val prediksiId = database.child("prediksi").push().key // Membuat ID unik
        prediksiId?.let {
            database.child("prediksi").child(it).setValue(prediksi)
                .addOnSuccessListener {
                    // Berhasil disimpan
                }
                .addOnFailureListener { exception ->
                    // Tangani kesalahan penyimpanan
                }
        }
    }
    fun simpanPrediksi(prediksi: Prediksi, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        val databaseReference = FirebaseDatabase.getInstance().getReference("prediksi")
        databaseReference.push().setValue(prediksi)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onFailure(it) }
    }

}
