package com.example.productsadder

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.productsadder.databinding.ActivityMainBinding
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.google.firebase.storage.storage
import com.skydoves.colorpickerview.ColorEnvelope
import com.skydoves.colorpickerview.ColorPickerDialog
import com.skydoves.colorpickerview.listeners.ColorEnvelopeListener
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.util.UUID

class MainActivity : AppCompatActivity() {

    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    private val selectedImages = mutableListOf<Uri>()
    private val selectedColors = mutableListOf<Int>()
    private val productStorage = Firebase.storage.reference
    private val firestore = Firebase.firestore

    private val selectImagesActivityResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val intent = result.data
                if (intent?.clipData != null) {
                    val count = intent.clipData?.itemCount ?: 0
                    (0 until count).forEach {
                        val imageUri = intent.clipData?.getItemAt(it)?.uri
                        imageUri?.let { uri ->
                            selectedImages.add(uri)
                        }
                    }
                } else {
                    val imageUri = intent?.data
                    imageUri?.let { uri ->
                        selectedImages.add(uri)
                    }
                }
                updateImages()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.buttonColorPicker.setOnClickListener {
            ColorPickerDialog.Builder(this)
                .setTitle("Product Color")
                .setPositiveButton("Select", object : ColorEnvelopeListener {
                    override fun onColorSelected(envelope: ColorEnvelope?, fromUser: Boolean) {
                        envelope?.let {
                            selectedColors.add(it.color)
                            updateColors()
                        }
                    }
                })
                .setNegativeButton("Cancel") { colorPicker, _ ->
                    colorPicker.dismiss()
                }
                .show()
        }

        binding.buttonImagesPicker.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
            intent.type = "image/*"
            selectImagesActivityResult.launch(intent)
        }
    }

    private fun updateColors() {
        var colors = ""
        selectedColors.forEach {
            colors += " ${Integer.toHexString(it)}"
        }
        binding.tvSelectedColors.text = colors
    }

    private fun updateImages() {
        binding.tvSelectedImages.text = selectedImages.size.toString()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.toolbar_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.saveProduct) {
            val productValidation = validateInformation()
            if (!productValidation) {
                Toast.makeText(this, "Check your inputs", Toast.LENGTH_SHORT).show()
                return false
            }
            saveProduct()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun saveProduct() {
        val name = binding.edName.text.toString().trim()
        val category = binding.edCategory.text.toString().trim()
        val price = binding.edPrice.text.toString().trim()
        val offerPercentage = binding.offerPercentage.text.toString().trim()
        val description = binding.edDescription.text.toString().trim()
        val sizes = getSizesList(binding.edSizes.text.toString().trim())
        val imagesByteArrays = getImagesByteArrays()
        val images = mutableListOf<String>()

        lifecycleScope.launch(Dispatchers.IO) {
            withContext(Dispatchers.Main) {
                showLoading()
            }
            try {
                // Use coroutineScope to ensure all uploads complete
                kotlinx.coroutines.coroutineScope {
                    val uploadJobs = imagesByteArrays.map { byteArray ->
                        async {
                            val id = UUID.randomUUID().toString()
                            val imageStorage = productStorage.child("products/images/$id")
                            imageStorage.putBytes(byteArray).await()
                            val downloadUrl = imageStorage.downloadUrl.await().toString()
                            images.add(downloadUrl)
                        }
                    }
                    uploadJobs.awaitAll()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    hideLoading()
                }
            }
            // Parse price and offerPercentage safely

            val product = Product(
                UUID.randomUUID().toString(),
                name,
                category,
                price.toFloat(),
                if (offerPercentage.isEmpty()) null else offerPercentage.toFloat(),
                if (description.isEmpty()) null else description,
                if (selectedColors.isEmpty()) null else selectedColors.toList(),
                sizes,
                images
            )

            firestore.collection("products").add(product).addOnSuccessListener {

                hideLoading()


            }.addOnFailureListener {
                hideLoading()
                Log.e("Error", it.message.toString())

            }
        }
    }

    private fun showLoading() {
        binding.progressBar.visibility = View.VISIBLE
    }

    private fun hideLoading() {
        binding.progressBar.visibility = View.INVISIBLE
    }



    private fun getImagesByteArrays(): MutableList<ByteArray> {
        val imagesByteArray = mutableListOf<ByteArray>()
        selectedImages.forEach { it
            val Stream = ByteArrayOutputStream()
            val imageBmp = MediaStore.Images.Media.getBitmap(contentResolver, it)
            if (imageBmp.compress(Bitmap.CompressFormat.JPEG, 100, Stream)) {
                imagesByteArray.add(Stream.toByteArray())
            }
        }
        return imagesByteArray

    }

    private fun getSizesList(sizesStr: String): List<String>? {
        if (sizesStr.isEmpty()) return null
        return sizesStr.split(",").map { it.trim() }
    }

    private fun validateInformation(): Boolean {
        if (binding.edPrice.text.toString().trim().isEmpty()) return false
        if (binding.edName.text.toString().trim().isEmpty()) return false
        if (binding.edCategory.text.toString().trim().isEmpty()) return false
        if (selectedImages.isEmpty()) return false
        return true
    }
}



