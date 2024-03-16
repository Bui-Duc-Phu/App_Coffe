package com.example.giuaky1.Administrator.Adapters

import android.content.Context
import android.opengl.Visibility
import android.util.SparseBooleanArray
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.giuaky1.Models.Order
import com.example.giuaky1.R
import com.example.giuaky1.databinding.ItemOrderManagerAdminBinding
import android.widget.PopupMenu
import androidx.core.util.isEmpty

class OrderListAdapter (
   private val context:Context,
   private var list:List<Order>,
   private var showIconToolbarMenu : (Boolean) -> Unit

):RecyclerView.Adapter<OrderListAdapter.viewholer>(){
    lateinit var binding: ItemOrderManagerAdminBinding



    private var isEnable = false
    private val selectedItems = SparseBooleanArray()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): viewholer {
       binding= ItemOrderManagerAdminBinding.inflate(LayoutInflater.from(parent.context),parent,false )
        return viewholer(binding.root)
    }


    override fun onBindViewHolder(holder: viewholer, position: Int) {
        val model= list[position]
        holder.orderID.text = model.orderID
        holder.dateAndTime.text = "${model.day} - ${model.time}"
        when{
            model.checkout.equals("0") ->{
                if(model.state.equals("2")){
                    Glide.with(context).load(R.drawable.delete_icon).into(holder.iconStatus)
                    holder.status.text = "Đơn hàng đã bị huỷ"
                    holder.status.setTextColor(ContextCompat.getColor(context, R.color.red_delete))
                }
                Glide.with(context).load(R.drawable.checkout_list).into(holder.iconStatus)
                holder.status.text = "Đơn hàng chưa xác thực !"
                holder.status.setTextColor(ContextCompat.getColor(context, R.color.black))
            }
            else ->{
                when{
                    model.state.equals("1") -> {
                        Glide.with(context).load(R.drawable.tick_green).into(holder.iconStatus)
                        holder.status.text = "Đơn đã hoàn thành"
                        holder.status.setTextColor(ContextCompat.getColor(context, R.color.Xanhla))
                    }

                    model.state.equals("0") -> {
                        Glide.with(context).load(R.drawable.car_ship).into(holder.iconStatus)
                        holder.status.text = "Đơn Chưa hoàn thành"
                        holder.status.setTextColor(ContextCompat.getColor(context, R.color.Xanhduong))
                    }
                    else->{
                        Glide.with(context).load(R.drawable.delete_icon).into(holder.iconStatus)
                        holder.status.text = "Đơn hàng đã bị huỷ"
                        holder.status.setTextColor(ContextCompat.getColor(context, R.color.red_delete))
                    }


                }
            }
        }
        holder.category.setOnClickListener {
            showPopupMenu(holder.category)
        }





        holder.itemView.setOnLongClickListener {
            selectItem(holder,model,position)
            true
        }

        holder.itemView.setOnClickListener {
            if (selectedItems.get(position, false)){
                selectedItems.delete(position)
                holder.checkBox.visibility = View.GONE
                if(selectedItems.isEmpty()){
                    showIconToolbarMenu(false)
                    isEnable =false
                }

            }else if (isEnable){
                selectItem(holder,model,position)
            }
        }







    }



    private fun selectItem(holder: OrderListAdapter.viewholer, model: Order, position: Int) {
        isEnable=true
        selectedItems.put(position, true)
        holder.checkBox.visibility = View.VISIBLE
        holder.checkBox.isChecked=true
        showIconToolbarMenu(true)

    }


    override fun getItemCount(): Int {
       return  list.size
    }

    private fun showPopupMenu(view: View) {
        val popupMenu = PopupMenu(context, view)
        popupMenu.inflate(R.menu.category_list_order_item)
        popupMenu.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.menu_delete -> {
                    true
                }
                R.id.menu_checkout -> {
                    // Xử lý khi người dùng chọn Edit
                    true
                }
                R.id.menu_details -> {
                    // Xử lý khi người dùng chọn Chi tiết
                    true
                }
                else -> false
            }
        }
        popupMenu.show()
    }


    inner class viewholer(view: View) : RecyclerView.ViewHolder(view){
        var orderID = binding.orderID
        var status= binding.orderStatus
        var dateAndTime = binding.dateAnhTime
        var iconStatus = binding.iconStatus
        var category = binding.category
        var checkBox = binding.checkbox


    }
}