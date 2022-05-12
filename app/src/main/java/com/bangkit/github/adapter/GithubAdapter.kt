package com.bangkit.github.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bangkit.github.model.GithubUser
import com.bangkit.github.R
import com.bangkit.github.databinding.ItemGithubBinding
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions

class GithubAdapter : RecyclerView.Adapter<GithubAdapter.GithubViewHolder>() {

    private val mData = ArrayList<GithubUser>()
    private var onItemClickCallback: OnItemClickCallback? = null

    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }

    interface OnItemClickCallback {
        fun onItemClicked(githubUser: GithubUser)
    }

    fun setData(items : ArrayList<GithubUser>) {
        mData.clear()
        mData.addAll(items)
        notifyDataSetChanged()
    }


    override fun onCreateViewHolder (
        parent: ViewGroup,
        viewType: Int
    ): GithubViewHolder {
        val mView = LayoutInflater.from(parent.context).inflate(R.layout.item_github, parent, false)
        return GithubViewHolder(mView)
    }

    override fun onBindViewHolder(holder: GithubViewHolder, position: Int) {
        holder.bind(mData[position])
    }

    override fun getItemCount(): Int {
        return mData.size
    }

    inner class GithubViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView) {

        private val binding = ItemGithubBinding.bind(itemView)

        fun bind(githubUser: GithubUser) {
            binding.apply {
                Glide.with(itemView)
                    .load(githubUser.avatar_url)
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .centerCrop()
                    .into(civPhoto)
                binding.tvUsername.text = githubUser.login
            }

            binding.root.setOnClickListener {
                onItemClickCallback?.onItemClicked(githubUser)
            }
        }
    }
}