package com.cleon.polinema.network.dataclass

class Location {
    var urlGambar: String = ""
    var nama: String = ""
    var nomorTelepon: String = ""
    var linkMaps: String = ""

    // Konstruktor tanpa argumen
    constructor()

    constructor(urlGambar: String, nama: String, nomorTelepon: String, linkMaps: String) {
        this.urlGambar = urlGambar
        this.nama = nama
        this.nomorTelepon = nomorTelepon
        this.linkMaps = linkMaps
    }
}