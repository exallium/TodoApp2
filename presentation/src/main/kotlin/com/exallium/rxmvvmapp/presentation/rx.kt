package com.exallium.rxmvvmapp.presentation

import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

class DisposableDelegate : Disposable {
    private val compositeDisposable = CompositeDisposable()
    override fun isDisposed() = compositeDisposable.isDisposed
    override fun dispose() = compositeDisposable.dispose()

    fun clear() {
        compositeDisposable.clear()
    }

    operator fun plusAssign(disposable: Disposable) {
        compositeDisposable.add(disposable)
    }
}