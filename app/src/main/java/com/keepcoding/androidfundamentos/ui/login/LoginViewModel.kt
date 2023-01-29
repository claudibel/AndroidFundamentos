package com.keepcoding.androidfundamentos.ui.login

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.*
import java.io.IOException

class LoginViewModel: ViewModel() {

    val loginLiveData: MutableLiveData<LoginState> by lazy{
        MutableLiveData<LoginState>()
    }

    fun login(username: String, password: String) {

        val client = OkHttpClient()
        val credentials = Credentials.basic(username, password)
        val url = "https://dragonball.keepcoding.education/api/auth/login"
        val formBody = FormBody.Builder()
            .add("username", username)
            .add("password", password)
            .build()
        val request = Request.Builder().header("Authorization", credentials).post(formBody).url(url).build()
        val call = client.newCall(request)

        call.enqueue(
            object: Callback{
                override fun onFailure(call: Call, e: IOException) {
                    println("Error")
                    setValueOnMainThread(LoginState.Success(e.message.toString()))
                }

                override fun onResponse(call: Call, response: Response) {
                    val responseBody = response.body!!.string()
                    println("Listado de bootcamps: $responseBody")
                    setValueOnMainThread(LoginState.Success(responseBody))
                }
            }
        )
    }

    fun setValueOnMainThread(value: LoginState){
        viewModelScope.launch(Dispatchers.Main) {
            loginLiveData.value = value
        }
    }

    sealed class LoginState{
        data class Success(val token: String): LoginState()
        data class Error(val message: String?): LoginState()
        //data class Loading: HeroListState()
    }
}