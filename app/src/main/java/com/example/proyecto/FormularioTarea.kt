package com.example.proyecto

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.proyecto.databinding.ActivityFormularioTareaBinding

class FormularioTarea : AppCompatActivity() {

    private lateinit var binding: ActivityFormularioTareaBinding
    private var editingId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Acceso protegido
        if (!SessionManager.isAuthenticated) {
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
            return
        }

        enableEdgeToEdge()
        binding = ActivityFormularioTareaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val tasks = TaskRepository.load(this)

        // Si llegó un id, estamos editando una tarea existente
        editingId = intent.getIntExtra(EXTRA_TASK_ID, -1)
        if (editingId != -1) {
            tasks.find { it.id == editingId }?.let {
                binding.etTitle.setText(it.title)
                binding.etDescription.setText(it.description)
            }
            binding.tvFormTitle.text = getString(R.string.form_title_edit)
        }

        binding.btnSave.setOnClickListener { saveTask(tasks) }
    }

    private fun saveTask(tasks: MutableList<Tareas>) {
        val title = binding.etTitle.text.toString().trim()
        val description = binding.etDescription.text.toString().trim()

        // Validación
        if (title.isEmpty()) {
            binding.tilTitle.error = getString(R.string.error_title_empty)
            return
        }
        if (title.length < 3) {
            binding.tilTitle.error = getString(R.string.error_title_short)
            return
        }
        binding.tilTitle.error = null

        if (editingId == -1) {
            tasks.add(Tareas(TaskRepository.nextId(tasks), title, description))
        } else {
            tasks.find { it.id == editingId }?.let {
                it.title = title
                it.description = description
            }
        }

        TaskRepository.save(this, tasks)
        finish()
    }

    companion object {
        const val EXTRA_TASK_ID = "task_id"
    }
}