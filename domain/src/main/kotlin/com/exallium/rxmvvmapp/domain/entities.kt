package com.exallium.rxmvvmapp.domain

const val NONE = ""

class TitleCannotBeBlankException: Exception()

data class Todo(val id: String = NONE, val title: String, val body: String, val isPinned: Boolean = false) {
    fun validateOrThrow() {
        if (title.isEmpty()) {
            throw TitleCannotBeBlankException()
        }
    }
}