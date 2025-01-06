package com.obu.restaurant.presentation.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.obu.restaurant.data.models.User
import com.obu.restaurant.domain.usecases.LoginUseCase
import kotlinx.coroutines.launch

class LoginViewModel(private val loginUseCase: LoginUseCase) : ViewModel() {

    val loginResult = MutableLiveData<Result<User>>()

    fun login(username: String, password: String) {
        viewModelScope.launch {
            val result = loginUseCase.execute(username, password)
            loginResult.postValue(result) // Publicar el resultado como un Result<User>
        }
    }
}