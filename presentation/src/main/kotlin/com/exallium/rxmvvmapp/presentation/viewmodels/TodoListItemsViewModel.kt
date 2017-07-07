package com.exallium.rxmvvmapp.presentation.viewmodels

import com.exallium.rxmvvmapp.domain.Todo
import com.exallium.rxmvvmapp.domain.repos.StringRepository
import com.exallium.rxmvvmapp.domain.repos.TodoRepository
import com.exallium.rxmvvmapp.presentation.CompositeDisposables.plusAssign
import com.jakewharton.rxrelay2.PublishRelay
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable

class TodoListItemsViewModel(private val todoRepository: TodoRepository,
                             private val stringRepository: StringRepository,
                             private val disposables: CompositeDisposable = CompositeDisposable()) {

    sealed class UiAction {
        class PinSuccess: UiAction()
        class DisplayError(val message: String): UiAction()
        class ModifyTodo(val todoId: String): UiAction()
    }

    private val actionRelay = PublishRelay.create<UiAction>()

    fun pin(isPinned: Boolean, todo: Todo) {
        if (isPinned == todo.isPinned) {
            return
        }

        disposables += todoRepository.save(todo.copy(isPinned = isPinned))
                .toSingleDefault<UiAction>(UiAction.PinSuccess())
                .onErrorReturn { UiAction.DisplayError(stringRepository.genericError()) }
                .subscribe(actionRelay)
    }

    fun press(todo: Todo) {
        actionRelay.accept(UiAction.ModifyTodo(todo.id))
    }

    fun actions(): Observable<UiAction> = actionRelay
}