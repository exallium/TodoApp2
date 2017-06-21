package com.exallium.rxmvvmapp.data

import android.content.res.Resources
import com.exallium.rxmvvmapp.domain.repos.StringRepository

class ResourceStringRepository(private val resources: Resources) : StringRepository {
    override fun defaultToolbarTitle(): String = resources.getString(R.string.app_name)
    override fun createTodo(): String = resources.getString(R.string.create_todo)
    override fun modifyTodo(): String = resources.getString(R.string.modify_todo)
    override fun genericError(): String = resources.getString(R.string.generic_error)
    override fun emptyTitleError(): String = resources.getString(R.string.empty_title_error)
}