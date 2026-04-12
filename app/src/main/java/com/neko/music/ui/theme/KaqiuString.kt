package com.neko.music.ui.theme

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import com.neko.music.util.KaqiuLanguageUtil

/**
 * 卡丘语字符串资源扩展
 * 自动根据卡丘语模式转换文本
 */
@Composable
fun rememberKaqiuString(resourceId: Int): String {
    val context = LocalContext.current
    val originalText = context.getString(resourceId)
    return remember(KaqiuLanguageUtil.isKaqiuModeEnabled(context)) {
        KaqiuLanguageUtil.getText(context, originalText)
    }
}

/**
 * 卡丘语字符串资源扩展（带格式参数）
 */
@Composable
fun rememberKaqiuString(resourceId: Int, vararg formatArgs: Any): String {
    val context = LocalContext.current
    val originalText = context.getString(resourceId, *formatArgs)
    return remember(KaqiuLanguageUtil.isKaqiuModeEnabled(context), *formatArgs) {
        KaqiuLanguageUtil.getText(context, originalText)
    }
}

/**
 * Context扩展函数 - 获取卡丘语字符串
 */
fun Context.getKaqiuString(resourceId: Int): String {
    val originalText = this.getString(resourceId)
    return KaqiuLanguageUtil.getText(this, originalText)
}

/**
 * Context扩展函数 - 获取卡丘语字符串（带格式参数）
 */
fun Context.getKaqiuString(resourceId: Int, vararg formatArgs: Any): String {
    val originalText = this.getString(resourceId, *formatArgs)
    return KaqiuLanguageUtil.getText(this, originalText)
}

/**
 * Composable函数 - 获取卡丘语字符串资源
 * 用于在Composable中直接获取卡丘语字符串
 */
@Composable
fun kaqiuStringResource(resourceId: Int): String {
    val context = LocalContext.current
    return context.getKaqiuString(resourceId)
}

/**
 * Composable函数 - 获取卡丘语字符串资源（带格式参数）
 */
@Composable
fun kaqiuStringResource(resourceId: Int, vararg formatArgs: Any): String {
    val context = LocalContext.current
    return context.getKaqiuString(resourceId, *formatArgs)
}