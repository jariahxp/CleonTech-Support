package com.cleon.polinema.view.ui.lokasi


import android.net.Uri
import androidx.lifecycle.ViewModel

import androidx.lifecycle.viewModelScope
import com.cleon.polinema.network.dataclass.Location
import com.cleon.polinema.repository.LocationRepository
import kotlinx.coroutines.launch

class LocationViewModel(private val repository: LocationRepository) : ViewModel() {

    fun uploadGambarAndSaveLokasi(nama: String, nomorTelepon: String, linkMaps: String, imageUri: Uri, callback: (Boolean, String?) -> Unit) {
        viewModelScope.launch {
            repository.uploadGambarAndSaveLokasi(nama, nomorTelepon, linkMaps, imageUri) { success, message ->
                callback(success, message)
            }
        }
    }
}

