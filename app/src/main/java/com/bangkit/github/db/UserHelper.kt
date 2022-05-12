package com.bangkit.github.db

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import com.bangkit.github.db.DatabaseContract.UserColumns.Companion.TABLE_NAME
import com.bangkit.github.db.DatabaseContract.UserColumns.Companion.USERNAME
import java.sql.SQLException
import kotlin.jvm.Throws

class UserHelper(context: Context) {
    private var dataBaseHelper : DatabaseHelper = DatabaseHelper(context)
    private lateinit var database : SQLiteDatabase

    companion object {
        private const val DATABASE_TABLE = TABLE_NAME

        private var INSTANCE : UserHelper? = null
        fun getInstance(context: Context) : UserHelper = INSTANCE ?: synchronized(this) {
            INSTANCE ?: UserHelper(context)
        }
    }

    @Throws(SQLException::class)
    fun open() {
        database = dataBaseHelper.writableDatabase
    }

    /*
    fun close() {
        dataBaseHelper.close()

        if (database.isOpen) database.close()
    }

     */

    fun queryByUsername() : Cursor {
        return database.query(
            DATABASE_TABLE,
            null,
            null,
            null,
            null,
            null,
            "$USERNAME ASC",
            null
        )
    }

    fun insert(values: ContentValues?) : Long {
        return database.insert(DATABASE_TABLE, null, values)
    }

    fun deletebyUsername(username: String) : Int {
        return database.delete(DATABASE_TABLE, "$USERNAME = '$username'", null)
    }
}