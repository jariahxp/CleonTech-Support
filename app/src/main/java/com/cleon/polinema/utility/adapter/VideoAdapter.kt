package com.cleon.polinema.utility.adapter

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.MediaController
import android.widget.TextView
import android.widget.VideoView
import androidx.recyclerview.widget.RecyclerView
import com.cleon.polinema.R
import com.cleon.polinema.network.dataclass.VideoTutorial

class VideoAdapter(
    private val context: Context,
    private val videoList: List<VideoTutorial>
) : RecyclerView.Adapter<VideoAdapter.VideoViewHolder>() {

    inner class VideoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val videoTitle: TextView = itemView.findViewById(R.id.videoTitle)
        val videoView: VideoView = itemView.findViewById(R.id.videoView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VideoViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_tutorial, parent, false)
        return VideoViewHolder(view)
    }

    override fun onBindViewHolder(holder: VideoViewHolder, position: Int) {
        val videoData = videoList[position]
        holder.videoTitle.text = videoData.title

        // Set video URL ke VideoView
        holder.videoView.setVideoURI(Uri.parse(videoData.videoUrl))

        // MediaController untuk kontrol video (play, pause, dll.)
        val mediaController = MediaController(context)
        mediaController.setAnchorView(holder.videoView)
        holder.videoView.setMediaController(mediaController)

        // Video tidak langsung berputar, hanya berputar ketika pengguna menekan play
        holder.videoView.setOnClickListener {
            if (!holder.videoView.isPlaying) {
                holder.videoView.start() // Mulai video saat klik
            }
        }

        // Stop video saat item di-scroll keluar layar
        holder.videoView.setOnPreparedListener { mediaPlayer ->
            mediaPlayer.isLooping = false // Video tidak mengulang
        }
    }


    override fun getItemCount(): Int {
        return videoList.size
    }
}
