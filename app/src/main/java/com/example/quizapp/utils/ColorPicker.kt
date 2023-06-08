package com.example.quizapp.utils

object ColorPicker {
    val colors = arrayOf(
        "#FF03DAC5",
        "#FF03DAD5",
        "#FF03DACD",
        "#FF03DAB9",
        "#FF03DA95",
        "#FF03DA82",
        "#FF03DA6E",
        "#FF03DA57"
    )

    var currentColor = 0

    fun getColor(): String {
        currentColor = (currentColor + 1) % colors.size
        return colors[currentColor]
    }
}