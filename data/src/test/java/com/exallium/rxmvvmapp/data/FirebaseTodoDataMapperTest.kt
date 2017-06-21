package com.exallium.rxmvvmapp.data

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import org.junit.Before
import org.junit.Test
import org.mockito.Answers
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.MockitoAnnotations

class FirebaseTodoDataMapperTest {

    companion object {
        const val USER_ID = "user_id"
    }

    @InjectMocks
    lateinit var testSubject: FirebaseTodoDataMapper

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    lateinit var database: FirebaseDatabase

    @Mock
    lateinit var auth: FirebaseAuth

    @Mock
    lateinit var user: FirebaseUser

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        whenever(user.uid).thenReturn(USER_ID)
        whenever(auth.currentUser).thenReturn(user)
    }

    @Test
    fun getAllTodos_dispose_removesValueEventListener() {
        // WHEN
        testSubject.getAllTodos().subscribe().dispose()

        // THEN
        verify(database.getReference(USER_ID)).removeEventListener(any<ValueEventListener>())
    }
}