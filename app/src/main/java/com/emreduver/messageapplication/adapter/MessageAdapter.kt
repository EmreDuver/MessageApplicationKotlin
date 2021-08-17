package com.emreduver.messageapplication.adapter

import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.emreduver.messageapplication.R
import com.emreduver.messageapplication.databinding.FragmentMessageRecyclerBinding
import com.emreduver.messageapplication.entities.receive.message.Message
import com.emreduver.messageapplication.services.api.MessageService
import com.emreduver.messageapplication.utilities.HelperService
import kotlinx.coroutines.runBlocking
import java.util.*

class MessageAdapter(var messageList: ArrayList<Message>) : RecyclerView.Adapter<MessageAdapter.MessageViewModel>() {

    class MessageViewModel(var view: FragmentMessageRecyclerBinding): RecyclerView.ViewHolder(view.root){}

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewModel {
        val inflater = LayoutInflater.from(parent.context)
        val view = DataBindingUtil.inflate<FragmentMessageRecyclerBinding>(inflater, R.layout.fragment_message_recycler, parent,false)
        return MessageViewModel(view)
    }

    override fun onBindViewHolder(holder: MessageViewModel, position: Int) {
        holder.view.message = messageList[position]

        holder.view.textMessageIncoming.visibility = View.GONE
        holder.view.textMessageSent.visibility = View.GONE

        if(messageList[position].SenderUserId == HelperService.getTokenSharedPreference()!!.UserId){
            holder.view.textMessageSent.visibility = View.VISIBLE
        }else{
            holder.view.textMessageIncoming.visibility = View.VISIBLE
            if(messageList[position].status == 0){

                Handler().post(Runnable {
                    runBlocking {
                        val result = MessageService.updateMessageStatus(messageList[position].MessageId)
                        if (result.Success){
                            messageList[position].status = 1
                            messageList[position].ReadDateUnix = System.currentTimeMillis() * 1000
                            Log.i("Konsol", System.currentTimeMillis().toString())
                            notifyDataSetChanged()
                        }
                    }
                })
            }
        }

        holder.view.MessageSentDate.visibility = View.GONE

        holder.view.textMessageSent.setOnClickListener{
            if(holder.view.MessageSentDate.text.isNullOrEmpty()){
                holder.view.MessageSentDate.text = sendDate(messageList[position].SendDateUnix)
                holder.view.MessageSentDate.setCompoundDrawablesWithIntrinsicBounds(R.drawable.baseline_check_24,0,0,0)
                holder.view.MessageSentDate.visibility = View.VISIBLE
            }else{
                holder.view.MessageSentDate.text = null
                holder.view.MessageSentDate.visibility = View.GONE
            }
        }

        holder.view.textMessageIncoming.setOnClickListener{
            if(holder.view.MessageReceiveDate.text.isNullOrEmpty()){
                holder.view.MessageReceiveDate.text = sendDate(messageList[position].SendDateUnix)
                holder.view.MessageReceiveDate.setCompoundDrawablesWithIntrinsicBounds(R.drawable.baseline_check_24,0,0,0)
                holder.view.MessageReceiveDate.visibility = View.VISIBLE
            }else{
                holder.view.MessageReceiveDate.text = null
                holder.view.MessageReceiveDate.visibility = View.GONE
                holder.view.MessageReadDate.text = null
                holder.view.MessageReadDate.visibility = View.GONE
            }
        }

        holder.view.textMessageIncoming.setOnLongClickListener {
            if(holder.view.MessageReadDate.text.isNullOrEmpty()){
                holder.view.MessageReadDate.text = sendDate(messageList[position].ReadDateUnix)
                holder.view.MessageReadDate.setCompoundDrawablesWithIntrinsicBounds(R.drawable.baseline_done_all_24,0,0,0)
                holder.view.MessageReadDate.visibility = View.VISIBLE
                true
            }else{
                holder.view.MessageReceiveDate.text = null
                holder.view.MessageReceiveDate.visibility = View.GONE
                holder.view.MessageReadDate.text = null
                holder.view.MessageReadDate.visibility = View.GONE
                true
            }
        }
    }

    override fun getItemCount(): Int {
        return messageList.size
    }

    fun updateMessageList(newMessageList: List<Message>){
        messageList.clear()
        messageList.addAll(newMessageList)
        notifyDataSetChanged()
    }

    private fun sendDate(unixTime: Long):String{
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = unixTime*1000
        calendar.timeZone = TimeZone.getDefault()
        return "${calendar.get(Calendar.DAY_OF_MONTH)}/${calendar.get(Calendar.MONTH)+1}/${calendar.get(Calendar.YEAR)} ${calendar.get(Calendar.HOUR_OF_DAY)}:${calendar.get(Calendar.MINUTE)}"
    }
}