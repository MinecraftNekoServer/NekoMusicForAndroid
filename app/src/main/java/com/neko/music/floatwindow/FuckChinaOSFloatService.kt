package com.neko.music.floatwindow

import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.PixelFormat
import android.os.Build
import android.os.IBinder
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.compose.ui.platform.LocalContext
import coil.load
import com.neko.music.R
import com.neko.music.service.MusicPlayerManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class FuckChinaOSFloatService : Service() {

    private var windowManager: WindowManager? = null
    private var floatView: View? = null
    private var isViewAdded = false
    
    private val serviceScope = CoroutineScope(Dispatchers.Main + Job())
    private var updateJob: Job? = null
    private var layoutParams: WindowManager.LayoutParams? = null

    companion object {
        const val ACTION_SHOW = "com.neko.music.action.SHOW_FLOAT"
        const val ACTION_HIDE = "com.neko.music.action.HIDE_FLOAT"
        const val ACTION_UPDATE = "com.neko.music.action.UPDATE_FLOAT"
        private var instance: FuckChinaOSFloatService? = null
        
        fun getInstance(): FuckChinaOSFloatService? {
            return instance
        }
    }

    override fun onCreate() {
        super.onCreate()
        android.util.Log.d("FuckChinaOSFloatService", "Service onCreate")
        instance = this
        createFloatView()
        showFloatView()
        startProgressUpdate()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_SHOW -> showFloatView()
            ACTION_HIDE -> hideFloatView()
            ACTION_UPDATE -> updateFloatView()
        }
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    private fun createFloatView() {
        windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager
        
        val layoutInflater = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        floatView = layoutInflater.inflate(R.layout.float_window_layout, null)
        
        // 设置点击事件
        val btnPlayPause = floatView?.findViewById<ImageButton>(R.id.float_play_pause)
        val btnPrevious = floatView?.findViewById<ImageButton>(R.id.float_previous)
        val btnNext = floatView?.findViewById<ImageButton>(R.id.float_next)
        val layoutFloat = floatView?.findViewById<LinearLayout>(R.id.float_layout)
        val infoLayout = floatView?.findViewById<LinearLayout>(R.id.float_info_layout)
        val playAnimation = floatView?.findViewById<PlayAnimationView>(R.id.float_play_animation)
        
        btnPlayPause?.setOnClickListener {
            val playerManager = MusicPlayerManager.getInstance(this)
            if (playerManager.isPlaying.value) {
                playerManager.pause()
            } else {
                playerManager.togglePlayPause()
            }
        }
        
        btnPrevious?.setOnClickListener {
            MusicPlayerManager.getInstance(this).previous()
        }
        
        btnNext?.setOnClickListener {
            MusicPlayerManager.getInstance(this).next()
        }

        // 点击整个悬浮窗打开应用
        layoutFloat?.setOnClickListener {
            android.util.Log.d("FuckChinaOSFloatService", "Layout clicked")
            val openIntent = Intent(this, com.neko.music.MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            }
            startActivity(openIntent)
        }
        
        // 确保按钮可以点击
        btnPlayPause?.isClickable = true
        btnPrevious?.isClickable = true
        btnNext?.isClickable = true
        layoutFloat?.isClickable = true
    }

    private fun showFloatView() {
        if (isViewAdded || floatView == null) return
        
        try {
            layoutParams = WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
                } else {
                    @Suppress("DEPRECATION")
                    WindowManager.LayoutParams.TYPE_PHONE
                },
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN or
                WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
                PixelFormat.TRANSLUCENT
            )
            
            layoutParams?.gravity = Gravity.TOP or Gravity.CENTER_HORIZONTAL
            layoutParams?.y = 80 // 距离顶部80像素
            
            // 直接显示
            floatView?.alpha = 1f
            floatView?.scaleX = 1f
            floatView?.scaleY = 1f
            
            // 确保所有子视图都可见
            floatView?.findViewById<LinearLayout>(R.id.float_info_layout)?.visibility = View.VISIBLE
            floatView?.findViewById<android.widget.ImageView>(R.id.float_cover)?.visibility = View.VISIBLE
            floatView?.findViewById<ImageButton>(R.id.float_previous)?.visibility = View.VISIBLE
            floatView?.findViewById<ImageButton>(R.id.float_play_pause)?.visibility = View.VISIBLE
            floatView?.findViewById<ImageButton>(R.id.float_next)?.visibility = View.VISIBLE
            floatView?.findViewById<LinearLayout>(R.id.float_controls_layout)?.visibility = View.VISIBLE
            
            windowManager?.addView(floatView, layoutParams)
            isViewAdded = true
            
            updateFloatView()
            android.util.Log.d("FuckChinaOSFloatService", "Float view added successfully")
        } catch (e: Exception) {
            android.util.Log.e("FuckChinaOSFloatService", "Error showing float view", e)
        }
    }

    private fun startProgressUpdate() {
        updateJob = serviceScope.launch {
            while (isActive) {
                updateFloatView()
                delay(500) // 每0.5秒更新一次
            }
        }
    }

    private fun hideFloatView() {
        if (!isViewAdded || floatView == null || windowManager == null) return
        
        try {
            windowManager?.removeView(floatView)
            isViewAdded = false
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun updateFloatView() {
        val playerManager = MusicPlayerManager.getInstance(this)
        
        val tvTitle = floatView?.findViewById<TextView>(R.id.float_title)
        val tvArtist = floatView?.findViewById<TextView>(R.id.float_artist)
        val btnPlayPause = floatView?.findViewById<ImageButton>(R.id.float_play_pause)
        val coverView = floatView?.findViewById<android.widget.ImageView>(R.id.float_cover)
        val playAnimation = floatView?.findViewById<PlayAnimationView>(R.id.float_play_animation)
        
        tvTitle?.text = playerManager.currentMusicTitle.value ?: "Neko云音乐"
        tvArtist?.text = playerManager.currentMusicArtist.value ?: "暂无播放"
        
        btnPlayPause?.setImageResource(
            if (playerManager.isPlaying.value) R.drawable.ic_widget_pause else R.drawable.ic_widget_play
        )

        // 更新播放动画
        if (playerManager.isPlaying.value) {
            playAnimation?.visibility = View.VISIBLE
            playAnimation?.setPlaying(true)
        } else {
            playAnimation?.visibility = View.INVISIBLE
            playAnimation?.setPlaying(false)
        }

        // 更新封面（如果有）
        val coverPath = playerManager.currentMusicCover.value
        if (coverPath != null && coverPath.isNotEmpty()) {
            if (coverPath.startsWith("http")) {
                // 网络URL，使用 Coil 加载
                coverView?.load(coverPath) {
                    placeholder(R.mipmap.ic_launcher)
                    error(R.mipmap.ic_launcher)
                    crossfade(true)
                }
            } else {
                // 本地路径，使用 Uri
                try {
                    coverView?.setImageURI(android.net.Uri.parse(coverPath))
                } catch (e: Exception) {
                    coverView?.setImageResource(R.mipmap.ic_launcher)
                }
            }
        } else {
            coverView?.setImageResource(R.mipmap.ic_launcher)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        hideFloatView()
        updateJob?.cancel()
        serviceScope.cancel()
        instance = null
    }
}