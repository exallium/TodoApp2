package com.exallium.rxmvvmapp.services

import com.exallium.rxmvvmapp.domain.repos.AuthRepository
import io.reactivex.Completable

class AuthGateway(private val dataMapper: DataMapper) : AuthRepository {
    override fun loginIfRequired(): Completable {
        return if (dataMapper.isAuthorized()) {
            Completable.complete()
        } else {
            dataMapper.login()
        }
    }

    interface DataMapper {
        fun isAuthorized(): Boolean
        fun login(): Completable
    }
}