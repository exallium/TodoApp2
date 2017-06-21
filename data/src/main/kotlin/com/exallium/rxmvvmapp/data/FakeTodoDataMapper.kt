package com.exallium.rxmvvmapp.data

import com.exallium.rxmvvmapp.domain.Todo
import com.exallium.rxmvvmapp.services.TodoGateway
import com.jakewharton.rxrelay2.BehaviorRelay
import io.reactivex.BackpressureStrategy
import io.reactivex.Completable
import io.reactivex.Observable

class FakeTodoDataMapper : TodoGateway.DataMapper {

    val todos = (1..10L).map { Todo(it.toString(), "title$it", "body$it", it % 2L == 0L) }

    val todoRelay: BehaviorRelay<List<Todo>> = BehaviorRelay.createDefault(todos)

    override fun save(todo: Todo): Completable {
        todoRelay.accept(todoRelay.value.filter { it.id != todo.id }.plus(todo))
        return Completable.complete()
    }

    override fun getTodoById(id: String): Observable<Todo> {
        return Observable.just(todos.first { it.id == id })
    }

    override fun getAllTodos(): Observable<List<Todo>> {
        return todoRelay
    }
}