package com.exallium.rxmvvmapp.domain.repos

import com.exallium.rxmvvmapp.domain.Todo
import io.reactivex.Completable
import io.reactivex.Observable

interface TodoRepository {
    fun getGroupedTodos(): Observable<List<Todo>>
    fun getTodoById(id: String): Observable<Todo>
    fun save(todo: Todo): Completable
}
