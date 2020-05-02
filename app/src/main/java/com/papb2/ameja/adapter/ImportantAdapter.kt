package com.papb2.ameja.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.papb2.ameja.R
import com.papb2.ameja.model.Schedule
import kotlinx.android.synthetic.main.item_important_agenda.view.*

class ImportantAdapter :
        RecyclerView.Adapter<ImportantAdapter.CardViewViewHolder>() {

    var listSchedule = ArrayList<Schedule>()
    private var onItemClickCallback: OnItemClickCallback? = null

    fun setData(items: ArrayList<Schedule>) {
        listSchedule.clear()
        listSchedule.addAll(items)
        notifyDataSetChanged()
    }

    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }

    override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int
    ): CardViewViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_important_agenda, parent, false)
        return CardViewViewHolder(view)
    }

    override fun getItemCount(): Int = listSchedule.size

    override fun onBindViewHolder(holder: CardViewViewHolder, position: Int) {
        holder.bind(listSchedule[position])
    }

    inner class CardViewViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(schedule: Schedule) {
            with(itemView) {
                tvDate.text = schedule.date
                tvAgenda.text = schedule.agenda
                tvInfo.text = "${schedule.start}-${schedule.end} at ${schedule.location}"

                itemView.setOnClickListener { onItemClickCallback?.onItemClicked(schedule) }
            }

        }
    }

    interface OnItemClickCallback {
        fun onItemClicked(data: Schedule)
    }
}