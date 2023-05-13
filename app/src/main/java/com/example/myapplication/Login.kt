package com.example.myapplication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast

class Login : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val email: EditText = findViewById(R.id.email)
        val password: EditText = findViewById(R.id.password)
        val loginBtn: Button = findViewById(R.id.loginBtn)
        val registerBtn: Button = findViewById(R.id.registerFromLoginBtn)

        loginBtn.setOnClickListener {
            @Override
            fun onClick(view: View) {
                val emailTxt: String = email.text.toString()
                val passwordTxt: String = password.text.toString()

                if(emailTxt.isEmpty() || passwordTxt.isEmpty()){
                    Toast.makeText(applicationContext, "Complete all fields", Toast.LENGTH_SHORT).show()
                }
                else{

                }
            }
        }

        registerBtn.setOnClickListener {
            val intent = Intent(this, Register::class.java)
            startActivity(intent)
        }
    }
}