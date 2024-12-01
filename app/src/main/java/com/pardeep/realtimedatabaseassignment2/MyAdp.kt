package com.pardeep.realtimedatabaseassignment2

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView

class MyAdp(var timeArray: ArrayList<DataClass>,
            var recyclerInterface: RecyclerInterface) : RecyclerView.Adapter<MyAdp.ViewHolder>() {
    class ViewHolder(var view : View) : RecyclerView.ViewHolder(view)  {
        var cardView : CardView = view.findViewById(R.id.cardView)
        var timeTv : TextView = view.findViewById(R.id.timeTv)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.custom_recycler_view,parent,false))
    }

    override fun getItemCount(): Int {
        return timeArray.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.cardView.setOnClickListener {
            recyclerInterface.onItemClick(position)
        }
        holder.timeTv.setText(timeArray[position].time)
    }

}
