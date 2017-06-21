package com.exallium.rxmvvmapp

import android.app.Application

open class TodoApp : Application() {

    companion object {
        lateinit var INJECTOR: Injector
            private set
    }

    override fun onCreate() {
        super.onCreate()
        setInjector()
    }

    open fun setInjector() {
        val daggerComponent = DaggerAppComponent.builder()
                .appModule(AppModule(this))
                .build()

        INJECTOR = object : Injector {
            override fun getAppComponent() = daggerComponent
        }
    }

}
