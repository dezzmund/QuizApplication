package com.example.quizapp.adapters

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.quizapp.R
import com.example.quizapp.models.Question

class OptionAdapter(val context: Context, val question: Question) :
    RecyclerView.Adapter<OptionAdapter.OptionViewHolder>() {

    private var options: List<String> =
        listOf(question.option1, question.option2, question.option3, question.option4)

    inner class OptionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var optionView = itemView.findViewById<TextView>(R.id.quiz_option)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OptionViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.option_item, parent, false)
        return OptionViewHolder(view)
    }

    override fun onBindViewHolder(holder: OptionViewHolder, position: Int) {
        holder.optionView.text = options[position]
        holder.itemView.setOnClickListener {
            question.userAnswer = options[position]
            notifyDataSetChanged()
        }
        if (question.userAnswer == options[position]) {
//            holder.itemView.setBackgroundResource(R.drawable.option_item_selected_bg)
            holder.optionView.setTextColor(Color.parseColor("#363A43"));
            holder.optionView.setTypeface(holder.optionView.typeface, Typeface.BOLD)
            holder.optionView.background =
                ContextCompat.getDrawable(context, R.drawable.selected_option_border_bg);
        } else {
//            holder.itemView.setBackgroundResource(R.drawable.option_item_bg)
            holder.optionView.setTextColor(Color.parseColor("#7A8089"));
            holder.optionView.typeface = Typeface.DEFAULT;
            holder.optionView.background =
                ContextCompat.getDrawable(context, R.drawable.default_option_border_bg);

        }
    }

    override fun getItemCount(): Int {
        return options.size
    }
}