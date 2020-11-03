package com.example.todolist.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.todolist.data.models.ToDoData
import com.example.todolist.databinding.EachTaskLayoutBinding


class TasksAdapter(val onClickListener: OnClickListener) :
    ListAdapter<ToDoData, TasksAdapter.TaskViewHolder>(ToDoTaskDiffCallback()) {

    class TaskViewHolder(private val binding: EachTaskLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(toDoData: ToDoData) {
            binding.toDoData = toDoData
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): TaskViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = EachTaskLayoutBinding.inflate(layoutInflater, parent, false)
                return TaskViewHolder(
                    binding
                )
            }
        }


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        return TaskViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val toDoData = getItem(position)
        holder.itemView.setOnClickListener {
            onClickListener.onClick(toDoData)
        }
        holder.bind(toDoData)
    }


    fun getDataItem(position: Int): ToDoData? {
        return getItem(position)
    }

    class ToDoTaskDiffCallback : DiffUtil.ItemCallback<ToDoData>() {

        override fun areItemsTheSame(oldItem: ToDoData, newItem: ToDoData): Boolean {

            return oldItem === newItem

        }

        override fun areContentsTheSame(oldItem: ToDoData, newItem: ToDoData): Boolean {

            return oldItem.id == newItem.id &&
                    oldItem.title == newItem.title &&
                    oldItem.description == newItem.description &&
                    oldItem.priority == newItem.priority
        }
    }

    class OnClickListener(val clickListener: (toDoItem: ToDoData) -> Unit) {
        fun onClick(toDoItem: ToDoData) = clickListener(toDoItem)
    }

}