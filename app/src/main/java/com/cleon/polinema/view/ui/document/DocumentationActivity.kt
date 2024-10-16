package com.cleon.polinema.view.ui.document

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.cleon.polinema.R
import com.cleon.polinema.databinding.ActivityDocumentationBinding
import com.cleon.polinema.network.dataclass.DocumentData
import com.cleon.polinema.utility.UserPreference
import com.cleon.polinema.utility.adapter.DocumentationAdapter
import com.google.firebase.storage.FirebaseStorage
import java.util.UUID

class DocumentationActivity : AppCompatActivity() {

    private lateinit var selectedFileUri: Uri
    private lateinit var binding: ActivityDocumentationBinding
    private lateinit var documentationAdapter: DocumentationAdapter
    private val documents = mutableListOf<DocumentData>()
    private lateinit var loadingDialog: AlertDialog
    private lateinit var username: String

    private val documentViewModel: DocumentViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDocumentationBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupLoadingDialog()

        val userPreference = UserPreference(this)
        username = userPreference.getUsername().toString() // Dapatkan username
        documentationAdapter = DocumentationAdapter(this, documents)
        binding.coba.apply {
            layoutManager = LinearLayoutManager(this@DocumentationActivity)
            adapter = documentationAdapter
        }

        // Observing ViewModel
        observeViewModel()

        // Fetching data from Firebase
        documentViewModel.fetchDocuments()

        // Pilih file
        binding.namedokumen.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "*/*"
            startActivityForResult(intent, 101)
        }

        binding.btnOpenAdd.setOnClickListener {
            toggleAddFormVisibility()
        }

        binding.btnTambah.setOnClickListener {
            if (::selectedFileUri.isInitialized) {
                uploadFileToFirebase(selectedFileUri)
            } else {
                Toast.makeText(this, "Pilih file terlebih dahulu", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun observeViewModel() {
        documentViewModel.documents.observe(this) {
            documents.clear()
            documents.addAll(it)
            documentationAdapter.notifyDataSetChanged()
        }

        documentViewModel.uploadStatus.observe(this) { isSuccess ->
            if (isSuccess) {
                Toast.makeText(this, "Data berhasil disimpan", Toast.LENGTH_SHORT).show()
                resetForm()
                reloadActivity()
            } else {
                Toast.makeText(this, "Gagal menyimpan data", Toast.LENGTH_SHORT).show()
            }
            loadingDialog.dismiss()
        }
    }

    private fun uploadFileToFirebase(fileUri: Uri) {
        loadingDialog.show()
        val storageReference = FirebaseStorage.getInstance().reference
        val fileName = "${UUID.randomUUID()}.pdf"
        val fileReference = storageReference.child("documents/$fileName")

        fileReference.putFile(fileUri)
            .addOnSuccessListener {
                fileReference.downloadUrl.addOnSuccessListener { uri ->
                    val fileUrl = uri.toString()
                    val judulPengembangan = binding.etJudulpengembangan.text.toString()
                    val keterangan = binding.etAddKeterangan.text.toString()
                    documentViewModel.uploadDocument(fileUrl, fileName, username, judulPengembangan, keterangan)
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Gagal mengunggah file", Toast.LENGTH_SHORT).show()
            }
    }

    private fun resetForm() {
        binding.etAddKeterangan.text.clear()
        binding.etJudulpengembangan.text.clear()
        binding.namedokumen.text = "Pilih file"
    }

    private fun reloadActivity() {
        val intent = intent
        finish()
        startActivity(intent)
    }

    private fun setupLoadingDialog() {
        val builder = AlertDialog.Builder(this)
        val inflater = this.layoutInflater
        val dialogView = inflater.inflate(R.layout.show_loading, null)
        builder.setView(dialogView)
        builder.setCancelable(false)
        loadingDialog = builder.create()
    }

    private fun toggleAddFormVisibility() {
        if (binding.linearLayout2.visibility == View.GONE) {
            binding.linearLayout2.visibility = View.VISIBLE
            binding.coba.visibility = View.GONE
            binding.bantuan.visibility = View.GONE
            binding.btnOpenAdd.setText("Close")
        } else {
            binding.linearLayout2.visibility = View.GONE
            binding.coba.visibility = View.VISIBLE
            binding.bantuan.visibility = View.VISIBLE
            binding.btnOpenAdd.setText("Open")
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 101 && resultCode == Activity.RESULT_OK) {
            data?.data?.let { uri ->
                selectedFileUri = uri
                binding.namedokumen.text = uri.path // Tampilkan nama file
            }
        }
    }
}
