package com.cleon.polinema.utility

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.cleon.polinema.repository.LocationRepository
import com.cleon.polinema.view.ui.lokasi.LocationViewModel

class LocationViewModelFactory(private val repository: LocationRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LocationViewModel::class.java)) {
            return LocationViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
