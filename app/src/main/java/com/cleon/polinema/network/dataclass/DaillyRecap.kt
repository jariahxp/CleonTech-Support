package com.cleon.polinema.network.dataclass

data class DaillyRecap(
    val date: String,
    val recapItems: List<Recap>
)