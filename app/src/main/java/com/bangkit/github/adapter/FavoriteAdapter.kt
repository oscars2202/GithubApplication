package com.bangkit.github.adapter

import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bangkit.github.CustomOnItemClickListener
import com.bangkit.github.R
import com.bangkit.github.activity.DetailActivity
import com.bangkit.github.databinding.ItemGithubBinding
import com.bangkit.github.model.GithubUser
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions

class FavoriteAdapter(private val activity: Activity) : RecyclerView.Adapter<FavoriteAdapter.FavoriteViewHolder>() {

    private var onItemClickCallback : OnItemClickCallback? = null

    var listFavorite = ArrayList<GithubUser>()
        set (listFavorite) {
            if (listFavorite.size > 0) {
                this.listFavorite.clear()
            }
            this.listFavorite.addAll(listFavorite)
            notifyDataSetChanged()
        }

    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }

    interface OnItemClickCallback {
        fun onItemClicked(githubUser: GithubUser)
    }

    fun addFavorite(githubUser: GithubUser) {
        this.listFavorite.add(githubUser)
        notifyItemInserted(this.listFavorite.size -1)
    }

    fun removeFavorite(position: Int) {
        this.listFavorite.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, this.listFavorite.size)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): FavoriteAdapter.FavoriteViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_github, parent, false)
        return FavoriteViewHolder(view)
    }

    override fun onBindViewHolder(holder: FavoriteAdapter.FavoriteViewHolder, position: Int) {
        holder.bind(listFavorite[position])
    }

    override fun getItemCount(): Int = listFavorite.size

    inner class FavoriteViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView) {
        private val binding = ItemGithubBinding.bind(itemView)
        fun bind(githubUser: GithubUser) {
            binding.apply {
                Glide.with(itemView)
                    .load(githubUser.avatar_url)
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .centerCrop()
                    .into(civPhoto)
                binding.tvUsername.text = githubUser.login
                binding.cvItemGithub.setOnClickListener(CustomOnItemClickListener(adapterPosition, object : CustomOnItemClickListener.OnItemClickCallback {
                    override fun onItemClicked(view: View, position: Int) {
                        val intent = Intent(activity, DetailActivity::class.java)
                        intent.putExtra(DetailActivity.EXTRA_POSITION, position)
                        intent.putExtra(DetailActivity.EXTRA_GITHUB, githubUser)
                        activity.startActivity(intent)
                    }
                }))
            }
        }
    }
}