package com.bangkit.userapp.activity

import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bangkit.userapp.R
import com.bangkit.userapp.adapter.GithubAdapter
import com.bangkit.userapp.databinding.ActivityMainBinding
import com.bangkit.userapp.model.GithubUser
import com.bangkit.userapp.viewModel.MainViewModel

class MainActivity : AppCompatActivity() {

    private lateinit var mainViewModel: MainViewModel
    private lateinit var adapter: GithubAdapter
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        title = "Github User"

        adapter = GithubAdapter()
        adapter.notifyDataSetChanged()
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = adapter

        adapter.setOnItemClickCallback(object : GithubAdapter.OnItemClickCallback {
            override fun onItemClicked(githubUser: GithubUser) {
                showSelectedUser(githubUser)
            }
        })

        mainViewModel = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory()).get(
            MainViewModel::class.java)
        mainViewModel.getGithubUsers().observe(this, {
            if (it != null) {
                adapter.setData(it)
                showLoading(false)
            } else {
                Toast.makeText(this, "Username not Found", Toast.LENGTH_SHORT).show()
                showLoading(false)
            }
        })
    }

    private fun showSelectedUser(githubUser: GithubUser) {
        val detail = Intent(this@MainActivity, DetailActivity::class.java)
        detail.putExtra(DetailActivity.EXTRA_GITHUB, githubUser)
        startActivity(detail)
    }

    override fun onCreateOptionsMenu(menu : Menu?): Boolean {

        val inflater = menuInflater
        inflater.inflate(R.menu.option_menu, menu)

        val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        val searchView = menu!!.findItem(R.id.search).actionView as SearchView

        searchView.setSearchableInfo(searchManager.getSearchableInfo(componentName))
        searchView.queryHint = resources.getString(R.string.searchHint)
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                searchUser(query.toString())
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }
        })
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.favorite) {
            val favorite = Intent(this, FavoriteActivity::class.java)
            startActivity(favorite)
        }
        if (item.itemId == R.id.settings) {
            val settings = Intent(this, SettingsActivity::class.java)
            startActivity(settings)
        }
        if (item.itemId == R.id.language) {
            val language = Intent(Settings.ACTION_LOCALE_SETTINGS)
            startActivity(language)
        }
        return super.onOptionsItemSelected(item)
    }

    private fun searchUser(username : String) {
        binding.apply {
            if (username.isEmpty()) return
            showLoading(true)
            mainViewModel.setGithubUsers(username)
        }
    }

    private fun showLoading(state : Boolean) {
        if (state) {
            binding.progressBar.visibility = View.VISIBLE
        } else {
            binding.progressBar.visibility = View.GONE
        }
    }
}