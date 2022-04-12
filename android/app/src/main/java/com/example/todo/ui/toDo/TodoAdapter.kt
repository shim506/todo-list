package com.example.todo.ui.toDo

import android.R
import android.app.Activity
import android.view.*
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.todo.databinding.TodoItemBinding
import com.example.todo.model.TodoItem


class TodoAdapter(
    todoDiffCallback: DiffUtil.ItemCallback<TodoItem>
) :
    ListAdapter<TodoItem, TodoAdapter.ViewHolder>(todoDiffCallback) {
    class ViewHolder(private val itemViewBinding: TodoItemBinding) : RecyclerView.ViewHolder(itemViewBinding.root) , View.OnCreateContextMenuListener {
        fun bind(cardItem: TodoItem) {
            itemViewBinding.toDoItem = cardItem
           itemView.setOnCreateContextMenuListener(this)
        }

        override fun onCreateContextMenu(
            menu: ContextMenu?,
            view: View?,
            menuInfo: ContextMenu.ContextMenuInfo?
        ) {
            val inflater: MenuInflater = MenuInflater(view?.context)
            inflater.inflate(R.menu., menu)

            (view?.context as Activity).menuInflater.inflate(R.menu., menu)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding= TodoItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

}