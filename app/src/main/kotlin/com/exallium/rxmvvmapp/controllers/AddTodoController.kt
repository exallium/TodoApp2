package com.exallium.rxmvvmapp.controllers

import android.databinding.DataBindingUtil
import android.support.design.widget.Snackbar
import android.view.*
import com.bluelinelabs.conductor.Controller
import com.exallium.rxmvvmapp.*
import com.exallium.rxmvvmapp.databinding.AddTodoBinding
import com.exallium.rxmvvmapp.domain.NONE
import com.exallium.rxmvvmapp.domain.Todo
import com.exallium.rxmvvmapp.presentation.CompositeDisposables.plusAssign
import com.exallium.rxmvvmapp.presentation.viewmodels.AddTodoViewModel
import io.reactivex.disposables.CompositeDisposable

class AddTodoController(private val todoId: String = NONE,
                        injector: Injector = TodoApp.INJECTOR,
                        private val disposables: CompositeDisposable = CompositeDisposable())
    : Controller() {

    private val viewModel = injector.getAddTodoComponent().viewModel()
    private lateinit var binding: AddTodoBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup): View {
        binding = DataBindingUtil.inflate<AddTodoBinding>(inflater, R.layout.add_todo, container, false)
        setHasOptionsMenu(true)

        disposables += viewModel.actions()
                .subscribe(this::onNextUiAction)

        viewModel.onStartDisplayScreen(todoId)
        return binding.root
    }

    override fun onDestroyView(view: View) {
        viewModel.onStopDisplayScreen()
        disposables.clear()
        super.onDestroyView(view)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.add_todo, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.save -> {
                viewModel.save(
                        id = todoId,
                        title = binding.todoTitle.text.toString(),
                        body = binding.todoBody.text.toString())
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun onNextUiAction(uiAction: AddTodoViewModel.UiAction) {
        when (uiAction) {
            is AddTodoViewModel.UiAction.DisplayContent ->
                updateDisplayedContent(uiAction.content)
            is AddTodoViewModel.UiAction.DisplayBodyError ->
                binding.todoBodyParent.applyError(uiAction.bodyError)
            is AddTodoViewModel.UiAction.DisplayTitleError ->
                binding.todoTitleParent.applyError(uiAction.titleError)
            is AddTodoViewModel.UiAction.DisplayError ->
                Snackbar.make(binding.root, uiAction.error, Snackbar.LENGTH_LONG).show()
            is AddTodoViewModel.UiAction.Finish ->
                router.popCurrentController()
        }
    }

    private fun updateDisplayedContent(todo: Todo) {
        binding.todoBody.setText(todo.body)
        binding.todoTitle.setText(todo.title)
    }
}