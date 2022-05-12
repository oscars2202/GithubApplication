package com.bangkit.github.provider

import android.content.ContentProvider
import android.content.ContentValues
import android.content.Context
import android.content.UriMatcher
import android.database.Cursor
import android.net.Uri
import com.bangkit.github.db.DatabaseContract.AUTHORITY
import com.bangkit.github.db.DatabaseContract.UserColumns.Companion.CONTENT_URI
import com.bangkit.github.db.DatabaseContract.UserColumns.Companion.TABLE_NAME
import com.bangkit.github.db.UserHelper

class GithubProvider : ContentProvider() {

    companion object {
        private const val GITHUB = 1
        private const val GITHUB_ID = 2
        private val sUriMatcher = UriMatcher(UriMatcher.NO_MATCH)
        private lateinit var githubHelper: UserHelper
        init {
            sUriMatcher.addURI(AUTHORITY, TABLE_NAME, GITHUB)
            sUriMatcher.addURI(AUTHORITY, "$TABLE_NAME/#", GITHUB_ID)
        }
    }

    override fun onCreate(): Boolean {
        githubHelper = UserHelper.getInstance(context as Context)
        githubHelper.open()
        return true
    }

    override fun query(
        uri: Uri, projection: Array<String>?, selection: String?,
        selectionArgs: Array<String>?, sortOrder: String?
    ): Cursor? {
        return when (sUriMatcher.match(uri)) {
            GITHUB -> githubHelper.queryByUsername()
            GITHUB_ID -> githubHelper.queryByUsername()
            else -> null
        }
    }

    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        val added: Long = when (GITHUB) {
            sUriMatcher.match(uri) -> githubHelper.insert(values)
            else -> 0
        }
        context?.contentResolver?.notifyChange(CONTENT_URI, null)
        return Uri.parse("$CONTENT_URI/$added")
        //return null
    }

    override fun getType(uri: Uri): String? {
        return null
    }

    override fun update(
        uri: Uri,
        values: ContentValues?,
        selection: String?,
        selectionArgs: Array<out String>?
    ): Int {
        return  0
    }

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<String>?): Int {

        val deleted: Int = when (GITHUB_ID) {
            sUriMatcher.match(uri) -> githubHelper.deletebyUsername(uri.lastPathSegment.toString())
            else -> 0
        }
        context?.contentResolver?.notifyChange(CONTENT_URI, null)

        return deleted
        return 0
    }
}