package com.cleon.polinema.repository
import com.cleon.polinema.network.dataclass.Gejala
import com.cleon.polinema.network.dataclass.Penyebab

class SistemPakarRepository {

    private val gejalaList = listOf(
        Gejala("G1", "Koin tidak terdeteksi", 0.8),
        Gejala("G2", "LED tidak menyala", 0.6),
        Gejala("G3", "LED merah tidak menyala", 0.7),
        Gejala("G4", "LED hijau tidak menyala saat klik insert", 0.7),
        Gejala("G5", "Koin acceptor macet", 0.6),
        Gejala("G6", "Koin susah kedeteksi", 0.8),
        Gejala("G7", "Koin terdeteksi lebih dari 1", 0.9),
        Gejala("G8", "NodeMCU gagal login Mikrotik", 0.6),
        Gejala("G9", "Mikrotik tidak terhubung internet", 0.7),
        Gejala("G10", "Landingpage tidak muncul", 0.7),
        Gejala("G11", "Juanfi setup tidak muncul", 0.8),
        Gejala("G12", "Koin selain 1000 bisa terdeteksi", 0.7),
        Gejala("G13", "Sudah ganti NodeMCU namun sistem tetap trouble", 0.8),
        Gejala("G14", "NodeMCU, LED, dan coinacceptor sering bermasalah", 0.9),
        Gejala("G15", "Juanfi setup muncul tapi tidak bisa diakses", 0.8)
    )

    private val penyebabList = listOf(
        Penyebab("P1", "Cek kabel jumper, sekiranya aman, maka kalibrasi ulang coinacceptor atau ganti coinacceptor", listOf("G1", "G5", "G6", "G7", "G12")),
        Penyebab("P2", "Cek kabel jumper LED atau ganti LED", listOf("G2", "G3", "G4")),
        Penyebab("P3", "NodeMCU belum terhubung mikrotik, pastikan konfigurasi pada juanfisetup sesuai dengan mikrotik", listOf("G3", "G8")),
        Penyebab("P4", "Cek konfigurasi mikrotik, pastikan sesuai dengan buku pedoman", listOf("G9", "G10")),
        Penyebab("P5", "Cek apakah led biru pada nodemcu menyala, apabila tidak maka ganti nodemcu, namun apabila menyala, matikan semua mikrotik kemudian juanfi setup akan muncul dan dapat di konfigurasi, setelah konfigurasi maka nyalakan semua mikrotik", listOf("G11", "G15")),
        Penyebab("P6", "Baseboard NodeMCU bermasalah, coba ganti dengan yang baru", listOf("G13", "G14"))
    )

    fun hitungCertaintyFactor(selectedGejala: List<String>): Map<String, Double> {
        val hasilCF = mutableMapOf<String, Double>()
        penyebabList.forEach { penyebab ->
            var cfCombine = 0.0
            penyebab.aturan.forEach { kodeGejala ->
                val gejala = gejalaList.find { it.kode == kodeGejala }
                if (gejala != null && selectedGejala.contains(gejala.kode)) {
                    cfCombine = if (cfCombine == 0.0) {
                        gejala.cf
                    } else {
                        cfCombine + (gejala.cf * (1 - cfCombine))
                    }
                }
            }
            if (cfCombine > 0.0) {
                hasilCF[penyebab.deskripsi] = cfCombine
            }
        }
        return hasilCF
    }
}