package com.exallium.rxmvvmapp.recyclers

import android.support.v7.widget.RecyclerView
import android.support.v7.widget.RecyclerView.NO_POSITION
import com.exallium.rxmvvmapp.databinding.TodoListItemBinding
import com.exallium.rxmvvmapp.domain.Todo
import com.jakewharton.rxbinding2.view.clicks
import com.jakewharton.rxbinding2.widget.checkedChanges
import io.reactivex.Observable

class TodoListItemViewHolder(private val itemBinding: TodoListItemBinding)
    : RecyclerView.ViewHolder(itemBinding.root) {

    fun bind(todo: Todo) {
        itemBinding.todoTitle.text = todo.title
        itemBinding.todoBody.text = todo.body
        itemBinding.pin.isChecked = todo.isPinned
    }

    fun clicks(): Observable<Int>
        = itemView.clicks()
            .map { adapterPosition }
            .filter { it != NO_POSITION }

    fun pinChecks(): Observable<Pair<Boolean, Int>>
        = itemBinding.pin.checkedChanges()
            .map { Pair(it, adapterPosition) }
            .filter { it.second != NO_POSITION }
}