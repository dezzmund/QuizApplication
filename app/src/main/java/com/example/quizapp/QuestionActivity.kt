package com.example.quizapp

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.quizapp.adapters.OptionAdapter
import com.example.quizapp.databinding.ActivityQuestionBinding
import com.example.quizapp.models.Question
import com.example.quizapp.models.Quiz
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson

class QuestionActivity : AppCompatActivity() {
    lateinit var binding: ActivityQuestionBinding
    private lateinit var progressBar: ProgressBar
    lateinit var firestore: FirebaseFirestore
    var quizzes: MutableList<Quiz>? = null
    var questions: MutableMap<String, Question>? =
        null // Mutable are reference thus changing this will change the quizzes question too
    var index = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityQuestionBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setUpFirestore()
        setUpEvent()
    }

    private fun updateProgress() {
        progressBar.max = questions!!.size
        binding.tvProgress.text =
            progressBar.progress.toString() + " / " + progressBar.max.toString()
        progressBar.progress = index
    }


    private fun setUpEvent() {

        progressBar = binding.progressBar
        progressBar.progress = 0
        val isAdmin = intent.getBooleanExtra("isAdmin",false)
        if(isAdmin)
        {
         binding.btnDel.visibility = View.VISIBLE
        binding.btnDel.setOnClickListener {
            //TODO write code to updated the questions and quizzes map and update it to Firestore
            for (i in index until questions!!.size) {
                if (questions!!.containsKey("question${i + 1}")) {
                    questions!!["question$i"] = questions!!["question${i + 1}"]!!
                }
            }
            questions!!.remove("question${questions!!.size}")
            firestore.collection("quizzes").document(intent.getStringExtra("quizId")!!)
                .update("questions", questions).addOnCompleteListener {
                    bindViews()
                    updateProgress()
                    Toast.makeText(this, "Question deleted", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Error deleting question", Toast.LENGTH_SHORT).show()
                }

            if(questions!!.size == 0)
                finish()
        }
        }
        else
            binding.btnDel.visibility = View.GONE
        binding.btnPrev.setOnClickListener {
            index--
            bindViews()
            updateProgress()
        }

        binding.btnNext.setOnClickListener {
            if (index == questions!!.size) {
                finish()
                val intent = Intent(this, ResultActivity::class.java)
                val json = Gson().toJson(quizzes!![0])
                intent.putExtra("QUIZ", json)
                startActivity(intent)
            } else {
                index++
                updateProgress()
                bindViews()
            }

        }

    }

    private fun setUpFirestore() {
        firestore = FirebaseFirestore.getInstance()
        val quizId: String? = intent.getStringExtra("quizId")
        if (quizId != null) {
            firestore.collection("quizzes").whereEqualTo("id", quizId)
                .get()
                .addOnSuccessListener {
                    if (it != null && !it.isEmpty) {
                        quizzes = it.toObjects(Quiz::class.java)
                        questions = quizzes!![0].questions
                        bindViews()
                    }
                }
        }
    }

    private fun bindViews() {

        binding.btnPrev.visibility = View.GONE
        binding.btnNext.visibility = View.GONE

        if (index == 1) { //first question
            binding.btnNext.visibility = View.VISIBLE
            if (index == questions!!.size) {
                binding.btnNext.text = "Submit"
            } else {
                binding.btnNext.text = "Next"
            }

        } else if (index == questions!!.size) { // last question
            binding.btnNext.text = "Submit"
            binding.btnNext.visibility = View.VISIBLE
            binding.btnPrev.visibility = View.VISIBLE

        } else { // Middle
            binding.btnNext.visibility = View.VISIBLE
            binding.btnNext.text = "Next"
            binding.btnPrev.visibility = View.VISIBLE
        }

        updateProgress()
        val question = questions!!["question$index"]
        question?.let {
            binding.tvQuestion.text = it.quesDescription
            val optionAdapter = OptionAdapter(this, it)
            binding.optionList.layoutManager = LinearLayoutManager(this)
            binding.optionList.adapter = optionAdapter
            binding.optionList.setHasFixedSize(true)
        }
    }
}