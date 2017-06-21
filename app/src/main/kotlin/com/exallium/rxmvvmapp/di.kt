package com.exallium.rxmvvmapp

import android.content.Context
import com.exallium.rxmvvmapp.data.*
import com.exallium.rxmvvmapp.domain.repos.AuthRepository
import com.exallium.rxmvvmapp.domain.repos.StringRepository
import com.exallium.rxmvvmapp.domain.repos.TodoRepository
import com.exallium.rxmvvmapp.presentation.services.ToolbarService
import com.exallium.rxmvvmapp.presentation.viewmodels.AddTodoViewModel
import com.exallium.rxmvvmapp.presentation.viewmodels.TodoListItemsViewModel
import com.exallium.rxmvvmapp.presentation.viewmodels.TodoListViewModel
import com.exallium.rxmvvmapp.services.AuthGateway
import com.exallium.rxmvvmapp.services.TodoGateway
import dagger.Component
import dagger.Module
import dagger.Provides
import javax.inject.Scope
import javax.inject.Singleton

@Scope
@Retention(AnnotationRetention.RUNTIME)
annotation class PerController

interface Injector {
    fun getAppComponent(): AppComponent
    fun getTodoListComponent(): TodoListComponent = DaggerTodoListComponent.builder()
            .appComponent(getAppComponent())
            .todoListModule(TodoListModule())
            .build()
    fun getAddTodoComponent(): AddTodoComponent = DaggerAddTodoComponent.builder()
            .appComponent(getAppComponent())
            .addTodoModule(AddTodoModule())
            .build()
}

@Singleton
@Component(modules = arrayOf(AppModule::class))
interface AppComponent {
    fun toolbarService(): ToolbarService
    fun todoRepository(): TodoRepository
    fun stringRepository(): StringRepository
}

@Module
class AppModule(val context: Context) {
    @Provides
    @Singleton
    fun provideToolbarService(stringRepository: StringRepository) = ToolbarService(stringRepository)

    @Provides
    @Singleton
    fun provideStringRepository(): StringRepository = ResourceStringRepository(context.resources)

    @Provides
    @Singleton
    fun provideAuthRepository(dataMapper: AuthGateway.DataMapper): AuthRepository = AuthGateway(dataMapper)

//    @Provides
//    @Singleton
//    fun provideTodoDataMapper(): TodoGateway.DataMapper = FakeTodoDataMapper()

    @Provides
    @Singleton
    fun provideTodoDataMapper(): TodoGateway.DataMapper = FirebaseTodoDataMapper()

//    @Provides
//    @Singleton
//    fun provideAuthDataMapper(): AuthGateway.DataMapper = FakeAuthDataMapper()

    @Provides
    @Singleton
    fun provideAuthDataMapper(): AuthGateway.DataMapper = FirebaseAnonAuthDataMapper()

    @Provides
    @Singleton
    fun provideTodoRepository(dataMapper: TodoGateway.DataMapper, authRepository: AuthRepository): TodoRepository
            = TodoGateway(dataMapper, authRepository)
}

@PerController
@Component(dependencies = arrayOf(AppComponent::class), modules = arrayOf(TodoListModule::class))
interface TodoListComponent {
    fun viewModel(): TodoListViewModel
    fun itemsViewModel(): TodoListItemsViewModel
}

@Module
class TodoListModule {
    @Provides
    @PerController
    fun provideViewModel(todoRepository: TodoRepository, toolbarService: ToolbarService, stringRepository: StringRepository)
            = TodoListViewModel(todoRepository, toolbarService, stringRepository)

    @Provides
    @PerController
    fun provideItemsViewModel(todoRepository: TodoRepository, stringRepository: StringRepository)
            = TodoListItemsViewModel(todoRepository, stringRepository)
}

@PerController
@Component(dependencies = arrayOf(AppComponent::class), modules = arrayOf(AddTodoModule::class))
interface AddTodoComponent {
    fun viewModel(): AddTodoViewModel
}

@Module
class AddTodoModule {
    @Provides
    @PerController
    fun provideViewModel(toolbarService: ToolbarService,
                         stringRepository: StringRepository,
                         todoRepository: TodoRepository)
        = AddTodoViewModel(toolbarService, stringRepository, todoRepository)
}