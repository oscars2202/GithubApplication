package com.bangkit.github.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bangkit.github.activity.DetailActivity
import com.bangkit.github.adapter.GithubAdapter
import com.bangkit.github.databinding.FragmentFollowersBinding
import com.bangkit.github.viewModel.FollowerViewModel

class FollowersFragment : Fragment() {

    private lateinit var followerViewModel : FollowerViewModel
    private lateinit var adapter : GithubAdapter
    private lateinit var binding : FragmentFollowersBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFollowersBinding.inflate(layoutInflater, container, false)
        binding.rvFollowers.layoutManager = LinearLayoutManager(activity)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = GithubAdapter()
        adapter.notifyDataSetChanged()

        binding.rvFollowers.adapter = adapter

        showLoading(true)

        followerViewModel = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory()).get(FollowerViewModel::class.java)

        val username = arguments?.getString(DetailActivity.EXTRA_GITHUB)
        Log.d("Followers", "$username")

        followerViewModel.setUserFollowers(username.toString())

        followerViewModel.getUserFollowers().observe(viewLifecycleOwner, {
            if (it != null) {
                adapter.setData(it)
                showLoading(false)
            }
        })
    }

    private fun showLoading(state : Boolean) {
        if (state) {
            binding.progressBar.visibility = View.VISIBLE
        } else {
            binding.progressBar.visibility = View.GONE
        }
    }
}