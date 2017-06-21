package com.exallium.rxmvvmapp.data

import com.exallium.rxmvvmapp.services.AuthGateway
import com.google.firebase.auth.FirebaseAuth
import io.reactivex.Completable

class FirebaseAnonAuthDataMapper : AuthGateway.DataMapper {

    override fun isAuthorized() = FirebaseAuth.getInstance().currentUser != null

    override fun login(): Completable = Completable.create { emitter ->
        FirebaseAuth.getInstance().signInAnonymously().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                emitter.onComplete()
            } else {
                emitter.onError(task.exception)
            }
        }
    }
}