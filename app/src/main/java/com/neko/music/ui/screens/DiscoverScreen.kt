package com.neko.music.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import com.neko.music.ui.theme.RoseRed

@Composable
fun DiscoverScreen() {
    val view = LocalView.current
    SideEffect {
        val window = (view.context as android.app.Activity).window
        WindowCompat.setDecorFitsSystemWindows(window, false)
        WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
    }
    
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(bottom = 150.dp)
    ) {
        item {
            DiscoverHeader()
        }
        
        item {
            Spacer(modifier = Modifier.height(20.dp))
            DiscoverContent()
        }
    }
}

@Composable
fun DiscoverHeader() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp)
            .background(RoseRed)
            .statusBarsPadding()
            .padding(20.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "发现",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
    }
}

@Composable
fun DiscoverContent() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
    ) {
        Text(
            text = "热门榜单",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        repeat(3) {
            RankingItem("榜单 ${it + 1}")
            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}

@Composable
fun RankingItem(title: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp)
            .background(
                color = RoseRed.copy(alpha = 0.1f),
                shape = androidx.compose.foundation.shape.RoundedCornerShape(8.dp)
            )
            .padding(16.dp)
    ) {
        Text(
            text = title,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}