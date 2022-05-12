package com.bangkit.github.activity

import android.content.Intent
import android.database.ContentObserver
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.HandlerThread
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.bangkit.github.adapter.FavoriteAdapter
import com.bangkit.github.databinding.ActivityFavoriteBinding
import com.bangkit.github.db.DatabaseContract.UserColumns.Companion.CONTENT_URI
import com.bangkit.github.db.UserHelper
import com.bangkit.github.helper.MappingHelper
import com.bangkit.github.model.GithubUser
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class FavoriteActivity : AppCompatActivity() {

    companion object {
        private const val EXTRA_STATE = "extra_state"
    }

    private lateinit var binding: ActivityFavoriteBinding
    private lateinit var adapter : FavoriteAdapter
    private lateinit var favoriteHelper: UserHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFavoriteBinding.inflate(layoutInflater)
        setContentView(binding.root)
        favoriteHelper = UserHelper.getInstance(applicationContext)
        favoriteHelper.open()

        adapter = FavoriteAdapter(this)

        if (savedInstanceState == null) {
            //get data
            loadFavoritesAsync()
        } else {
            val list =savedInstanceState.getParcelableArrayList<GithubUser>(EXTRA_STATE)
            if (list != null) {
                adapter.listFavorite = list
            }
        }

        binding.rvFavorite.layoutManager = LinearLayoutManager(this)
        binding.rvFavorite.setHasFixedSize(true)
        binding.rvFavorite.adapter = adapter

        adapter.setOnItemClickCallback(object : FavoriteAdapter.OnItemClickCallback {
            override fun onItemClicked(githubUser: GithubUser) {
                showSelectedUser(githubUser)
            }
        })


        val handlerThread = HandlerThread("DataObserver")
        handlerThread.start()
        val handler = Handler(handlerThread.looper)

        val myObserver = object : ContentObserver(handler) {
            override fun onChange(selfChange: Boolean) {
                loadFavoritesAsync()
            }
        }

        contentResolver.registerContentObserver(CONTENT_URI, true, myObserver)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (data != null) {
            when (requestCode) {
                DetailActivity.REQUEST_ADD -> if (resultCode == DetailActivity.RESULT_ADD) {
                    val favorite = data.getParcelableExtra<GithubUser>(DetailActivity.EXTRA_GITHUB) as GithubUser
                    adapter.addFavorite(favorite)
                    binding.rvFavorite.smoothScrollToPosition(adapter.itemCount -1)
                }
                DetailActivity.REQUEST_DELETE -> if (resultCode == DetailActivity.RESULT_DELETE) {
                    val position = data.getIntExtra(DetailActivity.EXTRA_POSITION, 0)
                    adapter.removeFavorite(position)
                }
            }
        }
    }

    private fun showSelectedUser(githubUser: GithubUser) {
        val detail = Intent(this, DetailActivity::class.java)
        detail.putExtra(DetailActivity.EXTRA_GITHUB, githubUser)
        startActivity(detail)
    }

    private fun loadFavoritesAsync() {
        GlobalScope.launch(Dispatchers.Main) {
            binding.progressBar.visibility = View.VISIBLE
            val favoriteHelper = UserHelper.getInstance(applicationContext)
            favoriteHelper.open()
            val defferedFavorites = async(Dispatchers.IO) {
                val cursor = contentResolver.query(CONTENT_URI, null, null, null, null)
                MappingHelper.mapCursorToArrayList(cursor)
            }

            binding.progressBar.visibility = View.INVISIBLE

            val favorites = defferedFavorites.await()
            if (favorites.size > 0) {
                adapter.listFavorite = favorites
            } else {
                adapter.listFavorite = ArrayList()
                showSnackbarMessage("There is no data right now")
            }
        }
    }

    private fun showSnackbarMessage(message: String) {
        Snackbar.make(binding.rvFavorite, message, Snackbar.LENGTH_SHORT).show()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelableArrayList(EXTRA_STATE, adapter.listFavorite)

    }

}