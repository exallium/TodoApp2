package com.exallium.rxmvvmapp.data

import com.exallium.rxmvvmapp.services.AuthGateway
import io.reactivex.Completable

class FakeAuthDataMapper : AuthGateway.DataMapper {
    override fun isAuthorized(): Boolean {
        return true
    }

    override fun login(): Completable {
        return Completable.complete()
    }
}
