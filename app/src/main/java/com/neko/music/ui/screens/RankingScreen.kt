package com.neko.music.ui.screens

import android.util.Log
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.neko.music.data.api.MusicApi
import com.neko.music.data.model.Music
import com.neko.music.ui.theme.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RankingScreen(
    onBackClick: () -> Unit = {},
    onPlayAll: (List<Music>) -> Unit = {}
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val musicApi = remember { MusicApi(context) }
    val listState = rememberLazyListState()
    
    var musicList by remember { mutableStateOf<List<Music>>(emptyList()) }
    var loading by remember { mutableStateOf(true) }
    var refreshing by remember { mutableStateOf(false) }
    var loadError by remember { mutableStateOf(false) }
    
    fun loadData() {
        loading = true
        scope.launch {
            try {
                Log.d("RankingScreen", "开始加载排行榜...")
                val result = musicApi.getRanking(200)
                result.onSuccess { list ->
                    musicList = list
                    Log.d("RankingScreen", "排行榜加载成功: ${list.size}首")
                }.onFailure { error ->
                    Log.e("RankingScreen", "排行榜加载失败: ${error.message}")
                    loadError = true
                }
            } catch (e: Exception) {
                Log.e("RankingScreen", "排行榜异常: ${e.message}", e)
                loadError = true
            } finally {
                loading = false
            }
        }
    }
    
    fun refreshData() {
        refreshing = true
        scope.launch {
            try {
                val result = musicApi.getRanking(200)
                result.onSuccess { list ->
                    musicList = list
                    loadError = false
                    Log.d("RankingScreen", "刷新成功: ${list.size}首")
                }.onFailure { error ->
                    Log.e("RankingScreen", "刷新失败: ${error.message}")
                    loadError = true
                }
            } catch (e: Exception) {
                Log.e("RankingScreen", "刷新异常: ${e.message}", e)
                loadError = true
            } finally {
                refreshing = false
            }
        }
    }
    
    LaunchedEffect(Unit) {
        loadData()
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "热门音乐",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = RoseRed
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "返回",
                            tint = RoseRed
                        )
                    }
                },
                actions = {
                    if (musicList.isNotEmpty()) {
                        TextButton(
                            onClick = { 
                                Log.d("RankingScreen", "播放全部: ${musicList.size}首")
                                onPlayAll(musicList)
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.PlayArrow,
                                contentDescription = "播放全部",
                                tint = RoseRed,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "播放全部",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                color = RoseRed
                            )
                        }
                    }
                    IconButton(
                        onClick = { refreshData() },
                        enabled = !refreshing
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "刷新",
                            tint = if (refreshing) RoseRed.copy(alpha = 0.4f) else RoseRed,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                )
            )
        },
        containerColor = Color.Transparent
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            SakuraPink.copy(alpha = 0.12f),
                            SkyBlue.copy(alpha = 0.08f),
                            Lilac.copy(alpha = 0.05f)
                        )
                    )
                )
        ) {
            when {
                loading && musicList.isEmpty() -> {
                    LoadingState()
                }
                loadError && musicList.isEmpty() -> {
                    ErrorState(
                        onRetry = { loadData() }
                    )
                }
                musicList.isEmpty() -> {
                    EmptyState()
                }
                else -> {
                    LazyColumn(
                        state = listState,
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        itemsIndexed(
                            items = musicList,
                            key = { _, music -> music.id }
                        ) { index, music ->
                            RankingItem(
                                music = music,
                                rank = index + 1,
                                onClick = {
                                    Log.d("RankingScreen", "点击歌曲: ${music.title}")
                                    // TODO: 播放音乐
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun LoadingState() {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        CircularProgressIndicator(
            color = RoseRed,
            strokeWidth = 3.dp
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "正在加载热门音乐...",
            fontSize = 14.sp,
            color = RoseRed.copy(alpha = 0.7f)
        )
    }
}

@Composable
fun ErrorState(
    onRetry: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "加载失败",
            fontSize = 18.sp,
            fontWeight = FontWeight.Medium,
            color = RoseRed
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "网络连接似乎出现了问题",
            fontSize = 14.sp,
            color = RoseRed.copy(alpha = 0.6f)
        )
        Spacer(modifier = Modifier.height(20.dp))
        Button(
            onClick = onRetry,
            colors = ButtonDefaults.buttonColors(
                containerColor = RoseRed
            ),
            shape = RoundedCornerShape(20.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Refresh,
                contentDescription = null,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "重试",
                fontSize = 14.sp
            )
        }
    }
}

@Composable
fun EmptyState() {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "暂无热门音乐",
            fontSize = 16.sp,
            color = RoseRed.copy(alpha = 0.6f)
        )
    }
}

@Composable
fun RankingItem(
    music: Music,
    rank: Int,
    onClick: () -> Unit
) {
    var coverUrl by remember { mutableStateOf<String?>(null) }
    var isLoaded by remember { mutableStateOf(false) }
    
    LaunchedEffect(music.id) {
        coverUrl = music.coverUrl
        isLoaded = true
    }
    
    val rankColor = when (rank) {
        1 -> Color(0xFFFFD700)
        2 -> Color(0xFFC0C0C0)
        3 -> Color(0xFFCD7F32)
        else -> RoseRed.copy(alpha = 0.5f)
    }
    
    val backgroundColor = when {
        rank <= 3 -> Brush.horizontalGradient(
            colors = listOf(
                RoseRed.copy(alpha = 0.15f),
                SakuraPink.copy(alpha = 0.1f)
            )
        )
        else -> Brush.horizontalGradient(
            colors = listOf(
                RoseRed.copy(alpha = 0.08f),
                Color.Transparent
            )
        )
    }
    
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(72.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(backgroundColor)
            .shadow(
                elevation = if (rank <= 3) 8.dp else 2.dp,
                spotColor = RoseRed.copy(alpha = 0.3f),
                ambientColor = RoseRed.copy(alpha = 0.1f)
            )
            .clickable { onClick() }
            .padding(horizontal = 14.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AnimatedVisibility(
            visible = isLoaded,
            enter = fadeIn(
                animationSpec = tween(300, delayMillis = rank * 30)
            ) + slideInHorizontally(
                animationSpec = tween(300, delayMillis = rank * 30),
                initialOffsetX = { -50 }
            )
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier.width(44.dp),
                    contentAlignment = Alignment.CenterStart
                ) {
                    Text(
                        text = rank.toString(),
                        fontSize = if (rank <= 3) 20.sp else 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = rankColor
                    )
                }
                
                Spacer(modifier = Modifier.width(8.dp))
                
                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            Brush.radialGradient(
                                colors = listOf(
                                    RoseRed.copy(alpha = 0.2f),
                                    RoseRed.copy(alpha = 0.05f)
                                )
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(coverUrl)
                            .crossfade(true)
                            .build(),
                        contentDescription = music.title,
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(12.dp)),
                        contentScale = ContentScale.Crop
                    )
                }
                
                Spacer(modifier = Modifier.width(12.dp))
                
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = music.title,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White.copy(alpha = 0.95f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(3.dp))
                    Text(
                        text = music.artist,
                        fontSize = 13.sp,
                        color = RoseRed.copy(alpha = 0.65f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                
                if (music.playCount != null && music.playCount > 0) {
                    Text(
                        text = formatPlayCount(music.playCount),
                        fontSize = 12.sp,
                        color = RoseRed.copy(alpha = 0.7f),
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

fun formatPlayCount(count: Int): String {
    return when {
        count >= 1000000 -> "${count / 1000000}M"
        count >= 1000 -> "${count / 1000}K"
        else -> count.toString()
    }
}