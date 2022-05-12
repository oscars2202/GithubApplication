package com.bangkit.userapp.activity

import android.content.ContentValues
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.widget.ViewPager2
import com.bangkit.userapp.R
import com.bangkit.userapp.adapter.SectionPagerAdapter
import com.bangkit.userapp.databinding.ActivityDetailBinding
import com.bangkit.userapp.db.DatabaseContract
import com.bangkit.userapp.db.DatabaseContract.UserColumns.Companion.CONTENT_URI
import com.bangkit.userapp.helper.MappingHelper
import com.bangkit.userapp.model.GithubUser
import com.bangkit.userapp.viewModel.DetailViewModel
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class DetailActivity : AppCompatActivity(), View.OnClickListener {

    companion object {
        const val EXTRA_GITHUB = "extra_github"
        const val EXTRA_POSITION = "extra_position"
        const val EXTRA_STATE = "extra_state"
        const val REQUEST_ADD = 100
        const val RESULT_ADD = 101
        const val REQUEST_DELETE = 300
        const val RESULT_DELETE = 301
    }

    private val tabTitle = intArrayOf(R.string.followers, R.string.following)

    private var isEdit : Boolean = false
    private var position : Int = 0
    private var userName : GithubUser? = null

    private lateinit var btnFavorite : FloatingActionButton
    private lateinit var detailViewModel: DetailViewModel
    private lateinit var binding: ActivityDetailBinding
    private lateinit var uriWithId : Uri

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        userName = intent.getParcelableExtra(EXTRA_GITHUB)
        val bundle = Bundle()
        bundle.putString(EXTRA_GITHUB, userName?.login.toString())
        Log.d("BUNDLE", bundle.toString())

        uriWithId = Uri.parse(CONTENT_URI.toString() + "/" + userName?.login)
        if (isEdit) {

            val cursor = contentResolver.query(uriWithId, null, null, null, null)

            if (cursor != null) {
                userName = MappingHelper.mapCursorToObject(cursor)
                cursor.close()
            }
        }


        val sectionPagerAdapter = SectionPagerAdapter(this, bundle)
        val viewPager : ViewPager2 = findViewById(R.id.view_pager)
        viewPager.adapter = sectionPagerAdapter

        val tabLayout : TabLayout = findViewById(R.id.tabLayout)
        TabLayoutMediator(tabLayout, viewPager) {tab, position ->
            tab.text = resources.getString(tabTitle[position])
        }.attach()

        detailViewModel = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory()).get(DetailViewModel::class.java)
        detailViewModel.setUserDetail(userName?.login.toString())

        detailViewModel.getUserDetail().observe(this, {
            if (it != null) {
                binding.apply {
                    Glide.with(this@DetailActivity)
                        .load(it.avatar_url)
                        .transition(DrawableTransitionOptions.withCrossFade())
                        .centerCrop()
                        .into(civAvatar)
                    tvUsername.text = it.login
                    tvName.text = it.name
                    tvCompany.text = it.company
                    tvLocation.text = it.location
                    tvFollowers.text = it.followers
                    tvFollowing.text = it.following
                }
            }
        })

        btnFavorite = findViewById(R.id.fab_favorite)
        setStatusFavorite()
        btnFavorite.setOnClickListener {
            val username = userName?.login
            val avatar = userName?.avatar_url

            intent.putExtra(EXTRA_STATE, userName)
            intent.putExtra(EXTRA_POSITION, position)

            val values = ContentValues().apply {
                put(DatabaseContract.UserColumns.USERNAME, username)
                put(DatabaseContract.UserColumns.AVATAR_URL, avatar)
            }

            if (isEdit) {

                val result = contentResolver.delete(uriWithId, username, null)

                if (result > 0) {
                    setResult(RESULT_DELETE, intent)
                    Toast.makeText(this, "NOT FAVORITE", Toast.LENGTH_SHORT).show()

                } else {
                    Log.d("DELETE", "CAN NOT DELETED")
                }

            } else {

                val result = contentResolver.insert(CONTENT_URI, values)

                if (result != null) {
                    userName?.login = result.toString()
                    setResult(RESULT_ADD, intent)
                    btnFavorite.setImageResource(R.drawable.ic_favorite_fill)
                    Toast.makeText(this, "FAVORITE", Toast.LENGTH_SHORT).show()

                } else {
                    Log.d("INSERT", "CAN NOT INSERTED")
                }
            }
            setStatusFavorite()
        }
    }


    private fun swapIconFavorite(isEdit : Boolean) {
        if (isEdit) {
            btnFavorite.setImageResource(R.drawable.ic_favorite_fill)
        } else {
            btnFavorite.setImageResource(R.drawable.ic_favorite)
        }
    }

    private fun setStatusFavorite() {
        val favoriteCursor = contentResolver.query(CONTENT_URI, null, userName?.login, null, null)
        val listFavorite = MappingHelper.mapCursorToArrayList(favoriteCursor)
        if (listFavorite.size > 0) {
            for (favorite in listFavorite) {
                if (userName?.login == favorite.login) {
                    isEdit = true
                    break
                } else {
                    isEdit = false
                }
            }
        } else {
            isEdit = false
        }
        swapIconFavorite(isEdit)
    }

    override fun onClick(v: View?) {
        setStatusFavorite()
    }
}