package com.keepcoding.androidfundamentos.ui.herolist

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.keepcoding.androidfundamentos.ui.login.LoginActivity
import com.keepcoding.androidfundamentos.model.Hero
import com.keepcoding.androidfundamentos.model.HeroDto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.*
import java.io.IOException

class HeroListViewModel: ViewModel() {

    val heroLiveData: MutableLiveData<HeroListState> by lazy{
        MutableLiveData<HeroListState>()
    }

    val updateHeroListAfterFight: MutableLiveData<List<Hero>> by lazy{
        MutableLiveData<List<Hero>>()
    }

    fun getHeroList() {
        //HeroListState.Loading
        val client = OkHttpClient()
        val token = LoginActivity.TOKEN
        val url = "https://dragonball.keepcoding.education/api/heros/all"
        val formBody = FormBody.Builder()
            .add("name", "")
            .build()
        val request = Request.Builder().header("Authorization", "Bearer $token").post(formBody).url(url).build()
        val call = client.newCall(request)

        call.enqueue(
            object: Callback {
                override fun onFailure(call: Call, e: IOException) {
                    println("Error")
                    setValueOnMainThread(HeroListState.Error(e.message))
                }

                override fun onResponse(call: Call, response: Response) {
                    val responseBody = response.body?.string()
                    println("List of heroes: $responseBody")

                    val heroDtoArray : Array<HeroDto> = Gson().fromJson(responseBody, Array<HeroDto>::class.java)
                    var contador = 0
                    val heroArray =  heroDtoArray.map { Hero(it.id,it.name, it.photo, contador++, 100,100) }
                    setValueOnMainThread(HeroListState.Success(heroArray))
                }
            }
        )
    }

    fun setValueOnMainThread(value: HeroListState){
        viewModelScope.launch(Dispatchers.Main) {
            heroLiveData.value = value
        }
    }

    sealed class HeroListState{
        data class Success(val heroList: List<Hero>): HeroListState()
        data class Error(val message: String?): HeroListState()
        //data class Loading: HeroListState()
    }

    fun updateHeroStatsAfterFight(heros: List<Hero>){
        viewModelScope.launch(Dispatchers.Main){
            Log.d("ViewModel after fight: ",heros.toString())
            updateHeroListAfterFight.value = heros
        }
    }
}