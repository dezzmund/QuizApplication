package com.example.quizapp

import android.os.Build
import android.os.Bundle
import android.text.Html
import androidx.appcompat.app.AppCompatActivity
import com.example.quizapp.databinding.ActivityResultBinding
import com.example.quizapp.models.Quiz
import com.google.gson.Gson

class ResultActivity : AppCompatActivity() {
    lateinit var binding: ActivityResultBinding
    lateinit var quiz: Quiz
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResultBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setUpViews()
    }

    private fun setUpViews() {
        val quizData = intent.getStringExtra("QUIZ")
        quiz = Gson().fromJson<Quiz>(quizData, Quiz::class.java)
        calculateScore()
        setAnswerView()
    }

    private fun setAnswerView() {
        val builder = StringBuilder("")
        for (entry in quiz.questions.entries) {
            val question = entry.value
            builder.append("<font color'#18206F'><b>Question: ${question.quesDescription}</b></font><br/><br/>")
            builder.append("<font color='#009688'>Answer: ${question.correctAns}</font><br/><br/>")
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            binding.txtAnswer.text = Html.fromHtml(builder.toString(), Html.FROM_HTML_MODE_COMPACT);
        } else {
            binding.txtAnswer.text = Html.fromHtml(builder.toString());
        }
    }


    private fun calculateScore() {
        var score = 0
        for (entry in quiz.questions.entries) {
            val question = entry.value
            if (question.userAnswer == question.correctAns) {
                score += 10
            }
        }
        binding.txtScore.text = "Your Score is $score"
    }
}