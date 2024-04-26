package com.example.giuaky1.Adapters


import android.annotation.SuppressLint
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.giuaky1.Models.SizeModel
import com.example.giuaky1.R

class SizeAdapter(private val sizeList: ArrayList<SizeModel>) :
    RecyclerView.Adapter<SizeAdapter.ViewHolder>() {
    var selectedSize: SizeModel? = null
        private set

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_size, parent, false)
        return ViewHolder(view)
    }

    @SuppressLint("SetTextI18n", "NotifyDataSetChanged")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val sizeModel = sizeList[position]
        holder.size.text = sizeModel.size
        holder.price.text = "+" + sizeModel.price + "đ"
        holder.itemView.setOnClickListener {
            selectedSize = sizeModel
            notifyDataSetChanged()
        }
        if (selectedSize === sizeModel) {
            (holder.itemView as CardView).setCardBackgroundColor(
                Color.parseColor(
                    holder.itemView.context.getString(
                        R.string.color_selected_size
                    )
                )
            )
        } else {
            (holder.itemView as CardView).setCardBackgroundColor(Color.WHITE)
        }
    }

    override fun getItemCount(): Int {
        return sizeList.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var size: TextView
        var price: TextView

        init {
            size = itemView.findViewById(R.id.textSizeName)
            price = itemView.findViewById(R.id.textSizePrice)
        }
    }
}