package com.exallium.rxmvvmapp.recyclers

import android.databinding.DataBindingUtil
import android.support.v7.util.DiffUtil
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import com.exallium.rxmvvmapp.R
import com.exallium.rxmvvmapp.databinding.TodoListItemBinding
import com.exallium.rxmvvmapp.domain.Todo
import com.exallium.rxmvvmapp.presentation.DisposableDelegate
import com.exallium.rxmvvmapp.presentation.viewmodels.TodoListItemsViewModel

class TodoListAdapter(private val viewModel: TodoListItemsViewModel)
    : RecyclerView.Adapter<TodoListItemViewHolder>() {

    var todoList = listOf<Todo>()
    val disposables = DisposableDelegate()

    fun updateList(todoList: List<Todo>) {
        val oldList = this.todoList
        this.todoList = todoList

        DiffUtil.calculateDiff(DiffCallback(oldList, this.todoList)).dispatchUpdatesTo(this)
    }

    override fun getItemCount() = todoList.size

    override fun onBindViewHolder(holder: TodoListItemViewHolder, position: Int) {
        holder.bind(todoList[position])
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TodoListItemViewHolder {
        val binding = DataBindingUtil.inflate<TodoListItemBinding>(
                LayoutInflater.from(parent.context),
                R.layout.todo_list_item, parent, false)
        val viewHolder = TodoListItemViewHolder(binding)
        disposables += viewHolder.clicks().map { todoList[it] }.subscribe(viewModel::press)
        disposables += viewHolder.pinChecks().map { (checked, position) ->
            Pair(checked, todoList[position])
        }.subscribe { (checked, todo) -> viewModel.pin(checked, todo) }

        return viewHolder
    }

    fun onStopDisplayScreen() {
        disposables.clear()
    }

    class DiffCallback(private val oldList: List<Todo>,
                       private val newList: List<Todo>) : DiffUtil.Callback() {

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int)
            = oldList[oldItemPosition].id == newList[newItemPosition].id

        override fun getOldListSize() = oldList.size

        override fun getNewListSize() = newList.size

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int)
                = oldList[oldItemPosition] == newList[newItemPosition]
    }
}