package com.neko.music.ui.screens

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import com.neko.music.ui.theme.*
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import com.neko.music.R

@RequiresApi(Build.VERSION_CODES.P)
@Composable
fun AboutScreen(
    onBackClick: () -> Unit = {}
) {
    val context = LocalContext.current
    val view = LocalView.current
    val isDarkTheme = androidx.compose.foundation.isSystemInDarkTheme()
    
    SideEffect {
        val window = (view.context as android.app.Activity).window
        WindowCompat.setDecorFitsSystemWindows(window, false)
        WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !isDarkTheme
    }
    
    // 获取应用版本信息
    val packageInfo = remember {
        try {
            context.packageManager.getPackageInfo(context.packageName, 0)
        } catch (e: Exception) {
            null
        }
    }
    
    val versionName = packageInfo?.versionName ?: "未知版本"
    val versionCode = packageInfo?.longVersionCode?.toString() ?: "未知"
    
    // 浮动动画
    val infiniteTransition = rememberInfiniteTransition(label = "floating")
    val floatOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 10f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        )
    )
    
    // 进入动画
    var isVisible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        isVisible = true
    }
    
    val scale by animateFloatAsState(
        targetValue = if (isVisible) 1f else 0.95f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        )
    )
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(if (isDarkTheme) Color(0xFF121228) else Color(0xFFFAFAFA))
            .statusBarsPadding()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(bottom = 150.dp)
        ) {
            Spacer(modifier = Modifier.height(24.dp))
            
            // 应用信息卡片
            InfoCard(
                iconRes = R.drawable.about,
                title = "应用信息",
                items = listOf(
                    "应用名称" to "Neko云音乐",
                    "版本号" to versionName,
                    "版本代码" to versionCode,
                    "包名" to "com.neko.music"
                ),
                scale = scale
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // 组织信息卡片
            InfoCard(
                iconRes = R.drawable.about,
                title = "组织信息",
                items = listOf(
                    "组织名称" to "Fantasy Network「梦幻网络」",
                    "联系方式" to "support@cnmsb.xin"
                ),
                scale = scale
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // 技术栈卡片
            TechStackCard(scale = scale)
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // 版权信息
            CopyrightCard()
            
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun InfoCard(
    iconRes: Int,
    title: String,
    items: List<Pair<String, String>>,
    scale: Float
) {
    val isDarkTheme = androidx.compose.foundation.isSystemInDarkTheme()
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .scale(scale)
            .shadow(
                elevation = 4.dp,
                spotColor = RoseRed.copy(alpha = 0.2f),
                ambientColor = if (isDarkTheme) Color(0xFFB8B8D1).copy(alpha = 0.1f) else Color.Gray.copy(alpha = 0.1f)
            ),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isDarkTheme) Color(0xFF252545).copy(alpha = 0.6f) else Color.White
        )
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(iconRes),
                    contentDescription = title,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = title,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isDarkTheme) Color(0xFFF0F0F5).copy(alpha = 0.95f) else Color.Black
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Divider(
                color = if (isDarkTheme) Color(0xFF2A2A4E).copy(alpha = 0.5f) else Color(0xFFE0E0E0),
                thickness = 1.dp
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            items.forEach { (label, value) ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = label,
                        fontSize = 15.sp,
                        color = if (isDarkTheme) Color(0xFFB8B8D1).copy(alpha = 0.8f) else Color.Gray,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = value,
                        fontSize = 15.sp,
                        color = if (isDarkTheme) Color(0xFFF0F0F5).copy(alpha = 0.95f) else Color.Black,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

@Composable
fun TechStackCard(scale: Float) {
    val isDarkTheme = androidx.compose.foundation.isSystemInDarkTheme()
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .scale(scale)
            .shadow(
                elevation = 4.dp,
                spotColor = RoseRed.copy(alpha = 0.2f),
                ambientColor = if (isDarkTheme) Color(0xFFB8B8D1).copy(alpha = 0.1f) else Color.Gray.copy(alpha = 0.1f)
            ),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isDarkTheme) Color(0xFF252545).copy(alpha = 0.6f) else Color.White
        )
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(id = R.drawable.setting),
                    contentDescription = "技术栈",
                    modifier = Modifier.size(28.dp),
                    colorFilter = ColorFilter.tint(RoseRed)
                )

                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "技术栈",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isDarkTheme) Color(0xFFF0F0F5).copy(alpha = 0.95f) else Color.Black
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Divider(
                color = if (isDarkTheme) Color(0xFF2A2A4E).copy(alpha = 0.5f) else Color(0xFFE0E0E0),
                thickness = 1.dp
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            val techStack = listOf(
                "Kotlin" to "编程语言",
                "Jetpack Compose" to "UI框架",
                "Ktor" to "网络请求",
                "ExoPlayer" to "音频播放",
                "Room" to "本地数据库",
                "Coil" to "图片加载"
            )
            
            techStack.chunked(2).forEach { chunk ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    chunk.forEach { (tech, desc) ->
                        TechItem(tech, desc, Modifier.weight(1f))
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}

@Composable
fun TechItem(tech: String, desc: String, modifier: Modifier = Modifier) {
    val isDarkTheme = androidx.compose.foundation.isSystemInDarkTheme()
    
    Box(
        modifier = modifier
            .padding(horizontal = 4.dp)
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(
                        RoseRed.copy(alpha = 0.15f),
                        SakuraPink.copy(alpha = 0.15f)
                    )
                ),
                shape = RoundedCornerShape(12.dp)
            )
            .padding(12.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = tech,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = RoseRed
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = desc,
                fontSize = 12.sp,
                color = if (isDarkTheme) Color(0xFFB8B8D1).copy(alpha = 0.8f) else Color.Gray
            )
        }
    }
}

@Composable
fun CopyrightCard() {
    val isDarkTheme = androidx.compose.foundation.isSystemInDarkTheme()
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .shadow(
                elevation = 4.dp,
                spotColor = RoseRed.copy(alpha = 0.2f),
                ambientColor = if (isDarkTheme) Color(0xFFB8B8D1).copy(alpha = 0.1f) else Color.Gray.copy(alpha = 0.1f)
            ),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isDarkTheme) Color(0xFF252545).copy(alpha = 0.6f) else Color.White
        )
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(12.dp))
            
            Text(
                text = "版权信息",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = if (isDarkTheme) Color(0xFFF0F0F5).copy(alpha = 0.95f) else Color.Black
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Divider(
                color = if (isDarkTheme) Color(0xFF2A2A4E).copy(alpha = 0.5f) else Color(0xFFE0E0E0),
                thickness = 1.dp
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = "蜀ICP备2025177767号-1",
                fontSize = 14.sp,
                color = if (isDarkTheme) Color(0xFFB8B8D1).copy(alpha = 0.8f) else Color.Gray,
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "如有侵权请联系 support@cnmsb.xin",
                fontSize = 14.sp,
                color = if (isDarkTheme) Color(0xFFB8B8D1).copy(alpha = 0.8f) else Color.Gray,
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Text(
                text = "© 2025-2026 Fantasy Network「梦幻网络」",
                fontSize = 14.sp,
                color = if (isDarkTheme) Color(0xFFB8B8D1).copy(alpha = 0.8f) else Color.Gray,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Medium
            )
            
            Spacer(modifier = Modifier.height(4.dp))
            
            Text(
                text = "保留所有权利",
                fontSize = 14.sp,
                color = if (isDarkTheme) Color(0xFFB8B8D1).copy(alpha = 0.8f) else Color.Gray,
                textAlign = TextAlign.Center
            )
        }
    }
}