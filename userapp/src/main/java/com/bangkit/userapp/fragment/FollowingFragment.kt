package com.bangkit.userapp.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bangkit.userapp.activity.DetailActivity
import com.bangkit.userapp.adapter.GithubAdapter
import com.bangkit.userapp.databinding.FragmentFollowingBinding
import com.bangkit.userapp.viewModel.FollowingViewModel

class FollowingFragment : Fragment() {

    private lateinit var followingViewModel : FollowingViewModel
    private lateinit var adapter : GithubAdapter
    private lateinit var binding : FragmentFollowingBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFollowingBinding.inflate(layoutInflater, container, false)
        binding.rvFollowing.layoutManager = LinearLayoutManager(activity)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = GithubAdapter()
        adapter.notifyDataSetChanged()

        binding.rvFollowing.adapter = adapter

        showLoading(true)

        followingViewModel = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory()).get(FollowingViewModel::class.java)


        val username = arguments?.getString(DetailActivity.EXTRA_GITHUB)

        followingViewModel.setUserFollowing(username.toString())

        followingViewModel.getUserFollowing().observe(viewLifecycleOwner, {
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