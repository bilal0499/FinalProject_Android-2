package com.example.and2_finalproject

import android.app.ProgressDialog
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.example.and2_finalproject.databinding.ActivityAddCategoryBinding
import com.example.and2_finalproject.databinding.ActivityAddProductBinding
import com.example.and2_finalproject.firebase.FirebaseFunctions
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import java.io.ByteArrayOutputStream

class AddProduct : AppCompatActivity() {
    private var progressDialog: ProgressDialog? = null
    lateinit var binding: ActivityAddProductBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddProductBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val storage = Firebase.storage
        val storageRef = storage.reference
        val imageRef = storageRef.child("images")

        val firebaseFunctions = FirebaseFunctions()


        binding.imgProduct.setOnClickListener {
            getContent.launch("image/*")
        }

        binding.btnSave.setOnClickListener {
            val name = binding.tvName.text.toString()
            val description = binding.tvDescription.text.toString()
            val price = binding.tvPrice.text.toString()
            val location = binding.tvLocation.text.toString()

            if (name.isNotEmpty() && description.isNotEmpty() &&
                price.isNotEmpty() && location.isNotEmpty()
            ) {
                showDialog()
                // Get the data from an ImageView as bytes
                val bitmap = (binding.imgProduct.drawable as BitmapDrawable).bitmap
                val baos = ByteArrayOutputStream()
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos)
                val data = baos.toByteArray()

                val childRef = imageRef.child(System.currentTimeMillis().toString() + "_images.png")
                var uploadTask = childRef.putBytes(data)
                uploadTask.addOnFailureListener { exception ->
                    Log.e("hzm", exception.message!!)
                    hideDialog()
                    // Handle unsuccessful uploads
                }.addOnSuccessListener {
                    // taskSnapshot.metadata contains file metadata such as size, content-type, etc.
                    // ...
                    Log.e("hzm", "Image Uploaded Successfully")
                    Toast.makeText(this, "Image Uploaded Successfully", Toast.LENGTH_SHORT).show()
                    childRef.downloadUrl.addOnSuccessListener { uri ->

// TODO: add categories
                        firebaseFunctions.addProduct(
                            name,
                            description,
                            price.toDouble(),
                            location,
                            uri!!.toString(),
                            ""
                        )
                    }
                    hideDialog()
                }
            }
        }


    }


    private val getContent =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            binding.imgProduct.setImageURI(uri)
        }

    private fun showDialog() {
        progressDialog = ProgressDialog(this)
        progressDialog!!.setMessage("Uploading image ...")
        progressDialog!!.setCancelable(false)
        progressDialog!!.show()
    }

    private fun hideDialog() {
        if (progressDialog!!.isShowing)
            progressDialog!!.dismiss()
    }

}