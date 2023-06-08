package com.example.quizapp.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.quizapp.NewQuestionActivity
import com.example.quizapp.QuestionActivity
import com.example.quizapp.R
import com.example.quizapp.models.Quiz
import com.example.quizapp.utils.IconPicker
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson

class QuizAdapter(
    val context: Context,
    val quizzes: List<Quiz>,
    val firestore: FirebaseFirestore,
) :
    RecyclerView.Adapter<QuizAdapter.QuizViewHolder>() {
    private var isAdmin: Boolean = false

    fun setAdminStatus(isAdmin: Boolean) {
        this.isAdmin = isAdmin
        notifyDataSetChanged()
    }

    inner class QuizViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val textViewTitle: TextView = itemView.findViewById(R.id.tv_quiztitle)
        val iconView: ImageView = itemView.findViewById(R.id.iv_quizimg)
        val descriptionView: TextView = itemView.findViewById(R.id.tv_description)
        val totalQuestion: TextView = itemView.findViewById(R.id.tv_questionCount)
        val addBtn: TextView = itemView.findViewById(R.id.bt_add)
        val remBtn: TextView = itemView.findViewById(R.id.bt_delete)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QuizViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.quizitem, parent, false)
        return QuizViewHolder(view)
    }

    override fun onBindViewHolder(holder: QuizViewHolder, position: Int) {
        holder.textViewTitle.text = quizzes[position].title
        holder.iconView.setImageResource(IconPicker.getIcon())
        holder.descriptionView.text = quizzes[position].quizDescription
        holder.totalQuestion.text = "${quizzes[position].questions.size} total question"
        holder.itemView.setOnClickListener {
            if (quizzes[position].questions.isNotEmpty()) {
                Toast.makeText(context, quizzes[position].title, Toast.LENGTH_SHORT).show()
                val intent = Intent(context, QuestionActivity::class.java)
                intent.putExtra("quizId", quizzes[position].id)
                intent.putExtra("isAdmin",isAdmin)
                context.startActivity(intent)
            } else
                Toast.makeText(context, "Add a question first", Toast.LENGTH_SHORT).show()
        }

        if (!isAdmin)
            holder.addBtn.visibility = View.GONE
        else
            holder.addBtn.visibility = View.VISIBLE
        if (!isAdmin)
            holder.remBtn.visibility = View.GONE
        else
            holder.remBtn.visibility = View.VISIBLE

        holder.addBtn.setOnClickListener {
            val intent = Intent(context, NewQuestionActivity::class.java)
            intent.putExtra("quizId", quizzes[position].id)
            val gson = Gson()
            val jsonQuestions = gson.toJson(quizzes[position].questions)
            intent.putExtra("oldQuestions", jsonQuestions)
            context.startActivity(intent)
        }

        holder.remBtn.setOnClickListener {
            firestore.collection("quizzes").document(quizzes[position].id).delete()
                .addOnCompleteListener {
                    Toast.makeText(context, "Quiz deleted Successfully", Toast.LENGTH_SHORT)
                        .show()
                }
                .addOnFailureListener {
                    Toast.makeText(context, "Quiz deletion failed", Toast.LENGTH_SHORT)
                        .show()
                }
        }
    }

    override fun getItemCount(): Int {
        return quizzes.size
    }
}