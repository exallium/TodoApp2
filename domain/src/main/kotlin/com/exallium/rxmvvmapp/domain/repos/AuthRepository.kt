package com.exallium.rxmvvmapp.domain.repos

import io.reactivex.Completable

interface AuthRepository {
    fun loginIfRequired(): Completable
}