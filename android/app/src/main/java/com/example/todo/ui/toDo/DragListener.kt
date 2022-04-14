package com.example.todo.ui.toDo

import android.view.DragEvent
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.example.todo.R

class DragListener (myDragListener: MyDragListener): View.OnDragListener  {
    override fun onDrag(view: View, event: DragEvent): Boolean {
        when (event.action) {
            DragEvent.ACTION_DROP -> {
                var positionTarget = -1
                // drag 하는 view
                val viewSource = event.localState as View?
                // view 를 놓는 위치
                val viewId = view.id

                val todoRecyclerView = R.id.rv_todo
                val inProgressRecyclerView = R.id.rv_in_progress
                val doneRecyclerView = R.id.rv_done
                val todoItemContainer = R.id.todo_container

                when (viewId) {
                    todoItemContainer, todoRecyclerView, inProgressRecyclerView, doneRecyclerView -> {
                        val target: RecyclerView
                        // rootView의 의미를 찾지 못함
                        when (viewId) {
                            todoRecyclerView -> target =
                                view.rootView.findViewById<View>(todoRecyclerView) as RecyclerView
                            inProgressRecyclerView -> target =
                                view.rootView.findViewById<View>(inProgressRecyclerView) as RecyclerView
                            doneRecyclerView -> target =
                                view.rootView.findViewById<View>(doneRecyclerView) as RecyclerView
                            else -> {
                                target = view.parent as RecyclerView
                                positionTarget = view.tag as Int
                            }
                        }
                        if (viewSource != null) {
                            // 기존 위치 리사이클러뷰
                            val source = viewSource.paddingBottom as RecyclerView
                            // 기존 어뎁터
                            val adapterSource = source.adapter as TodoAdapter
                            // tag  무엇?
                            val positionSource = viewSource.tag as Int
                            val targetItem = adapterSource.currentList[positionSource]
                            val listSource =
                                adapterSource.currentList.apply { removeAt(positionSource) }
                            adapterSource.submitList(listSource)

                            val adapterTarget = target.adapter as TodoAdapter
                            val todoListTarget = adapterTarget.currentList
                            if (positionTarget >= 0) {
                                todoListTarget.add(positionTarget, targetItem)
                            }
                            adapterTarget.submitList(todoListTarget)

                        }
                    }
                }


            }
        }
        return true
    }
}