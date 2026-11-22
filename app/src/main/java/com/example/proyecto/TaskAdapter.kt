package com.example.proyecto

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.proyecto.databinding.ItemTaskBinding

class TaskAdapter(private val tasks: MutableList<Tareas>) :
    RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {

    // Guarda las vistas de una fila (con ViewBinding)
    class TaskViewHolder(val binding: ItemTaskBinding) : RecyclerView.ViewHolder(binding.root)

    // Crea una fila nueva a partir de item_task.xml
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val binding = ItemTaskBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TaskViewHolder(binding)
    }

    // Llena una fila con los datos de la tarea en esa posición
    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val task = tasks[position]
        holder.binding.tvTaskName.text = task.title
        holder.binding.tvTaskDesc.text = task.description

        // Se quita el listener antes de marcar para evitar disparos por reciclaje de filas
        holder.binding.cbDone.setOnCheckedChangeListener(null)
        holder.binding.cbDone.isChecked = task.done
        holder.binding.cbDone.setOnCheckedChangeListener { _, isChecked ->
            task.done = isChecked
        }
    }

    // Cuántas filas hay
    override fun getItemCount(): Int = tasks.size
}