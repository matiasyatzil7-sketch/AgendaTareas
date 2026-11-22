package com.example.proyecto

data class Tareas(
    val id: Int,
    var title: String,
    var description: String,
    var done: Boolean = false
)