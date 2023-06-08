package com.example.quizapp

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.quizapp.adapters.QuizAdapter
import com.example.quizapp.databinding.ActivityMainBinding
import com.example.quizapp.models.Quiz
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : AppCompatActivity() {
    var isAdmin: Boolean = false
    lateinit var actionBarDrawerToggle: ActionBarDrawerToggle
    lateinit var adapter: QuizAdapter
    lateinit var firestore: FirebaseFirestore
    var quizList = mutableListOf<Quiz>()
    lateinit var binding: ActivityMainBinding
    lateinit var firebaseAuth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        firestore = FirebaseFirestore.getInstance()
        firebaseAuth = FirebaseAuth.getInstance()
        setUpView()
        binding.btnAddNewQuiz.setOnClickListener {
            showAddQuizDialog()
        }
    }

    private fun showAddQuizDialog() {

        val dialogView = LayoutInflater.from(this).inflate(R.layout.new_quiz_dialog, null)

        val editTextQuizName = dialogView.findViewById<EditText>(R.id.editTextQuizName)
        val editTextQuizDescription =
            dialogView.findViewById<EditText>(R.id.editTextQuizDescription)

        val builder = AlertDialog.Builder(this)
            .setTitle("Add Quiz")
            .setView(dialogView)
            .setPositiveButton("Add") { dialog, which ->
                val quizName = editTextQuizName.text.toString()
                val quizDescription = editTextQuizDescription.text.toString()

                if (quizName.isNotEmpty() && quizDescription.isNotEmpty()) {
                    val collectionReference = firestore.collection("quizzes")

                    collectionReference.add(
                        Quiz(
                            title = quizName,
                            quizDescription = quizDescription
                        )
                    )
                        .addOnSuccessListener {
                            collectionReference.document(it.id).update("id", it.id)
                                .addOnSuccessListener {
                                    Toast.makeText(
                                        this,
                                        "Quiz added Successfully",
                                        Toast.LENGTH_SHORT
                                    )
                                        .show()
                                }

                        }
                        .addOnFailureListener { e ->
                            Toast.makeText(this, "Failed adding the Quiz", Toast.LENGTH_SHORT)
                                .show()
                        }
                } else
                    Toast.makeText(this, "Enter the required fields", Toast.LENGTH_SHORT).show()

                dialog.dismiss()
            }
            .setNegativeButton("Cancel") { dialog, which ->
                dialog.dismiss()
            }

        val dialog = builder.create()
        dialog.show()
    }


    fun setUpView() {
        setUpFirestore()
        setUpDrawerLayout()
        setUpRecycleView()
    }

    fun setUpFirestore() {
        val collectionReference = firestore.collection("quizzes")
        collectionReference.addSnapshotListener { value, error ->
            if (value == null || error != null) {
                Toast.makeText(this, "Something Happened", Toast.LENGTH_SHORT).show()
                return@addSnapshotListener
            }
//            Log.d("DATA", value.toObjects(Quiz::class.java).toString())
            quizList.clear()
            quizList.addAll(value.toObjects(Quiz::class.java))
            if (quizList.isEmpty()) {
                binding.quizRecyclerView.visibility = View.GONE
                binding.tvNoQuizAvailable.visibility = View.VISIBLE
            } else {
                binding.quizRecyclerView.visibility = View.VISIBLE
                binding.tvNoQuizAvailable.visibility = View.GONE
            }
            adapter.notifyDataSetChanged()

        }
        val authRef = firestore.collection("userData")
        authRef.document(firebaseAuth.currentUser!!.uid).addSnapshotListener { value, error ->
            if (value == null || error != null) {
                Toast.makeText(this, "Something Happened", Toast.LENGTH_SHORT).show()
                return@addSnapshotListener
            }
            isAdmin = value["userType"] == "admin"
            if (!isAdmin) {
                binding.btnAddNewQuiz.visibility = View.GONE
            } else {
                binding.btnAddNewQuiz.visibility = View.VISIBLE
            }
            adapter.setAdminStatus(isAdmin)
//            binding.quizRecyclerView.adapter = QuizAdapter(this, quizList, firestore, isAdmin)
        }
    }

    fun setUpRecycleView() {
        adapter = QuizAdapter(this, quizList, firestore)
        binding.quizRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.quizRecyclerView.adapter = adapter
    }

    fun setUpDrawerLayout() {
        setSupportActionBar(binding.topAppBar)
        actionBarDrawerToggle =
            ActionBarDrawerToggle(this, binding.mainDrawer, R.string.app_name, R.string.app_name)
        actionBarDrawerToggle.syncState()

        binding.navigationView.setNavigationItemSelectedListener {
            val intent = Intent(this, ProfileActivity::class.java)
            startActivity(intent)
            binding.mainDrawer.closeDrawers()
            true
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}