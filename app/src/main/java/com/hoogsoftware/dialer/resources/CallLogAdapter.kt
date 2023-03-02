package com.hoogsoftware.dialer.resources


import android.content.Context
import android.content.res.Resources
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.MarginLayoutParams
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.hoogsoftware.dialer.R


class CallLogAdapter(context: Context, callLogModelArrayList: ArrayList<CallLogModel>?) :
    RecyclerView.Adapter<CallLogAdapter.MyViewHolder>() {
    private var px = 0
    var context: Context
    var callLogModelArrayList: ArrayList<CallLogModel>?

    init {
        this.context = context
        this.callLogModelArrayList = callLogModelArrayList
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val r: Resources = parent.resources
        px = Math.round(
            TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 8f, r.getDisplayMetrics()
            )
        )
        val v: View = LayoutInflater.from(context).inflate(R.layout.log, parent, false)
        return MyViewHolder(v)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        if (position == 0) {
            val layoutParams = holder.cardView.layoutParams as MarginLayoutParams
            layoutParams.topMargin = px
            holder.cardView.requestLayout()
        }
        val currentLog: CallLogModel = callLogModelArrayList!![position]
        holder.tv_ph_num.setText(currentLog.phNumber)
        holder.tv_contact_name.setText(currentLog.contactName)
        holder.tv_call_type.setText(currentLog.callType)
        holder.tv_call_date.setText(currentLog.callDate)
        holder.tv_call_time.setText(currentLog.callTime)
        holder.tv_call_duration.setText(currentLog.callDuration)
    }

    override fun getItemCount(): Int {
        return if (callLogModelArrayList == null) 0 else callLogModelArrayList!!.size
    }

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var cardView: CardView
        var tv_ph_num: TextView
        var tv_contact_name: TextView
        var tv_call_type: TextView
        var tv_call_date: TextView
        var tv_call_time: TextView
        var tv_call_duration: TextView

        init {
            tv_ph_num = itemView.findViewById(R.id.layout_call_log_ph_no)
            tv_contact_name = itemView.findViewById(R.id.layout_call_log_contact_name)
            tv_call_type = itemView.findViewById(R.id.layout_call_log_type)
            tv_call_date = itemView.findViewById(R.id.layout_call_log_date)
            tv_call_time = itemView.findViewById(R.id.layout_call_log_time)
            tv_call_duration = itemView.findViewById(R.id.layout_call_log_duration)
            cardView = itemView.findViewById(R.id.layout_call_log_cardview)
        }
    }
}