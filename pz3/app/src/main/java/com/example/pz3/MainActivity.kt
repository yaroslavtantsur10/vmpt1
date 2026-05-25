package com.example.pz3

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val editTextA = findViewById<EditText>(R.id.editTextA)
        val editTextB = findViewById<EditText>(R.id.editTextB)
        val btnDivide = findViewById<Button>(R.id.btnDivide)
        val textResult = findViewById<TextView>(R.id.textResult)

        btnDivide.setOnClickListener {
            val inputA = editTextA.text.toString().trim()
            val inputB = editTextB.text.toString().trim()

            // Перевірка на порожні поля
            if (inputA.isEmpty() || inputB.isEmpty()) {
                Toast.makeText(this, "Введіть обидва числа", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val a = inputA.toDoubleOrNull()
            val b = inputB.toDoubleOrNull()

            // Перевірка на коректність введених значень
            if (a == null || b == null) {
                Toast.makeText(this, "Некоректне число", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Перевірка ділення на нуль
            if (b == 0.0) {
                textResult.text = "Помилка: ділення на нуль!"
                textResult.setTextColor(0xFFE53935.toInt())
                return@setOnClickListener
            }

            val result = a / b
            textResult.setTextColor(0xFF1976D2.toInt())
            textResult.text = "$a / $b = $result"
        }
    }
}