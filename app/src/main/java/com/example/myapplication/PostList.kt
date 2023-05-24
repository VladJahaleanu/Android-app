package com.example.myapplication

import android.R.attr.bottom
import android.R.attr.left
import android.R.attr.right
import android.R.attr.top
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Handler
import android.view.ViewGroup.LayoutParams.FILL_PARENT
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.databinding.ActivityPostListBinding
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage


class PostList : AppCompatActivity() {

    var user: String = ""
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

        // Add ImageView to LinearLayout
        binding.scrollableLayout.addView(imageView)
    }

}