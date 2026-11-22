package com.example.proyecto

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.proyecto.databinding.ItemTaskBinding

class TaskAdapter(
    private val tasks: MutableList<Tareas>,
    private val onToggle: () -> Unit,
    private val onEdit: (Tareas) -> Unit,
    private val onDelete: (Tareas) -> Unit
) : RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {

    class TaskViewHolder(val binding: ItemTaskBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val binding = ItemTaskBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TaskViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val task = tasks[position]
        holder.binding.tvTaskName.text = task.title
        holder.binding.tvTaskDesc.text = task.description

        holder.binding.cbDone.setOnCheckedChangeListener(null)
        holder.binding.cbDone.isChecked = task.done
        holder.binding.cbDone.setOnCheckedChangeListener { _, isChecked ->
            task.done = isChecked
            onToggle()
        }

        holder.binding.root.setOnClickListener { onEdit(task) }
        holder.binding.btnDelete.setOnClickListener { onDelete(task) }
    }

    override fun getItemCount(): Int = tasks.size
}