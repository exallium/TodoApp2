package com.exallium.rxmvvmapp.data

import com.exallium.rxmvvmapp.domain.NONE
import com.exallium.rxmvvmapp.domain.Todo
import com.exallium.rxmvvmapp.services.TodoGateway
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import io.reactivex.Completable
import io.reactivex.Emitter
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.Callable

class FirebaseTodoDataMapper : TodoGateway.DataMapper {

    private val db = FirebaseDatabase.getInstance()
    private val auth = FirebaseAuth.getInstance()

    /**
     * Saves a node to:
     * /uid/todo_id
     */
    override fun save(todo: Todo): Completable = Completable.create { emitter ->
        val todoRef = todoDatabaseReference()
        val child = if (todo.id == NONE) {
            todoRef.push()
        } else {
            todoRef.child(todo.id)
        }

        child.setValue(todo.toMap())
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        emitter.onComplete()
                    } else {
                        emitter.onError(task.exception)
                    }
                }
    }

    /**
     * Reads all todos under /uid
     */
    override fun getAllTodos(): Observable<List<Todo>> =
            { todoDatabaseReference() }.observe()
                    .map { it.children.map { it.toTodo() } }
                    .subscribeOn(Schedulers.io())

    /**
     * Attempts to read and return /uid/id
     */
    override fun getTodoById(id: String): Observable<Todo> =
            { todoDatabaseReference().child(id) }.observe()
                    .map { it.toTodo() }
                    .subscribeOn(Schedulers.io())

    private fun (() -> DatabaseReference).observe(): Observable<DataSnapshot> = Observable.create { emitter ->
        val dbRef = this()
        val eventListener = ValueEventListenerToEmitter(emitter)
        dbRef.addValueEventListener(eventListener)
        emitter.setCancellable { dbRef.removeEventListener(eventListener) }
    }

    private fun Todo.toMap(): Map<String, Any> =
            mapOf(
                "title" to this.title,
                "body" to this.body,
                "pinned" to this.isPinned)

    private fun DataSnapshot.toTodo() = (this.value as Map<String, Any>).toTodo(this.key)

    private fun Map<String, Any>.toTodo(id: String): Todo =
            Todo(id, this["title"] as String, this["body"] as String, this["pinned"] as Boolean)

    private fun todoDatabaseReference() = db.getReference(auth.currentUser?.uid)

    private class ValueEventListenerToEmitter(private val emitter: Emitter<DataSnapshot>) : ValueEventListener {

        override fun onCancelled(error: DatabaseError) {
            emitter.onError(error.toException())
        }

        override fun onDataChange(snapshot: DataSnapshot) {
            emitter.onNext(snapshot)
        }
    }
}