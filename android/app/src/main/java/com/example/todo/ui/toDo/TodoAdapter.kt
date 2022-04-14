package com.example.todo.ui.toDo


import android.content.ClipData
import android.content.Context
import android.os.Build
import android.util.Log
import android.view.*
import android.widget.PopupMenu
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.todo.R
import com.example.todo.databinding.TodoItemBinding
import com.example.todo.model.TodoItem

interface MyDragListener {
    //fun setEmptyList(visibility: Int, recyclerView: Int, emptyTextView: Int)
}

class TodoAdapter(
    private val context: Context,
    private val listener: UpdateDialogListener,
    private val viewModel: ToDoViewModel,
    private val myDragListener: MyDragListener,
    todoDiffCallback: DiffUtil.ItemCallback<TodoItem>
) :
    ListAdapter<TodoItem, TodoAdapter.ViewHolder>(todoDiffCallback), View.OnTouchListener {


    interface UpdateDialogListener {
        fun updateDialog(item: TodoItem)
    }

    val dragInstance: DragListener?
        get() = if (myDragListener != null) {
            DragListener(myDragListener)
        } else {
            Log.e(javaClass::class.simpleName, "Listener not initialized")
            null
        }

    inner class ViewHolder(private val itemViewBinding: TodoItemBinding) :
        RecyclerView.ViewHolder(itemViewBinding.root)
        ,PopupMenu.OnMenuItemClickListener {
        private lateinit var cardItem: TodoItem
        fun bind(cardItem: TodoItem) {
            this.cardItem = cardItem
            itemViewBinding.toDoItem = cardItem
            itemView.setOnLongClickListener {
                displayPopupMenu(it)
                true
            }
        }

        private fun displayPopupMenu(view: View) {
            val popupMenu = PopupMenu(context, view)
            popupMenu.menuInflater.inflate(R.menu.menu_popup, popupMenu.menu)
            popupMenu.setOnMenuItemClickListener(this)
            popupMenu.show()
        }

        override fun onMenuItemClick(item: MenuItem?): Boolean {
            when (item?.itemId) {
                R.id.popup_move_to_done -> viewModel.moveToDone(cardItem)
                R.id.popup_update -> listener.updateDialog(cardItem)
                R.id.popup_delete -> viewModel.deleteItem(cardItem)
            }
            return true
        }
    }
    override fun onTouch(view: View, event: MotionEvent): Boolean {
        Log.d("testDrag", "ddd")
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                Log.d("testDrag", "ddd")
                val data = ClipData.newPlainText("", "")
                val shadowBuilder = View.DragShadowBuilder(view)
                // view.startDragAndDrop(data, shadowBuilder, view, 0)
                return true
            }
        }
        return false
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = TodoItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }


}
