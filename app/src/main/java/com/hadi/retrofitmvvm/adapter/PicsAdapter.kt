package com.hadi.retrofitmvvm.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import coil.api.load
import com.hadi.retrofitmvvm.R
import com.hadi.retrofitmvvm.model.DataItem
import kotlinx.android.synthetic.main.item_pics.view.*

class PicsAdapter : RecyclerView.Adapter<PicsAdapter.PicsViewHolder>() {

    inner class PicsViewHolder(itemView: View): RecyclerView.ViewHolder(itemView)

    private val differCallback = object : DiffUtil.ItemCallback<DataItem>() {
        override fun areItemsTheSame(oldItem: DataItem, newItem: DataItem): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: DataItem, newItem: DataItem): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, differCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = PicsViewHolder(
        LayoutInflater.from(parent.context).inflate(
            R.layout.item_pics,
            parent,
            false
        )
    )
    override fun getItemCount() =  differ.currentList.size

    override fun onBindViewHolder(holder: PicsViewHolder, position: Int) {
        val picItem = differ.currentList[position]
        holder.itemView.apply {
            ivImage.load(picItem.download_url)
            tvImageId.text = picItem.id
            tvImageSize.text = "${picItem.width} x ${picItem.height}"
            tvImageAuthor.text = picItem.author
        }
    }
}