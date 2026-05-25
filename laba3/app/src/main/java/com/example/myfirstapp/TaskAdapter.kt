package com.example.myfirstapp

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class TaskAdapter(
    private var tasks: MutableList<Task>,
    private val onToggle: (Task) -> Unit,
    private val onDelete: (Task) -> Unit
) : RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {

    inner class TaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val checkBox: CheckBox = itemView.findViewById(R.id.checkBoxDone)
        val textTitle: TextView = itemView.findViewById(R.id.textTitle)
        val btnDelete: Button = itemView.findViewById(R.id.btnDelete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_task, parent, false)
        return TaskViewHolder(view)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val task = tasks[position]
        holder.textTitle.text = task.title
        holder.checkBox.isChecked = task.isDone

        if (task.isDone) {
            holder.textTitle.paintFlags =
                holder.textTitle.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
            holder.textTitle.setTextColor(0xFF9E9E9E.toInt())
            holder.itemView.setBackgroundColor(0xFFE8F5E9.toInt())
        } else {
            holder.textTitle.paintFlags =
                holder.textTitle.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
            holder.textTitle.setTextColor(0xFF212121.toInt())
            holder.itemView.setBackgroundColor(0xFFFFFFFF.toInt())
        }

        holder.checkBox.setOnClickListener { onToggle(task) }
        holder.btnDelete.setOnClickListener { onDelete(task) }
    }

    override fun getItemCount() = tasks.size

    fun updateTasks(newTasks: List<Task>) {
        tasks = newTasks.toMutableList()
        notifyDataSetChanged()
    }
}