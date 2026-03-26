package com.neko.music

import android.app.Application
import coil.ImageLoader
import coil.ImageLoaderFactory

class NekoMusicApplication : Application(), ImageLoaderFactory {
    
    override fun newImageLoader(): ImageLoader {
        return ImageLoader.Builder(this)
            // 强制禁用所有磁盘缓存
            .diskCache(null)
            // 强制禁用所有内存缓存
            .memoryCache(null)
            // 禁用所有缓存策略
            .allowHardware(false)
            .build()
    }
}