package com.example.myfirstapp

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MainActivity : AppCompatActivity() {

    private val allTasks = mutableListOf(
        Task(1, "Вивчити Kotlin"),
        Task(2, "Зробити лабораторну роботу")
    )
    private var nextId = 3
    private var currentFilter = "all"
    private lateinit var adapter: TaskAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val editText = findViewById<EditText>(R.id.editTextTask)
        val btnAdd = findViewById<Button>(R.id.btnAdd)
        val btnAll = findViewById<Button>(R.id.btnAll)
        val btnActive = findViewById<Button>(R.id.btnActive)
        val btnDone = findViewById<Button>(R.id.btnDone)
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        val textStats = findViewById<TextView>(R.id.textStats)

        adapter = TaskAdapter(
            allTasks,
            onToggle = { task ->
                task.isDone = !task.isDone
                updateStats(textStats)
                refreshList()
            },
            onDelete = { task ->
                allTasks.remove(task)
                updateStats(textStats)
                refreshList()
                Toast.makeText(this, "Задачу видалено", Toast.LENGTH_SHORT).show()
            }
        )

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        updateStats(textStats)

        // Кнопка Додати
        btnAdd.setOnClickListener {
            val title = editText.text.toString().trim()
            if (title.isEmpty()) {
                Toast.makeText(this, "Введіть назву задачі", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            allTasks.add(Task(nextId++, title))
            editText.text.clear()
            updateStats(textStats)
            refreshList()
            Toast.makeText(this, "Задачу додано", Toast.LENGTH_SHORT).show()
        }

        // Фільтри
        btnAll.setOnClickListener {
            currentFilter = "all"
            btnAll.backgroundTintList = android.content.res.ColorStateList.valueOf(0xFF1976D2.toInt())
            btnActive.backgroundTintList = android.content.res.ColorStateList.valueOf(0xFFBDBDBD.toInt())
            btnDone.backgroundTintList = android.content.res.ColorStateList.valueOf(0xFFBDBDBD.toInt())
            refreshList()
        }

        btnActive.setOnClickListener {
            currentFilter = "active"
            btnAll.backgroundTintList = android.content.res.ColorStateList.valueOf(0xFFBDBDBD.toInt())
            btnActive.backgroundTintList = android.content.res.ColorStateList.valueOf(0xFF1976D2.toInt())
            btnDone.backgroundTintList = android.content.res.ColorStateList.valueOf(0xFFBDBDBD.toInt())
            refreshList()
        }

        btnDone.setOnClickListener {
            currentFilter = "done"
            btnAll.backgroundTintList = android.content.res.ColorStateList.valueOf(0xFFBDBDBD.toInt())
            btnActive.backgroundTintList = android.content.res.ColorStateList.valueOf(0xFFBDBDBD.toInt())
            btnDone.backgroundTintList = android.content.res.ColorStateList.valueOf(0xFF1976D2.toInt())
            refreshList()
        }
    }

    private fun refreshList() {
        val filtered = when (currentFilter) {
            "active" -> allTasks.filter { !it.isDone }
            "done" -> allTasks.filter { it.isDone }
            else -> allTasks.toList()
        }
        adapter.updateTasks(filtered)
    }

    private fun updateStats(textStats: TextView) {
        val active = allTasks.count { !it.isDone }
        val done = allTasks.count { it.isDone }
        textStats.text = "Активних: $active  •  Виконаних: $done"
    }
}