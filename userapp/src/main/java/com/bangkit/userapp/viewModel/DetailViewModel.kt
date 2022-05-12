package com.bangkit.userapp.viewModel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.bangkit.userapp.model.GithubUser
import com.loopj.android.http.AsyncHttpClient
import com.loopj.android.http.AsyncHttpResponseHandler
import cz.msebera.android.httpclient.Header
import org.json.JSONObject

class DetailViewModel : ViewModel() {
    val userDetail = MutableLiveData<GithubUser>()

    fun setUserDetail(username : String) {

        val url = "https://api.github.com/users/${username}"
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
                    val responseObject = JSONObject(result)
                    val userList = GithubUser()
                    userList.id = responseObject.getInt("id").toString()
                    userList.login = responseObject.getString("login")
                    userList.avatar_url = responseObject.getString("avatar_url")
                    userList.name = responseObject.getString("name")
                    userList.location = responseObject.getString("location")
                    userList.company = responseObject.getString("company")
                    userList.followers = responseObject.getString("followers")
                    userList.following = responseObject.getString("following")
                    userDetail.postValue(userList)
                } catch (e: Exception) {
                    Log.d("DVMException", e.message.toString())
                }
            }

            override fun onFailure(
                statusCode: Int,
                headers: Array<out Header>?,
                responseBody: ByteArray?,
                error: Throwable?
            ) {

                Log.d("DVMonFailure", error?.message.toString())
            }
        })
    }

    fun getUserDetail() : LiveData<GithubUser> {
        return userDetail
    }
}