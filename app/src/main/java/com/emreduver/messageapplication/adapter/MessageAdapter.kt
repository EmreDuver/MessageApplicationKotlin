package com.emreduver.messageapplication.adapter

import android.app.DownloadManager
import android.content.Context
import android.content.Context.DOWNLOAD_SERVICE
import android.content.Intent
import android.location.Geocoder
import android.media.MediaPlayer
import android.net.Uri
import android.os.Environment
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.CookieManager
import android.widget.MediaController
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.emreduver.messageapplication.R
import com.emreduver.messageapplication.constants.Api
import com.emreduver.messageapplication.databinding.FragmentMessageRecyclerBinding
import com.emreduver.messageapplication.entities.receive.message.Message
import com.emreduver.messageapplication.entities.send.message.FileType
import com.emreduver.messageapplication.services.api.MessageService
import com.emreduver.messageapplication.utilities.HelperService
import com.google.android.material.slider.Slider
import kotlinx.coroutines.runBlocking
import java.util.*


class MessageAdapter(var messageList: ArrayList<Message>) : RecyclerView.Adapter<MessageAdapter.MessageViewModel>() {
    private var mediaPlayer: MediaPlayer? = null
    class MessageViewModel(var view: FragmentMessageRecyclerBinding): RecyclerView.ViewHolder(view.root){}

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewModel {
        val inflater = LayoutInflater.from(parent.context)
        val view = DataBindingUtil.inflate<FragmentMessageRecyclerBinding>(inflater, R.layout.fragment_message_recycler, parent, false)
        return MessageViewModel(view)
    }

    override fun onBindViewHolder(holder: MessageViewModel, position: Int) {
        holder.view.message = messageList[position]

        holder.view.cardViewMessageIncoming.visibility = View.GONE
        holder.view.cardViewMessageSent.visibility = View.GONE

        if(messageList[position].SenderUserId == HelperService.getTokenSharedPreference()!!.UserId){
            holder.view.cardViewMessageSent.visibility = View.VISIBLE
            if (!messageList[position].IsFile){
                holder.view.MessageSentText.visibility = View.VISIBLE
            }else{
                when(messageList[position].FileType){
                    FileType.Image.fileType -> {
                        HelperService.loadImageForMessageFromPicasso(messageList[position].FilePath!!, holder.view.MessageSentImage)
                        holder.view.MessageSentImage.visibility = View.VISIBLE
                    }
                    FileType.Video.fileType -> {
                        holder.view.MessageSentVideo.setVideoURI(Uri.parse("${Api.baseUrl}/${messageList[position].FilePath}"))
                        val control = MediaController(holder.view.MessageSentVideo.context)
                        control.setMediaPlayer( holder.view.MessageSentVideo)
                        holder.view.MessageSentVideo.setMediaController(control)
                        holder.view.MessageSentVideo.visibility = View.VISIBLE
                    }
                    FileType.File.fileType -> {
                        val fileName = messageList[position].FileName
                        val fileExtension = messageList[position].FilePath?.substringAfterLast('.', "")
                        holder.view.MessageSentFileName.text = "$fileName.$fileExtension"
                        holder.view.MessageSentFile.visibility = View.VISIBLE
                    }
                    FileType.Audio.fileType -> {
                        holder.view.cardViewMessageSentAudio.visibility = View.VISIBLE
                    }
                    FileType.Location.fileType -> {
                        val geocoder = Geocoder(holder.view.cardViewMessageSentLocation.context, Locale.getDefault())
                        val addresses = geocoder.getFromLocation(messageList[position].Latitude!!, messageList[position].Longitude!!, 1)
                        holder.view.textMessageSentLocation.text = addresses[0].getAddressLine(0).toString()
                        holder.view.cardViewMessageSentLocation.visibility = View.VISIBLE
                    }
                }
            }
        }else{
            holder.view.cardViewMessageIncoming.visibility = View.VISIBLE
            if (!messageList[position].IsFile){
                holder.view.MessageReceiveText.visibility = View.VISIBLE
            }else{
                when(messageList[position].FileType){
                    FileType.Image.fileType -> {
                        HelperService.loadImageForMessageFromPicasso(messageList[position].FilePath!!, holder.view.MessageReceiveImage)
                        holder.view.MessageReceiveImage.visibility = View.VISIBLE
                    }
                    FileType.Video.fileType -> {
                        holder.view.MessageReceiveVideo.setVideoURI(Uri.parse("${Api.baseUrl}/${messageList[position].FilePath}"))
                        val control = MediaController(holder.view.MessageReceiveVideo.context)
                        control.setMediaPlayer( holder.view.MessageReceiveVideo)
                        holder.view.MessageReceiveVideo.setMediaController(control)
                        holder.view.MessageReceiveVideo.visibility = View.VISIBLE
                    }
                    FileType.File.fileType -> {
                        val fileName = messageList[position].FileName
                        val fileExtension = messageList[position].FilePath?.substringAfterLast('.', "")
                        holder.view.MessageReceiveFileName.text = "$fileName.$fileExtension"
                        holder.view.MessageReceiveFile.visibility = View.VISIBLE
                    }
                    FileType.Audio.fileType -> {
                        holder.view.cardViewMessageReceiveAudio.visibility = View.VISIBLE
                    }
                    FileType.Location.fileType -> {
                        val geocoder = Geocoder(holder.view.cardViewMessageReceiveLocation.context, Locale.getDefault())
                        val addresses = geocoder.getFromLocation(messageList[position].Latitude!!, messageList[position].Longitude!!, 1)
                        holder.view.textMessageReceiveLocation.text = addresses[0].getAddressLine(0).toString()
                        holder.view.cardViewMessageReceiveLocation.visibility = View.VISIBLE
                    }
                }
            }
            if(messageList[position].status == 0){
                Handler().post(Runnable {
                    runBlocking {
                        val result =
                            MessageService.updateMessageStatus(messageList[position].MessageId)
                        if (result.Success) {
                            messageList[position].status = 1
                            messageList[position].ReadDateUnix = System.currentTimeMillis() * 1000
                            Log.i("Konsol", System.currentTimeMillis().toString())
                            notifyDataSetChanged()
                        }
                    }
                })
            }
        }

        holder.view.cardViewMessageSentLocation.setOnClickListener{
            val latitude = messageList[position].Latitude
            val longitude = messageList[position].Longitude
            val uriBegin = "geo:$latitude,$longitude"
            val query = "$latitude,$longitude"
            val encodedQuery = Uri.encode(query)
            val uriString = "$uriBegin?q=$encodedQuery&z=16"
            val uri = Uri.parse(uriString)
            val intent = Intent(Intent.ACTION_VIEW, uri);
            holder.view.cardViewMessageSentLocation.context.startActivity(intent)
        }

        holder.view.cardViewMessageReceiveLocation.setOnClickListener{
            val latitude = messageList[position].Latitude
            val longitude = messageList[position].Longitude
            val uriBegin = "geo:$latitude,$longitude"
            val query = "$latitude,$longitude"
            val encodedQuery = Uri.encode(query)
            val uriString = "$uriBegin?q=$encodedQuery&z=16"
            val uri = Uri.parse(uriString)
            val intent = Intent(Intent.ACTION_VIEW, uri);
            holder.view.cardViewMessageReceiveLocation.context.startActivity(intent)
        }

        holder.view.btnMessageSentAudioStop.setOnClickListener {
            if (mediaPlayer!=null){
                mediaPlayer?.stop()
                mediaPlayer?.reset()
                mediaPlayer?.release()
                mediaPlayer = null
            }
            holder.view.btnMessageSentAudioPlay.visibility = View.VISIBLE
            holder.view.btnMessageSentAudioStop.visibility = View.GONE
        }

        holder.view.btnMessageReceiveAudioStop.setOnClickListener {
            if (mediaPlayer!=null){
                mediaPlayer?.stop()
                mediaPlayer?.reset()
                mediaPlayer?.release()
                mediaPlayer = null
            }
            holder.view.btnMessageReceiveAudioPlay.visibility = View.VISIBLE
            holder.view.btnMessageReceiveAudioStop.visibility = View.GONE
        }

        holder.view.btnMessageSentAudioPlay.setOnClickListener {
            holder.view.btnMessageSentAudioPlay.visibility = View.GONE
            holder.view.btnMessageSentAudioStop.visibility = View.VISIBLE
            mediaPlayer=null
            val uri = Uri.parse("${Api.baseUrl}/${messageList[position].FilePath}")
            mediaPlayer = MediaPlayer.create(holder.view.cardViewMessageSentAudio.context,uri)
            initialiseSlider(holder.view.sliderMessageSentAudio)
            val duration = mediaPlayer?.duration!!
            mediaPlayer?.start()
            val seconds = (duration/1000)
            val minute =  kotlin.math.ceil((seconds/60).toDouble()).toInt()
            val second = (seconds - (minute*60))

            var minuteStr = minute.toString()
            var secondStr = second.toString()

            if (minuteStr.length ==1)
                minuteStr = "0$minuteStr"

            if (secondStr.length ==1)
                secondStr = "0$secondStr"

            holder.view.textMessageSentAudioDuration.text = "$minuteStr:$secondStr"

            Log.i("OkHttp",seconds.toString())
            Log.i("OkHttp",minuteStr)
            Log.i("OkHttp",secondStr)

            holder.view.sliderMessageSentAudio.addOnChangeListener { slider, value, fromUser ->
                mediaPlayer?.seekTo(value.toInt())
            }
        }

        holder.view.btnMessageReceiveAudioPlay.setOnClickListener {
            holder.view.btnMessageReceiveAudioPlay.visibility = View.GONE
            holder.view.btnMessageReceiveAudioStop.visibility = View.VISIBLE
            mediaPlayer=null
            val uri = Uri.parse("${Api.baseUrl}/${messageList[position].FilePath}")
            mediaPlayer = MediaPlayer.create(holder.view.cardViewMessageReceiveAudio.context,uri)
            initialiseSlider(holder.view.sliderMessageReceiveAudio)
            val duration = mediaPlayer?.duration!!
            mediaPlayer?.start()
            val seconds = (duration/1000)
            val minute =  kotlin.math.ceil((seconds/60).toDouble()).toInt()
            val second = (seconds - (minute*60))

            var minuteStr = minute.toString()
            var secondStr = second.toString()

            if (minuteStr.length ==1)
                minuteStr = "0$minuteStr"

            if (secondStr.length ==1)
                secondStr = "0$secondStr"

            holder.view.textMessageReceiveAudioDuration.text = "$minuteStr:$secondStr"

            Log.i("OkHttp",seconds.toString())
            Log.i("OkHttp",minuteStr)
            Log.i("OkHttp",secondStr)

            holder.view.sliderMessageReceiveAudio.addOnChangeListener { slider, value, fromUser ->
                mediaPlayer?.seekTo(value.toInt())
            }
        }

        holder.view.MessageSentFileOpen.setOnClickListener {
            openFile(Uri.parse("${Api.baseUrl}/${messageList[position].FilePath}"),holder.view.MessageSentFile.context)
        }
        holder.view.MessageReceiveFileOpen.setOnClickListener {
            openFile(Uri.parse("${Api.baseUrl}/${messageList[position].FilePath}"),holder.view.MessageReceiveFile.context)
        }

        holder.view.MessageSentFileDownload.setOnClickListener {
            downloadFile("${Api.baseUrl}/${messageList[position].FilePath}",messageList[position].FileName.toString(),holder.view.MessageSentFile.context)
        }
        holder.view.MessageReceiveFileDownload.setOnClickListener {
            downloadFile("${Api.baseUrl}/${messageList[position].FilePath}",messageList[position].FileName.toString(),holder.view.MessageReceiveFile.context)
        }

        holder.view.MessageSentDate.visibility = View.GONE

        holder.view.cardViewMessageSent.setOnClickListener{
            if(holder.view.MessageSentDate.text.isNullOrEmpty()){
                holder.view.MessageSentDate.text = sendDate(messageList[position].SendDateUnix)
                holder.view.MessageSentDate.setCompoundDrawablesWithIntrinsicBounds(R.drawable.baseline_check_24, 0, 0, 0)
                holder.view.MessageSentDate.visibility = View.VISIBLE
            }else{
                holder.view.MessageSentDate.text = null
                holder.view.MessageSentDate.visibility = View.GONE
            }
        }

        holder.view.cardViewMessageIncoming.setOnClickListener{
            if(holder.view.MessageReceiveDate.text.isNullOrEmpty()){
                holder.view.MessageReceiveDate.text = sendDate(messageList[position].SendDateUnix)
                holder.view.MessageReceiveDate.setCompoundDrawablesWithIntrinsicBounds(R.drawable.baseline_check_24, 0, 0, 0)
                holder.view.MessageReceiveDate.visibility = View.VISIBLE
            }else{
                holder.view.MessageReceiveDate.text = null
                holder.view.MessageReceiveDate.visibility = View.GONE
                holder.view.MessageReadDate.text = null
                holder.view.MessageReadDate.visibility = View.GONE
            }
        }

        holder.view.cardViewMessageIncoming.setOnLongClickListener {
            if(holder.view.MessageReadDate.text.isNullOrEmpty()){
                holder.view.MessageReadDate.text = sendDate(messageList[position].ReadDateUnix)
                holder.view.MessageReadDate.setCompoundDrawablesWithIntrinsicBounds(R.drawable.baseline_done_all_24, 0, 0, 0)
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

    private fun openFile(it:Uri,context:Context) {
        try {
            val mime = context.contentResolver.getType(it)
            val intent = Intent()
            intent.action = Intent.ACTION_VIEW
            intent.setDataAndType(it, mime)
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            context.startActivity(intent)
        }catch (e:Exception){
            Toast.makeText(context,"Dosya açılamıyor!", Toast.LENGTH_LONG).show()
        }
    }

    private fun downloadFile(url:String,title:String,context: Context){
        try {
            val request = DownloadManager.Request(Uri.parse(url))
            request.setTitle(title)
            request.setDescription("Dosyanız indiriliyor.")
            val cookie = CookieManager.getInstance().getCookie(url)
            request.addRequestHeader("cookie",cookie)
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS,title)
            val downloadManager : DownloadManager = context.getSystemService(DOWNLOAD_SERVICE) as DownloadManager
            downloadManager.enqueue(request)
            Toast.makeText(context,"İndirme en kısa sürede başlayacak!", Toast.LENGTH_SHORT).show()
        }catch (e:Exception){
            Toast.makeText(context,e.message.toString(), Toast.LENGTH_SHORT).show()
        }
    }

    private fun initialiseSlider(slider:Slider){
        slider.valueTo= mediaPlayer!!.duration.toFloat()

        var handler = Handler()
        handler.postDelayed(object:Runnable {
            override fun run(){
                try {
                    slider.value = mediaPlayer!!.currentPosition.toFloat()
                    handler.postDelayed(this,1000)
                }catch (e:Exception){
                    slider.value = 0f
                }
            }
        },0)
    }
    fun clearData() {
        messageList.clear()
        notifyDataSetChanged()
    }
}