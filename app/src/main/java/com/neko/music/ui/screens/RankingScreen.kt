package com.neko.music.ui.screens

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
    
    var musicList by remember { mutableStateOf<List<Music>>(emptyList()) }
    var loading by remember { mutableStateOf(true) }
    var loadError by remember { mutableStateOf(false) }
    
    LaunchedEffect(Unit) {
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
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "热门音乐",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = RoseRed.copy(alpha = 0.8f)
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "返回",
                            tint = RoseRed.copy(alpha = 0.8f)
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
                                tint = RoseRed.copy(alpha = 0.8f),
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "播放全部",
                                fontSize = 14.sp,
                                color = RoseRed.copy(alpha = 0.8f)
                            )
                        }
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
                            SakuraPink.copy(alpha = 0.15f),
                            SkyBlue.copy(alpha = 0.15f)
                        )
                    )
                )
        ) {
            when {
                loading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            color = RoseRed.copy(alpha = 0.8f)
                        )
                    }
                }
                loadError -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "网络错误",
                            fontSize = 16.sp,
                            color = RoseRed.copy(alpha = 0.6f)
                        )
                    }
                }
                musicList.isEmpty() -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "暂无热门音乐",
                            fontSize = 16.sp,
                            color = RoseRed.copy(alpha = 0.6f)
                        )
                    }
                }
                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        itemsIndexed(musicList) { index, music ->
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
fun RankingItem(
    music: Music,
    rank: Int,
    onClick: () -> Unit
) {
    val context = LocalContext.current
    val musicApi = remember { MusicApi(context) }
    var coverUrl by remember { mutableStateOf<String?>(null) }
    
    LaunchedEffect(music.id) {
        coverUrl = musicApi.getMusicCoverUrl(music)
    }
    
    // 排名颜色
    val rankColor = when (rank) {
        1 -> Color(0xFFFFD700) // 金色
        2 -> Color(0xFFC0C0C0) // 银色
        3 -> Color(0xFFCD7F32) // 铜色
        else -> RoseRed.copy(alpha = 0.6f)
    }
    
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(
                color = RoseRed.copy(alpha = 0.1f),
                shape = RoundedCornerShape(12.dp)
            )
            .clickable { onClick() }
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 排名
        Text(
            text = rank.toString(),
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = rankColor,
            modifier = Modifier.width(40.dp)
        )
        
        Spacer(modifier = Modifier.width(12.dp))
        
        // 封面
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(coverUrl ?: "https://music.cnmsb.xin/api/user/avatar/default")
                .crossfade(true)
                .build(),
            contentDescription = music.title,
            modifier = Modifier
                .size(56.dp)
                .clip(RoundedCornerShape(8.dp)),
            contentScale = ContentScale.Crop
        )
        
        Spacer(modifier = Modifier.width(12.dp))
        
        // 歌曲信息
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = music.title,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = Color.White.copy(alpha = 0.95f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = music.artist,
                fontSize = 14.sp,
                color = RoseRed.copy(alpha = 0.7f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
        
        // 播放次数
        Text(
            text = "${music.playCount ?: 0}次",
            fontSize = 14.sp,
            color = RoseRed.copy(alpha = 0.8f),
            fontWeight = FontWeight.Medium
        )
    }
}