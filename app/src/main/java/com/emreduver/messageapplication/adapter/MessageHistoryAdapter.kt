package com.emreduver.messageapplication.adapter

import android.location.Geocoder
import android.net.Uri
import android.text.SpannableStringBuilder
import android.text.style.ImageSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.MediaController
import androidx.databinding.DataBindingUtil
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.emreduver.messageapplication.R
import com.emreduver.messageapplication.constants.Api
import com.emreduver.messageapplication.databinding.FragmentMainScreenRecyclerBinding
import com.emreduver.messageapplication.entities.receive.message.MessageHistoryDto
import com.emreduver.messageapplication.entities.send.message.FileType
import com.emreduver.messageapplication.ui.main.MainScreenFragmentDirections
import com.emreduver.messageapplication.utilities.HelperService
import java.util.*
import kotlin.collections.ArrayList

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

        if(messageHistory[position].ReadStatus && messageHistory[position].Sender)
            holder.view.imageReadMessageHistory.visibility = View.VISIBLE

        when(messageHistory[position].FileType){
            FileType.NoFile.fileType -> {
                holder.view.textLastMessageHistory.text = messageHistory[position].MessageText
            }
            FileType.Image.fileType -> {
                holder.view.imageFileMessageHistory.setImageResource(R.drawable.ic_baseline_image_24)
                holder.view.textLastMessageHistory.text = "Resim"
                holder.view.imageFileMessageHistory.visibility = View.VISIBLE
            }
            FileType.Video.fileType -> {
                holder.view.imageFileMessageHistory.setImageResource(R.drawable.ic_baseline_video_library_24)
                holder.view.textLastMessageHistory.text = "Video"
                holder.view.imageFileMessageHistory.visibility = View.VISIBLE
            }
            FileType.File.fileType -> {
                holder.view.imageFileMessageHistory.setImageResource(R.drawable.ic_baseline_insert_drive_file_24)
                holder.view.textLastMessageHistory.text = "Dosya"
                holder.view.imageFileMessageHistory.visibility = View.VISIBLE
            }
            FileType.Audio.fileType -> {
                holder.view.imageFileMessageHistory.setImageResource(R.drawable.ic_baseline_audiotrack_24)
                holder.view.textLastMessageHistory.text = "Ses"
                holder.view.imageFileMessageHistory.visibility = View.VISIBLE
            }
            FileType.Location.fileType -> {
                holder.view.imageFileMessageHistory.setImageResource(R.drawable.ic_baseline_location_on_24)
                holder.view.textLastMessageHistory.text = "Konum"
                holder.view.imageFileMessageHistory.visibility = View.VISIBLE
            }
        }

        holder.view.cardViewMainScreenRecycler.setOnClickListener {
            val action = MainScreenFragmentDirections.actionMainScreenFragmentToMessageFragment(messageHistory[position].UserId,messageHistory[position].Firstname,messageHistory[position].Lastname,messageHistory[position].PhotoPath)
            Navigation.findNavController(it).navigate(action)
        }
    }

    override fun getItemCount(): Int {
        return messageHistory.size
    }
}