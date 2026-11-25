package com.example.degreeplanner.firebase

import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

//manages anonymous student authentication
class FirebaseAuthManager {

    private val auth = FirebaseAuth.getInstance()

    //get current user or sign in anonymously
    suspend fun signInAnonymously(): String {

        //reuse existing account if available
        val currentUser = auth.currentUser

        if (currentUser != null) {
            return currentUser.uid
        }

        //make an anonymous account
        return suspendCancellableCoroutine { continuation ->

            auth.signInAnonymously()
                .addOnSuccessListener { result ->

                    val userId = result.user?.uid

                    if (continuation.isActive) {
                        if (userId != null) {
                            continuation.resume(userId)
                        } else {
                            continuation.resumeWithException(
                                IllegalStateException(
                                    "Firebase user ID was not returned"
                                )
                            )
                        }
                    }
                }
                .addOnFailureListener { exception ->

                    if (continuation.isActive) {
                        continuation.resumeWithException(exception)
                    }
                }
        }
    }

    //get id of the currently signed in user
    fun getCurrentUserId(): String? {
        return auth.currentUser?.uid
    }
}