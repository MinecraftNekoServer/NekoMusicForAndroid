package com.neko.music.ui.components

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.neko.music.util.KaqiuLanguageUtil

/**
 * 卡丘语字符串资源Composable
 * 自动根据卡丘语模式转换文本
 * 用法：kaqiuStringResource(R.string.xxx)
 */
@Composable
fun kaqiuStringResource(id: Int): String {
    val context = LocalContext.current
    val originalText = stringResource(id)
    val isKaqiuMode = remember { KaqiuLanguageUtil.isKaqiuModeEnabled(context) }
    
    return remember(originalText, isKaqiuMode) {
        if (isKaqiuMode) {
            KaqiuLanguageUtil.toKaqiuText(originalText)
        } else {
            originalText
        }
    }
}

/**
 * 卡丘语字符串资源Composable（带格式参数）
 * 用法：kaqiuStringResource(R.string.xxx, arg1, arg2)
 */
@Composable
fun kaqiuStringResource(id: Int, vararg formatArgs: Any): String {
    val context = LocalContext.current
    val originalText = stringResource(id, *formatArgs)
    val isKaqiuMode = remember { KaqiuLanguageUtil.isKaqiuModeEnabled(context) }
    
    return remember(originalText, isKaqiuMode, *formatArgs) {
        if (isKaqiuMode) {
            KaqiuLanguageUtil.toKaqiuText(originalText)
        } else {
            originalText
        }
    }
}

/**
 * Context扩展函数 - 获取卡丘语字符串
 * 用法：context.kaqiuString(R.string.xxx)
 */
fun Context.kaqiuString(id: Int): String {
    val originalText = this.getString(id)
    return if (KaqiuLanguageUtil.isKaqiuModeEnabled(this)) {
        KaqiuLanguageUtil.toKaqiuText(originalText)
    } else {
        originalText
    }
}

/**
 * Context扩展函数 - 获取卡丘语字符串（带格式参数）
 * 用法：context.kaqiuString(R.string.xxx, arg1, arg2)
 */
fun Context.kaqiuString(id: Int, vararg formatArgs: Any): String {
    val originalText = this.getString(id, *formatArgs)
    return if (KaqiuLanguageUtil.isKaqiuModeEnabled(this)) {
        KaqiuLanguageUtil.toKaqiuText(originalText)
    } else {
        originalText
    }
}