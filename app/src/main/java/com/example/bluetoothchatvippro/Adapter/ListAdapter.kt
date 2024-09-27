package com.example.bluetoothchatvippro.Adapter
import android.annotation.SuppressLint
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.bluetoothchatvippro.Model.Device
import com.example.bluetoothchatvippro.R

class ListAdapter(
    private val devices: ArrayList<Device>,
    private val itemClick: (String) -> Unit
) : RecyclerView.Adapter<ListAdapter.MyViewHolder>() {
    private var selectedPosition = -1
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_device, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, @SuppressLint("RecyclerView") position: Int) {
        val currentItem = devices[position]
        holder.tvName.text = currentItem.name
        holder.tvName.setOnClickListener {
            itemClick(currentItem.address)
            selectedPosition = position
            notifyDataSetChanged()
        }
        if (selectedPosition == position) {
            holder.itemView.setBackgroundColor(Color.BLUE)
        } else {
            holder.itemView.setBackgroundColor(Color.LTGRAY)
        }
    }

    override fun getItemCount() = devices.size

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvName: TextView = itemView.findViewById(R.id.itemTextView)
    }
}
