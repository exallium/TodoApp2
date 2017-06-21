package com.exallium.rxmvvmapp.services

import com.exallium.rxmvvmapp.domain.Todo
import com.exallium.rxmvvmapp.domain.repos.AuthRepository
import com.exallium.rxmvvmapp.domain.repos.TodoRepository
import io.reactivex.Completable
import io.reactivex.CompletableSource
import io.reactivex.Observable
import io.reactivex.ObservableSource

class TodoGateway(private val dataMapper: DataMapper,
                  private val authRepository: AuthRepository) : TodoRepository {

    val pinComparator = Comparator<Todo> { o1, o2 ->
        if (o1.isPinned xor o2.isPinned) {
            if ((o1.isPinned)) -1 else 1
        } else {
            o1.id.compareTo(o2.id)
        }
    }

    override fun save(todo: Todo): Completable {
        return loginAndThen(dataMapper.save(todo))
    }

    override fun getTodoById(id: String): Observable<Todo> {
        return loginAndThen(dataMapper.getTodoById(id))
    }

    override fun getGroupedTodos(): Observable<List<Todo>> {
        return loginAndThen(dataMapper.getAllTodos().map { it.sortedWith(pinComparator) })
    }

    private fun loginAndThen(obs: CompletableSource): Completable {
        return authRepository.loginIfRequired().andThen(obs)
    }

    private fun <T> loginAndThen(obs: ObservableSource<T>): Observable<T> {
        return authRepository.loginIfRequired().andThen(obs)
    }

    interface DataMapper {
        fun save(todo: Todo): Completable
        fun getAllTodos(): Observable<List<Todo>>
        fun getTodoById(id: String): Observable<Todo>
    }
}
