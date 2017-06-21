package com.exallium.rxmvvmapp.domain.repos

interface StringRepository {
    fun defaultToolbarTitle(): String
    fun createTodo(): String
    fun modifyTodo(): String
    fun genericError(): String
    fun emptyTitleError(): String
}
