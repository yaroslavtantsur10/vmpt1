package com.example.game

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private var diceCount = 3
    private var score1 = 0
    private var score2 = 0

    // Емодзі граней кубика
    private val diceFaces = listOf("⚀", "⚁", "⚂", "⚃", "⚄", "⚅")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btnMinus      = findViewById<Button>(R.id.btnMinus)
        val btnPlus       = findViewById<Button>(R.id.btnPlus)
        val btnRoll       = findViewById<Button>(R.id.btnRoll)
        val btnReset      = findViewById<Button>(R.id.btnReset)
        val textDiceCount = findViewById<TextView>(R.id.textDiceCount)
        val textPlayer1Dice = findViewById<TextView>(R.id.textPlayer1Dice)
        val textPlayer2Dice = findViewById<TextView>(R.id.textPlayer2Dice)
        val textPlayer1Sum  = findViewById<TextView>(R.id.textPlayer1Sum)
        val textPlayer2Sum  = findViewById<TextView>(R.id.textPlayer2Sum)
        val textResult    = findViewById<TextView>(R.id.textResult)
        val textScore     = findViewById<TextView>(R.id.textScore)

        // Зміна кількості кубиків
        btnMinus.setOnClickListener {
            if (diceCount > 1) {
                diceCount--
                textDiceCount.text = diceCount.toString()
                resetRound(textPlayer1Dice, textPlayer2Dice,
                    textPlayer1Sum, textPlayer2Sum, textResult)
            }
        }

        btnPlus.setOnClickListener {
            if (diceCount < 6) {
                diceCount++
                textDiceCount.text = diceCount.toString()
                resetRound(textPlayer1Dice, textPlayer2Dice,
                    textPlayer1Sum, textPlayer2Sum, textResult)
            }
        }

        // Кинути кубики
        btnRoll.setOnClickListener {
            val rolls1 = List(diceCount) { (1..6).random() }
            val rolls2 = List(diceCount) { (1..6).random() }

            val sum1 = rolls1.sum()
            val sum2 = rolls2.sum()

            // Показати кубики емодзі
            textPlayer1Dice.text = rolls1.joinToString(" ") { diceFaces[it - 1] }
            textPlayer2Dice.text = rolls2.joinToString(" ") { diceFaces[it - 1] }

            textPlayer1Sum.text = "Сума: $sum1"
            textPlayer2Sum.text = "Сума: $sum2"

            // Визначити переможця
            when {
                sum1 > sum2 -> {
                    score1++
                    textResult.text = "🏆 Перемагає Гравець 1!"
                    textResult.setTextColor(0xFF1976D2.toInt())
                }
                sum2 > sum1 -> {
                    score2++
                    textResult.text = "🏆 Перемагає Гравець 2!"
                    textResult.setTextColor(0xFFE53935.toInt())
                }
                else -> {
                    textResult.text = "🤝 Нічия!"
                    textResult.setTextColor(0xFF555555.toInt())
                }
            }

            textScore.text = "Рахунок: Гравець 1 — $score1 : $score2 — Гравець 2"
        }

        // Скинути рахунок
        btnReset.setOnClickListener {
            score1 = 0
            score2 = 0
            textScore.text = "Рахунок: Гравець 1 — 0 : 0 — Гравець 2"
            resetRound(textPlayer1Dice, textPlayer2Dice,
                textPlayer1Sum, textPlayer2Sum, textResult)
        }
    }

    private fun resetRound(
        p1Dice: TextView, p2Dice: TextView,
        p1Sum: TextView, p2Sum: TextView,
        result: TextView
    ) {
        p1Dice.text = "—"
        p2Dice.text = "—"
        p1Sum.text = ""
        p2Sum.text = ""
        result.text = ""
    }
}