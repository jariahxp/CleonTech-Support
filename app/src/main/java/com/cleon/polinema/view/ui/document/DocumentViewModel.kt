package com.cleon.polinema.view.ui.document

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.cleon.polinema.network.dataclass.DocumentData
import com.cleon.polinema.repository.DocumentRepository

class DocumentViewModel : ViewModel() {
    private val documentRepository = DocumentRepository()

    private val _documents = MutableLiveData<List<DocumentData>>()
    val documents: LiveData<List<DocumentData>> = _documents

    private val _uploadStatus = MutableLiveData<Boolean>()
    val uploadStatus: LiveData<Boolean> = _uploadStatus

    fun fetchDocuments() {
        documentRepository.fetchDocuments().observeForever {
            _documents.value = it
        }
    }

    fun uploadDocument(
        fileUrl: String,
        fileName: String,
        username: String,
        judulPengembangan: String,
        keterangan: String
    ) {
        documentRepository.uploadDocumentToFirebase(fileUrl, fileName, username, judulPengembangan, keterangan)
            .observeForever {
                _uploadStatus.value = it
            }
    }
}