package com.neko.music.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import androidx.compose.foundation.isSystemInDarkTheme
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.neko.music.R
import com.neko.music.ui.theme.*

@Composable
fun MineScreen(
    onRecentPlayClick: () -> Unit = {},
    onLoginClick: () -> Unit = {},
    onLogoutClick: () -> Unit = {},
    onFavoriteClick: () -> Unit = {},
    onAboutClick: () -> Unit = {},
    onNavigateToSettings: () -> Unit = {},
    onAccountInfoClick: () -> Unit = {},
    onUploadClick: () -> Unit = {},
    isLoggedIn: Boolean = false,
    username: String? = null,
    userId: Int = -1,
    onLoginSuccess: () -> Unit = {}
) {
    val context = LocalContext.current
    val view = LocalView.current
    SideEffect {
        val window = (view.context as android.app.Activity).window
        WindowCompat.setDecorFitsSystemWindows(window, false)
        WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
    }
    
    // 气泡上升动画
    val infiniteTransition = rememberInfiniteTransition(label = "bubbles")
    val bubble1Y by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = -100f,
        animationSpec = infiniteRepeatable(
            animation = tween(6000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        )
    )
    val bubble2Y by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = -120f,
        animationSpec = infiniteRepeatable(
            animation = tween(7000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        )
    )
    val bubble3Y by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = -90f,
        animationSpec = infiniteRepeatable(
            animation = tween(5500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        )
    )
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 60.dp)
        ) {
            item {
                MineHeader(
                    onLoginClick = onLoginClick,
                    isLoggedIn = isLoggedIn,
                    username = username,
                    userId = userId,
                    onLogoutClick = onLogoutClick,
                    onAccountInfoClick = onAccountInfoClick,
                    bubble1Y = bubble1Y,
                    bubble2Y = bubble2Y,
                    bubble3Y = bubble3Y
                )
                
                Spacer(modifier = Modifier.height(24.dp))

                MineStats(onUploadClick = onUploadClick)
                
                Spacer(modifier = Modifier.height(16.dp))
                
                MineMenu(
                    onRecentPlayClick = onRecentPlayClick,
                    onFavoriteClick = onFavoriteClick
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                MoreSettings(
                    onAboutClick = onAboutClick,
                    onNavigateToSettings = onNavigateToSettings,
                    isLoggedIn = isLoggedIn,
                    onLoginClick = onLoginClick,
                    onLogoutClick = onLogoutClick
                )
                
                Spacer(modifier = Modifier.height(100.dp))
            }
        }
    }
}

@Composable
fun MineHeader(
    onLoginClick: () -> Unit = {},
    isLoggedIn: Boolean = false,
    username: String? = null,
    userId: Int = -1,
    onLogoutClick: () -> Unit = {},
    onAccountInfoClick: () -> Unit = {},
    bubble1Y: Float = 0f,
    bubble2Y: Float = 0f,
    bubble3Y: Float = 0f
) {
    val context = LocalContext.current
    
    // 头像更新时间戳，用于绕过缓存
    var avatarUpdateTime by remember { mutableStateOf(System.currentTimeMillis()) }
    
    // 监听登录状态变化，重新加载头像
    LaunchedEffect(isLoggedIn, userId) {
        avatarUpdateTime = System.currentTimeMillis()
    }
    
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(240.dp)
    ) {
        Image(
            painter = painterResource(id = R.drawable.background),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        // 装饰圆圈
        androidx.compose.foundation.Canvas(
            modifier = Modifier.fillMaxSize()
        ) {
            drawCircle(
                color = SakuraPink.copy(alpha = 0.15f),
                radius = 100.dp.toPx(),
                center = androidx.compose.ui.geometry.Offset(size.width * 0.85f, size.height * 0.3f)
            )
            drawCircle(
                color = SkyBlue.copy(alpha = 0.1f),
                radius = 70.dp.toPx(),
                center = androidx.compose.ui.geometry.Offset(size.width * 0.15f, size.height * 0.6f)
            )
        }
        
        // 气泡装饰
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .offset(x = 30.dp, y = bubble1Y.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.3f))
            )
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .offset(x = 80.dp, y = bubble2Y.dp)
                    .clip(CircleShape)
                    .background(SkyBlue.copy(alpha = 0.4f))
            )
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .offset(x = 120.dp, y = bubble3Y.dp)
                    .clip(CircleShape)
                    .background(SakuraPink.copy(alpha = 0.3f))
            )
        }
        
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            // 头像容器
            Box(
                modifier = Modifier
                    .size(90.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(MaterialTheme.colorScheme.surface)
                    .shadow(
                        elevation = 8.dp,
                        spotColor = RoseRed.copy(alpha = 0.4f),
                        ambientColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.2f)
                    )
                    .clickable {
                        if (isLoggedIn) {
                            onAccountInfoClick()
                        } else {
                            onLoginClick()
                        }
                    },
                contentAlignment = Alignment.Center
            ) {
                if (isLoggedIn && userId != -1) {
                    AsyncImage(
                        model = ImageRequest.Builder(context)
                            .data("https://music.cnmsb.xin/api/user/avatar/$userId?t=$avatarUpdateTime")
                            .crossfade(true)
                            .build(),
                        contentDescription = "用户头像",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    AsyncImage(
                        model = ImageRequest.Builder(context)
                            .data("https://music.cnmsb.xin/api/user/avatar/default")
                            .crossfade(true)
                            .build(),
                        contentDescription = "默认头像",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = if (isLoggedIn && username != null) username else "未登录",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFFF6B6B)
            )
            
            Spacer(modifier = Modifier.height(6.dp))

            // 名字下方的内容
//            Text(
//                text = if (isLoggedIn) "已登录" else "点击登录",
//                fontSize = 14.sp,
//                color = Color.White.copy(alpha = 0.8f),
//                fontWeight = FontWeight.Medium
//            )
        }
    }
}

@Composable
fun MineStats(onUploadClick: () -> Unit = {}) {
    var uploadCount by remember { mutableStateOf(0) }
    
    LaunchedEffect(Unit) {
        // TODO: 调用API获取上传数量
        // val response = userApi.getUploadedMusic()
        // if (response.success) {
        //     uploadCount = response.total
        // }
    }
    
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(
                        RoseRed.copy(alpha = 0.12f),
                        SakuraPink.copy(alpha = 0.12f)
                    )
                ),
                shape = RoundedCornerShape(20.dp)
            )
            .shadow(
                elevation = 4.dp,
                spotColor = RoseRed.copy(alpha = 0.2f),
                ambientColor = Color.Gray.copy(alpha = 0.1f)
            )
            .padding(20.dp),
        horizontalArrangement = Arrangement.Center
    ) {
        StatItem(uploadCount.toString(), "上传", onUploadClick = onUploadClick)
    }
}

@Composable
fun StatItem(count: String, label: String, onUploadClick: () -> Unit = {}) {
    var isPressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        )
    )
    
    Column(
        modifier = Modifier
            .scale(scale)
            .clickable {
                isPressed = true
                onUploadClick()
            },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = count,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = RoseRed
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = label,
            fontSize = 13.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun NekoMemberBanner() {
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
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .height(100.dp)
            .background(
                brush = Brush.horizontalGradient(
                    colors = listOf(
                        RoseRed.copy(alpha = 0.15f),
                        SakuraPink.copy(alpha = 0.15f)
                    )
                ),
                shape = RoundedCornerShape(20.dp)
            )
            .scale(scale)
            .shadow(
                elevation = 4.dp,
                spotColor = RoseRed.copy(alpha = 0.2f),
                ambientColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.1f)
            )
            .clickable {
                // 暂未实现
            },
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 内容已移除
        }
    }
}

@Composable
fun MineMenu(
    onRecentPlayClick: () -> Unit = {},
    onFavoriteClick: () -> Unit = {},
    isLoggedIn: Boolean = false,
    onLogoutClick: () -> Unit = {},
    onLoginClick: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
    ) {
        Text(
            text = "我的",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        MenuItem("我的音乐", R.drawable.music, RoseRed)
        MenuItem("我的收藏", R.drawable.ic_favorite_filled, SakuraPink, onClick = onFavoriteClick)
        MenuItem("最近播放", R.drawable.recently_played, SkyBlue, onClick = onRecentPlayClick)
    }
}

@Composable
fun MenuItem(
    title: String,
    iconResId: Int,
    iconColor: Color = RoseRed,
    onClick: () -> Unit = {}
) {
    var isPressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.98f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        )
    )
    
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .background(
                color = MaterialTheme.colorScheme.surface,
                shape = RoundedCornerShape(16.dp)
            )
            .padding(16.dp)
            .shadow(
                elevation = 2.dp,
                spotColor = RoseRed.copy(alpha = 0.15f),
                ambientColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.08f)
            )
            .scale(scale)
            .clickable {
                isPressed = true
                onClick()
            },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(iconColor.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = iconResId),
                contentDescription = title,
                modifier = Modifier.size(28.dp),
                colorFilter = androidx.compose.ui.graphics.ColorFilter.tint(iconColor)
            )
        }
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = title,
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.onSurface,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = "›",
            fontSize = 20.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
        )
    }
}

@Composable
fun MoreSettings(
    onAboutClick: () -> Unit = {},
    onNavigateToSettings: () -> Unit = {},
    isLoggedIn: Boolean = false,
    onLoginClick: () -> Unit = {},
    onLogoutClick: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
    ) {
        Text(
            text = "设置",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        MenuItem("设置", R.drawable.setting, RoseRed, onClick = onNavigateToSettings)
        MenuItem("关于我们", R.drawable.about, StarYellow, onClick = onAboutClick)
        
        Spacer(modifier = Modifier.height(16.dp))
        
        if (isLoggedIn) {
            MenuItem("退出登录", R.drawable.logout, Lilac, onClick = onLogoutClick)
        } else {
            MenuItem("登录", R.drawable.login, Peach, onClick = onLoginClick)
        }
    }
    
    // 页脚
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = "蜀ICP备2025177767号-1 如有侵权请联系support@cnmsb.xin",
            fontSize = 10.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
        Text(
            text = "© 2025-2026 Fantasy Network「梦幻网络」 保留所有权利.",
            fontSize = 10.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}