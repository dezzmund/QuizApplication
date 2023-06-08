package com.example.quizapp

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.quizapp.databinding.ActivityLoginBinding
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException

class LoginActivity : AppCompatActivity() {

    lateinit var firebaseAuth: FirebaseAuth
    private val emailPattern = "[a-zA-Z0-9._-]+@[a-z]+.+[a-z]+"
    lateinit var binding: ActivityLoginBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        firebaseAuth = FirebaseAuth.getInstance()

        binding.loginButton.setOnClickListener {
            loginUser()
        }

        binding.loginSignUpText.setOnClickListener {
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun loginUser() {
        val email: String = binding.loginEmailAddress.text.toString().trim()
        val password: String = binding.loginPassword.text.toString().trim()

        if (email.isBlank() || password.isBlank()) {
            Toast.makeText(this, "Blank Email or Password", Toast.LENGTH_SHORT).show()
            return
        }
        firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this) {
            if (it.isSuccessful) {
                Toast.makeText(this, "Login Success", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                val exception = it.exception
                if (exception is FirebaseAuthInvalidUserException) {
                    // Handle case when the user is not registered
                    Toast.makeText(this, "User not registered!", Toast.LENGTH_SHORT).show()
                } else if (exception is FirebaseAuthInvalidCredentialsException) {
                    // Handle case when the password is incorrect
                    Toast.makeText(this, "Incorrect password!", Toast.LENGTH_SHORT).show()
                } else if (exception is FirebaseNetworkException) {
                    // Handle case when there is no network connection
                    Toast.makeText(this, "No network connection!", Toast.LENGTH_SHORT).show()
                } else if (exception is FirebaseTooManyRequestsException) {
                    // Handle case when there is no network connection
                    Toast.makeText(this, "Too many unsuccessful tried! Try again later", Toast.LENGTH_SHORT).show()
                } else {
                    // Handle other exceptions
                    Toast.makeText(
                        this,
                        "Internal Error occurred",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }
}