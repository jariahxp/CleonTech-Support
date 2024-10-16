package com.cleon.polinema.repository

import androidx.lifecycle.MutableLiveData
import com.cleon.polinema.network.dataclass.DocumentData
import com.google.firebase.database.*
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class DocumentRepository {
    private val databaseReference: DatabaseReference = FirebaseDatabase.getInstance().getReference("files")

    fun fetchDocuments(): MutableLiveData<List<DocumentData>> {
        val documentLiveData = MutableLiveData<List<DocumentData>>()
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val documents = mutableListOf<DocumentData>()
                for (documentSnapshot in snapshot.children) {
                    val document = documentSnapshot.getValue(DocumentData::class.java)
                    document?.let { documents.add(it) }
                }
                documentLiveData.value = documents
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle errors here
            }
        })
        return documentLiveData
    }

    fun uploadDocumentToFirebase(
        fileUrl: String,
        fileName: String,
        username: String,
        judulPengembangan: String,
        keterangan: String
    ): MutableLiveData<Boolean> {
        val isSuccess = MutableLiveData<Boolean>()
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val currentDate = dateFormat.format(Date())

        val fileData = mapOf(
            "judul" to judulPengembangan,
            "username" to username,
            "fileUrl" to fileUrl,
            "keterangan" to keterangan,
            "fileName" to fileName,
            "tanggal" to currentDate
        )

        databaseReference.push().setValue(fileData)
            .addOnSuccessListener { isSuccess.value = true }
            .addOnFailureListener { isSuccess.value = false }

        return isSuccess
    }
}
