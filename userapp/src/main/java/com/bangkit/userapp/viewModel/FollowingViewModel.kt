package com.bangkit.userapp.viewModel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.bangkit.userapp.model.GithubUser
import com.loopj.android.http.AsyncHttpClient
import com.loopj.android.http.AsyncHttpResponseHandler
import cz.msebera.android.httpclient.Header
import org.json.JSONArray

class FollowingViewModel : ViewModel() {

    val lsFollowing = MutableLiveData<ArrayList<GithubUser>>()

    fun setUserFollowing(username : String) {
        val listItems = ArrayList<GithubUser>()

        val url = "https://api.github.com/users/${username}/following"
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
                    val jsonArray = JSONArray(result)

                    for (i in 0 until jsonArray.length()) {
                        val user = jsonArray.getJSONObject(i)
                        val userList = GithubUser()
                        userList.login = user.getString("login")
                        userList.avatar_url = user.getString("avatar_url")
                        listItems.add(userList)
                        }
                    lsFollowing.postValue(listItems)

                } catch (e: Exception) {
                    Log.d("FlWMException", e.message.toString())
                }
            }

            override fun onFailure(
                statusCode: Int,
                headers: Array<out Header>?,
                responseBody: ByteArray?,
                error: Throwable?
            ) {
                Log.d("FlWMonFailure", error?.message.toString())
            }
        })
    }

    fun getUserFollowing() : LiveData<ArrayList<GithubUser>> {
        return lsFollowing
    }
}