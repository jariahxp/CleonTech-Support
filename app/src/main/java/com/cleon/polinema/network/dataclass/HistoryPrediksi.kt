package com.cleon.polinema.network.dataclass

data class HistoryPrediksi(
    val username: String,
    val tanggal: String,
    val gejala: List<String>,
    val hasil: String
)