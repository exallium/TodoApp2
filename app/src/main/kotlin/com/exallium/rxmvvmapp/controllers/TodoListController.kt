package com.exallium.rxmvvmapp.controllers

import android.databinding.DataBindingUtil
import android.support.design.widget.Snackbar
import android.support.v7.widget.GridLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bluelinelabs.conductor.Controller
import com.bluelinelabs.conductor.RouterTransaction
import com.exallium.rxmvvmapp.Injector
import com.exallium.rxmvvmapp.R
import com.exallium.rxmvvmapp.TodoApp
import com.exallium.rxmvvmapp.databinding.TodoListBinding
import com.exallium.rxmvvmapp.domain.NONE
import com.exallium.rxmvvmapp.presentation.DisposableDelegate
import com.exallium.rxmvvmapp.presentation.viewmodels.TodoListItemsViewModel
import com.exallium.rxmvvmapp.presentation.viewmodels.TodoListViewModel
import com.exallium.rxmvvmapp.recyclers.TodoListAdapter
import com.jakewharton.rxbinding2.view.clicks

class TodoListController(injector: Injector = TodoApp.INJECTOR) : Controller() {

    private val disposables = DisposableDelegate()

    private val viewModel = injector.getTodoListComponent().viewModel()
    private val itemsViewModel = injector.getTodoListComponent().itemsViewModel()
    private val adapter = TodoListAdapter(itemsViewModel)

    private lateinit var binding: TodoListBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup): View {
        binding = DataBindingUtil.inflate<TodoListBinding>(inflater, R.layout.todo_list, container, false)
        binding.todoRecycler.layoutManager = GridLayoutManager(this.activity, 2)
        binding.todoRecycler.adapter = adapter

        disposables += binding.todoAdd.clicks().subscribe { viewModel.onAdd() }
        disposables += viewModel.actions().subscribe(this::onNextUiAction)
        disposables += itemsViewModel.actions().subscribe(this::onNextItemsUiAction)

        viewModel.onStartDisplayScreen()

        return binding.root
    }

    override fun onDestroyView(view: View) {
        viewModel.onStopDisplayScreen()
        adapter.onStopDisplayScreen()
        disposables.clear()
        super.onDestroyView(view)
    }

    private fun onNextUiAction(uiAction: TodoListViewModel.UiAction) {
        when (uiAction) {
            is TodoListViewModel.UiAction.DisplayContent ->
                adapter.updateList(uiAction.todoList)
            is TodoListViewModel.UiAction.CreateTodo ->
                displayAddTodo()
            is TodoListViewModel.UiAction.DisplayError ->
                displayError(uiAction.message)
        }
    }

    private fun onNextItemsUiAction(uiAction: TodoListItemsViewModel.UiAction) {
        when (uiAction) {
            is TodoListItemsViewModel.UiAction.ModifyTodo ->
                displayAddTodo(uiAction.todoId)
            is TodoListItemsViewModel.UiAction.DisplayError ->
                displayError(uiAction.message)
        }
    }

    private fun displayAddTodo(todoId: String = NONE) {
        router.pushController(RouterTransaction.with(AddTodoController(todoId)))
    }

    private fun displayError(message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG).show()
    }
}