package com.bangkit.userapp.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class GithubUser(
    var id: String? = null,
    var login: String? = null,
    var name: String? = null,
    var avatar_url: String? = null,
    var company: String? = null,
    var location: String? = null,
    var followers: String? = null,
    var following: String? = null
) : Parcelable