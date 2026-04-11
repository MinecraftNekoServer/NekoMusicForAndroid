package com.neko.music.ui.components

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import com.neko.music.util.KaqiuLanguageUtil

/**
 * 卡丘语文本组件（完整版）
 * 根据卡丘语模式设置自动将文本转换为卡丘语风格
 * 
 * 使用示例：
 * KaqiuText(text = "这是一个文本")
 * KaqiuString(resourceId = R.string.app_name)
 */
@Composable
fun KaqiuText(
    text: String,
    modifier: Modifier = Modifier,
    style: TextStyle = androidx.compose.material3.LocalTextStyle.current,
    fontWeight: FontWeight? = null,
    textAlign: TextAlign? = null,
    maxLines: Int = Int.MAX_VALUE,
    overflow: TextOverflow = TextOverflow.Clip
) {
    val context = LocalContext.current
    val kaqiuText = KaqiuLanguageUtil.getText(context, text)
    
    Text(
        text = kaqiuText,
        modifier = modifier,
        style = style,
        fontWeight = fontWeight ?: style.fontWeight,
        textAlign = textAlign ?: TextAlign.Start,
        maxLines = maxLines,
        overflow = overflow
    )
}

/**
 * 卡丘语文本组件（带资源ID）
 * 根据卡丘语模式设置自动将字符串资源转换为卡丘语风格
 * 
 * 使用示例：
 * KaqiuString(resourceId = R.string.app_name)
 */
@Composable
fun KaqiuString(
    resourceId: Int,
    modifier: Modifier = Modifier,
    style: TextStyle = androidx.compose.material3.LocalTextStyle.current,
    fontWeight: FontWeight? = null,
    textAlign: TextAlign? = null,
    maxLines: Int = Int.MAX_VALUE,
    overflow: TextOverflow = TextOverflow.Clip
) {
    val context = LocalContext.current
    val originalText = context.getString(resourceId)
    val kaqiuText = KaqiuLanguageUtil.getText(context, originalText)
    
    Text(
        text = kaqiuText,
        modifier = modifier,
        style = style,
        fontWeight = fontWeight ?: style.fontWeight,
        textAlign = textAlign ?: TextAlign.Start,
        maxLines = maxLines,
        overflow = overflow
    )
}