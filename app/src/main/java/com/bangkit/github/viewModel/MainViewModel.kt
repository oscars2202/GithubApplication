package com.bangkit.github.viewModel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.bangkit.github.model.GithubUser
import com.loopj.android.http.AsyncHttpClient
import com.loopj.android.http.AsyncHttpResponseHandler
import cz.msebera.android.httpclient.Header
import org.json.JSONObject
import java.lang.Exception

class MainViewModel : ViewModel() {

    val lsUsers = MutableLiveData<ArrayList<GithubUser>>()

    fun setGithubUsers(username : String) {
        val listItems = ArrayList<GithubUser>()

        val url = "https://api.github.com/search/users?q=${username}"
        val apiKey = "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"

        val client = AsyncHttpClient()
        client.addHeader("Authorization", apiKey)
        client.addHeader("User-Agent", "request")

        client.get(url, object : AsyncHttpResponseHandler() {
            override fun onSuccess(
                statusCode: Int,
                headers: Array<out Header>?,
                responseBody: ByteArray?
            ) {
                try {
                    val result = String(responseBody!!)
                    val jsonObject = JSONObject(result)
                    val list = jsonObject.getJSONArray("items")

                    for (i in 0 until list.length()) {
                        val user = list.getJSONObject(i)
                        val userList = GithubUser()
                        userList.id = user.getInt("id").toString()
                        userList.login = user.getString("login")
                        userList.avatar_url = user.getString("avatar_url")
                        listItems.add(userList)
                    }
                    lsUsers.postValue(listItems)

                } catch (e: Exception) {
                    Log.d("MVMException", e.message.toString())
                }
            }

            override fun onFailure(
                statusCode: Int,
                headers: Array<out Header>?,
                responseBody: ByteArray?,
                error: Throwable?
            ) {
                Log.d("MVMonFailure", error?.message.toString())
            }
        })
    }

    fun getGithubUsers() : LiveData<ArrayList<GithubUser>> {
        return lsUsers
    }
}