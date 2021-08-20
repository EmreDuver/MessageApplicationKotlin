package com.emreduver.messageapplication.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.emreduver.messageapplication.R
import com.emreduver.messageapplication.databinding.FragmentMainScreenRecyclerBinding
import com.emreduver.messageapplication.entities.receive.message.MessageHistoryDto
import com.emreduver.messageapplication.ui.main.MainScreenFragmentDirections
import com.emreduver.messageapplication.utilities.HelperService

class MessageHistoryAdapter(var messageHistory: ArrayList<MessageHistoryDto>) : RecyclerView.Adapter<MessageHistoryAdapter.MessageHistoryViewModel>() {

    class MessageHistoryViewModel(var view: FragmentMainScreenRecyclerBinding): RecyclerView.ViewHolder(view.root){}

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageHistoryViewModel {
        val inflater = LayoutInflater.from(parent.context)
        val view = DataBindingUtil.inflate<FragmentMainScreenRecyclerBinding>(inflater, R.layout.fragment_main_screen_recycler, parent, false)
        return MessageHistoryViewModel(view)
    }

    override fun onBindViewHolder(holder: MessageHistoryViewModel, position: Int) {
        holder.view.messageHistory = messageHistory[position]
        HelperService.loadImageFromPicasso(messageHistory[position].PhotoPath, holder.view.imageProfileMainScreenRecycler)
        holder.view.cardViewMainScreenRecycler.setOnClickListener {
            val action = MainScreenFragmentDirections.actionMainScreenFragmentToMessageFragment(messageHistory[position].UserId,messageHistory[position].Firstname,messageHistory[position].Lastname,messageHistory[position].PhotoPath)
            Navigation.findNavController(it).navigate(action)
        }
    }

    override fun getItemCount(): Int {
        return messageHistory.size
    }
}