package com.example.proyecto

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.proyecto.databinding.ActivityListaTareasBinding
import androidx.recyclerview.widget.LinearLayoutManager

class ListaTareas : AppCompatActivity() {

    private lateinit var binding: ActivityListaTareasBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Acceso protegido: si no hay sesión, regresa al login
        if (!SessionManager.isAuthenticated) {
            goToLogin()
            return
        }

        enableEdgeToEdge()
        binding = ActivityListaTareasBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.btnLogout.setOnClickListener {
            SessionManager.isAuthenticated = false
            goToLogin()
        }
        // Datos de ejemplo (después los reemplazaremos por datos guardados)
        val tasks = mutableListOf(
            Tareas(1, "Estudiar Kotlin", "Repasar ViewBinding y Activities"),
            Tareas(2, "Hacer el proyecto", "Terminar la lista de tareas"),
            Tareas(3, "Grabar el video", "Demostración de 2 a 4 minutos")
        )

        binding.rvTasks.layoutManager = LinearLayoutManager(this)
        binding.rvTasks.adapter = TaskAdapter(tasks)
    }

    // Va al login y borra el historial para que "atrás" no regrese aquí
    private fun goToLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}