package com.example.giuaky1.Adapters


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.giuaky1.R

class BannerAdapter(private val bannerImages: List<String>) :
    RecyclerView.Adapter<BannerAdapter.BannerViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BannerViewHolder {
        return BannerViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_banner, parent, false)
        )
    }

    override fun onBindViewHolder(holder: BannerViewHolder, position: Int) {
        Glide.with(holder.itemView.context).load(bannerImages[position])
            .into(holder.imageViewBanner)
    }

    override fun getItemCount(): Int {
        return bannerImages.size
    }

    class BannerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageViewBanner: ImageView

        init {
            imageViewBanner = itemView.findViewById(R.id.banner_product_image)
        }
    }
}