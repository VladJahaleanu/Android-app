package com.example.myapplication

import android.R.attr.bottom
import android.R.attr.left
import android.R.attr.right
import android.R.attr.top
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Handler
import android.view.ViewGroup.LayoutParams.FILL_PARENT
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import com.example.myapplication.databinding.ActivityPostListBinding
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import java.io.File
import java.io.FileOutputStream


class PostList : AppCompatActivity() {

    var user: String = ""
    var postCount: Int = 0
    lateinit var storage: FirebaseStorage

    private lateinit var binding: ActivityPostListBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_post_list)

        binding = ActivityPostListBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        // Initiate storage
        storage = Firebase.storage
        val storageRef = storage.reference

        // Set user
        val extras = intent.extras
        if (extras != null) {
            user = extras.getString("user").toString()
        }

        // Create a reference with an initial file path and name
//        val pathReference = storageRef.child("${user.toString()}/download.jpg")

        // Download a photo
        val ONE_MEGABYTE: Long = 1024 * 1024
        val link = "gs://androidapp-1d5d7.appspot.com/$user/"
        val folderReference = storage.getReferenceFromUrl(link)
        folderReference.listAll().addOnSuccessListener { listResult ->
            for (fileRef in listResult.items) {
                val tempLink = link + fileRef.name
                val fileReference = storage.getReferenceFromUrl(tempLink)
                fileReference.getBytes(ONE_MEGABYTE).addOnSuccessListener {
                    addImageView(it);
                }.addOnFailureListener {
                    Toast.makeText(this,  it.toString(), Toast.LENGTH_LONG).show()
                }
            }
        }.addOnFailureListener {
            Toast.makeText(this,  it.toString(), Toast.LENGTH_LONG).show()
        }


        // Navigation
        binding.bottomNavigationView.setOnItemSelectedListener {
            when(it.itemId){
                R.id.home -> {
                    Handler().post(Runnable {
                        val intent = Intent(this, MainActivity::class.java)
                        intent.putExtra("user", user)
                        startActivity(intent)
                        finish()
                    })
                }
                else ->{
                }
            }
            true
        }
    }

    private fun addImageView(bytes: ByteArray) {
        val imageView = ImageView(this)
        val params = LinearLayout.LayoutParams(
            FILL_PARENT,
            800
        )
        params.setMargins(0, 50, 0, 50)
        imageView.layoutParams = params

        val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)

        imageView.setImageBitmap(bitmap)
        imageView.id = postCount

        // Add ImageView to LinearLayout
        binding.scrollableLayout.addView(imageView)

        val dynamicButton = Button(this)
        dynamicButton.text = "Share"
        dynamicButton.id = postCount
        postCount += 1
        binding.scrollableLayout.addView(dynamicButton)

        dynamicButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_SEND)
            intent.type = "image/jpeg"

            // Save the bitmap to a temporary file
            val cachePath = File(externalCacheDir, "images")
            cachePath.mkdirs()
            val file = File(cachePath, "shared_image.jpg")
            val fileOutputStream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream)
            fileOutputStream.flush()
            fileOutputStream.close()

            // Attach the photo to the intent
            val photoUri = FileProvider.getUriForFile(this, packageName + ".fileprovider", file)
            intent.putExtra(Intent.EXTRA_STREAM, photoUri)

            // Launch the sharing activity
            startActivity(Intent.createChooser(intent, "Share Photo"))
        }
    }



}