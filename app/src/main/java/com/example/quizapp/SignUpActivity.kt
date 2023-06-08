package com.example.quizapp

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.quizapp.databinding.ActivitySignUpBinding
import com.example.quizapp.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class SignUpActivity : AppCompatActivity() {
    lateinit var binding: ActivitySignUpBinding
    lateinit var firebaseAuth: FirebaseAuth
    lateinit var firestore: FirebaseFirestore
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        firestore = FirebaseFirestore.getInstance()
        setContentView(binding.root)
        firebaseAuth = FirebaseAuth.getInstance()

        binding.signUpButton.setOnClickListener {
            signUpUser()
        }

        binding.signUpButtonText.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun signUpUser() {
        val email: String = binding.signUpEmailAddress.text.toString().trim()
        val fullName: String = binding.signUpFullName.text.toString()
        val collegeName: String = binding.signUpCollegeName.text.toString()
        val password: String = binding.signUpPassword.text.toString().trim()
        val confirmPassword: String = binding.signUpPasswordConfirm.text.toString().trim()

        if (email.isBlank() || password.isBlank() || confirmPassword.isBlank() || fullName.isBlank() || collegeName.isBlank()) {
            Toast.makeText(this, "Please fill the all required values", Toast.LENGTH_SHORT).show()
            return
        }

        if (password != confirmPassword) {
            Toast.makeText(
                this,
                "Password and Confirm Password does not match!!",
                Toast.LENGTH_SHORT
            ).show()
        }

        firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener {
            if (it.isSuccessful && firebaseAuth.uid != null) {
                val collectionReference = firestore.collection("userData")
                val userDocument = collectionReference.document(firebaseAuth.uid!!)
                val userProfileData =
                    User(fullName = fullName, collegeName = collegeName, email = email)
                userDocument.set(userProfileData)
                    .addOnSuccessListener {
                        Toast.makeText(this, "Profile data added to Firestore!", Toast.LENGTH_SHORT)
                            .show()

                        // Proceed with the login process
                        val intent = Intent(this, LoginActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(this, "Error adding profile data", Toast.LENGTH_SHORT).show()
                    }
            } else {
                Toast.makeText(
                    this,
                    "Error Occurred !! Try Again${it.exception}",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }
}