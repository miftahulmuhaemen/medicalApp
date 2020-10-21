package com.ega.medicalapp.ui.user.health.meditation.mediaplayer

import android.media.AudioAttributes
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.*
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import com.ega.medicalapp.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.fragment_health_meditation_mediaplayer.*
import java.io.IOException

class MediaPlayerFragment : Fragment() {

    private lateinit var auth: FirebaseAuth
    private lateinit var storage: FirebaseStorage
    private var isReady: Boolean = false
    private lateinit var mMediaPlayer: MediaPlayer

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

        storage.getReferenceFromUrl("gs://medicalapp-e2fc9.appspot.com/Livin' Up (Sting) - Otis McDonald.mp3").downloadUrl
            .addOnSuccessListener {
                init(it.toString())
            }

        btnPlay.setOnClickListener {

            if (!isReady) {
                btnPlay.icon = AppCompatResources.getDrawable(requireActivity(),R.drawable.ic_pause)
                mMediaPlayer.prepareAsync()
            } else {
                if (mMediaPlayer.isPlaying as Boolean) {
                    btnPlay.icon = AppCompatResources.getDrawable(requireActivity(),R.drawable.ic_play)
                    mMediaPlayer.pause()
                } else {
                    btnPlay.icon = AppCompatResources.getDrawable(requireActivity(),R.drawable.ic_pause)
                    mMediaPlayer.start()
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
            mMediaPlayer.start()
        }
        mMediaPlayer.setOnErrorListener { _, _, _ -> false }

        mMediaPlayer.setOnBufferingUpdateListener { mediaPlayer, i ->

        }
    }

}