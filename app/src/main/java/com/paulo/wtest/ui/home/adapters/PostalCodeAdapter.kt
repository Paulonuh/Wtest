package com.paulo.wtest.ui.home.adapters

import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import com.paulo.wtest.model.postalcode.PostalCode

class PostalCodeAdapter :
    PagingDataAdapter<PostalCode, PostalCodeViewHolder>(ITEM_COMPARATOR) {

    companion object {
        private val ITEM_COMPARATOR =
            object : DiffUtil.ItemCallback<PostalCode>() {
                override fun areItemsTheSame(
                    oldItem: PostalCode,
                    newItem: PostalCode
                ): Boolean {
                    return oldItem.code == newItem.code
                }

                override fun areContentsTheSame(
                    oldItem: PostalCode,
                    newItem: PostalCode
                ): Boolean = oldItem == newItem
            }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): PostalCodeViewHolder {
        return PostalCodeViewHolder.create(parent)
    }

    override fun onBindViewHolder(holder: PostalCodeViewHolder, position: Int) {
        val postalCode = getItem(position)
        if (postalCode != null) {
            holder.bind(postalCode)
        }

    }
}