package com.example.todo.ui.common

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.DataBindingComponent
import androidx.databinding.DataBindingUtil
import com.example.todo.R
import com.example.todo.databinding.TodoTitleViewBinding


class TodoTitleView(context: Context, attrs: AttributeSet) :
    LinearLayout(context, attrs) {
    lateinit var title: TextView
    lateinit var count: TextView

    init {
        LayoutInflater.from(context).inflate(R.layout.todo_title_view, this, true)
        title = findViewById(R.id.tv_todo_title)
        count = findViewById(R.id.tv_content_count)
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    }


}