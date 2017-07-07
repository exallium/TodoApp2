package com.exallium.rxmvvmapp

import android.databinding.ObservableField
import io.reactivex.Observable

object RxUtils {
    fun <T> ObservableField<T>.toObservable() : Observable<T> {
        return Observable.create { emitter ->

            fun emitIfNotNull() {
                this.get().let {
                    emitter.onNext(it)
                }
            }

            emitIfNotNull()
            val listener = object : android.databinding.Observable.OnPropertyChangedCallback() {
                override fun onPropertyChanged(observable: android.databinding.Observable, p1: Int) {
                    emitIfNotNull()
                }
            }

            this.addOnPropertyChangedCallback(listener)
            emitter.setCancellable { this.removeOnPropertyChangedCallback(listener) }
        }
    }
}