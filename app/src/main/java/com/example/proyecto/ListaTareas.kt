package com.example.proyecto

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.proyecto.databinding.ActivityListaTareasBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class ListaTareas : AppCompatActivity() {

    private lateinit var binding: ActivityListaTareasBinding
    private lateinit var adapter: TaskAdapter
    private val tasks = mutableListOf<Tareas>()

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

        adapter = TaskAdapter(
            tasks,
            onToggle = { TaskRepository.save(this, tasks) },
            onEdit = { task -> openForm(task.id) },
            onDelete = { task -> confirmDelete(task) }
        )
        binding.rvTasks.layoutManager = LinearLayoutManager(this)
        binding.rvTasks.adapter = adapter

        binding.btnAdd.setOnClickListener { openForm(-1) }

        binding.btnSettings.setOnClickListener {
            startActivity(Intent(this, Ajustes::class.java))
        }

        binding.btnLogout.setOnClickListener {
            SessionManager.isAuthenticated = false
            goToLogin()
        }
    }

    // Se ejecuta al volver del formulario: recarga las tareas guardadas
    override fun onResume() {
        super.onResume()
        if (!::adapter.isInitialized) return
        tasks.clear()
        tasks.addAll(TaskRepository.load(this))
        adapter.notifyDataSetChanged()
        updateEmptyMessage()
    }

    private fun openForm(taskId: Int) {
        val intent = Intent(this, FormularioTarea::class.java)
        intent.putExtra(FormularioTarea.EXTRA_TASK_ID, taskId)
        startActivity(intent)
    }

    // Diálogo de confirmación antes de borrar
    private fun confirmDelete(task: Tareas) {
        MaterialAlertDialogBuilder(this)
            .setTitle(R.string.delete_title)
            .setMessage(getString(R.string.delete_message, task.title))
            .setPositiveButton(R.string.delete_confirm) { _, _ ->
                tasks.remove(task)
                TaskRepository.save(this, tasks)
                adapter.notifyDataSetChanged()
                updateEmptyMessage()
            }
            .setNegativeButton(R.string.prompt_cancel, null)
            .show()
    }

    private fun updateEmptyMessage() {
        binding.tvEmpty.visibility = if (tasks.isEmpty()) View.VISIBLE else View.GONE
    }

    // Va al login y borra el historial para que "atrás" no regrese aquí
    private fun goToLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}