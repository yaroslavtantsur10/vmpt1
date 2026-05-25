package com.example.myfirstapp

data class Task(
    val id: Int,
    val title: String,
    var isDone: Boolean = false
)