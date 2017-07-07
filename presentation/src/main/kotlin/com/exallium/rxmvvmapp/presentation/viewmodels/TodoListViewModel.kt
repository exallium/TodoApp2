package com.exallium.rxmvvmapp.presentation.viewmodels

import com.exallium.rxmvvmapp.domain.Todo
import com.exallium.rxmvvmapp.domain.repos.StringRepository
import com.exallium.rxmvvmapp.domain.repos.TodoRepository
import com.exallium.rxmvvmapp.presentation.CompositeDisposables.plusAssign
import com.exallium.rxmvvmapp.presentation.services.ToolbarService
import com.jakewharton.rxrelay2.PublishRelay
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable

class TodoListViewModel(private val todoRepository: TodoRepository,
                        private val toolbarService: ToolbarService,
                        private val stringRepository: StringRepository,
                        private val disposables: CompositeDisposable = CompositeDisposable()) {

    sealed class UiAction {
        class DisplayContent(val todoList: List<Todo>) : UiAction()
        class DisplayError(val message: String) : UiAction()
        class CreateTodo : UiAction()
    }

    private val uiActionRelay = PublishRelay.create<UiAction>()

    fun onStartDisplayScreen() {
        toolbarService.setState(ToolbarService.UiState())
        disposables += todoRepository
                .getGroupedTodos()
                .map<UiAction> { UiAction.DisplayContent(it) }
                .onErrorReturn { UiAction.DisplayError(stringRepository.genericError()) }
                .subscribe(uiActionRelay)
    }

    fun onStopDisplayScreen() {
        disposables.clear()
    }

    fun onAdd() {
        uiActionRelay.accept(UiAction.CreateTodo())
    }

    fun actions(): Observable<UiAction> = uiActionRelay
}
