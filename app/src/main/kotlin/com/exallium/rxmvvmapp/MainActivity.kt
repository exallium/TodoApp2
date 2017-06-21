package com.exallium.rxmvvmapp

import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.bluelinelabs.conductor.Conductor
import com.bluelinelabs.conductor.Router
import com.bluelinelabs.conductor.RouterTransaction
import com.exallium.rxmvvmapp.controllers.TodoListController
import com.exallium.rxmvvmapp.databinding.MainActivityBinding
import com.exallium.rxmvvmapp.presentation.DisposableDelegate

class MainActivity : AppCompatActivity() {

    private lateinit var pageRouter: Router
    private val disposables = DisposableDelegate()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = DataBindingUtil.setContentView<MainActivityBinding>(this, R.layout.main_activity)
        val toolbar = binding.toolbarBinding?.toolbar
        setSupportActionBar(toolbar)

        disposables += TodoApp.INJECTOR.getAppComponent().toolbarService().states().subscribe {
            toolbar?.title = it.title
        }

        pageRouter = Conductor.attachRouter(this, binding.pageContainer, savedInstanceState)
        if (!pageRouter.hasRootController()) {
            pageRouter.setRoot(RouterTransaction.with(TodoListController()))
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        disposables.clear()
    }

    override fun onBackPressed() {
        if (!pageRouter.handleBack()) {
            super.onBackPressed()
        }
    }
}