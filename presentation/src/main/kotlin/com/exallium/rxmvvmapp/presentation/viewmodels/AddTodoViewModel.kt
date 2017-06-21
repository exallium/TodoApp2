package com.exallium.rxmvvmapp.presentation.viewmodels

import com.exallium.rxmvvmapp.domain.NONE
import com.exallium.rxmvvmapp.domain.TitleCannotBeBlankException
import com.exallium.rxmvvmapp.domain.Todo
import com.exallium.rxmvvmapp.domain.repos.StringRepository
import com.exallium.rxmvvmapp.domain.repos.TodoRepository
import com.exallium.rxmvvmapp.presentation.DisposableDelegate
import com.exallium.rxmvvmapp.presentation.services.ToolbarService
import com.jakewharton.rxrelay2.PublishRelay
import io.reactivex.Observable

class AddTodoViewModel(private val toolbarService: ToolbarService,
                       private val stringRepository: StringRepository,
                       private val todoRepository: TodoRepository,
                       private val disposables: DisposableDelegate = DisposableDelegate()) {

    sealed class UiAction {
        class DisplayContent(val content: Todo): UiAction()
        class DisplayTitleError(val titleError: String): UiAction()
        class DisplayBodyError(val bodyError: String): UiAction()
        class DisplayError(val error: String): UiAction()
        class Finish : UiAction()
    }

    private val actionsRelay = PublishRelay.create<UiAction>()

    fun onStartDisplayScreen(todoId: String = NONE) {
        toolbarService.setState(ToolbarService.UiState(
                if (todoId == NONE) {
                    stringRepository.createTodo()
                } else stringRepository.modifyTodo()
        ))

        if (todoId == NONE) {
            return
        }

        disposables += todoRepository.getTodoById(todoId)
                .map { UiAction.DisplayContent(it) }
                .subscribe(actionsRelay)
    }

    fun onStopDisplayScreen() {
        disposables.clear()
    }

    fun save(id: String, title: String, body: String) {
        disposables += if (id == NONE) {
            saveTodo(id, title, body, false)
        } else {
            todoRepository.getTodoById(id)
                .take(1)
                .map { it.isPinned }
                .single(false)
                .flatMap { saveTodo(id, title, body, it) }
        }.subscribe(actionsRelay)
    }

    fun actions(): Observable<UiAction> = actionsRelay

    private fun saveTodo(id: String, title: String, body: String, pinned: Boolean)
            = Observable.just(Todo(id, title, body, pinned))
            .map { it.apply { validateOrThrow() } }
            .flatMapCompletable(todoRepository::save)
            .toSingleDefault<UiAction>(UiAction.Finish())
            .onErrorReturn(this::handleError)

    private fun handleError(throwable: Throwable): UiAction {
        return when (throwable) {
            is TitleCannotBeBlankException ->
                UiAction.DisplayTitleError(stringRepository.emptyTitleError())
            else ->
                UiAction.DisplayError(stringRepository.genericError())
        }
    }
}
