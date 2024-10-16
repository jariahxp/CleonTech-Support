package com.cleon.polinema.network.dataclass

data class Prediksi(
    val username: String = "",
    val tanggal: String = "",
    val gejala: List<String> = emptyList(),
    val hasil: String = ""
)
