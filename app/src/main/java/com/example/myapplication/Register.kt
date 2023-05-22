package com.example.myapplication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth

class Register : AppCompatActivity() {

    private lateinit var firebaseAuth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        firebaseAuth = FirebaseAuth.getInstance()

        val email : EditText = findViewById(R.id.emailReg)
        val password: EditText = findViewById(R.id.passwordReg)
        val confirmPassword: EditText = findViewById(R.id.confirmPassword)
        val loginBtn: Button = findViewById(R.id.loginFromRegButton)
        val registerBtn: Button = findViewById(R.id.registerBtn)

        registerBtn.setOnClickListener {
                val emailTxt: String = email.text.toString()
                val passwordTxt: String = password.text.toString()
                val confirmPassTxt: String = confirmPassword.text.toString()

                if(emailTxt.isEmpty() || passwordTxt.isEmpty() || confirmPassTxt.isEmpty()){
                    Toast.makeText(applicationContext, "Complete all fields!", Toast.LENGTH_SHORT).show()
                }
                else if (!passwordTxt.equals(confirmPassTxt)){
                    Toast.makeText(applicationContext, "Passwords are not matching!", Toast.LENGTH_SHORT).show()
                }
                else {
                    firebaseAuth.createUserWithEmailAndPassword(emailTxt, passwordTxt).addOnCompleteListener{
                        if (it.isSuccessful) {
                            Toast.makeText(applicationContext, "Successfully created account!", Toast.LENGTH_SHORT).show()
                            val intent = Intent(this, Login::class.java)
                            startActivity(intent)
                        }
                        else {
                            Toast.makeText(applicationContext, it.exception.toString(), Toast.LENGTH_SHORT).show()
                        }
                    }
                }
        }

        loginBtn.setOnClickListener {
            val intent = Intent(this, Login::class.java)
            startActivity(intent)
        }
    }
}