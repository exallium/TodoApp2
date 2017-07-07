package com.exallium.rxmvvmapp.presentation

import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

object CompositeDisposables {
    operator fun CompositeDisposable.plusAssign(disposable: Disposable) {
        this.add(disposable)
    }
}
