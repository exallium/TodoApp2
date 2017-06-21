package com.exallium.rxmvvmapp.presentation.services

import com.exallium.rxmvvmapp.domain.repos.StringRepository
import com.jakewharton.rxrelay2.BehaviorRelay
import io.reactivex.Observable

class ToolbarService(private val stringRepository: StringRepository) {

    companion object {
        const val EMPTY = ""
    }

    data class UiState(val title: String = EMPTY) {
        internal fun validateOrModify(stringRepository: StringRepository): UiState {
            return if (this.title == EMPTY) {
                this.copy(title = stringRepository.defaultToolbarTitle())
            } else this
        }
    }

    private val stateRelay = BehaviorRelay.create<UiState>()

    fun states(): Observable<UiState> = stateRelay

    fun setState(uiState: UiState) {
        stateRelay.accept(uiState.validateOrModify(stringRepository))
    }
}