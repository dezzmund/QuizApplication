package com.example.quizapp

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.quizapp.databinding.NewQuestionBinding
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class NewQuestionActivity : AppCompatActivity() {
    lateinit var firestore: FirebaseFirestore
    lateinit var binding: NewQuestionBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        firestore = FirebaseFirestore.getInstance()
        binding = NewQuestionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnAddQuestion.setOnClickListener {
            if (binding.tvQuestionDescription.text.isEmpty() || binding.tvOption1.text.isEmpty() || binding.tvOption2.text.isEmpty() || binding.tvOption3.text.isEmpty() || binding.tvOption4.text.isEmpty()) {
                Toast.makeText(this, "Please Enter Required Field", Toast.LENGTH_SHORT).show()
            } else {
                val quizId: String = intent.getStringExtra("quizId")!!
                val questionsJson = intent.getStringExtra("oldQuestions")
                val gson = Gson()
                val questionData: MutableMap<String, Any> = gson.fromJson(
                    questionsJson,
                    object : TypeToken<MutableMap<String, Any>>() {}.type
                )
                val collectionReference = firestore.collection("quizzes").document(
                    quizId
                )
                questionData["question${questionData.size + 1}"] = hashMapOf(
                    "questionId" to binding.etQuestionID.text.toString(),
                    "quesDescription" to binding.tvQuestionDescription.text.toString(),
                    "option1" to binding.tvOption1.text.toString(),
                    "option2" to binding.tvOption2.text.toString(),
                    "option3" to binding.tvOption3.text.toString(),
                    "option4" to binding.tvOption4.text.toString(),
                    "correctAns" to binding.tvOption1.text.toString()
                )

                collectionReference.update(
                    "questions", questionData
                )
                    .addOnSuccessListener {
                        Toast.makeText(this, "Question added Successfully", Toast.LENGTH_SHORT)
                            .show()

                        finish()
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(this, "Failed adding the question $e", Toast.LENGTH_SHORT)
                            .show()
                    }
            }

        }
    }
}
