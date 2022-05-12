package com.bangkit.userapp.helper

import android.database.Cursor
import com.bangkit.userapp.db.DatabaseContract
import com.bangkit.userapp.model.GithubUser

object MappingHelper {

    fun mapCursorToArrayList(favoritesCursor: Cursor?) : ArrayList<GithubUser> {

        val favoriteList = ArrayList<GithubUser>()

        favoritesCursor?.apply {
            while (moveToNext()) {
                val username = getString(getColumnIndexOrThrow(DatabaseContract.UserColumns.USERNAME))
                val avatar = getString(getColumnIndexOrThrow(DatabaseContract.UserColumns.USERNAME))

                favoriteList.add(GithubUser(username, avatar))
            }
        }
        return favoriteList
    }

    fun mapCursorToObject(favoritesCursor: Cursor?) : GithubUser {

        var githubUser = GithubUser()

        favoritesCursor?.apply {
            moveToFirst()
            val username = getString(getColumnIndexOrThrow(DatabaseContract.UserColumns.USERNAME))
            val avatar = getString(getColumnIndexOrThrow(DatabaseContract.UserColumns.AVATAR_URL))

            githubUser = GithubUser(username, avatar)
        }
        return githubUser
    }
}