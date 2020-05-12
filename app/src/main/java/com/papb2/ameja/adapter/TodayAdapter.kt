package com.papb2.ameja.adapter

import android.graphics.Color
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.recyclerview.widget.RecyclerView
import com.papb2.ameja.R
import com.papb2.ameja.model.Schedule
import kotlinx.android.synthetic.main.item_today_schedule.view.*

class TodayAdapter :
        RecyclerView.Adapter<TodayAdapter.CardViewViewHolder>() {

    var listSchedule = ArrayList<Schedule>()
    private var onCheckedCallback: OnCheckedCallback? = null

    fun setData(items: ArrayList<Schedule>) {
        listSchedule.clear()
        listSchedule.addAll(items)
        notifyDataSetChanged()
    }

    fun setOnCheckedCallback(onCheckedCallback: OnCheckedCallback) {
        this.onCheckedCallback = onCheckedCallback
    }

    override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int
    ): CardViewViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_today_schedule, parent, false)
        return CardViewViewHolder(view)
    }

    override fun getItemCount(): Int = listSchedule.size

    override fun onBindViewHolder(holder: CardViewViewHolder, position: Int) {
        holder.bind(listSchedule[position])
    }

    inner class CardViewViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(schedule: Schedule) {
            with(itemView) {
                tvAgenda.text = schedule.agenda
                tvInfo.text = "${schedule.start}-${schedule.end}${context.getString(R.string.at)}${schedule.location}"
                if (schedule.status > 0) {
                    checkBox.isChecked = true
                    tvAgenda.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
                    tvInfo.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
                    tvAgenda.setTextColor(Color.GRAY)
                    tvInfo.setTextColor(Color.GRAY)
                }
                checkBox.setOnCheckedChangeListener { _, isChecked ->  onCheckedCallback?.onChecked(itemView, schedule, isChecked) }
            }

        }
    }

    interface OnCheckedCallback {
        fun onChecked(itemView: View, data: Schedule, isChecked: Boolean)
    }
}