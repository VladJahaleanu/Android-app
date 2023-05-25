package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.databinding.ActivityHelpBinding
import com.google.android.material.bottomnavigation.BottomNavigationView


class Help : AppCompatActivity() {

    var user: String = ""

    private lateinit var binding: ActivityHelpBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_help)

        binding = ActivityHelpBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        // Set user
        val extras = intent.extras
        if (extras != null) {
            user = extras.getString("user").toString()
        }

        // Navigation
        val btmNav:BottomNavigationView  = findViewById(R.id.bottomNavigationView)
        btmNav.menu.getItem(2).isChecked = true
        btmNav.setOnItemSelectedListener {
            when(it.itemId){
                R.id.post -> {
                    Handler().post(Runnable {
                        val intent = Intent(this, MainActivity::class.java)
                        intent.putExtra("user", user)
                        startActivity(intent)
                        finish()
                    })
                }
                R.id.timeline -> {
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

    }
}