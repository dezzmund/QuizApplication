package com.example.quizapp

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.quizapp.databinding.ActivityLoginIntroBinding
import com.google.firebase.auth.FirebaseAuth

class LoginIntro : AppCompatActivity() {
    lateinit var binding: ActivityLoginIntroBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginIntroBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val auth = FirebaseAuth.getInstance()
        if (auth.currentUser != null) {
            Toast.makeText(this, "User is already logged in!", Toast.LENGTH_SHORT).show()
            redirect("MAIN")
        }

        binding.loginIntroButton.setOnClickListener {
            redirect("LOGIN")
        }
    }

    private fun redirect(name: String) {
        val intent = when (name) {
            "LOGIN" -> Intent(this, LoginActivity::class.java)
            "MAIN" -> Intent(this, MainActivity::class.java)
            else -> throw Exception("no path exists")
        }
        startActivity(intent)
        finish()
    }
}