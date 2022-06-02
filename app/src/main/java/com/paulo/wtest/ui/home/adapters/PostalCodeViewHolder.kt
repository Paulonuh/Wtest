package com.paulo.wtest.ui.home.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.paulo.wtest.databinding.AdapterPostalCodeBinding
import com.paulo.wtest.model.postalcode.PostalCode


/**
 * Created by Paulo Henrique Teixeira.
 */

class PostalCodeViewHolder(private val binding: AdapterPostalCodeBinding) : RecyclerView.ViewHolder(binding.root) {

    companion object {
        fun create(parent: ViewGroup): PostalCodeViewHolder {
            val inflater = LayoutInflater.from(parent.context)
            val binding = AdapterPostalCodeBinding.inflate(inflater, parent, false)
            return PostalCodeViewHolder(binding)
        }
    }

    private var postalCode: PostalCode? = null

    fun bind(pCode: PostalCode?) {
        this.postalCode = pCode
        pCode?.let {
            val postalCode = "${it.code}-${it.ext_code}"
            binding.tvPostalCode.text = postalCode
            binding.tvName.text = pCode.name
        }
    }

}