package com.hexagram.febys.models.api.response

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
class SearchSuggestionPagingListingResponse(

    val listing: SearchSuggestionPagingListing? = null

) : Parcelable