package com.tchibo.plantbuddy.ui.viewmodels

import androidx.lifecycle.ViewModel
import com.tchibo.plantbuddy.controller.FirebaseController
import com.tchibo.plantbuddy.domain.SignInResult
import com.tchibo.plantbuddy.domain.UserData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update


data class SignInState (
    val isSignInSuccessful: Boolean = false,
    val errorMessage: String? = null,
)

class SignInViewModel: ViewModel() {

    private val _state = MutableStateFlow(SignInState())
    val state = _state.asStateFlow()

    fun onSignInResult(result: SignInResult) {
        _state.update {
            it.copy(
                isSignInSuccessful = result.data != null,
                errorMessage = result.errorMessage
            )
        }
    }

    fun resetState() {
        _state.update {
            SignInState()
        }
    }

    suspend fun registerUser(signedInUser: UserData?) {
        if (signedInUser == null) {
            return
        }

        FirebaseController.INSTANCE.registerUser(signedInUser)
    }
}