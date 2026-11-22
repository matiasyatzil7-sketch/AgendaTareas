package com.example.proyecto

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject

object TaskRepository {

    private const val PREFS = "tasks_prefs"
    private const val KEY = "tasks"

    // Lee las tareas guardadas
    fun load(context: Context): MutableList<Tareas> {
        val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        val array = JSONArray(prefs.getString(KEY, "[]") ?: "[]")
        val list = mutableListOf<Tareas>()
        for (i in 0 until array.length()) {
            val o = array.getJSONObject(i)
            list.add(
                Tareas(
                    o.getInt("id"),
                    o.getString("title"),
                    o.getString("description"),
                    o.getBoolean("done")
                )
            )
        }
        return list
    }

    // Guarda todas las tareas
    fun save(context: Context, tasks: List<Tareas>) {
        val array = JSONArray()
        tasks.forEach {
            array.put(
                JSONObject()
                    .put("id", it.id)
                    .put("title", it.title)
                    .put("description", it.description)
                    .put("done", it.done)
            )
        }
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .edit().putString(KEY, array.toString()).apply()
    }

    // Da un id nuevo que no se repite
    fun nextId(tasks: List<Tareas>): Int = (tasks.maxOfOrNull { it.id } ?: 0) + 1
}