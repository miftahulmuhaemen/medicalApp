package com.ega.medicalapp.ui.user.health.meditation.mediaplayer

import android.media.AudioAttributes
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.fragment.app.Fragment
import com.ega.medicalapp.R
import com.ega.medicalapp.data.model.ChannelEntity
import com.ega.medicalapp.data.model.MeditationEntity
import com.ega.medicalapp.ui.psychologist.progress.ProgressViewHolder
import com.ega.medicalapp.util.GlideApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_profile_user.*
import kotlinx.android.synthetic.main.fragment_health_meditation_mediaplayer.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.io.IOException
import kotlin.properties.Delegates

class MediaPlayerFragment : Fragment() {

    private lateinit var auth: FirebaseAuth
    private lateinit var storage: FirebaseStorage
    private var isReady: Boolean = false
    private lateinit var mMediaPlayer: MediaPlayer
    private var mediaFileLengthInMilliseconds = 0

    companion object {
        const val TAG_MP = "MP11"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = Firebase.auth
        storage = FirebaseStorage.getInstance()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_health_meditation_mediaplayer, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val meditationEntity: MeditationEntity = arguments?.getParcelable(TAG_MP)!!

        GlideApp.with(requireActivity())
            .load(storage.getReferenceFromUrl(meditationEntity.photo.toString()))
            .into(imgPhoto)

        storage.getReferenceFromUrl(meditationEntity.url.toString()).downloadUrl
            .addOnSuccessListener {
                init(it.toString())
            }

        btnPlay.isEnabled = false
        btnPlay.setOnClickListener {

            if (!isReady) {
                btnPlay.icon = AppCompatResources.getDrawable(
                    requireActivity(),
                    R.drawable.ic_pause
                )
                mMediaPlayer.prepareAsync()
            } else {
                if (mMediaPlayer.isPlaying) {
                    btnPlay.icon = AppCompatResources.getDrawable(
                        requireActivity(),
                        R.drawable.ic_play
                    )
                    mMediaPlayer.pause()
                    primarySeekBarProgressUpdater()
                } else {
                    btnPlay.icon = AppCompatResources.getDrawable(
                        requireActivity(),
                        R.drawable.ic_pause
                    )
                    mMediaPlayer.start()
                    primarySeekBarProgressUpdater()
                }
            }
        }

    }

    private fun init(uri: String) {
        mMediaPlayer = MediaPlayer()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val attribute = AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_MEDIA)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build()
            mMediaPlayer.setAudioAttributes(attribute)
        } else {
            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC)
        }

        try {
            mMediaPlayer.setDataSource(uri)
        } catch (e: IOException) {
            e.printStackTrace()
        }

        mMediaPlayer.setOnPreparedListener {
            isReady = true
            mediaFileLengthInMilliseconds = mMediaPlayer.duration
            btnPlay.isEnabled = true
        }
        mMediaPlayer.setOnErrorListener { _, _, _ -> false }

        mMediaPlayer.setOnBufferingUpdateListener { mediaPlayer, i ->
        }

        mMediaPlayer.prepareAsync()
    }

    private fun primarySeekBarProgressUpdater(){
        if (mMediaPlayer.isPlaying) {
            pbMedia.progress = (mMediaPlayer.currentPosition.toFloat() / mediaFileLengthInMilliseconds * 100).toInt()
            Looper.myLooper()?.let {
                Handler(it).postDelayed({
                    primarySeekBarProgressUpdater()
                }, 1000)
            }
        } else {
            pbMedia.progress = 100
            btnPlay.icon = AppCompatResources.getDrawable(
                requireActivity(),
                R.drawable.ic_play
            )
        }
    }

}