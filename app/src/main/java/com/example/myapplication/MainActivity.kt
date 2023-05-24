package com.example.myapplication

import android.content.ActivityNotFoundException
import android.content.ContentValues.TAG
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.os.Handler
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.myapplication.databinding.ActivityMainBinding
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import java.io.ByteArrayOutputStream


class MainActivity : AppCompatActivity() {

    private lateinit var binding : ActivityMainBinding

    lateinit var imageView: ImageView
    lateinit var button: Button
    lateinit var post_button: Button
    var user: String = ""
    val REQUEST_IMAGE_CAPTURE = 100

    lateinit var storage: FirebaseStorage

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initiate storage
        storage = Firebase.storage

        // Set user
        val extras = intent.extras
        if (extras != null) {
            user = extras.getString("user").toString()
        }

        // Hello text
        var txt: TextView = findViewById(R.id.hello_txt)
        txt.text = "Hello $user!"

        // Navigation
        binding.bottomNavigationView.setOnItemSelectedListener {
            when(it.itemId){
                R.id.settings -> {
                    Handler().post(Runnable {
                        val intent = Intent(this, PostList::class.java)
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

        // Take photo
        imageView = findViewById(R.id.image_save)
        button = findViewById(R.id.take_photo_btn)
        button.setOnClickListener {
            val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            try {
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
            }
            catch(e: ActivityNotFoundException){
                Toast.makeText(this, "Error: " + e.localizedMessage, Toast.LENGTH_SHORT).show()
            }
        }
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w(TAG, "Fetching FCM registration token failed", task.exception)
                return@OnCompleteListener
            }

            // Get new FCM registration token
            val token = task.result

            // Log and toast
            Log.d(TAG, "token is " + token)
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?){

        if(requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK){
            val imageBitMap = data?.extras?.get("data") as Bitmap
            post_button = findViewById(R.id.post_btn)
            post_button.setOnClickListener {
                // Create a storage reference from our app
                val storageRef = storage.reference
                val randomName = java.util.UUID.randomUUID().toString()
                val imagePath = storageRef.child("$user/$randomName.jpg")
                val baos = ByteArrayOutputStream()
                imageBitMap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
                val data = baos.toByteArray()
                var uploadTask = imagePath.putBytes(data)
                uploadTask.addOnFailureListener {
                    Toast.makeText(this, "Error $it", Toast.LENGTH_LONG).show()
                }.addOnSuccessListener {
                    Toast.makeText(this, "Success", Toast.LENGTH_SHORT).show()
                    post_button.visibility = View.INVISIBLE
                    imageView.setImageBitmap(null)
                }
            }
            post_button.visibility = View.VISIBLE
            imageView.setImageBitmap(imageBitMap)
        }
        else{
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    private fun replaceFragment(fragment : Fragment){
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frame_layout,fragment)
        fragmentTransaction.commit()
    }
}