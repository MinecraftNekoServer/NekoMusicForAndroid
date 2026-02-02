package com.neko.music.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.neko.music.data.cache.MusicCacheManager
import com.neko.music.ui.theme.RoseRed
import androidx.compose.foundation.Image
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.ui.res.painterResource
import com.neko.music.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CacheManagementScreen(
    onBackClick: () -> Unit
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val isDarkTheme = isSystemInDarkTheme()
    val cacheManager = remember { MusicCacheManager.getInstance(context) }
    
    // 缓存数据
    var cacheSize by remember { mutableStateOf(cacheManager.getCacheSizeFormatted()) }
    var cachedMusicCount by remember { mutableStateOf(cacheManager.getCachedMusicCount()) }
    var cachedItems by remember { mutableStateOf(cacheManager.getAllCachedItems()) }
    var showClearDialog by remember { mutableStateOf(false) }
    var showDeleteItemDialog by remember { mutableStateOf<Pair<String, String>?>(null) }
    
    // 定期更新缓存数据
    LaunchedEffect(Unit) {
        while (true) {
            kotlinx.coroutines.delay(2000)
            cacheSize = cacheManager.getCacheSizeFormatted()
            cachedMusicCount = cacheManager.getCachedMusicCount()
            cachedItems = cacheManager.getAllCachedItems()
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("缓存管理") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "返回")
                    }
                },
                actions = {
                    TextButton(
                        onClick = { 
                            if (cachedItems.isNotEmpty()) {
                                showClearDialog = true
                            }
                        },
                        enabled = cachedItems.isNotEmpty()
                    ) {
                        Text(
                            text = "清空全部",
                            color = if (cachedItems.isNotEmpty()) RoseRed else Color.Gray,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(if (isDarkTheme) Color(0xFF121228) else Color(0xFFFAFAFA))
        ) {
            if (cachedItems.isEmpty()) {
                // 空状态
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "暂无缓存",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isDarkTheme) Color(0xFFB8B8D1).copy(alpha = 0.8f) else Color.Gray
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "播放音乐后会自动缓存",
                        fontSize = 14.sp,
                        color = if (isDarkTheme) Color(0xFFB8B8D1).copy(alpha = 0.6f) else Color.Gray
                    )
                }
            } else {
                // 缓存列表
                Column(
                    modifier = Modifier.fillMaxSize()
                ) {
                    // 统计信息卡片
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isDarkTheme) Color(0xFF252545).copy(alpha = 0.6f) else Color.White
                        ),
                        elevation = CardDefaults.cardElevation(
                            defaultElevation = 2.dp
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(20.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column {
                                    Text(
                                        text = "缓存统计",
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isDarkTheme) Color(0xFFF0F0F5).copy(alpha = 0.95f) else Color.Black
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = "已缓存 $cachedMusicCount 首歌曲",
                                        fontSize = 14.sp,
                                        color = if (isDarkTheme) Color(0xFFB8B8D1).copy(alpha = 0.8f) else Color.Gray
                                    )
                                    Text(
                                        text = "占用空间：$cacheSize",
                                        fontSize = 14.sp,
                                        color = if (isDarkTheme) Color(0xFFB8B8D1).copy(alpha = 0.8f) else Color.Gray
                                    )
                                }
                                Image(
                                    painter = painterResource(R.drawable.music),
                                    contentDescription = null,
                                    modifier = Modifier.size(48.dp)
                                )
                            }
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Text(
                        text = "缓存列表",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isDarkTheme) Color(0xFFF0F0F5).copy(alpha = 0.95f) else Color.Black,
                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
                    )
                    
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(cachedItems) { item ->
                            CacheItem(
                                musicId = item.first,
                                title = item.second,
                                onDelete = {
                                    showDeleteItemDialog = item
                                }
                            )
                        }
                    }
                }
            }
        }
    }
    
    // 清空全部缓存对话框
    if (showClearDialog) {
        AlertDialog(
            onDismissRequest = { showClearDialog = false },
            title = {
                Text(
                    text = "清空全部缓存",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = RoseRed
                )
            },
            text = {
                Column {
                    Text(
                        text = "确定要清空所有缓存吗？",
                        fontSize = 16.sp,
                        color = if (isDarkTheme) Color(0xFFB8B8D1).copy(alpha = 0.8f) else Color.Gray
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "这将删除 $cachedMusicCount 首歌曲的缓存",
                        fontSize = 14.sp,
                        color = Color.Red
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "释放空间：$cacheSize",
                        fontSize = 14.sp,
                        color = Color.Red
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        cacheManager.clearAllCache()
                        cacheSize = cacheManager.getCacheSizeFormatted()
                        cachedMusicCount = cacheManager.getCachedMusicCount()
                        cachedItems = cacheManager.getAllCachedItems()
                        showClearDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = RoseRed
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = "清空",
                        fontSize = 16.sp,
                        color = Color.White
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearDialog = false }) {
                    Text(
                        text = "取消",
                        fontSize = 16.sp,
                        color = Color.Gray
                    )
                }
            }
        )
    }
    
    // 删除单个缓存对话框
    showDeleteItemDialog?.let { (musicId, title) ->
        AlertDialog(
            onDismissRequest = { showDeleteItemDialog = null },
            title = {
                Text(
                    text = "删除缓存",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = RoseRed
                )
            },
            text = {
                Column {
                    Text(
                        text = "确定要删除这首歌的缓存吗？",
                        fontSize = 16.sp,
                        color = if (isDarkTheme) Color(0xFFB8B8D1).copy(alpha = 0.8f) else Color.Gray
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = title,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isDarkTheme) Color(0xFFF0F0F5).copy(alpha = 0.95f) else Color.Black
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        cacheManager.deleteMusicCache(musicId.toInt())
                        cacheSize = cacheManager.getCacheSizeFormatted()
                        cachedMusicCount = cacheManager.getCachedMusicCount()
                        cachedItems = cacheManager.getAllCachedItems()
                        showDeleteItemDialog = null
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = RoseRed
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = "删除",
                        fontSize = 16.sp,
                        color = Color.White
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteItemDialog = null }) {
                    Text(
                        text = "取消",
                        fontSize = 16.sp,
                        color = Color.Gray
                    )
                }
            }
        )
    }
}

@Composable
fun CacheItem(
    musicId: String,
    title: String,
    onDelete: () -> Unit
) {
    var isPressed by remember { mutableStateOf(false) }
    val isDarkTheme = isSystemInDarkTheme()
    val context = androidx.compose.ui.platform.LocalContext.current
    val cacheManager = remember { com.neko.music.data.cache.MusicCacheManager.getInstance(context) }
    val cachedCover = remember { cacheManager.getCachedCoverFile(musicId.toInt()) }
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                isPressed = true
                onDelete()
            },
        colors = CardDefaults.cardColors(
            containerColor = if (isPressed) 
                (if (isDarkTheme) Color(0xFF2A2A4E).copy(alpha = 0.5f) else Color(0xFFF5F5F5))
            else 
                (if (isDarkTheme) Color(0xFF252545).copy(alpha = 0.6f) else Color.White)
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 封面或默认图标
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .background(
                        color = RoseRed.copy(alpha = 0.1f),
                        shape = RoundedCornerShape(10.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (cachedCover != null && cachedCover.exists()) {
                    coil.compose.AsyncImage(
                        model = cachedCover,
                        contentDescription = "封面",
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Image(
                        painter = painterResource(R.drawable.music),
                        contentDescription = null,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = if (isDarkTheme) Color(0xFFF0F0F5).copy(alpha = 0.95f) else Color.Black,
                    maxLines = 1
                )
                Text(
                    text = "ID: $musicId",
                    fontSize = 13.sp,
                    color = if (isDarkTheme) Color(0xFFB8B8D1).copy(alpha = 0.8f) else Color.Gray
                )
            }
            
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = "删除",
                tint = RoseRed,
                modifier = Modifier.size(24.dp)
            )
        }
    }
    
    LaunchedEffect(isPressed) {
        if (isPressed) {
            kotlinx.coroutines.delay(100)
            isPressed = false
        }
    }
}